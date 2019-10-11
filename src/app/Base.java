// TODO waitting for refactoring
//package app;
//
//import java.lang.reflect.Field;
//import java.util.HashMap;
//
//import db.DB;
//
//public abstract class Base {
//	private long id;// Integer
//
//	public Base() {}
//	
//	public Object getColumn(String columnName) {
//		try {
//			Field field = this.getClass().getDeclaredField(columnName);
//			return field.get(this);
//		} catch (Exception e) {
//			return null;
//		}
//	}
//	
//	// Saving instance into database TODO refactor to a common method
//	protected Boolean saveInstance(String sql) {
//		DB db = new DB();
//    	HashMap<String, Object> insertRs = db.insert(db.getConn(), sql, this);
//	    db.db_close();
//	    if ((Boolean) insertRs.get("status") == true) {
//		this.setId((Long) insertRs.get("id"));
//			return true;
//		}else {
//			return false;
//		}
//	}
//	
//}
