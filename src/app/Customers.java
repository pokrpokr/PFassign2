package app;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Customers {
	private long id;
	private String userNum;
	private String password;
	private String type;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp delete_at = null;
	
	private enum Type{
		Admin,
		Staff,
		Student
	}
	
	public Customers() {}
	
	public Customers(String userNum, String password, String type) {
		this.userNum = userNum;
		this.password = password;
		this.type = type;
		
		switch (type) {
		case "admin":
			this.type = Type.Admin.toString(); 
			break;
		case "staff":
			this.type = Type.Staff.toString();
		default:
			this.type = Type.Student.toString();
			break;
		}
		
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public Object getColumn(String columnName) {
		try {
			Field field = this.getClass().getDeclaredField(columnName);
			return field.get(this);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean setId(Long id) {
		try {
			this.id = id;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
