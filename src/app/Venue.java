package app;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import cu_exceptions.InsertFailedException;
import db.DB;

public class Venue {
	private long id;
	private String location;
	private int capacity;
	private String purpose;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp delete_at = null;
	
	public Venue() {}
	
	public Venue(String location, int capacity, String purpose) {
		this.location = location;
		this.capacity = capacity;
		this.purpose = purpose;
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public static Venue createVenue(String[] args) throws InsertFailedException {
		for (int i = 0; i < args.length; i++) {
			if (args[i].isEmpty()) {
				throw new InsertFailedException("Infos can not be empty");
			}
		}
		String location = args[0]; 
		String cap = args[1];
		String purpose = args[2];
		int capacity = Integer.parseInt(cap);
		Venue venue = new Venue(location, capacity, purpose);
		String sql = "insert into venues (location, capacity, purpose, created_at, updated_at, deleted_at) values(?,?,?,?,?,?)";
		venue.saveInstance(sql);
		return venue;
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
	
	public boolean addLesson(Lesson l) throws InsertFailedException {
		return l.setColumn("venueId", (Long) this.getColumn("id"));
	}
	
	public Object getColumn(String columnName) {
		try {
			Field field = this.getClass().getDeclaredField(columnName);
			return field.get(this);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Boolean setColumn(String columnName, Object value) throws InsertFailedException {
		DB db = new DB();
		String sql = "update venues set " + columnName + " = ?";
		HashMap<String, Object> result = db.update(db.getConn(), sql, columnName, value, this.id);
		if ((boolean) result.get("status")) {
			return true;
		}else {
			throw new InsertFailedException((String) result.get("message"));
		}
	}
	
	private boolean setId(Long id) {
		try {
			this.id = id;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void saveInstance(String sql) throws InsertFailedException {
		DB db = new DB();
    	HashMap<String, Object> insertRs = 
    			db.insert(db.getConn(), sql, this);
    	db.db_close();
    	if ((Boolean) insertRs.get("status")) {
			this.setId((Long) insertRs.get("id"));
		}else {
			throw new InsertFailedException((String) insertRs.get("message"));
		}
	}
}
