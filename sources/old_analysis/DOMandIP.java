import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


public class DOMandIP{

	public void runQuery(connectDB c,Connection conn,int testid) throws SQLException{
		ResultSet rs=c.runQueryBugs(conn, testid);
		rs.last();
		int row=rs.getRow();
		rs.first();
		String strPos="";
		String strSize="";
		String strVis="";
		String strImg="";
		String strEmd="";
		 do{
//			 System.out.println(rs.getInt(1));
			 	if(rs.getInt(4)==0){
			 		strPos=rs.getString(2);
				 }
				if(rs.getInt(4)==1){
					strSize=rs.getString(2);			 
				 }
				if(rs.getInt(4)==2){
					strVis=rs.getString(2);
				}
				if(rs.getInt(4)==3){
					strImg=rs.getString(2);
					strEmd=rs.getString(3);
				}
			}while(rs.next());
			 
			 String[] posArrS=strPos.split(";");
			 String[] sizeArrS=strSize.split(";");
			 String[] visArrS=strVis.split(";");
			 
			 String[] imgArrS=strImg.split(";");
			 Set posS = new HashSet(); 
			 Set sizeS = new HashSet(); 
			 Set visS = new HashSet();
			 Set genS = new HashSet();
			 for(String element:imgArrS){
				 for(String x:posArrS){
					 if(element.equals(x)){
						 posS.add(element);
					 }
				 }
				 for(String x:sizeArrS){
					 
					 if(element.equals(x)){
						 sizeS.add(element);
					 }
				 }
				 for(String x:visArrS){
					 if(element.equals(x)){
						 visS.add(element);
					 }
				 }
			 }
			 String[] allEmd=strEmd.split(";");
			 String[] finalEmd=new String[imgArrS.length];
			 int i=0;
			 int dontRun=0;
			 for(String x:allEmd){
				System.out.println("Xstr: "+x);
				try{
					finalEmd[i]=x.split(":")[1];
				}catch(Exception e){
					System.out.println("In excep");
					dontRun=1;
					}
				 i++;
			 }
			 int j=0;
			 for(String x:finalEmd){
				try{
				 if(Double.parseDouble(x)>1){
					 genS.add(imgArrS[j]);
				 }
				}catch(Exception e){
					System.out.println("E2");
				}
			 }
			 System.out.println(posS.size());
			 System.out.println(sizeS.size());
			 System.out.println(visS.size());
			 System.out.println(genS.size());
			 c.compareToTable(conn,  posS.toArray(), testid, 4);
			 c.compareToTable(conn,  sizeS.toArray(), testid, 5);
			 c.compareToTable(conn, visS.toArray(), testid, 6);
			 c.compareToTable(conn,  genS.toArray(), testid, 7);
	
	}

	
public static void main(String[] args) throws SQLException {

	  DOMandIP da=new DOMandIP();
	  connectDB c=new connectDB();
	  Connection conn=c.dbConnect();
	  int testid1=Integer.parseInt(args[0]);
	  
	da.runQuery(c,conn,testid1);
	  
  }
 

	  


}

