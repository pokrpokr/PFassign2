package app;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import cu_exceptions.InsertFailedException;
import db.DB;

public class Course {
	private long id;
	private String courseId;
	private String name;
	private String objective;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp deleted_at;
	
	
	public Course() {}
	
	public Course(String[] args) {
		this.courseId = args[0];
		this.name = args[1];
		this.objective = args[2];
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public static Course createCourse(String[] args) throws InsertFailedException {
		for (int i = 0; i < args.length; i++) {
			if (args[i].isEmpty()) {
				throw new InsertFailedException("Infos can not be empty");
			}
		}
		Course course = new Course(args);
		String sql = "insert into courses (courseId, name, objective, created_at, updated_at, deleted_at) values(?,?,?,?,?,?)";
		course.saveInstance(sql);
		return course;
	}
	
	public static ArrayList<Course> courses() throws SQLException {
		ArrayList<Course> courses = new ArrayList<>();
		DB db = new DB();
		String sql = "select * from courses where deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "Course", sql);
		if ((boolean) result.get("status") == true) {
			for (int i = 0; i < ((ArrayList<Object>) result.get("data")).size(); i++) {
				courses.add(i, (Course) ((ArrayList<Object>) result.get("data")).get(i));
			}
			return courses;
		} else {
			throw new SQLException((String) result.get("message"));
		}
	}
	
	public boolean createOffering(int maxNum) throws InsertFailedException, SQLException {
		DB db = new DB();
		String sql = "select id from cos where courseId = " + this.getColumn("id") + " and deleted_at is null";
	    HashMap<String, Object> result = db.search(db.getConn(), "CourseOffering", sql);
	    if ((boolean) result.get("status") == true) {
			int count = ((ArrayList<Object>) result.get("data")).size();
			if (count ==1) {
				throw new SQLException("Already created offering!");
			}
		} else {
			throw new SQLException((String) result.get("message"));
		}
		CourseOffering.createCourseOffering(maxNum, this);	
		return true;
	}
	
	public CourseOffering getOffering() throws SQLException {
			DB db = new DB();
			String sql = "select id, maxNum from cos where courseId = " + this.getColumn("id") + " and deleted_at is null";
			HashMap<String, Object> result = db.search(db.getConn(), "Course", sql);
			if ((boolean) result.get("status")) {
				ArrayList<Object> data = (ArrayList<Object>) result.get("data");
				CourseOffering co = (CourseOffering) data.get(0);
				return co;
			} else {
				throw new SQLException((String) result.get("message"));
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
		String sql = "update courses set " + columnName + " = ?";
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

