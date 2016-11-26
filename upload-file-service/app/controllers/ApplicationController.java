package controllers;

import java.io.File;

import com.google.inject.Inject;

import play.Configuration;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.MessageSenderService;
import play.mvc.Result;

import views.html.*;

public class ApplicationController extends Controller {

	@Inject
	Configuration config;
	@Inject
	MessageSenderService messageSenderService;

	public Result upload() {
		MultipartFormData<File> body = request().body().asMultipartFormData();
		FilePart<File> fileUploaded = body.getFile("fileUpload");
		if (fileUploaded != null) {

			File file = fileUploaded.getFile();
			Logger.info("Copy file: " + fileUploaded.getFilename() + " to " + config.getString("upload.url"));
			if (fileUploaded.getContentType().equals("text/csv")) {
				String newFileName = config.getString("upload.url") + fileUploaded.getFilename();
				file.renameTo(new File(config.getString("upload.url"), fileUploaded.getFilename()));
				Logger.info("file Type: " + fileUploaded.getContentType());
				Logger.info("Send message to activeMQ sender");
				messageSenderService.sendMessage(newFileName);
				return ok("File " + fileUploaded.getFilename() + " uploaded");
			} else {
				Logger.error("Incorrect File type " + fileUploaded.getContentType() + " only csv is accepted");
				return badRequest();
			}
		} else {
			Logger.error("error", "Missing file");
			return badRequest();
		}
	}

	public Result index() {
		return ok(index.render("Your new application is ready."));
	}
}
