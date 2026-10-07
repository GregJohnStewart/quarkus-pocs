package com.example;

import javax.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory; // Provider specific implementation

/**
 * Hello world!
 */
public class App {
    private static final String CONN_STRING = "tcp://192.168.122.76:61616";
    private static final String QUEUE = "quote-requests";


    public static void main(String[] args) {

        System.out.println("Starting thing");


        // 1. Instantiate the broker-specific ConnectionFactory (e.g., ActiveMQ Artemis)
        // For a remote broker, change "vm://0" to your broker URL (e.g., "tcp://localhost:61616")
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONN_STRING)
                .setUser("admin")
                .setPassword("admin");

        System.out.println("Created connection factory.");

//        try (Connection conn = cf.createConnection()) {
//            conn.start();
//            try (Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
//                Destination queue = session.createQueue(DESTINATION);
//                try (MessageProducer producer = session.createProducer(queue)) {
//                    TextMessage msg = session.createTextMessage("Hello, Artemis!");
//                    producer.send(msg);
//                    System.out.println("Sent: " + msg.getText());
//                }
//            }
//        }

        // 2. Use JMSContext (JMS 2.0+) inside a try-with-resources block.
        // This automatically handles opening and closing connections and sessions.
        try (JMSContext context = connectionFactory.createContext()) {

            System.out.println("Created connection context.");

            // 3. Define or look up the target Queue destination
            Queue queue = context.createQueue(QUEUE);

            System.out.println("Created/connected to queue.");

            String messageBody = "Hello World!";

            // 4. Create the producer and send the message
            context.createProducer().send(queue, messageBody);

            System.out.println("Sent message: " + messageBody);

        } catch (Exception e) {
            System.err.println("Error sending JMS message: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Message sent!");
    }
}
