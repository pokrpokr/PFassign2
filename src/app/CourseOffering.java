package app;

import java.lang.reflect.Field;

public class CourseOffering {
	private long id;
	private long courseId;
	private long lessonId;
	private int maxNum;
//	private Course course;
	
	public CourseOffering(int maxNum, Course course) {
		this.maxNum = maxNum;
	}
	
	public boolean addLecture(int day, double start, double dur, Venue ven) {
		
	}
	
	public boolean addTutorial(int day, double start, double dur, Venue ven) {
		
	}
	
	public Lesson getLesson(int day, int start, int end) {
		
	}
	
	public String checkClash(int day, int start) {
		
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
