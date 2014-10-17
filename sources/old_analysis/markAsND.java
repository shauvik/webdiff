import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class markAsND {

	public static void main(String[] args) throws SQLException{
		comparePages pageCompareND=new comparePages();
		  connectDB c=new connectDB();
		  Connection conn=c.dbConnect();
		  int testid1=1;
//		  int testid1=Integer.parseInt(args[0]);
//		  int testid2=Integer.parseInt(args[1]);
		  int testid2=4;
		  Node[]list1=pageCompareND.runQuery(c,conn,testid1);
		  Node[]list1Pre=new Node[list1.length];
		
//Copy the arrays compare with hash and put rest in db
		  System.arraycopy(list1,0,list1Pre,0,list1.length);
		  Node[]list2=pageCompareND.runQuery(c,conn,testid2);
		  Node[]list2Pre=new Node[list2.length];
		  System.arraycopy(list2,0,list2Pre,0,list2.length);
		  Node rootQ1=list1[1];
		  Node rootQ2=list2[1];
		 //Call on root
		  ArrayList<Node> origLevel1=new ArrayList<Node>();
		  origLevel1.add(rootQ1);
		  ArrayList<Node> origLevel2=new ArrayList<Node>();
		  origLevel2.add(rootQ2);
		  pageCompareND.computeMatchIndex(origLevel1, origLevel2,100);
		  pageCompareND.compareAllLevels(origLevel1, origLevel2,100);
//		  System.out.println(list2[3].isVisible());
		  pageCompareND.cleanList(pageCompareND, list1, list2);
		 ArrayList<Node> list1New=new ArrayList<Node>();
		 pageCompareND.createNewList(list1, list1New);
		  ArrayList<Node> list2New=new ArrayList<Node>();
		  pageCompareND.createNewList(list2, list2New);
		  pageCompareND.computeMatchIndex(list1New, list2New,100);
		  pageCompareND.cleanList(pageCompareND, list1, list2);
			 System.out.println("List 1");
			 ArrayList<Node> list1clone=pageCompareND.printList(list1);
			 System.out.println("-----------------------------------");
			 System.out.println("List 2");
			 ArrayList<Node> list2clone=pageCompareND.printList(list2);
			 System.out.println(pageCompareND.getHash().size());
			 System.out.println("-----------------------------------");
			 System.out.println(pageCompareND.errorlist.size());
			 System.out.println(pageCompareND.hash.size());
			for(Node n:list1Pre){
				 if(n==null){
					 
					 continue;
				 }
				 Set<Node> t=pageCompareND.hash.keySet();
				 if((t.contains(n)!=true)&& (n.isVisible()) && (!n.getName().equals("SCRIPT"))){
					 addToDBNonDet(c, conn, n);
					 String runGrey="./grey_out img/"+testid1+".png";
					 pageCompareND.sysCall(runGrey+" "+n.getX0()+" "+n.getY0()+" "+n.getX1()+" "+n.getY1());
				 }
			 }
			 for(Node n:list2Pre){
				 if(n==null){
					 
					 continue;
				 }
				 Collection<Node> t=pageCompareND.hash.values();
				 //&& (!n.getName().equals("SCRIPT")) && n.isVisible()
				 if((t.contains(n)!=true)&& (n.isVisible()) && (!n.getName().equals("SCRIPT"))){
					 addToDBNonDet(c, conn, n);
					 String runGrey="./grey_out img/"+testid2+".png";
					 pageCompareND.sysCall(runGrey+" "+n.getX0()+" "+n.getY0()+" "+n.getX1()+" "+n.getY1());
				 }
			 }
			 pageCompareND.callEMD(false,pageCompareND.hash,1, testid1, testid2,c,conn);
			 //Go thru testidlists add to db
			 //
			 for(Node n:pageCompareND.testid1list){
//				 System.out.println(n.getTestid()+",");
				 addToDBNonDet(c, conn, n);
			 }
			 for(Node n:pageCompareND.testid2list){
				 addToDBNonDet(c, conn, n);
			 }
//			 System.out.println(pageCompare.hash);
			 
			 /*for(Node n:pageCompare.hash.values()){
//				 System.out.println(n.getId());
				 if(n.getId()==156)
				 {
					 //n = ie child
					 Node chromekid=getKeysFromValue(pageCompare.hash, n);
					 //chrome kid=chrome child
//					 System.out.println(+"=");
					 System.out.println("Distance between chrome 156 and parent"+pageCompare.distanceFormula(chromekid.getX0(), chromekid.getY0(), chromekid.getParent().getX0(), chromekid.getParent().getY0()));
					 System.out.println("Distance between IE 156 and parent"+pageCompare.distanceFormula(n.getX0(), n.getY0(), n.getParent().getX0(), n.getParent().getY0()));
					 System.out.println((pageCompare.distanceFormula(chromekid.getX0(), chromekid.getY0(), chromekid.getParent().getX0(), chromekid.getParent().getY0()))- pageCompare.distanceFormula(n.getX0(), n.getY0(), n.getParent().getX0(), n.getParent().getY0()));
					 System.out.println(getKeysFromValue(pageCompare.hash, n.getParent())+" "+n.getParent().getId());
//				 System.out.println(n);
				 }
			 }*/
	}
	public static void addToDBNonDet(connectDB c, Connection conn,Node n) throws SQLException {
			
			c.insertND(conn, n.getTestid(), n.getId());
			
				
	}
}
