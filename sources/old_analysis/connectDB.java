import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class connectDB {
	public Connection dbConnect(){
	    Connection conn = null;
	    String url = "jdbc:mysql://cheetah.cc.gt.atl.ga.us:3306/";
	    String dbName = "thepin";
	    String driver = "com.mysql.jdbc.Driver";
	    String userName = "thepin"; 
	    String password = "hrWVjJxLLtnMG8SS";
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
	public ResultSet runQuery(Connection conn,int testid) throws SQLException{
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM domdata where testid="+testid);
		return rs;
	}
	public void insertND(Connection conn,int testid,int nodeId) throws SQLException{
		Statement st = conn.createStatement();
		 st.executeUpdate("UPDATE domdata SET non_det = 1 WHERE testid="+testid+" and id="+nodeId);
		
	}
	public void hashToTable(Connection conn, HashMap<Node, Node> hash,int testid1,int testid2) throws SQLException {
		int[]keys=new int[hash.size()];
		int[]values=new int[hash.size()];
		int i=0;
		for(Node n:hash.keySet()){
			keys[i]=n.getId();
			i++;
		}
		i=0;
		for(Node n:hash.values()){
			values[i]=n.getId();
			i++;
		}
		String mapping="";
		for(i=0;i<keys.length;i++){
			mapping=mapping+"("+keys[i]+","+values[i]+");";
		}
		
		Statement st = conn.createStatement();
		st.executeUpdate("INSERT INTO map (testid1,testid2,mapping) VALUES("+testid1+","+testid2+",'"+mapping+"')");
		
	}
	public void errorlistToTable(Connection conn, ArrayList<Node> errorlist,int testid2) throws SQLException {
		//position(X0,Y0,X1,Y1);(X0,Y0,X1,Y1);
		//message id;id;
		String bugpos="";
		String message="";
		for(Node n:errorlist){
			if(n==null){
			System.out.println(n);
			continue;
			}
			
			bugpos=bugpos+"("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
			message=message+n.getId()+";";
		}
		Statement st = conn.createStatement();
		st.executeUpdate("INSERT INTO bugs (testid,position,message) VALUES("+testid2+",'"+bugpos+"','"+message+"')");
	}
	public void errorlistImgToTable(Connection conn, ArrayList<Node> errorlist,ArrayList<Double> percents,int testid2) throws SQLException {
		//position(X0,Y0,X1,Y1);(X0,Y0,X1,Y1);
		//message id;id;
		String bugpos="";
		String message="";
		int i=0;
		for(Node n:errorlist){
			bugpos=bugpos+"("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
			message=message+n.getId()+":"+percents.get(i)+";";
			i++;
		}
		Statement st = conn.createStatement();
		st.executeUpdate("INSERT INTO bugs (testid,position,message,type) VALUES("+testid2+",'"+bugpos+"','"+message+"',1)");
	}
}
