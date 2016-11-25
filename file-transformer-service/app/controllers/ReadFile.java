package controllers;

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

import models.User;
import play.Logger;
import play.db.jpa.Transactional;

public class ReadFile {
	
	@Transactional(readOnly = true)
	public void loadFileFromDisk(String fileUrl) {

		BufferedReader br = null;
		String line = "";
		TreeMap<String, User> users = new TreeMap<>();

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
							if (users.containsKey(word[0]) == false) {

								User user = createUser(word);
								users.put(word[0], user);
							}
						}
					} catch (Exception e) {
						Logger.error("Line without appropiate format rejected with error: "+e.toString());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();

			Logger.info("FileNotFoundException");
		} catch (IOException e) {
			Logger.info("IOException");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Logger.info("IOException");
				}
			}
		}
		manageReadedUsers(users);

	}

	private void manageReadedUsers(TreeMap<String, User> users) {
		for (Map.Entry<String, User> entry : users.entrySet()) {

			PersistenceController persistenceController = new PersistenceController();

			if (!persistenceController.userIdExists(entry.getValue().getId())) {
				persistenceController.insertUserWithTransaction(entry.getValue());
				System.out.println("Inserted user with id " + entry.getKey() + " in database");

			} else
				System.out.println("Existing user with id " + entry.getKey() + " in database");

		}
	}

	private User createUser(String[] column) throws ParseException {
		User user = new User();
		user.setId(Long.parseLong(column[0]));
		user.setName(column[1].toLowerCase());
		user.setTime_of_start(timeToUTC(column[2]));
		return user;
	}

	private String timeToUTC(String dateTimeToConvert) throws ParseException {

		String pattern = "d-MM-yyyy HH:mm:ss";
		DateFormat sdf = new SimpleDateFormat(pattern);
		Date date = sdf.parse(dateTimeToConvert);
		return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).toString();

	}
}
