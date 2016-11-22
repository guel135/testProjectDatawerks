package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import play.Configuration;
import play.Logger.ALogger;
import play.Play;

public class MailConsumer implements Runnable, ExceptionListener {
	// mira esto del Alogger que no se que cono es
	private static final ALogger logger = play.Logger.of(MailConsumer.class);
	private static Thread mailConsumerService;

	public static synchronized void initService() {
		System.out.println("llamando al init");
		MailConsumer mailConsumer = new MailConsumer();
		if (mailConsumerService != null) {
			logger.info("STOPPING MailConsumer thread.");
			mailConsumerService.interrupt();
		}
		logger.info("Starting MailConsumer thread.");
		mailConsumerService = new Thread(mailConsumer);
		mailConsumerService.setDaemon(true);
		mailConsumerService.setName("MailConsumer Service");
		mailConsumerService.start();
		logger.info("MailConsumer thread started.");
	}

	@Inject
	Configuration config;

	@Override
	public void run() {
		try {
			System.out.println("entra en run" );
			//System.out.println("entra en run" + config.getString("activemq.server.user"));
					//+ config.getString("activemq.server.password") + config.getString("activemq.server.url"));
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"admin", "admin","tcp://localhost:61616");
			System.out.println("entra en run");
			// Create a Connection
			Connection connection = connectionFactory.createConnection();
			connection.start();

			connection.setExceptionListener(this);

			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			Destination destination = session.createTopic("miguelTopic");

			// Create a MessageConsumer from the Session to the Topic or Queue
			MessageConsumer consumer = session.createConsumer(destination);

			while (!Thread.currentThread().isInterrupted()) {
				// Wait for a message
				Message message = consumer.receive();

				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					System.out.println("Received: 25" + text);

				} else {
					System.out.println("Received: " + message);
					logger.info("message type: " + message.getClass().getSimpleName());
				}

			}
			logger.info("MailConsumer interrupted.");
			consumer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
			if (e instanceof InterruptedException) {
				logger.info("MailConsumer thread interrupted.");
			} else {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	}

	public synchronized void onException(JMSException ex) {
		System.out.println("JMS Exception occured.  Shutting down client.");
		logger.error("ErrorCode=" + ex.getErrorCode() + " , " + ex.getMessage(), ex);
	}
}
