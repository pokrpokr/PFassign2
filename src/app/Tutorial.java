package app;

import cu_exceptions.InsertFailedException;

public class Tutorial extends Lesson {
	public Tutorial(int day, double startHr, double dur, Venue venue, CourseOffering co) {
		super(day, startHr, dur, venue, co);
		this.type = Type.Tutorial.toString();
	}
	
	public static Lesson createTutorial(int day, double startHr, double dur, Venue venue, CourseOffering co) throws InsertFailedException {
		try {
			Lesson tutorial = new Tutorial(day, startHr, dur, venue, co);
			String sql = "insert into lessons (staffId, venueId, coId, startHour, endHour, day, type, created_at, updated_at, deleted_at) values(?,?,?,?,?,?,?,?,?,?)";
			tutorial.saveInstance(sql);
			return tutorial;
		} catch (InsertFailedException ie) {
			throw ie;
		}
	} 
}
