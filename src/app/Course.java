package app;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import db.DB;
import db_files.dbFile;

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
	
	public boolean createOffering(int maxNum) {
		try {
			DB db = new DB();
			String sql = "select id from course_offerings where courseId = " + this.getColumn("id") + " and deleted_at is null";
	    	HashMap<String, Object> result = db.search(db.getConn(), "CourseOffering", sql);
	    	if ((boolean) result.get("status") == true) {
				int count = ((ArrayList<Object>) result.get("data")).size();
				if (count ==1) {
					System.err.println("Already created offering!");
					return false;
				}
			} else {
				System.err.println("Finding offering failed: " + (String) result.get("message"));
				return false;
			}
//	    	TODO create a method to finish create and save to db
			CourseOffering courseOffering = new CourseOffering(maxNum, this);
			
		}
		catch (Exception e) {
			System.err.println("Creating offering failed: " + e);
			return false;
		}
		return true;
	}
	
	public CourseOffering getOffering() {
		try {
			DB db = new DB();
			String sql = "select id, maxNum from course_offerings where courseId = " + this.getColumn("id") + " and deleted_at is null";
		} catch (Exception e) {
			// TODO: handle exception
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
}

