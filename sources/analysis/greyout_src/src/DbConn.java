import java.sql.Connection;
import java.sql.DriverManager;

public class DbConn{
	public Connection connectToDB(){
	    Connection conn = null;
	    String url = "jdbc:mysql://localhost:3306/";
	    String dbName = "webdiff";
	    String driver = "com.mysql.jdbc.Driver";
	    String userName = "webdiff"; 
	    String password = "db_passw0rd";
	    try {
	      Class.forName(driver).newInstance();
	      conn = DriverManager.getConnection(url+dbName,userName,password);
	      System.out.println("Connected to the database");
	      return conn;
	    } catch (Exception e) {
	    	System.out.println("Con prob");
	      e.printStackTrace();
	    }
		return null;
	}
	
}