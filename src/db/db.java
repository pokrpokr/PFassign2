package db;
import app.*;
import cu_exceptions.InsertFailedException;

import java.sql.*;
import java.util.*;

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
            result.put("status", true);
            result.put("data", data);
            result.put("message", "Query success!");
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
            Field[] fields = classN.getDeclaredFields();
            for (int i = 1; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				Object value = field.get(object);
				this.ptmt.setObject((i), value);
			}
            
            int count = this.ptmt.executeUpdate();
            System.out.println(count);
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
    
    public boolean update(Connection conn, String sql, ArrayList<Object> parameters) {
    	// Create preparedStatement
    	//this.ptmt = conn.prepareStatement(sql);
    	return false;
	}
    
    public boolean delete(Connection conn, String sql) {
    	return false;
    }
    
    public Connection getConn() {
		return this.conn;
	}
    
    public static void main(String[] args) throws InsertFailedException {
    	DB db = new DB();
    	Customers.createCustomer("test9", "123456789", "");
//    	HashMap<String, Object> insertRs = db.insert(db.conn, "insert into Customers (userNum, password, type, created_at, updated_at, deleted_at) values(?,?,?,?,?,?)", cus1);
//    	System.out.println(insertRs.get("status"));
//    	System.out.println(insertRs.get("id"));
//    	System.out.println(insertRs.get("message"));
    	HashMap<String, Object> result = db.search(db.conn, "Customers", "select userNum, id, password,type from customers where deleted_at is null");
    	db.db_close();
    	System.out.println(result.get("message"));
    	ArrayList<Object> cuses = (ArrayList<Object>) result.get("data");
    	for (int i = 0; i < cuses.size(); i++) {
			System.out.println(((Customers) cuses.get(i)).getColumn("userNum"));
		}
    }
}
 
