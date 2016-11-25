package controllers;

import static play.libs.Json.toJson;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

public class PersistenceController extends Controller {

	@Transactional
	@BodyParser.Of(BodyParser.Json.class)
	public Result addUserFromJson() {

		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode json = request().body().asJson();
			User newUser = mapper.readValue(json.toString(), User.class);

			JPA.em().persist(newUser);
			ObjectNode result = Json.newObject();
			result.set("User", Json.toJson(newUser));

			return created(result);

		} catch (Exception e) {
			e.printStackTrace();
			return badRequest("Missing information");
		}

	}

	public Result listUsers() {

		CriteriaBuilder criteriaBuilder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

		Root<User> root = criteriaQuery.from(User.class);
		CriteriaQuery<User> all = criteriaQuery.select(root);
		TypedQuery<User> allQuery = JPA.em().createQuery(all);
		JsonNode jsonNodes = toJson(allQuery.getResultList());

		return ok(jsonNodes);
	}

	public Boolean userIdExists(Long id) {
		if (JPA.em().find(User.class, id) == null) {
			return false;
		} else {
			return true;
		}

	}

	public void insertUserWithTransaction(User user) {
		JPA.em().persist(user); 
	}

}
