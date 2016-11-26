package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Person {

	@Id
	private long id;
	
	private String name;
	private String time_of_start;

	
	
//	public User(long id, String name, String time_of_start) {
//		super();
//		this.id = id;
//		this.name = name;
//		this.time_of_start = time_of_start;
//	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime_of_start() {
		return time_of_start;
	}

	public void setTime_of_start(String time_of_start) {
		this.time_of_start = time_of_start;
	}
}
