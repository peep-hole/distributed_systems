package z3;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Administrator {


    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("ADMINISTRATOR");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "exchange5";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        String EXCHANGE_NAME_2 = "exchange6";
        channel.exchangeDeclare(EXCHANGE_NAME_2, BuiltinExchangeType.FANOUT);


        while (true) {

            // read msg
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter message: ");
            String message = br.readLine();


            // break condition
            if ("exit".equals(message)) {
                break;
            }

            // publish

            boolean correct = false;

            while (!correct) {
                System.out.println("Choose who should receive this message:");
                System.out.println("1. All Teams");
                System.out.println("2. All Distributors");
                System.out.println("3. All Teams and Distributors");
                String receiver = br.readLine();

                if ("1".equals(receiver) || "3".equals(receiver)) {
                    channel.basicPublish(EXCHANGE_NAME, message, null, message.getBytes("UTF-8"));
                    System.out.println("Sent: " + message + " to all teams");
                    correct = true;
                }

                if ("2".equals(receiver) || "3".equals(receiver)) {
                    channel.basicPublish(EXCHANGE_NAME_2, message, null, message.getBytes("UTF-8"));
                    System.out.println("Sent: " + message + " to all distributors");
                    correct = true;
                }

            }


        }
    }
}
