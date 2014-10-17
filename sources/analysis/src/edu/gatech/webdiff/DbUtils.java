package edu.gatech.webdiff;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DbUtils{

    public static Connection conn = null;
    public DbUtils(){
    	 if(DbUtils.conn == null){
    		 this.connectToDB();
    	 }
    }
	public Connection connectToDB(){
	    String url = WDConstants.DB_URL;
	    String dbName = WDConstants.DB_NAME;
	    String driver = WDConstants.DB_DRIVER;
	    String userName = WDConstants.DB_USER; 
	    String password = WDConstants.DB_PASSWORD;
	    try {
	      Class.forName(driver).newInstance();
	      DbUtils.conn = DriverManager.getConnection(url+dbName,userName,password);
	      System.out.println(" - Connected to the database");
	      return DbUtils.conn;
	    } catch (Exception e) {
	    	System.out.println("Con prob");
	      e.printStackTrace();
	    }
		return null;
	}
	
	public void insQuery(String query){
		try{
			java.sql.Statement st = DbUtils.conn.createStatement();
			st.executeUpdate(query);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	public ResultSet getQuery(String query){
		ResultSet rs;
		try{
			java.sql.Statement st = DbUtils.conn.createStatement();
			rs = st.executeQuery(query);
		}
		catch(Exception e){
			System.out.println(e);
			rs=null;
		}
		return rs;
	}
}