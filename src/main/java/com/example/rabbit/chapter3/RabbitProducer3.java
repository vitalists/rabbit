package com.example.rabbit.chapter3;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
// 备用交换机
public class RabbitProducer3 {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取Rabbit 工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置Rabbit 地址 端口 用户名 密码
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();



        //声明一个备用交换机,生产者发出的消息没有找到对应路由的队列,则此条消息转发给备用队列.
        Map<String, Object> argsMap = new HashMap<>();
        // 声明一个 chapter1Exchange 的备用交换机
        argsMap.put("alternate-exchange", "myAe");
        channel.exchangeDeclare("normalExchange", "direct", true, false, argsMap);
        channel.exchangeDeclare("myAe", "fanout", true,  false, null);


        channel.queueDeclare("normalQueue", true, false, false, null);
        channel.queueBind("normalQueue", "normalExchange", "normalKey");


        channel.queueDeclare("unroutedQueue", true, false, false, null);
        channel.queueBind("unroutedQueue", "myAe", "normalKey");

        //向正常交换机发送消息,发送一个交换机没有的路由.消息就会转到备用的队列中去
        String message = "chapter1 demo!";
        // 传入一个无法路由到queue的RoutingKey
        channel.basicPublish("normalExchange", "chapter12415",true,true, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                System.out.println("收到的消息" + msg);
            }
        });


        channel.close();
        connection.close();
    }
}
