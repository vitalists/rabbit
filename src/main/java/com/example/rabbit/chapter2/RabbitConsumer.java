package com.example.rabbit.chapter2;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitConsumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(64);
        // 创建一个消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                System.out.println("message:" + new String(body));
                try {
                    // 模拟接收消息处理..
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(envelope.getDeliveryTag());
                channel.basicAck(envelope.getDeliveryTag(), false);
                
            }
        };
        // 将消费者绑定到队列中去,设置接收到消息的回调对象,如果收到消息将调用consumer中的方法
        channel.basicConsume("chaepter1Queen", consumer);
        // channel 要在ack 应答之后关闭:因为ack应答方法中检查了channel是否已经关闭,如果关闭会抛异常
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();
    }
}
