package app;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;

import cu_exceptions.InsertFailedException;
import db.DB;

public class Lesson {
	private long id;
	private long staffId;//	private Staff staff;
	private long venueId;//	private Venue venue;
	private long coId;//	private CourseOffering co;
	private double startHour;
	private double endHour;
	private int day;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp deleted_at = null;
	protected String type;
	
	
	protected enum Type{
		Lecture, 
		Tutorial
	}
	
	public Lesson() {
		// TODO Auto-generated constructor stub
	}
	
	protected Lesson(int day, double startHr, double dur, CourseOffering co) {
		this.day = day;
		this.startHour = startHr;
		this.endHour = startHr + dur;
		this.coId = (Long) co.getColumn("id");
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	// TODO Refactor logic
	protected boolean setStaff(Staff staff) throws InsertFailedException {
		return this.setColumn("staffId", staff.getColumn("id"));
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
		String sql = "update lessons set " + columnName + " = ?";
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
	
	// Saving instance into database TODO refactor to a common method
	protected void saveInstance(String sql) throws InsertFailedException {
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
