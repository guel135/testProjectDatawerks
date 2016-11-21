package controllers;

import java.io.File;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class Application extends Controller{
	
	public Result upload() {
	    MultipartFormData<File> body = request().body().asMultipartFormData();
	    FilePart<File> picture = body.getFile("fileUpload");
	    if (picture != null) {
	        String fileName = picture.getFilename();
	        String contentType = picture.getContentType();
	        File file = picture.getFile();
	        System.out.println(picture.getFilename());
	        return ok("File uploaded");
	    } else {
	        flash("error", "Missing file");
	        return badRequest();
	    }
	}
}
