import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import models.Person;
import play.test.WithApplication;
import services.PersistenceService;
import services.ReadFileService;

public class LoadingFileTest extends WithApplication {

	private static final Long PERSONID2 = Long.valueOf(2);

	private static final Long PERSONID1 = Long.valueOf(1);

	private static final String TIME_START = "time_start";
	private static final String NAME = "Miguel";
	private static final long PERSONID = 8;

	@Test
	public void loadFileFromDiskTest() {
	
		Person person1;
		Person person2;
	
		person1 = new Person();
		person1.setId(PERSONID1);
		person1.setName("john");
		person1.setTime_of_start("1980-06-12T10:00:12Z[UTC]");
	
		person2 = new Person();
		person2.setId(PERSONID2);
		person2.setName("marrie angelina");
		person2.setTime_of_start("1981-06-12T10:00:12Z[UTC]");
	
		ReadFileService readFileService = new ReadFileService();
	
		// Create persistence mock
		readFileService.persistenceService = mock(PersistenceService.class);
		when(readFileService.persistenceService.personIdExists(PERSONID1)).thenReturn(false);
		when(readFileService.persistenceService.personIdExists(PERSONID2)).thenReturn(false);
		when(readFileService.persistenceService.insertPersonWithTransaction(person1)).thenReturn(true);
		when(readFileService.persistenceService.insertPersonWithTransaction(person2)).thenReturn(true);
	
		readFileService.loadFileFromDisk("resources/data_test_cut.csv");
	
		verify(readFileService.persistenceService).insertPersonWithTransaction(person1);
		verify(readFileService.persistenceService).insertPersonWithTransaction(person2);
		
		verify(readFileService.persistenceService).personIdExists(PERSONID1);
		verify(readFileService.persistenceService).personIdExists(PERSONID2);
	
	}

	@Test
	public void findPersonByIdWithValidId() {

		Person person = new Person();
		person.setId(PERSONID);
		person.setName(NAME);
		person.setTime_of_start(TIME_START);

		PersistenceService mockPersistence = mock(PersistenceService.class);
		when(mockPersistence.findPersonById(PERSONID)).thenReturn(person);

		Person personResult = mockPersistence.findPersonById(PERSONID);

		assertEquals(personResult, person);

		verify(mockPersistence);
	}

}
