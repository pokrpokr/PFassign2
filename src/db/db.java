package db;
import java.sql.*;
import java.util.*;

public class db {
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Enrol_sys?serverTimezone=UTC ";
    
    private static final String USER = "root";
    private static final String PASS = "root";
    
    private Connection conn = null;
	private Statement  stmt = null;
	private PreparedStatement ptmt = null;
	
	// Create statement            
    this.stmt = this.conn.createStatement();
    // Create preparedStatement
    this.ptmt = this.conn.prepareStatement();
	
    public db() {
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
    
    public void search(Connection conn, String sql) {
		
	}
    
    public void insert(Connection conn, String sql, ArrayList<Object> data) {
		
	}
    
    public void update(Connection conn, String sql, ArrayList<Object> data) {
		
	}
    
    public void delete(Connection conn, String sql) {
    	
    }
    
    public static void main(String[] args) {
    	Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName(JDBC_DRIVER);
        
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, userNum, password FROM customers";
            ResultSet rs = stmt.executeQuery(sql);
        
            while(rs.next()){
                int id  = rs.getInt("id");
                String name = rs.getString("userNum");
                String url = rs.getString("password");
                
                System.out.print("ID: " + id);
                System.out.print(", 站点名称: " + name);
                System.out.print(", 站点 URL: " + url);
                System.out.print("\n");
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
	}
 
}
