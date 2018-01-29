package com.hry.spring.rabbitmq.advanced.publisherconfirm;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 通过通过AMQP事务机制实现保证发送的消息正常到达Broken
 *
 * Created by huangrongyou@yixin.im on 2018/1/29.
 */
public class PublisherConfirmRecv {
    private final static String QUEUE_NAME = "publisherconfirm";

    public static void execute(String host, String userName, String password){
        // 配置连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        // 需要在管理后台增加一个hry帐号
        factory.setUsername(userName);
        factory.setPassword(password);

        Connection connection = null;
        Channel channel = null;
        try {
            // 建立TCP连接
            connection = factory.newConnection();
            // 在TCP连接的基础上创建通道
            channel = connection.createChannel();
            // 声明一个持久化队列
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.println(" [PublisherConfirmRecv] Waiting for messages.");

            channel.basicQos(1);
            // 默认消费者实现
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [PublisherConfirmRecv] Received '" + message + "'");
                }
            };
            // 接收消息
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 不能执行关闭，如果关闭链接，则后续消息无法收到。无法模拟后续接收消息的情况
//            try {
//                channel.close();
//                connection.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//            }
        }
    }
}
