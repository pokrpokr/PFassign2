package app;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import db.DB;

public class CourseOffering {
	private long id;
	private long courseId;//	private Course course;
	private int maxNum;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp deleted_at = null;

	
	public CourseOffering(int maxNum, Course course) {
		this.maxNum = maxNum;
		this.courseId = (Long) course.getColumn("id");
	}
	
	public boolean addLecture(int day, double start, double dur, Venue ven) {
		
	}
	
	public boolean addTutorial(int day, double start, double dur, Venue ven) {
		
	}
	
	public ArrayList<Lesson> lessons(int day, int start, int end) {
		ArrayList<Lesson> lessons = new ArrayList<>();
		DB db = new DB();
		String sql = "select * from lessons where day = " + day + 
				" and startHour >= " + Double.valueOf(start) + " and starHour <= " + Double.valueOf(end) + " and deleted_at is null";
    	HashMap<String, Object> result = db.search(db.getConn(), "Lesson", sql);
    	db.db_close();
    	ArrayList<Object> rsData = (ArrayList<Object>) result.get("data");
    	for (int i = 0; i < rsData.size(); i++) {
			lessons.add((Lesson) rsData.get(i));
		}
    	return lessons;
	}
	
	public Boolean checkClash(int day, int start, Lesson lesson) throws SQLException {
		DB db = new DB();
		String sql = "select * from lessons where day = " 
				+ " dayz" + " and startHour = " + Double.valueOf(start) + " and deleted_at is null";
    	HashMap<String, Object> result = db.search(db.getConn(), "Lesson", sql);
    	if ((Boolean) result.get("status") == true) {
    		ArrayList<Object> data = (ArrayList<Object>) result.get("data");
			int count = data.size();
			if (count > 1) {
				switch (lesson.getColumn("type").toString()) {
				case "Lecture":
					return true;
				case "Tutorial":
					for (int i = 0; i < count; i++) {
						if (((Lesson) data.get(i)).getColumn("venueId") == lesson.getColumn("venueId"))
							return true;
					}
					break;
				default:
					break;
				}
			}
			return false;
		}else {
			throw new SQLException("SQL is wrong");
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
