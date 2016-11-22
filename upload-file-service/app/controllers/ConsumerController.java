package controllers;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import play.Configuration;
import play.mvc.Controller;
import play.mvc.Result;

public class ConsumerController extends Controller implements Runnable {
	@Inject
	Configuration config;

	public Result messageConsumer() throws InterruptedException {

		run();
		Thread.sleep(2000);

		return ok("mensaje recibido");
	}

	public void run() {
		try {
			System.out.println("entry to messageConsumer");
			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					config.getString("activemq.server.user"), config.getString("activemq.server.password"),
					config.getString("activemq.server.url"));

			// Create a Connection
			Connection connection = connectionFactory.createConnection();
			connection.start();

			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue("miguelTopic");

			// Create a MessageConsumer from the Session to the Topic or Queue
			MessageConsumer consumer = session.createConsumer(destination);
			Thread.sleep(2000);
			// Wait for a message
			Message message = consumer.receive(1000);

			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();
				System.out.println("Received: hellooooooo" + text);
			} else {
				System.out.println("Received: helloooooo else" + message);
			}

			consumer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	public synchronized void onException(JMSException ex) {
		System.out.println("JMS Exception occured.  Shutting down client.");
	}
}
