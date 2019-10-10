package app;

public class Tutorial extends Lesson {
	public Tutorial(int day, double startHr, double dur, Venue venue, CourseOffering co) {
		super(day, startHr, dur, venue, co);
		this.type = Type.Tutorial.toString();
	}
}
