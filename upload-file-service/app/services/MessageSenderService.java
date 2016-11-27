package services;

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

public class MessageSenderService {
	@Inject
	Configuration config;

	public void sendMessage(String message) {
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					config.getString("activemq.user"), config.getString("activemq.admin"),
					config.getString("activemq.url"));

			// ActiveMQConnectionFactory connectionFactory = new
			// ActiveMQConnectionFactory("admin", "admin",
			// "tcp://localhost:61616");
			Logger.info("Creating connection sending Activemq");
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Logger.info("Create destination topic");
			Destination destination = session.createTopic(config.getString("activemq.topic"));
			
			Logger.info("Create message producer");
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			TextMessage textMessage = session.createTextMessage(message);

			Logger.info("Sent message: " + message + " hashcode: " + textMessage.hashCode());
			producer.send(textMessage);

			session.close();
			connection.close();
		} catch (Exception e) {
			Logger.error("Message cannot be delivered: " + e.getMessage());
		}
	}
}
