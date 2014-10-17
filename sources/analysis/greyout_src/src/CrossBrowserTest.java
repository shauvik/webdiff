import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class CrossBrowserTest {
	private HashMap[] map;
	//Using reverse map because I want to find the keys for the given values
	//In rmap value= map's key and rmaps key=map's value
	/*
	 * map 
	 * key= ref b value = browser
	 * rmap
	 * key= browser value= refb
	 */
	private HashMap[] rmap;
	private HashMap[] rmap_index;
	//private Connection conn;
	private Bugs[] mismatch;
	private DbConn c;
	private Connection conn;
	private static int blah=0;
	private HashMap<String,Double> alphaCache;
	public CrossBrowserTest(int[] browserTestId,int refBrowserTestId, HashMap[] map) throws SQLException{
		c=new DbConn();
    	conn = c.connectToDB();
		this.alphaCache=new HashMap<String, Double>();
    	this.map=map;
    	this.rmap=createReverseMap(map);
    	this.mismatch=new Bugs[browserTestId.length];
    	for(int i=0;i<mismatch.length;i++)
    		mismatch[i]=new Bugs();
		visualAnalysis(browserTestId,refBrowserTestId);
	}
	
	private HashMap[] createReverseMap(HashMap[] map) {
		HashMap[] rev_map=new HashMap[map.length];
		rmap_index=new HashMap[map.length];
		for(int i=0;i<rev_map.length;i++){
			rev_map[i]=new HashMap<Node, Node>();
			rmap_index[i]=new HashMap<Integer, Node>();
			Set keysMap = map[i].keySet();
			Iterator<Node> itr = keysMap.iterator();
			while(itr.hasNext()){
				Node key=itr.next();
				Node value=(Node) map[i].get(key);
				rmap_index[i].put(value.getId(), value);
				rev_map[i].put(value, key);
			}
		}
		return rev_map;
	}
	private double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(((x1-x2)*(x1-x2)) + ((y1-y2)*(y1-y2)));
	}
	private double relDistToContainer(Node node){
		return distance(node.getX0(),node.getY0(),node.getParent().getX0(),node.getParent().getY0());
	}
	public String sysCall(String s){
		RunCommand r=new RunCommand();
		//System.out.println(r.runCmd(s).length());
		return r.runCmd(s);
	}

	public boolean VChk(int refBrowserTestId, int browserTestId, int[] boxi, int[] boxj){
	
		//Choose threshold based on color density and size
		double threshold=chooseThreshold();
		if(boxi[2]<0 || boxi[3]<0 || boxj[2]<0 || boxj[3]<0)
		{
			return true;
		}
		//Earth movers distance
		String tmp = "get_emd.exe D:/workspace/webdiff/"+refBrowserTestId+".png "+"D:/workspace/webdiff/"+browserTestId+".png "+boxi[0]+" "+boxi[1]+" "+boxi[2]+" "+boxi[3]+" "+boxj[0]+" "+boxj[1]+" "+boxj[2]+" "+boxj[3];
		double emd=Double.parseDouble(sysCall(tmp));
		
		if(emd<=threshold)
			return true;
		else{
			System.out.println(emd);
			return false;
			
		}
	}
	
	private double chooseThreshold() {
		return 0.1;
	}

	public void visualAnalysis(int[] browserTestId,int refBrowserTestId){
		for(int i=1;i<=browserTestId.length;i++){ // Ln3
			//TODO: Skipping Line 5 and 6 needs to be done after algo is complete, Levenshetin changes, cache EMD data
			//Creating sorted array to know order of node traversal
			Set nodeTraversal = rmap_index[i-1].keySet();
			int[] nodeTraversalArr=new int[nodeTraversal.size()];
			int j=0;
			Iterator<Integer> itr = nodeTraversal.iterator();
			while(itr.hasNext()){
				Integer curr=itr.next();
				nodeTraversalArr[j]=(int) curr;
				j++;
			}
			
			Arrays.sort(nodeTraversalArr);
			for(int x=nodeTraversalArr.length-1;x>0;x--){
				int k=nodeTraversalArr[x];
				Node nodej=(Node) rmap_index[i-1].get(new Integer(k)); //rmap index's value is the key of rmap/value of map= nodej in algo
				Node node=(Node) rmap[i-1].get(nodej);
				//Check all properties
				if(node.getDomid().equals(nodej.getDomid()) && node.getContenthash().equals(nodej.getContenthash()) && node.getName().equals(nodej.getName()) && node.isClickable()==nodej.isClickable() && node.isVisible()==nodej.isVisible() && node.getXpath().equals(nodej.getXpath()) && node.getX0()==nodej.getX0() && node.getY0()==nodej.getY0() && nodej.getX1()==node.getX1() && node.getY1()==node.getY1()){
					//Do nothing
				}
				else{
					System.out.println("here!");
					markAsND(refBrowserTestId,node);
				}
				if(node.getContenthash().equals("no hash")){
					if(!VChk(refBrowserTestId, browserTestId[i-1],getBoxArr(node) , getBoxArr(nodej))){
						System.out.println("here!");
						markAsND(refBrowserTestId,node);
					}
				}
			/*	if(!VChk(refBrowserTestId, browserTestId[i-1],getBoxArr(node) , getBoxArr(nodej))){
					markAsND(refBrowserTestId,node);
				}*/
			
			}
			
			//Cluster Nodes markAsND(refBrowserTestId,node);
			
		}
		
	}
	private int[] getBoxArr(Node node){
		int nodeX0=Math.max(0, node.getX0());
		int nodeX1=Math.max(0, node.getX1());
		int nodeY0=Math.max(0, node.getY0());
		int nodeY1=Math.max(0, node.getY1());
		int nodeW=nodeX1-nodeX0;
		int nodeH=nodeY1-nodeY0;
		int[] boxi={nodeX0,nodeY0 ,nodeW,nodeH};
		return boxi;
	}
	private void markAsND(int refB, Node node) {
		insQuery("UPDATE domdata SET non_det = 1 WHERE testid="+refB+" and id="+node.getId());
		//Grey out refb
		int nodeX0=Math.max(0, node.getX0());
		int nodeX1=Math.max(0, node.getX1());
		int nodeY0=Math.max(0, node.getY0());
		int nodeY1=Math.max(0, node.getY1());
		int nodeW=nodeX1-nodeX0;
		int nodeH=nodeY1-nodeY0;
		if(nodeW>0 && nodeH>0){
			String tmp = "grey_out.exe D:/workspace/webdiff/"+refB+".png "+nodeX0+" "+nodeY0+" "+nodeX1+" "+nodeY1;
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			System.out.println(tmp);
			sysCall(tmp);
			
			//System.out.println(op + " " +blah++);
		}
		
	}

	public void insQuery(String query){
		try{
			java.sql.Statement st = conn.createStatement();
			st.executeUpdate(query);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}
