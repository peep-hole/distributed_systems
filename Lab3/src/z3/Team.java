package z3;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Team {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("TEAM");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "exchange4";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter team name: ");
        String team_name = br2.readLine();

        String EXCHANGE_NAME_2 = "exchange5";
        channel.exchangeDeclare(EXCHANGE_NAME_2, BuiltinExchangeType.FANOUT);

        channel.queueDeclare(team_name, false, false, false, null);

        channel.queueBind(team_name, EXCHANGE_NAME_2, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received message from administrator:  "  + message);
            }
        };

        channel.basicConsume(team_name, true, consumer);

        while (true) {

            // read msg
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter message: ");
            String message = br.readLine();

            String key = team_name + "." + message;

            // break condition
            if ("exit".equals(message)) {
                break;
            }

            // publish
            channel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message + " with " + key + " key");
        }
    }
}
