package db;
import java.sql.*;
import java.util.*;
import app.*;
import java.lang.reflect.Field;

public class DB {
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Enrol_sys?serverTimezone=UTC&zeroDateTimeBehavior=ConvertToNull";
    
    private static final String USER = "root";
    private static final String PASS = "root";
    
    private Connection conn = null;
	private Statement  stmt = null;
	private PreparedStatement ptmt = null;
	
    public DB() {
    	try {
    		Class.forName(JDBC_DRIVER);
    		// Connecting database...            
            this.conn = DriverManager.getConnection(DB_URL,USER,PASS);
		}
    	catch (SQLException se) {
    		se.printStackTrace();
    	}
    	catch (Exception e) {
			System.err.println("DB Error " + e);
		}
    }
    
    // ClassName: ['Course', 'CourseOffering', 'Customers', 'Lesson', 'Staff', 'Venue']    
    public HashMap<String, Object> search(Connection conn, String className, String sql) {
    	try {
    		// Create statement            
            this.stmt = this.conn.createStatement();
            ResultSet rs = this.stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
    		ArrayList<Object> data = new ArrayList<>();
    		// result = {"status": [true, false], "data": [Object], "message": ""}    		
			HashMap<String, Object> result = new HashMap<>();
    		
            switch (className) {
    		case "Course":
    			className = "app.Course";
    			break;
    		case "CourseOffering":
    			className = "app.CourseOffering";
    			break;
    		case "Customers":
    			className = "app.Customers";
    			break;
    		case "Lesson":
    			className = "app.Lesson";
    			break;
    		case "Staff":
    			className = "app.Staff";
    			break;
    		case "Venue":
    			className = "app.Venue";
    			break;
    		case "Enrolment":
    			className = "app.Enrolment";
    			break;
    		default:
    			result.put("status", false);
    			result.put("data", new ArrayList<>());
    			result.put("message", "Class name not found!");
    			return result;
    		}
           
            while (rs.next()) {
                int cols = meta.getColumnCount();
                Class<?> classN = Class.forName(className);
    			Object obj = classN.getDeclaredConstructor().newInstance();
                for (int i = 1; i <=cols; i++) {
    				Field field = null;
    				field = classN.getDeclaredField(meta.getColumnName(i));
    				field.setAccessible(true);
    				field.set(obj, rs.getObject(i));
    			}
                
				data.add(obj);
    		}
            if (!data.isEmpty()) {
            	result.put("status", true);
                result.put("data", data);
                result.put("message", "Query success!");
			}else {
				result.put("status", true);
                result.put("data", data);
                result.put("message", "No results!");
			}
            return result;
		} catch (Exception e) {
			HashMap<String, Object> result = new HashMap<>();
			result.put("status", false);
			result.put("data", new ArrayList<>());
			result.put("message", e);
			return result;
		} finally {
			try {
				if (this.stmt != null) this.stmt.close();
			} 
			catch (Exception e2) {
				System.out.println("Closing statement failed: " + e2);
			}
		}

	}
    
    public HashMap<String, Object> insert(Connection conn, String sql, Object object) {
    	// result = {"status": [true, false], "id": 1, "message": ""}    		    	
    	HashMap<String, Object> result = new HashMap<>();
    	result.put("status", false);
    	result.put("id", null);
    	result.put("message", "Inserting failed");
 
    	try {
    		// Create preparedStatement
            this.ptmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            Class<?> classN = object.getClass();
            if (object instanceof app.Lecture || object instanceof app.Tutorial) {
        		classN = Lesson.class;
    		}
            Field[] fields = classN.getDeclaredFields();
            for (int i = 1; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				Object value = field.get(object);
				this.ptmt.setObject((i), value);
			}
            
            int count = this.ptmt.executeUpdate();

            if (count > 0) {
            	ResultSet rs = this.ptmt.getGeneratedKeys();
            	while (rs.next()) {
    				result.put("status", true);
    				result.put("id", rs.getLong(1));
    				result.put("message", "Inserting sucessful");
    				return result;
				}
			}
            
		}
    	catch (SQLException se) {
    		//se.printStackTrace();
			result.put("status", false);
			result.put("message", "SQL excetion occuring: " + se);
		}
    	catch (Exception e) {
    		result.put("status", false);
			result.put("message", "Excetion occuring: " + e);
		}
    	finally {
    		try {
				if (this.ptmt != null) this.ptmt.close();
			} 
			catch (Exception e2) {
				System.out.println("Closing statement failed: " + e2);
			}
		}
    	return result;
	}
    
    public HashMap<String, Object> update(Connection conn, String sql, String fieldName, Object value, Long id) {
    	//    	
    	HashMap<String, Object> result = new HashMap<>();
    	result.put("status", false);
    	result.put("message", "Updating failed");
    	String upSql = sql + ", updated_at = ? where id = " + id + " and deleted_at is null";
    	try {
    		// Create preparedStatement
            this.ptmt = conn.prepareStatement(upSql);  
			this.ptmt.setObject(1, value);
			java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
			this.ptmt.setObject(2, nowTime);
            int count = this.ptmt.executeUpdate();
    
            if (count > 0) {
            	result.put("status", true);
            	result.put("message", "Updating successful");			
            }
            
		}
    	catch (SQLException se) {
    		//se.printStackTrace();
			result.put("status", false);
			result.put("message", "SQL excetion occuring: " + se);
		}
    	catch (Exception e) {
    		result.put("status", false);
			result.put("message", "Excetion occuring: " + e);
		}
    	finally {
    		try {
				if (this.ptmt != null) this.ptmt.close();
			} 
			catch (Exception e2) {
				System.out.println("Closing statement failed: " + e2);
			}
		}
    	return result;
	}
    
    public HashMap<String, Object> delete(Connection conn, String tableName, Long id) {
    	HashMap<String, Object> result = new HashMap<>();
    	result.put("status", false);
    	result.put("message", "Updating failed");
    	String sql = "update " + tableName + " set deleted_at = ? where id = " + id;
    	try {
    		this.ptmt = conn.prepareStatement(sql);
			java.sql.Timestamp nowTime = new Timestamp(System.currentTimeMillis());
    		this.ptmt.setObject(1, nowTime);
            int count = this.ptmt.executeUpdate();

            if (count > 0) {
            	result.put("status", true);
            	result.put("message", "Updating successful");			
            }
		}
    	catch (SQLException se) {
    		//se.printStackTrace();
			result.put("status", false);
			result.put("message", "SQL excetion occuring: " + se);
		}
    	catch (Exception e) {
    		result.put("status", false);
			result.put("message", "Excetion occuring: " + e);
		}
    	finally {
    		try {
				if (this.ptmt != null) this.ptmt.close();
			} 
			catch (Exception e2) {
				System.out.println("Closing statement failed: " + e2);
			}
		}
    	return result;
    }
    
    public Connection getConn() {
		return this.conn;
	}
    
    public void db_close() {
    	try {
            this.conn.close();
		} 
    	catch (SQLException se) {
			se.printStackTrace();
		}
    	catch (Exception e) {
    		System.err.println("Close DB " + e);
    	}
    	finally {
    		// Close source          
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
		}
    }
    
//    TODO  Function test code, need to delete before commit to assignment
    
//    public static void main(String[] args) throws InsertFailedException, InterruptedException {
//    	DB db = new DB();
//    	Customers customer = Customers.createCustomer("test12", "123456789", "");
////    	HashMap<String, Object> insertRs = db.insert(db.conn, "insert into Customers (userNum, password, type, created_at, updated_at, deleted_at) values(?,?,?,?,?,?)", cus1);
////    	System.out.println(insertRs.get("status"));
////    	System.out.println(insertRs.get("id"));
////    	System.out.println(insertRs.get("message"));
//    	HashMap<String, Object> result = db.search(db.conn, "Customers", "select userNum, id, password,type from customers where deleted_at is null");
//    	System.out.println(result.get("message"));
//    	ArrayList<Object> cuses = (ArrayList<Object>) result.get("data");
//    	for (int i = 0; i < cuses.size(); i++) {
//			System.out.println(((Customers) cuses.get(i)).getColumn("userNum"));
//		}
//    	TimeUnit.SECONDS.sleep(30);
//    	customer.setColumn("password", "555555555");
//    	db.db_close();
//
//    }
}
 
