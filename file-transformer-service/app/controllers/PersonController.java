package controllers;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Person;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.PersistenceService;

public class PersonController extends Controller {
	@Inject
	PersistenceService persistenceService;

	@Transactional
	@BodyParser.Of(BodyParser.Json.class)
	public Result addPersonFromJson() {

		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode json = request().body().asJson();
			Person newPerson = mapper.readValue(json.toString(), Person.class);

			persistenceService.insertPerson(newPerson);

			ObjectNode result = Json.newObject();
			result.set("Person", Json.toJson(newPerson));

			return created(result);

		} catch (Exception e) {
			Logger.error("AddPerson fail " + e.toString());
			return badRequest(e.getMessage());
		}

	}

	public Result listPersons() {

		JsonNode jsonNodes = persistenceService.readAll();

		return ok(jsonNodes);
	}

}
