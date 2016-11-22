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
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class ReadFileController extends Controller {

	String csvFile = "resources/data_test_cut.csv";
	String csvFileOriginal = "resources/data_test.csv";

	public void readFile() {
		// File file = new File("resources/data_test_cut.csv");
		// return file;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// esto deberia ser con una lambda y un filtro
				try {
					String[] user = line.split(cvsSplitBy);
					String message = "";
					if (user[0] != null && !(user[0].equals("Id"))) {
						message = "User [id= " + user[0];
						if (user[1] != null) {
							message = message + " , name=" + user[1];
							if (user[2] != null) {
								message = message + " , start_time=" + user[2] + "]";
							}
						}
					}
					System.out.println(message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Transactional(readOnly = true)
	public Result readFileSecondOption() {

		BufferedReader br = null;
		String line = "";
		TreeMap<String, User> users = new TreeMap<>();

		try {

			br = new BufferedReader(new FileReader(csvFileOriginal));
			while ((line = br.readLine()) != null) {
				
				// Filtering incomplete lines, this filter increase performance avoiding split empty lines.
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
						return ok(e.toString());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();

			return ok("FileNotFoundException");
		} catch (IOException e) {
			return ok("IOException");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					return ok("IOException");
				}
			}
		}
		manageReadedUsers(users);

		return ok();
	}

	private void manageReadedUsers(TreeMap<String, User> users) {
		for (Map.Entry<String, User> entry : users.entrySet()) {
			
			UserController userController=new UserController();
			
			if (!userController.userIdExists(entry.getValue().getId())){
				userController.insertUser(entry.getValue());
				System.out.println("Inserted user with id "+ entry.getKey()+ " in database");
				
			}else System.out.println("Existing user with id "+ entry.getKey()+ " in database");
			
			


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
