package services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import models.Person;
import play.Logger;
import play.db.jpa.Transactional;

public class ReadFileService {

	public PersistenceService persistenceService = new PersistenceService();

	@Transactional
	public void loadFileFromDisk(String fileUrl) {

		BufferedReader br = null;
		String line = "";
		TreeMap<String, Person> persons = new TreeMap<>();

		try {

			br = new BufferedReader(new FileReader(fileUrl));
			while ((line = br.readLine()) != null) {

				// Filtering incomplete lines, this filter increase performance
				// avoiding split empty lines.
				if (!line.contains(",,,,,"))
				// esto deberia ser con una lambda y un filtro
				{
					try {
						String[] word = line.split(",");
						if (word[0] != null && !(word[0].equals("Id")) && (word[1] != null) && (word[2] != null)) {
							if (persons.containsKey(word[0]) == false) {

								Person person = createPerson(word);
								persons.put(word[0], person);
							}
						}
					} catch (Exception e) {
						Logger.error("Line without appropiate, format rejected with error: " + e.getMessage());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();

			Logger.error("FileNotFoundException " + e.getMessage());
		} catch (IOException e) {
			Logger.error("IOException " + e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Logger.error("IOException " + e.getMessage());
				}
			}
		}
		manageReadedPersons(persons);

	}

	private void manageReadedPersons(TreeMap<String, Person> persons) {
		for (Map.Entry<String, Person> entry : persons.entrySet()) {

			if (!persistenceService.personIdExists(entry.getValue().getId())) {
				if (persistenceService.insertPersonWithTransaction(entry.getValue())) {
					Logger.info("Inserted person with id " + entry.getKey() + " in database");

				} else
					Logger.error("Error calling persistence");

			} else
				Logger.info("Existing person with id " + entry.getKey() + " in database");

		}
	}

	private Person createPerson(String[] column) throws ParseException {
		Person person = new Person();
		person.setId(Long.parseLong(column[0]));
		person.setName(column[1].toLowerCase());
		person.setTime_of_start(timeToUTC(column[2]));
		return person;
	}

	private String timeToUTC(String dateTimeToConvert) throws ParseException {

		String pattern = "d-MM-yyyy HH:mm:ss";
		DateFormat sdf = new SimpleDateFormat(pattern);
		Date date = sdf.parse(dateTimeToConvert);
		return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).toString();

	}
}
