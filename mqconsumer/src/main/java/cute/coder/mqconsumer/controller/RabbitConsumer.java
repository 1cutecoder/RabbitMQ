package cute.coder.mqconsumer.controller;

import com.rabbitmq.client.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by cute coder
 * 2019/11/10 11:13
 */
@RestController
public class RabbitConsumer {
    private static final String QUEUE_NAME="queue_demo";
    private static final String IP_ADDRESS = "192.168.43.254";
    private static final int PORT = 5672;//RbbitMq服务端默认端口号位5672

    @RequestMapping("hello1")
    public String consumer1() throws IOException, TimeoutException, InterruptedException {
        Address[] addresses = {new Address(IP_ADDRESS, PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root123");
        //连接方式与生产者不同
        Connection connection = factory.newConnection(addresses);//创建连接
        final Channel channel = connection.createChannel();//创建信道
        channel.basicQos(64);//设置客户端最多接受未被ack的消息个数
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("rev message:"+new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        //关闭连接 释放资源
        channel.basicConsume(QUEUE_NAME,consumer);
        TimeUnit.SECONDS.sleep(5);
        channel.close();;
        connection.close();
        return "";
    }

}
