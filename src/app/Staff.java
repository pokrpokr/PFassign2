package app;

public class Staff {
	private String eNo;
	private String name;
	private String position;
	private String office;
	
	public Staff(String[] args) {
		this.eNo = args[0];
		this.name = args[1];
		this.position = args[2];
		this.office = args[3];
	}
	
	public boolean assign(Lesson lesson) {
		return lesson.setStaff(this);
	}
}
