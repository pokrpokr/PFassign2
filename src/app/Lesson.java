package app;

import java.lang.reflect.Field;

public abstract class Lesson {
	private long id;
	private long staffId;
	private long venueId;
	private long coId;
	private double startHour;
	private double endHour;
	private int day;
//	private Staff staff;
//	private Venue venue;
//	private CourseOffering co;
	protected Type type;
	
	enum Type{
		Lecture, 
		Tutorial
	}
	
	public Lesson() {
		// TODO Auto-generated constructor stub
	}
	
	protected Lesson(int day, double startHr, double dur, Venue venue, CourseOffering co) {
		this.day = day;
		this .startHour = startHr;
		this.endHour = dur;
		this.venueId = (long) venue.getColumn("id");
		this.coId = (long) co.getColumn("id");
	}
	
	protected boolean setStaff(Staff staff) {
		try {
			this.staff = staff;
		} catch (Exception e) {
			System.err.println(e);
		}
		return true;
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
