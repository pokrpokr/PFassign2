package app;

public abstract class Lesson {
	private double startHour;
	private double endHour;
	private int day;
	private Staff staff;
	private Venue venue;
	private CourseOffering co;
	protected Type type;
	
	enum Type{
		Lecture, 
		Tutorial
	}
	
	protected Lesson(int day, double startHr, double dur, Venue venue, CourseOffering co) {
		this.day = day;
		this .startHour = startHr;
		this.endHour = dur;
		this.venue = venue;
		this.co = co;
	}
	
	protected boolean setStaff(Staff staff) {
		try {
			this.staff = staff;
		} catch (Exception e) {
			System.err.println(e);
		}
		return true;
	};
}
