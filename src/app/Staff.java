package app;

import java.lang.reflect.Field;

public class Staff {
	private long id;
	private String eNo;
	private String name;
	private String position;
	private String office;

	public Staff() {};
	
	public Staff(String[] args) {
		this.eNo = args[0];
		this.name = args[1];
		this.position = args[2];
		this.office = args[3];
	}
	
	public boolean assign(Lesson lesson) {
		return lesson.setStaff(this);
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
