package app;

import cu_exceptions.InsertFailedException;

public class Lecture extends Lesson {
	public Lecture(int day, double startHr, double dur, CourseOffering co) {
		super(day, startHr, dur, co);
		this.type = Type.Lecture.toString();
	}
	
	public static Lesson createLecture(int day, double startHr, double dur, CourseOffering co) throws InsertFailedException {
		Lesson lecture = new Lecture(day, startHr, dur, co);
		String sql = "insert into lessons (staffId, venueId, coId, startHour, endHour, day, type, created_at, updated_at, deleted_at) values(?,?,?,?,?,?,?,?,?,?)";
		lecture.saveInstance(sql);
		return lecture;
	} 
}
