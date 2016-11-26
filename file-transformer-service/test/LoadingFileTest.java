import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;

import controllers.PersonController;
import models.Person;
import play.test.Helpers;
import play.test.WithApplication;
import services.PersistenceService;

public class LoadingFileTest  extends WithApplication{

	@Inject PersistenceService persistenceService;
	
	private static final String TIME_START = "time_start";
	private static final String NAME = "Miguel";
	private static final long PERSONID = 8;
//	@Test
//	public void insertCorrectPersonReturnsOk()
//	{
//		String jsonPerson="{ \" id \": 55522,\"name\": \"Robert\","
//				+ "	\"time_of_start\": \"Tomson\" }";
//		
//		
//	      Helpers.running(Helpers.fakeApplication(), () -> {  
//	    	  PersonController personController=new PersonController();
//	    	  personController.addPersonFromJson();
//	    	  
//	           // put asserts  
//	       });
//	      
//	      
//	}
	@Test
	public void insertPersonPersistence(){
		Person person=new Person();
		person.setId(PERSONID);
		person.setName(NAME);
		person.setTime_of_start(TIME_START);
		
		persistenceService.insertPerson(person);
		
		Person personResult= persistenceService.findPersonById(PERSONID);
		
		assertEquals(personResult.getId(),PERSONID);
		assertEquals(personResult.getName(),NAME);
		assertEquals(personResult.getTime_of_start(),TIME_START);
		
	}
	
}

