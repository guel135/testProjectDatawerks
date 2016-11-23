package controllers;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.google.inject.Inject;

import play.Configuration;
import play.Logger;

public class MessageConsumerController implements Runnable, ExceptionListener {
	private static final String TOPIC_NAME = "miguelTopic";
	private static Thread mailConsumerService;

	public static synchronized void initService() {
		Logger.info("Message Consumer initialized");
		MessageConsumerController mailConsumer = new MessageConsumerController();
		if (mailConsumerService != null) {
			Logger.info("STOPPING MailConsumer thread.");
			mailConsumerService.interrupt();
		}
		Logger.info("Starting MailConsumer thread.");
		mailConsumerService = new Thread(mailConsumer);
		mailConsumerService.setDaemon(true);
		mailConsumerService.setName("MailConsumer Service");
		mailConsumerService.start();
		Logger.info("MailConsumer thread started.");
	}

	@Inject
	Configuration config;

	@Override
	public void run() {
		try {

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin",
					"tcp://localhost:61616");

			Logger.info("Creating ActiveMQ connection");
			Connection connection = connectionFactory.createConnection();
			connection.start();
			connection.setExceptionListener(this);
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Logger.info("Connecting to topic " + TOPIC_NAME);
			Destination destination = session.createTopic(TOPIC_NAME);

			Logger.info("Creating consumer");
			MessageConsumer consumer = session.createConsumer(destination);

			while (!Thread.currentThread().isInterrupted()) {
				Logger.info("Wait for messages...");
				Message message = consumer.receive();

				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					Logger.info("Received: " + text);

				} else {
					Logger.info("Received: " + message.getClass().getSimpleName());
				}

			}
			Logger.info("MailConsumer interrupted.");
			consumer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
			if (e instanceof InterruptedException) {
				Logger.info("MailConsumer thread interrupted.");
			} else {
				Logger.error(e.getLocalizedMessage(), e);
			}
		}
	}

	public synchronized void onException(JMSException ex) {
		Logger.error("JMS Exception occured.  Shutting down client.");
		Logger.error("ErrorCode=" + ex.getErrorCode() + " , " + ex.getMessage(), ex);
	}
}