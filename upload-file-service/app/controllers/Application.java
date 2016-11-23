package controllers;

import java.io.File;

import com.google.inject.Inject;

import play.Configuration;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class Application extends Controller {

	@Inject
	Configuration config;
	
	public Result upload() {
		MultipartFormData<File> body = request().body().asMultipartFormData();
		FilePart<File> fileUploaded = body.getFile("fileUpload");
		if (fileUploaded != null) {
			
			File file = fileUploaded.getFile();
			Logger.info("copy file: "+fileUploaded.getFilename()+" to "+config.getString("upload.url")+config.getString("activemq.url"));
			String newFileName=config.getString("upload.url")+fileUploaded.getFilename();
			file.renameTo(new File(config.getString("upload.url"),fileUploaded.getFilename()));
			
			
			Logger.info("Send message to activeMQ sender");
			MessageSender sender = new MessageSender();
			sender.sendMessage(newFileName);
			
			return ok("File " + fileUploaded.getFilename() + " uploaded");
		} else {
			Logger.error("error", "Missing file");
			return badRequest();
		}
	}
}
