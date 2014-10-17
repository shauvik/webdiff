import java.awt.List;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.transform.ErrorListener;

public class comparePages{
	HashMap<Node, Node> hash=new HashMap<Node, Node>();
	ArrayList<Node> errorlist=new ArrayList<Node>();
	//Used for Bhatta
	ArrayList<Node> testid1list=new ArrayList<Node>();
	ArrayList<Node> testid2list=new ArrayList<Node>();
	public void setHash(HashMap<Node, Node> hash) {
		this.hash = hash;
	}
	public HashMap<Node, Node> getHash() {
		return hash;
	}

	
	public Node[] runQuery(connectDB c,Connection conn,int testid) throws SQLException{
		ResultSet rs=c.runQuery(conn, testid);
		rs.last();
		int row=rs.getRow();
		rs.first();
		Node[] nodelist=new Node[row+1];
		String[] childlist=new String[row+1];
		 do{
			 boolean clickable=false;
			 boolean visible=false;
			 if(rs.getInt(11)==1)
				 clickable=true;
			 
			 if(rs.getInt(12)==1)
				 visible=true;
			 nodelist[rs.getInt(4)]=new Node(rs.getInt(2),rs.getString(1), rs.getString(15) , rs.getString(17), rs.getString(5), Integer.parseInt(rs.getString(4)), Integer.parseInt(rs.getString(6)), Integer.parseInt(rs.getString(7)), Integer.parseInt(rs.getString(8)), Integer.parseInt(rs.getString(9)), Integer.parseInt(rs.getString(10)), Integer.parseInt(rs.getString(16)), clickable, visible, nodelist[rs.getInt(13)]);
			 if((nodelist[rs.getInt(4)].getX0()==nodelist[rs.getInt(4)].getX1())||(nodelist[rs.getInt(4)].getY0()==nodelist[rs.getInt(4)].getY1())){
				 nodelist[rs.getInt(4)].setVisible(false);
			 }
			if(rs.getInt(18)==1){
				nodelist[rs.getInt(4)].setNonDet();
			}
			 childlist[rs.getInt(4)]=rs.getString(14);
			}while(rs.next());
			 
			 for(int i=1;i<childlist.length;i++){
				 String temp[]=childlist[i].split(" ");
		for(String t:temp){
			String tempNos[]=t.split(",");
			for(String tNo:tempNos){
				
				try{
					nodelist[i].addChild(nodelist[Integer.parseInt(tNo)]);
				}
				catch (Exception e) {
				}
			}
		}
			 }
			 //Return root
			 return nodelist;
		 
	}
	public  ArrayList<Node> nextLevel(ArrayList<Node> nodes){
		ArrayList<Node> nodeList=new ArrayList<Node>();
		for(Node n:nodes){
			nodeList.addAll(n.getChildren());
		}
		return nodeList;
		
	}
	/*
	 * TODO: Compute Match Index
	 */

	public void computeMatchIndex(ArrayList<Node> nodes1,ArrayList<Node> nodes2, int threshold){
		/*for(int i=0;i<nodes1.size();i++){
			for(int j=0;j<nodes2.size();j++){
				if( (!nodes2.get(j).getDomid().equals("")) && (!nodes1.get(i).getDomid().equals("")) && (nodes1.get(i).getDomid().equals(nodes2.get(j).getDomid()))){
					getHash().put(nodes1.get(i),nodes2.get(j));
					nodes1.get(i).setMapped();
					nodes2.get(j).setMapped();
				}
			}
		}*/
		/*for(int i=0;i<nodes1.size();i++){
			if(!nodes1.get(i).getDomid().equals("")){
				for(int j=0;j<nodes2.size();j++){
					if(nodes2.get(j).getDomid().equals(nodes1.get(i).getDomid())){
						getHash().put(nodes1.get(i), nodes2.get(j));
						nodes1.get(i).setMapped();
						nodes2.get(j).setMapped();
						break;
					}
				}
			}
		}*/
//		System.out.println(hash);
//		System.out.println("-------------------");
		
		for(int i=0;i<nodes1.size();i++){
			
			int bestNode=-1;
			double bestNodeVal=-1;
			for(int j=0;j<nodes2.size();j++){
//				System.out.println((nodes1.get(i).isVisible()));
				if((nodes2.get(j).isVisible()) && (nodes1.get(i).isVisible())&&(!nodes1.get(i).isMapped()) && (!nodes2.get(j).isMapped()) && ((!nodes2.get(j).isNonDet()) && (!nodes1.get(i).isNonDet()) ) && (!((nodes1.get(i).getName().equals("SCRIPT"))||(nodes2.get(j).getName().equals("SCRIPT"))))){
					
					Node n1 = nodes1.get(i);
					Node n2 = nodes2.get(j);
					
					double tempNodeVal = getMatchIndex(n1, n2); 
//					System.out.println(tempNodeVal);
					if(tempNodeVal>bestNodeVal){
						bestNodeVal=tempNodeVal;
						bestNode=j;
					}
				}
			}
			//Insert into keymap, mark as mapped. Up check if j hasnt been mapped:: Threshold = threshold
			//;
//			System.out.println(bestNodeVal);
			if((bestNodeVal>=threshold) && bestNodeVal!=-1 && nodes1.get(i).isMapped()==false && nodes2.get(bestNode).isMapped()==false){
				getHash().put(nodes1.get(i),nodes2.get(bestNode));
				nodes2.get(bestNode).setMapped();
				nodes1.get(i).setMapped();
				if(nodes2.get(bestNode).getId()!=nodes1.get(i).getId()){
					System.out.println("NNNNNN");
					System.out.println(nodes1.get(i).getId()+" "+nodes2.get(bestNode).getId()+" "+bestNodeVal);
				}
			}
		}
	
	}
	private double getMatchIndex(Node n1, Node n2) {
		boolean x=false;
		if(n1.getId()==117 && n2.getId()==116){
			System.out.println(n1);
			System.out.println(n2);
			x=true;
		}
		double matchIndex=0;
		int name=1000;
		int clickable=5;
		int visible=5;
		int zindex=5;
		int xpath=100;
		int contenthash=5;
		int noChild=5;
		int domid=1000;
		double total = (name+clickable+visible+zindex+xpath+contenthash+noChild+domid);
		if(n1.getDomid().equals(n2.getDomid())){
			if(!n2.getDomid().equals("")){
				matchIndex+=domid;	
			}else{
				total-=domid; // Ignore when not present
			}
		}
		if(x)
			System.out.println(matchIndex + "MI TOT "+ total);
		if(n1.getName().equals(n2.getName())){
			matchIndex+=name;
		}
		if(x)
			System.out.println(matchIndex + "MI TOT "+ total);
		if(n1.isClickable()==n2.isClickable()){
			matchIndex+=clickable;
		}
		if(x)
			System.out.println(matchIndex + "MI TOT "+ total);
		if(n1.isVisible()==n2.isVisible()){
			matchIndex+=visible;
		}
		if(x)
			System.out.println(matchIndex + " "+ total);
		if(n1.getZindex()==n2.getZindex()){
			matchIndex+=zindex;
		}
		//Strip Xpath and compare
		//TODO: Fraction comparision of Xpath
		/*String xPath1 =  n1.getXpath().replaceAll("\\[[0-9]+\\]", "");
		String xPath2 =  n2.getXpath().replaceAll("\\[[0-9]+\\]", "");
		if(xPath1.equals(xPath2)){
			matchIndex+=xpath;
		}*/
		LevenshteinDistance l=new LevenshteinDistance();
		String xPath1 =  n1.getXpath();
		String xPath2 =  n2.getXpath();
		int levenDist=l.computeLevenshteinDistance(xPath1, xPath2);
		//Formula to calculate [1- (levenshteinD/totallength)]*100
		matchIndex+=(1-(double)((double)levenDist/(double)(xPath1.length()+xPath2.length())))*xpath;
		if(x)
			System.out.println(matchIndex + "MI TOT"+ total);
		if(n1.getContenthash().equals(n2.getContenthash())){
			matchIndex+=contenthash;
		}
		if(x)
			System.out.println(matchIndex + "MI TOT"+ total);
		if(n1.getChildren().size()==n2.getChildren().size()){
			matchIndex+=noChild;
		}
		if(x)
			System.out.println(matchIndex + "MI TOT"+ total);
		 matchIndex *= ((double)100/(double)total);
			if(x)
				System.out.println(matchIndex + "MI TOT"+ total);
		return matchIndex;
	}

	public void compareAllLevels(ArrayList<Node> origLevel1,ArrayList<Node> origLevel2,int threshold){
		ArrayList<Node> currLevel1=nextLevel(origLevel1);
		ArrayList<Node> currLevel2=nextLevel(origLevel2);
		if(currLevel1.size()!=0 && currLevel2.size()!=0){
			computeMatchIndex(currLevel1, currLevel2,threshold);
			compareAllLevels(currLevel1, currLevel2,threshold);
		}
	}
	public void cleanList(comparePages c, Node[] list1, Node[] list2) {
		//For A 
		 for(Node n:c.getHash().keySet()){
			 
			 for(int i=0;i<list1.length;i++){
				 if(list1[i]==null){
					 continue;
				 }
				 if(n.equals(list1[i])){
					 list1[i]=null;
				 }
			 }
		 }
		 //For B 
		 for(Node n:c.getHash().values()){
			 for(int i=0;i<list2.length;i++){
				 if(list2[i]==null){
					 continue;
				 }
				 if(n.equals(list2[i])){
					 list2[i]=null;
				 }
			 }
		 }
	}
	public ArrayList<Node> printList(Node[] list) {
		ArrayList<Node> listret=new ArrayList<Node>();
		 for(int i=0;i<list.length;i++){
			  if(list[i]!=null &&(!list[i].getName().equals("SCRIPT")) &&(list[i].isNonDet()==false)){
				  listret.add(list[i]);
				  if(list[i].isVisible()==true){
					  
				 System.out.println(list[i].toCSVString());
				  }
			  }
		  }
		 return listret;
	}
	public void createNewList(Node[] list1, ArrayList<Node> list1New) {
		for(int i=0;i<list1.length;i++){
			  if(list1[i]!=null){
				  list1New.add(list1[i]);
			  }
		  }
	}
	public static boolean has(Node n,ArrayList<Node>list1){
		for(Node x:list1){
			if(x.equals(n)){
				return true;
			}
		}
		return false;
	}
	public void compareDistance(Node root1, HashMap<Node, Node> hash, ArrayList<Node>list1, ArrayList<Node>list2, int threshold){
		ArrayList<Node> children1=root1.getChildren();
//		ArrayList<Node> children2=root2.getChildren();
//		System.out.println(root1);
//		boolean check=has(root1,list1);
//		boolean check1=has(root1,list2);
		
		if(children1.size()!=0 && (!root1.getName().equals("SCRIPT"))){
			if(root1.isVisible()==false)
			System.out.println(root1);
		for(Node n:children1){
//			boolean check2=has(n,list1);
//			boolean check3=has(n,list2);
//			System.out.println("Dist");
			if(n.isVisible()==true &&(!n.getName().equals("SCRIPT"))){
			//Find distance between child and parent
			//distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
			double x1=(double)n.getX0();
			double y1=(double)n.getY0();
			double x2=(double)root1.getX0();
			double y2=(double)root1.getY0();
			double distance = distanceFormula(x1, y1, x2, y2);
			/*System.out.println(n.getId());
			if(n.getId()==156){
			System.out.println("T1:child"+n);
			System.out.println("parent"+root1);
			System.out.println("dist"+distance);
			}*/
			//Find mapped nodes in root2 use hash
			try{
			
			Node parent=hash.get(root1);
			Node child=hash.get(n);
			//Pick the nodes using list2 check if the distance is the same +/- threshold
			x1=(double)parent.getX0();
			y1=(double)parent.getY0();
			x2=(double)child.getX0();
			y2=(double)child.getY0();
			double distance1 = distanceFormula(x1, y1, x2, y2);
			/*if(child.getId()==156){
			System.out.println("T1:child"+child);
			System.out.println("parent"+parent);
			System.out.println("dist"+distance1);
			}*/
			double distanceP=distance+threshold;
			double distanceM=distance-threshold;
			if(!((distance1<=distanceP) &&(distance1>=distanceM)) && (child.isVisible()==true)){
			/*	System.out.println(distance);
				System.out.println(distance1);
				System.out.println(root1.getName());
				System.out.println(n.getName()+" "+n.getDomid());
				System.out.println(parent.getName());
				System.out.println(child.getName()+" "+child.getDomid());
				System.out.println("_________________________");
				*/
				System.out.println("Delta X:"+(distance-distance1));
				System.out.println("Tree1:"+n);
				
				System.out.println("Tree2:"+child);
				System.out.println("Tree1P:"+root1);
				System.out.println("Tree2P:"+parent);
				errorlist.add(child);
			}
			//Call on all elements
			
			}
			catch(Exception e){
				
			}
		}
			compareDistance(n, hash,list1,list2, threshold);
		}
		}
	}
	
	private double distanceFormula(double x1, double y1, double x2, double y2) {
		return Math.sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
	}

	/*public HashMap getSortedMap(HashMap hmap)
	{
		HashMap map = new LinkedHashMap();
		ArrayList mapKeys = new ArrayList(hmap.keySet());
		ArrayList mapValues = new ArrayList(hmap.values());
		hmap.clear();
		TreeSet sortedSet = new TreeSet(mapValues);
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;
//		a) Ascending sort
 
		for (int i=0; i<size; i++)
		{
 
		map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), sortedArray[i]);
 
		}
		return map;
	}*/
	public static String sysCall(String s){
		RunCommand r=new RunCommand();
		//System.out.println(r.runCmd(s).length());
		return r.runCmd(s);
	}
	public void callEMD(boolean runQ,HashMap<Node, Node> hash,int forND,int testid1,int testid2,connectDB c,Connection conn) throws SQLException{
		Node[]keys=new Node[hash.size()];
		int i=0;
		for(Node n:hash.keySet()){
			keys[i]=n;
			i++;
		}
		IdComparator idC=new IdComparator();
		Arrays.sort(keys,idC);
		ArrayList<Node> errorimages=new ArrayList<Node>();
		ArrayList<Double> percents=new ArrayList<Double>();
		for(int j=0;j<keys.length;j++){
			ArrayList<String> ignoreList=new ArrayList<String>();
			ignoreList.add("SELECT");
			ignoreList.add("INPUT");
			
			Node n=hash.get(keys[j]);
			if((n.getChildren().size()==0 || keys[j].getChildren().size()==0) && (!(ignoreList.contains(n.getName())|| ignoreList.contains(keys[j].getName())))){
				continue;
			}
			int kX0=Math.max(0, keys[j].getX0());
			int kX1=Math.max(0, keys[j].getX1());
			int kY0=Math.max(0, keys[j].getY0());
			int kY1=Math.max(0, keys[j].getY1());
			
			int nX0=Math.max(0, n.getX0());
			int nX1=Math.max(0, n.getX1());
			int nY0=Math.max(0, n.getY0());
			int nY1=Math.max(0, n.getY1());
			
			int width=kX1-kX0;
			int width1=nX1-nX0;
			int height=kY1-kY0;
			int height1=nY1-nY0;
			if(width==0 || width1==0 || height==0 || height1==0)
				continue;
			String s="./opencv_test img/"+testid1+".png img/"+testid2+".png "+kX0+" "+kY0+" "+width+" "+height+" "+nX0+" "+nY0+" "+width1+" "+height1;
			double emdValue=Double.parseDouble(sysCall(s));
			if(emdValue==-99)
				continue;
			String s1="./colorspp img/"+testid1+".png " +kX0+" "+kY0+" "+width+" "+height;
			double threshold=0;
			String runGrey1="./grey_out img/"+testid1+".png";
			String runGrey2="./grey_out img/"+testid2+".png";
			if(forND==0){
			double noColors=Double.parseDouble(sysCall(s1));
			double area=width*height;
			
			if(noColors<1){
				threshold= 2;
				if(area>10000)
					threshold=1;
			}
			else{
				threshold= 1;
				if(area>10000)
					threshold=0.5;
			}
				
				
			
			}
			else{
				threshold=0.01;
			}
			if(emdValue>threshold){
				testid1list.add(keys[j]);
				testid2list.add(n);
				errorimages.add(n);
				percents.add(new Double(emdValue));
				if(forND==1){
				sysCall(runGrey1+" "+keys[j].getX0()+" "+keys[j].getY0()+" "+keys[j].getX1()+" "+keys[j].getY1());
				sysCall(runGrey2+" "+n.getX0()+" "+n.getY0()+" "+n.getX1()+" "+n.getY1());
				//wait(10);
				}
			}
		}
		if(runQ){
		c.errorlistImgToTable(conn, errorimages, percents, testid2);
		}
	}
	class IdComparator implements Comparator{
		public int compare(Object n1, Object n2){
		int id1 = ((Node) n1).getId();
		int id2 = ((Node) n2).getId();
//		System.out.println(id2);
		Integer nid1=new Integer(id1);
		Integer nid2=new Integer(id2);
		return nid2.compareTo(nid1);
		}
		}

	
public static void wait (int n){
        long t0,t1;
        t0=System.currentTimeMillis();
        do{
            t1=System.currentTimeMillis();
        }
        while (t1-t0<1000);
}
public static void main(String[] args) throws SQLException {
//	  sysCall();
	 // System.exit(0);
	  comparePages pageCompare=new comparePages();
	  connectDB c=new connectDB();
	  Connection conn=c.dbConnect();
	  int testid1=Integer.parseInt(args[0]);
	  int testid2=Integer.parseInt(args[1]);
	  Node[]list1=pageCompare.runQuery(c,conn,testid1);
	  Node[]list2=pageCompare.runQuery(c,conn,testid2);
	/*  for(int i=1;i<list1.length;i++){
			if(!list1[i].getDomid().equals("")){
				for(int j=1;j<list2.length;j++){
					if(list2[j].getDomid().equals(list1[i].getDomid())){
						pageCompare.getHash().put(list1[i], list2[j]);
						list1[i].setMapped();
						list2[j].setMapped();
						break;
					}
				}
			}
	  }*/
	  Node rootQ1=list1[1];
	  Node rootQ2=list2[1];
	  int cnt=0;
	  for(int i=1;i<list1.length;i++)
	  {
		  if(list1[i].getDomid().equals("footer"))
			  System.out.println(list1[i]+"list1 footer");
	  }
	  for(int i=1;i<list2.length;i++)
	  {
		  if(list2[i].getDomid().equals("footer"))
			  System.out.println(list2[i]+"list2 footer");
	  }
//	  System.out.println(cnt+"invisible");
	 //Call on root
	  ArrayList<Node> origLevel1=new ArrayList<Node>();
	  origLevel1.add(rootQ1);
	  ArrayList<Node> origLevel2=new ArrayList<Node>();
	  origLevel2.add(rootQ2);
	  pageCompare.computeMatchIndex(origLevel1, origLevel2,100);
	  pageCompare.compareAllLevels(origLevel1, origLevel2,100);
//	  System.out.println(list2[3].isVisible());
	  pageCompare.cleanList(pageCompare, list1, list2);
	 ArrayList<Node> list1New=new ArrayList<Node>();
	 pageCompare.createNewList(list1, list1New);
	  ArrayList<Node> list2New=new ArrayList<Node>();
	  pageCompare.createNewList(list2, list2New);
	  pageCompare.computeMatchIndex(list1New, list2New,50);
	  pageCompare.cleanList(pageCompare, list1, list2);
		 System.out.println("List 1");
		 ArrayList<Node> list1clone=pageCompare.printList(list1);
		 System.out.println("-----------------------------------");
		 System.out.println("List 2");
		 ArrayList<Node> list2clone=pageCompare.printList(list2);
		 System.out.println(pageCompare.getHash().size());
		 System.out.println("-----------------------------------");
		 pageCompare.compareDistance(rootQ1, pageCompare.getHash(),list1clone,list2clone, 2);
	 System.out.println(pageCompare.errorlist);
		 System.out.println(pageCompare.errorlist.size());
		 System.out.println(pageCompare.hash.size());
		 c.hashToTable(conn,pageCompare.hash,testid1,testid2);
		 c.errorlistToTable(conn,pageCompare.errorlist,testid2);
		 pageCompare.callEMD(true,pageCompare.hash,0, testid1, testid2,c,conn);
//		 System.out.println(pageCompare.hash);
		 
		 /*for(Node n:pageCompare.hash.values()){
//			 System.out.println(n.getId());
			 if(n.getId()==156)
			 {
				 //n = ie child
				 Node chromekid=getKeysFromValue(pageCompare.hash, n);
				 //chrome kid=chrome child
//				 System.out.println(+"=");
				 System.out.println("Distance between chrome 156 and parent"+pageCompare.distanceFormula(chromekid.getX0(), chromekid.getY0(), chromekid.getParent().getX0(), chromekid.getParent().getY0()));
				 System.out.println("Distance between IE 156 and parent"+pageCompare.distanceFormula(n.getX0(), n.getY0(), n.getParent().getX0(), n.getParent().getY0()));
				 System.out.println((pageCompare.distanceFormula(chromekid.getX0(), chromekid.getY0(), chromekid.getParent().getX0(), chromekid.getParent().getY0()))- pageCompare.distanceFormula(n.getX0(), n.getY0(), n.getParent().getX0(), n.getParent().getY0()));
				 System.out.println(getKeysFromValue(pageCompare.hash, n.getParent())+" "+n.getParent().getId());
//			 System.out.println(n);
			 }
		 }*/
  }
 
public static Node getKeysFromValue(HashMap<Node, Node> hm, Object value){
	    for(Node n:hm.keySet()){
	        if(hm.get(n).equals(value)) {
	            return n;
	        }
	    }
		return null;
	  }
	  


}

