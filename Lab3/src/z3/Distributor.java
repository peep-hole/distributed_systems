package z3;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Distributor {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("DISTRIBUTOR");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        // exchange
        String EXCHANGE_NAME = "exchange4";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String EXCHANGE_NAME_2 = "exchange6";
        channel.exchangeDeclare(EXCHANGE_NAME_2, BuiltinExchangeType.FANOUT);

        // queue & bind

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter distributor name: ");
        String distQ = br.readLine();

        System.out.println("Enter queue name 1: ");
        String q1 = br.readLine();

        channel.queueDeclare(q1, false, false, false, null);


        BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter queue name 2: ");
        String q2 = br.readLine();

        channel.queueDeclare(q2, false, false, false, null);

        channel.queueBind(q1, EXCHANGE_NAME, "*."+q1);
        System.out.println("created queue: " + q1);

        channel.queueBind(q2, EXCHANGE_NAME, "*."+q2);
        System.out.println("created queue: " + q2);


        channel.queueDeclare(distQ, false, false, false, null);

        channel.queueBind(distQ, EXCHANGE_NAME_2, "");


        final int[] order_id = {0};

        // consumer (message handling)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Order nr " + order_id[0] + " for "  + message + " from team " + envelope.getRoutingKey().split("\\.")[0]);
                order_id[0] +=1;
            }
        };

        Consumer consumer2 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received message from administrator:  "  + message);
            }
        };

        // start listening
        System.out.println("Waiting for messages...");
        channel.basicConsume(q1, true, consumer);
        channel.basicConsume(q2, true, consumer);
        channel.basicConsume(distQ, true, consumer2);


    }
}
