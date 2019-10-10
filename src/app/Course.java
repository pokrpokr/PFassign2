package app;
import java.lang.reflect.Field;
import java.sql.Timestamp;

public class Course {
	private long id;
	private String name;
	private String objective;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp deleted_at;
	
	
	public Course() {}
	
	public Course(String[] args) {
		this.name = args[0];
		this.objective = args[1];
	}
	
	public boolean createOffering(int maxNum) {
		try {
			CourseOffering courseOffering = new CourseOffering(maxNum, this);
		} catch (Exception e) {
			System.err.println("Create Offering failed: " + e);
		}
		return true;
	}
	
	public CourseOffering getOffering() {
		
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

