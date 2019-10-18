package app;
import db.DB;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import cu_exceptions.CuLoginException;
import cu_exceptions.InsertFailedException;
import cu_exceptions.NoResultException;

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
	
	public static Customers login(String userNum, String password) throws CuLoginException {
		DB db = new DB();
		String sql = "select userNum, id, password,type from customers where userNum = '" + userNum + "' and deleted_at is null";
    	HashMap<String, Object> result = db.search(db.getConn(), "Customers", sql);
    	db.db_close();
    	if ((Boolean) result.get("status") == true) {
        	ArrayList<Object> users = (ArrayList<Object>) result.get("data");
        	if (users.size() == 0) {
        		throw new CuLoginException("No userNum!");
			}
        	Customers user = (Customers) users.get(0);
        	if (user.getColumn("password").equals(password)) {
				return user;
			} else {
				throw new CuLoginException("Wrong password!");
			}
		} else {
			throw new CuLoginException("No userNum!");
		}
	}
	
	//Student operations
	public Enrolment enrolCourseOffering(CourseOffering co, Lesson lecture) throws InsertFailedException {
		try {
			Enrolment enrol = Enrolment.creatEnrolment(this, co, lecture);
			return enrol;
		} 
		catch (InsertFailedException ie) {
			throw ie;		
		}
	}
	//Enrol Tutorial
	public boolean enrolTutorial(Enrolment enrolment, Lesson tu) throws InsertFailedException {
		return enrolment.enrolTutorial(tu);
	}
	//Finding student's enrolments
	public ArrayList<Enrolment> enrolments() throws SQLException, NoResultException {
		ArrayList<Enrolment> enrolments = new ArrayList<>();
		DB db = new DB();
		String sql = "select * from enrolments where studentId = " + this.getColumn("id") + " and deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "Enrolment", sql);
		if ((boolean) result.get("status")) {
			int count = ((ArrayList<Object>) result.get("data")).size();
			if ( count == 0) {
				throw new NoResultException((String) result.get("message"));
			}
			for (int i = 0; i < count; i++) {
				enrolments.add( i, (Enrolment) ((ArrayList<Object>) result.get("data")).get(i) );
			}
			return enrolments;
		} else {
			throw new SQLException((String) result.get("message"));
		}
	}
	
	//Timetable
	public void getTimeTable() {
		ArrayList<Enrolment> enrolments = new ArrayList<>();
		ArrayList<CourseOffering> cos = new ArrayList<>();
		ArrayList<Lesson> lessons = new ArrayList<>();
		try {
			enrolments = this.enrolments();
			for (int i = 0; i < enrolments.size(); i++) {
				cos.add(enrolments.get(i).getCourseOffering());
			}
			for (int i = 0; i < cos.size(); i++) {
				ArrayList<Lesson> ls = cos.get(i).lessons();
				for (int j = 0; j < ls.size(); j++) {
					lessons.add(ls.get(j));
				}
			}
			for (int i = 0; i < lessons.size(); i++) {
				String timeInfoString = "Day: " + lessons.get(i).getColumn("day") + ", Start: " + lessons.get(i).getColumn("startHour") + 
						", End: " + lessons.get(i).getColumn("endHour") + ", Type: " + lessons.get(i).getColumn("type");
				Venue venue = null;
				try {
					venue = lessons.get(i).getVenue();
				}
				catch (NoResultException nre) {
					System.err.println(nre);
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				catch (SQLException e) {
					System.err.println(e);
					try {
						Thread.sleep(3);
					} catch (InterruptedException ie) {
						// TODO Auto-generated catch block
						ie.printStackTrace();
					}
					return;
				}
				
				timeInfoString += ", location: " + venue.getColumn("location");
				System.out.println("**************************");
				System.out.println(timeInfoString);
			}
		}
		catch (NoResultException nre) {
			System.err.println("No Time Table");
		}
		catch (Exception e) {
			System.err.println("Student timetable error:" + e);
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
