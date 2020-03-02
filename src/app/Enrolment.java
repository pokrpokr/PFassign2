package app;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import cu_exceptions.ClashException;
import cu_exceptions.InsertFailedException;
import cu_exceptions.NoResultException;
import db.DB;

public class Enrolment {
	private long id;
	private long studentId;
	private long coId;
	private long lectureId;
	private long tutorialId;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp deleted_at = null;
	
	public Enrolment() {}
	
	public Enrolment(Customers student, CourseOffering co, Lesson lecture) {
		this.studentId = (long) student.getColumn("id");
		this.coId = (long) co.getColumn("id");
		this.lectureId = (long) lecture.getColumn("id");
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public static Enrolment creatEnrolment(Customers student, CourseOffering co, Lesson lecture) throws InsertFailedException {
		Enrolment enrol = new Enrolment(student, co, lecture);
		String sql = "insert into enrolments (studentId, coId, lectureId, tutorialId, created_at, updated_at, deleted_at) values(?,?,?,?,?,?,?)";
		enrol.saveInstance(sql);
		return enrol;
	}
	
	//Computing student number
	public static int enrolNum(Lesson l) {
		String cName = "";
		if (l.getColumn("type").equals("Tutorial")) {
			cName = "tutorialId";
		} else if (l.getColumn("type").equals("Lecture")) {
			cName = "lectureId";
		} else {
			return -1;
		}
		DB db = new DB();
		String sql = "select id from enrolments where "+ cName + " = " + l.getColumn("id") + " and deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "Enrolment", sql);
		if ((boolean) result.get("status")) {
			return ((ArrayList<Object>) result.get("data")).size(); 
		}else {
			return -1;
		}
	}
	
	public static void checkClash(Customers s, CourseOffering co) throws SQLException, ClashException {
		DB db = new DB();
		String sql = "select id from enrolments where studentId = " + s.getColumn("id") + " and coId = " + co.getColumn("id")+ " and tutorialId = 0 and deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "Enrolment", sql);
		if ((boolean) result.get("status")) {
			int count = ((ArrayList<Object>) result.get("data")).size();
			if (count > 0) throw new ClashException("Can not enrol one course twice!");
		}else {
			throw new SQLException("SQL wrong!");
		}
	}
	
	public boolean enrolTutorial(Lesson tutorial) throws InsertFailedException {
		return this.setColumn("tutorialId", tutorial.getColumn("id"));
	}
	
	public CourseOffering getCourseOffering() throws SQLException, NoResultException {
		DB db = new DB();
		String sql = "select * from cos where id = " + this.coId + " and deleted_at is null";
		HashMap<String, Object> result = db.search(db.getConn(), "CourseOffering", sql);
		if ((boolean) result.get("status")) {
			if (((ArrayList<Object>) result.get("data")).size() == 0) {
				throw new NoResultException((String) result.get("message"));
			}
			CourseOffering co = (CourseOffering) ((ArrayList<Object>) result.get("data")).get(0); 
			return co;
		} else {
			throw new SQLException((String) result.get("message"));
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
			String sql = "update enrolments set " + columnName + " = ?";
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
