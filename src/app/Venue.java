package app;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Venue {
	private long id;
	private String location;
	private int capacity;
	private String purpose;
	
	public Venue(String location, int capacity, String purpose) {
		this.location = location;
		this.capacity = capacity;
		this.purpose = purpose;
	}
	
	public ArrayList<Lesson> getLessons() {
		
	}
	
	public boolean addLesson(Lesson l) {
		
	}
	
	public Object getColumn(String columnName) {
		try {
			Field field = this.getClass().getDeclaredField(columnName);
			return field.get(this);
		} catch (Exception e) {
			return null;
		}
	}
}
