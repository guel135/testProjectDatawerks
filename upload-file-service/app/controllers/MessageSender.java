package controllers;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import play.Configuration;
import play.Logger;

public class MessageSender {
	private static final String MESSAGE_TOPIC = "miguelTopic";
	@Inject	 Configuration config;

	public void sendMessage(String message) {
		try {
//			Logger.info(config.getString("upload.url") + config.getString("activemq.url"));
//			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//					config.getString("activemq.user"), "activemq.admin", "activemq.url");

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin",
					"tcp://localhost:61616");

			Logger.info("Creating connection sending Activemq");
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Logger.info("Create destination topic");
			Destination destination = session.createTopic(MESSAGE_TOPIC);
			Logger.info("Create message producer");
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Create a messages
			TextMessage textMessage = session.createTextMessage(message);

			// Tell the producer to send the message
			Logger.info("Sent message: " + message + " hashcode: " + textMessage.hashCode() + " : "
					+ Thread.currentThread().getName());
			producer.send(textMessage);

			// Clean up
			session.close();
			connection.close();
		} catch (Exception e) {
			Logger.error("Caught: " + e);
		}
	}
}
