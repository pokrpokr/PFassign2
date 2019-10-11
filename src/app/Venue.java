package app;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import db.DB;

public class Venue {
	private long id;
	private String location;
	private int capacity;
	private String purpose;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp delete_at = null;
	
	public Venue(String location, int capacity, String purpose) {
		this.location = location;
		this.capacity = capacity;
		this.purpose = purpose;
		
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public ArrayList<Lesson> getLessons() {
		ArrayList<Lesson> lessons = new ArrayList<>();
		DB db = new DB();
		String sql = "select * from lessons where venueId = " + this.getColumn("venueId") + " and deleted_at is null";
    	HashMap<String, Object> result = db.search(db.getConn(), "Lesson", sql);
    	db.db_close();
    	ArrayList<Object> rsData = (ArrayList<Object>) result.get("data");
    	for (int i = 0; i < rsData.size(); i++) {
			lessons.add((Lesson) rsData.get(i));
		}
    	return lessons;
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
