package edu.gatech.webdiff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClusterIssues {
	private DbUtils db;
	private ArrayList<Node> tList=new ArrayList<Node>();
	private ArrayList<Node> pList=new ArrayList<Node>();
	private ArrayList<Node> listAll=new ArrayList<Node>();
	private ArrayList<ArrayList<Node>> clusters=new ArrayList<ArrayList<Node>>();
	public ClusterIssues(int testid) throws SQLException{
		db=new DbUtils();
		populateListAll(testid);
		runCluster(testid);
	}
	private void runCluster(int testid) {
		while(listAll.size()>0){
			if(tList.size()==0){
				tList.add(listAll.remove(0));
				if(pList.size()!=0){
					clusters.add(pList);
					pList=new ArrayList<Node>();
				}
			}
			while(tList.size()>0){
				Node item=tList.remove(0);
				ArrayList<Node> tempRemoveList=new ArrayList<Node>();
				for(Node j:listAll){
					//Compare function
					boolean xPathClusterable = clusterable(j.getXpath(), item.getXpath());
					boolean contained=containment(j, item);
					if(contained || xPathClusterable){
						tList.add(j);
						tempRemoveList.add(j);
					}
				}
				for(Node n:tempRemoveList){
					listAll.remove(n);
				}
				pList.add(item);
			}
			if(listAll.size() == 0){
				clusters.add(pList);
				pList=new ArrayList<Node>();
			}
		}
		for(ArrayList<Node> cluster:clusters){
			String nodeid="";
			Node tn = cluster.get(0);
			int x0=tn.getX0(),x1=tn.getX1(),y0=tn.getY0(),y1=tn.getY1();
			String xPathPrefix = tn.getXpath();
			for(Node n:cluster){
				nodeid=nodeid+n.getId()+",";
				if(x0>n.getX0()) x0 = n.getX0();
				if(x1<n.getX1()) x1 = n.getX1();
				if(y0>n.getY0()) y0 = n.getY0();
				if(y1<n.getY1()) y1 = n.getY1();
				xPathPrefix = getCommonPrefix(xPathPrefix, n.getXpath());
			}
			nodeid=nodeid.substring(0,nodeid.length()-1);
			//System.out.println(nodeid + "("+x0+","+y0+","+x1+","+y1+")" + xPathPrefix);
			String coords = "("+x0+","+y0+","+x1+","+y1+")";
			this.db.insQuery("INSERT INTO report (testid,nodeid,coords,xpath) VALUES("+testid+",'"+nodeid+"','"+coords+"','"+xPathPrefix+"')");
		}
	}
	private String getCommonPrefix(String a, String b) {
		int m = Math.min(a.length(), b.length());
		StringBuffer prefix =new StringBuffer();
		for(int i=0;i<m;i++){
			if(a.charAt(i) == b.charAt(i)){
				prefix.append(a.charAt(i));
			}else{
				break;
			}
		}
		int pl = prefix.length();
		if(prefix.charAt(pl-1) == '['){
			prefix.deleteCharAt(pl-1);
		}
//		if(b.length()!=prefix.length() || a.length()!=b.length()){
//			prefix.append("*");
//		}
		return prefix.toString();
	}
	private boolean containment(Node A,Node B){
		if ((A.getX0() >= B.getX0() && A.getX1() <= B.getX1() && A.getY0() >= B.getY0() && A.getY1()<= B.getY1()) || (B.getX0()>= A.getX0()&& B.getX1()<= A.getX1()&& B.getY0()>= A.getY0()&& B.getY1()<= A.getY1())) {
			return true;
		}
		return false;
	}
	private boolean clusterable(String xp1, String xp2) {
		int l1 = xp1.length(), l2 = xp2.length();
	    int prlen = (int)((l1+l2)*0.4); //80% same prefix
	        if(prlen < l1 && prlen < l2 && xp1.substring(0,prlen).equals(xp2.substring(0,prlen))){
	            return true;
	        }
	        return false;
	    }
	private void populateListAll(int testid) throws SQLException {
		 ResultSet rs=this.db.getQuery("SELECT * FROM issues where testid="+testid);
		 while(rs.next()){
			 
			 String posStr=rs.getString(3);
			 posStr=posStr.substring(1);
			 int x0=Integer.parseInt(posStr.substring(0,posStr.indexOf(',')));
			 posStr=posStr.substring(posStr.indexOf(',')+1);
			 
			 int y0=Integer.parseInt(posStr.substring(0,posStr.indexOf(',')));
			 posStr=posStr.substring(posStr.indexOf(',')+1);
			 
			 int x1=Integer.parseInt(posStr.substring(0,posStr.indexOf(',')));
			 posStr=posStr.substring(posStr.indexOf(',')+1);
			 
			 int y1=Integer.parseInt(posStr.substring(0,posStr.indexOf(')')));
			 posStr=posStr.substring(posStr.indexOf(',')+1);
			 //listAll.add(new Node(rs.getInt(1), "", rs.getString(5), "", "", "", 0, x0, y0, x1, y1, 0, 0, 0, 0));
			 listAll.add(new Node(rs.getInt(1), null, rs.getString(5), null, null, rs.getInt(2), 0, x0, y0, x1, y1, 0, false, false, null));
		 }
		 
		 //Sorts nodes in descending order
		 Collections.sort(listAll, new MyNodeComparable());
	}
	public static void main(String[] args) throws SQLException{
		new ClusterIssues(1);
		new ClusterIssues(3);
	}
	public class MyNodeComparable implements Comparator<Node>{

		@Override
		public int compare(Node o1, Node o2) {
			if(o1.getId()>o2.getId()){
				return -1;
			}
			else if(o1.getId()==o2.getId()){
				return 0;
			}
			else{
				return 1;
			}
		}
	}

}
