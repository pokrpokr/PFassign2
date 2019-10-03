package app;

public class Course {
	private String courseId;
	private String name;
	private String objective;
	
	public Course(String[] args) {
		this.courseId = args[0];
		this.name = args[1];
		this.objective = args[2];
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
}

