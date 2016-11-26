package services;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is to initialize the message listener thread in activemq
 */
@Singleton
public class ApplicationTimer {

    @Inject
    	public ApplicationTimer() {
        MessageListener.initService();
    }

}
