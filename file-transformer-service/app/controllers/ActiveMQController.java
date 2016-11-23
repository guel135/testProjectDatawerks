package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class ActiveMQController extends Controller {
	public Result startActiveMQ() {

		MessageConsumerController.initService();
		return ok("Active MQ started");

	}
}
