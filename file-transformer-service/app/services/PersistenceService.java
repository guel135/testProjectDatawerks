package services;

import static play.libs.Json.toJson;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.fasterxml.jackson.databind.JsonNode;

import models.Person;
import play.db.jpa.JPA;

public class PersistenceService {

	public Boolean personIdExists(Long id) {
		if (JPA.em().find(Person.class, id) == null) {
			return false;
		} else {
			return true;
		}

	}

	public  Person findPersonById(Long id) {
		Person person = JPA.em().find(Person.class, id);

		return person;
	}

	public  JsonNode readAll() {
		CriteriaBuilder criteriaBuilder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);

		Root<Person> root = criteriaQuery.from(Person.class);
		CriteriaQuery<Person> all = criteriaQuery.select(root);
		TypedQuery<Person> allQuery = JPA.em().createQuery(all);
		JsonNode jsonNodes = toJson(allQuery.getResultList());
		return jsonNodes;
	}

	public void insertPersonWithTransaction(Person person) {
		JPA.em().persist(person);
	}

	public  void insertPerson(Person person) {
		JPA.em().getTransaction().begin();
		JPA.em().persist(person);
		JPA.em().getTransaction().commit();
	}
}
