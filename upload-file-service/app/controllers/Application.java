package controllers;

import java.io.File;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class Application extends Controller {

	public Result upload() {
		MultipartFormData<File> body = request().body().asMultipartFormData();
		FilePart<File> fileUploaded = body.getFile("fileUpload");
		if (fileUploaded != null) {
			String fileName = fileUploaded.getFilename();
			String contentType = fileUploaded.getContentType();
			File file = fileUploaded.getFile();
			System.out.println(fileUploaded.getFilename());
			MessageSender sender = new MessageSender();
			sender.sendMessage(fileName);

			return ok("File " + fileUploaded.getFilename() + " uploaded");
		} else {
			flash("error", "Missing file");
			return badRequest();
		}
	}

	public Result startActiveMQ() {

		MailConsumer.initService();
		return ok("Active MQ started");

	}

}
