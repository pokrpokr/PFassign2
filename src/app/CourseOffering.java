package app;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import cu_exceptions.*;
import db.DB;

public class CourseOffering {
	private long id;
	private long courseId;//	private Course course;
	private int maxNum;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp deleted_at = null;
	
	public CourseOffering() {}
	
	public CourseOffering(int maxNum, Course course) {
		this.courseId = (Long) course.getColumn("id");
		this.maxNum = maxNum;
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public static CourseOffering createCourseOffering(int maxNum, Course course) throws InsertFailedException {
		CourseOffering co = new CourseOffering(maxNum, course);
		String sql = "insert into cos (courseId, maxNum, created_at, updated_at, deleted_at) values(?,?,?,?,?)";
		co.saveInstance(sql);
		return co;
	}
	
	public static ArrayList<CourseOffering> cos() throws SQLException {
		ArrayList<CourseOffering> cos = new ArrayList<>();
		DB db = new DB();
		String sql = "select * from cos where deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "CourseOffering", sql);
		if ((boolean) result.get("status") == true) {
			for (int i = 0; i < ((ArrayList<Object>) result.get("data")).size(); i++) {
				cos.add(i, (CourseOffering) ((ArrayList<Object>) result.get("data")).get(i));
			}
			return cos;
		} else {
			throw new SQLException((String) result.get("message"));
		}
	}
	
	public Lesson lecture() throws NoResultException, SQLException {
		DB db = new DB();
		String sql = "select * from lessons where coId = " + this.getColumn("id") + " and type = 'Lecture' and deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "Lesson", sql);
		if ((boolean) result.get("status")) {
			if (((ArrayList<Object>) result.get("data")).size() == 0) {
				throw new NoResultException("This Course Offering hasn't add Lecture");
			}
			Lesson lecture = (Lesson) ((ArrayList<Object>) result.get("data")).get(0);
			return lecture;
		}else {
			throw new SQLException((String) result.get("message"));
		}
	}
	
	public Lesson addLecture(int day, double start, double dur) throws Exception {
		try {
			if (this.checkExist("Lecture")) throw new PreExistException("Lecture already exist");
			if (this.checkClash(day, start)) throw new ClashException("Time is clashed"); 
			Lesson lecture = Lecture.createLecture(day, start, dur, this);
			return lecture;
		} 
		catch (SQLException se) {
			throw se;
		}
		catch (InsertFailedException ie) {
			throw ie;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	public Lesson addTutorial(int day, double start, double dur) throws Exception {
		try {
			if (this.checkClash(day, start)) throw new ClashException("Time is clashed"); 
			Lesson tutorial = Tutorial.createTutorial(day, start, dur, this);
			return tutorial;
		} 
		catch (SQLException se) {
			throw se;
		}
		catch (InsertFailedException ie) {
			throw ie;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	public ArrayList<Lesson> lessons() throws SQLException {
		ArrayList<Lesson> lessons = new ArrayList<>();
		DB db = new DB();
		String sql = "select * from lessons where coId = " + this.getColumn("id") + " and deleted_at is null";
    	HashMap<String, Object> result = db.search(db.getConn(), "Lesson", sql);
    	db.db_close();
    	if ((boolean) result.get("status")) {
    		ArrayList<Object> rsData = (ArrayList<Object>) result.get("data");
        	for (int i = 0; i < rsData.size(); i++) {
    			lessons.add((Lesson) rsData.get(i));
    		}
        	return lessons;
		}else {
			throw new SQLException((String) result.get("message"));
		}
	}
	
	public Boolean checkClash(int day, double start) throws SQLException {
		DB db = new DB();
		String sql = "select * from lessons where day = " 
				+ day + " and startHour <= " + start + " and endHour > " + start + " and coId = " + this.getColumn("id") + " and deleted_at is null";
    	HashMap<String, Object> result = db.search(db.getConn(), "Lesson", sql);
    	if ((Boolean) result.get("status") == true) {
    		ArrayList<Object> data = (ArrayList<Object>) result.get("data");
			int count = data.size();
			if (count == 1) {
				return true;
			}
			return false;
		}else {
			throw new SQLException("SQL is wrong : " + ((String) result.get("message")));
		}
	}
	
	public boolean checkExist(String type) throws SQLException {
		DB db = new DB();
		String sql = "select * from lessons where type = '" + type + "' and coId = " + this.getColumn("id") + " and deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "Lesson", sql);
		if ((Boolean) result.get("status")) {
			ArrayList<Object> data = (ArrayList<Object>) result.get("data");
			int count = data.size();
			if (count == 1) {
				return true;
			}
			return false;
		} else {
			throw new SQLException("SQL is wrong : " + ((String) result.get("message")));
		}
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
		String sql = "update cos set " + columnName + " = ?";
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
