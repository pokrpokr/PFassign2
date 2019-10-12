package app;
import db.DB;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import cu_exceptions.InsertFailedException;

public class Customers {
	private long id;// Integer
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
			break;
		case "student":
			this.type = Type.Student.toString();
			break;
		default:
			this.type = Type.Student.toString();
			break;
		}
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public static Customers createCustomer(String userNum, String password, String type) throws InsertFailedException {
		Customers customer = new Customers(userNum, password, type);
		String sql = "insert into Customers (userNum, password, type, created_at, updated_at, deleted_at) values(?,?,?,?,?,?)";
		customer.saveInstance(sql);
		return customer;
	}
	
	public Object login(String userNum, String password) {
		DB db = new DB();
		String sql = "select userNum, id, password,type from customers where userNum =" + userNum + " and deleted_at is null";
    	HashMap<String, Object> result = db.search(db.getConn(), "Customers", sql);
    	db.db_close();
    	if ((Boolean) result.get("status") == true) {
        	ArrayList<Object> users = (ArrayList<Object>) result.get("data");
        	Customers user = (Customers) users.get(0);
        	if (user.getColumn("password").equals(password)) {
				return user;
			} else {
				return "Wrong password!";
			}
		} else {
			return "Wrong userNm!";
		}
	}
	
	public Object getColumn(String columnName) {
		try {
			Field field = this.getClass().getDeclaredField(columnName);
			return field.get(this);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Boolean setColumn(String columnName, Object value) throws InsertFailedException {
			DB db = new DB();
			String sql = "update customers set " + columnName + " = ?";
			HashMap<String, Object> result = db.update(db.getConn(), sql, columnName, value, this.id);
			if ((boolean) result.get("status")) {
				return true;
			}else {
				throw new InsertFailedException((String) result.get("message"));
			}
	}
	
	private boolean setId(Long id) {
		try {
			this.id = id;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	// Saving instance into database TODO refactor to a common method
	private void saveInstance(String sql) throws InsertFailedException {
		DB db = new DB();
    	HashMap<String, Object> insertRs = 
    			db.insert(db.getConn(), sql, this);
    	db.db_close();
    	if ((Boolean) insertRs.get("status")) {
			this.setId((Long) insertRs.get("id"));
		}else {
			throw new InsertFailedException((String) insertRs.get("message"));
		}
	}
}
