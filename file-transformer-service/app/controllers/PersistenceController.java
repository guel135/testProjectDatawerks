package controllers;

import static play.libs.Json.toJson;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Person;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

public class PersistenceController extends Controller {

	@Transactional
	@BodyParser.Of(BodyParser.Json.class)
	public Result addPersonFromJson() {

		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode json = request().body().asJson();
			Person newPerson = mapper.readValue(json.toString(), Person.class);

			JPA.em().persist(newPerson);
			ObjectNode result = Json.newObject();
			result.set("Person", Json.toJson(newPerson));

			return created(result);

		} catch (Exception e) {
			e.printStackTrace();
			return badRequest("Missing information");
		}

	}

	public Result listPersons() {

		CriteriaBuilder criteriaBuilder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);

		Root<Person> root = criteriaQuery.from(Person.class);
		CriteriaQuery<Person> all = criteriaQuery.select(root);
		TypedQuery<Person> allQuery = JPA.em().createQuery(all);
		JsonNode jsonNodes = toJson(allQuery.getResultList());

		return ok(jsonNodes);
	}

	public Boolean personIdExists(Long id) {
		if (JPA.em().find(Person.class, id) == null) {
			return false;
		} else {
			return true;
		}

	}

	public void insertPersonWithTransaction(Person person) {
		JPA.em().persist(person); 
	}

}
