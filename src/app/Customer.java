package app;

public class Customer {
	private String userNum;
	private String password;
	private Type   type;
	private enum Type{
		Admin,
		Staff,
		Student
	}
	
	
}
