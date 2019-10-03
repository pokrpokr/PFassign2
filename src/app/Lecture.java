package app;

public class Lecture extends Lesson {
	public Lecture(int day, double startHr, double dur, Venue venue, CourseOffering co) {
		super(day, startHr, dur, venue, co);
		this.type = Type.Lecture;
	}
}
