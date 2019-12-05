package com.example.rabbit.chapter5.doc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

// 设置单条消息的过期时间 1
public class RabbitProducer5 {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // 获取Rabbit 工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置Rabbit 地址 端口 用户名 密码
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("chapter4Exchange", "direct", true, false, null);
        // 设置队列的过期时间
        Map<String, Object> argss = new HashMap<>();
        // 设置队列的过期时间单位是毫秒 12秒没有没有任何消费者也没有获取过消息则队列会被删除
        // 注意时间类型为long类型字符串类型则会报错:unacceptable_type,longstr
        argss.put("x-expires", 12000);
        channel.queueDeclare("chapter4Queue", true, false, false, argss);
        channel.queueBind("chapter4Queue", "chapter4Exchange", "chapter4");
        String message = "chapter1 demo!";
        //设置过期属性 消息过期直接将消息删除
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.expiration("6000");
        AMQP.BasicProperties properties = builder.build();
        // 开启confirm模式
        channel.confirmSelect();

        channel.addConfirmListener(new ConfirmListener() {
            // 消息成功接收
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("deliveryTag" + deliveryTag + ",multiple:" + multiple);
            }

            // 消息丢失
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println(deliveryTag);
            }
        });
//                channel.close();
//        connection.close();
        channel.basicPublish("chapter4Exchange", "chapter4", true, properties, "222".getBytes());
        message = "chapter1 demo!22";
        channel.basicPublish("chapter4Exchange", "chapter4", true, properties, "message".getBytes());
    }

}
