package app;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;

import cu_exceptions.InsertFailedException;
import db.DB;

public class Staff {
	private long id;
	private String eNo;
	private String name;
	private String position;
	private String office;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp deleted_at = null;

	public Staff() {};
	
	public Staff(String[] args) {
		this.eNo = args[0];
		this.name = args[1];
		this.position = args[2];
		this.office = args[3];
		java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		this.created_at = nowTime;
		this.updated_at = nowTime;
	}
	
	public static Staff createStaff(String[] args) throws InsertFailedException {
		for (int i = 0; i < args.length; i++) {
			if (args[i].isEmpty()) {
				throw new InsertFailedException("Infos can not be empty");
			}
		}
		Staff staff = new Staff(args);
		String sql = "insert into staffs (eNo, name, position, office, created_at, updated_at, deleted_at) values(?,?,?,?,?,?,?)";
		staff.saveInstance(sql);
		return staff;
	}
	
	public boolean assign(Lesson lesson) throws InsertFailedException {
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
	
	public Boolean setColumn(String columnName, Object value) throws InsertFailedException {
		DB db = new DB();
		String sql = "update staffs set " + columnName + " = ?";
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
