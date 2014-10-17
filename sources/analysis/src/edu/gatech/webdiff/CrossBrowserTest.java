package edu.gatech.webdiff;
import java.sql.SQLException;
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
	protected HashMap[] rmap;
	protected HashMap[] rmap_index;
	//private Connection conn;
	private Issues[] mismatch;
	protected DbUtils db;
	private HashMap<String,Double> alphaCache;
	public CrossBrowserTest(int[] browserTestId,int refBrowserTestId, HashMap[] map) throws SQLException{
		db=new DbUtils();
		this.alphaCache=new HashMap<String, Double>();
    	this.map=map;
    	this.rmap=createReverseMap(map);
    	this.mismatch=new Issues[browserTestId.length];
    	for(int i=0;i<mismatch.length;i++)
    		mismatch[i]=new Issues();
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

	public boolean VChk(int refBrowserTestId, int browserTestId, int[] boxi, int[] boxj, int m, Node nodej){
		if(nodej.getName().equalsIgnoreCase("input") || nodej.getName().equalsIgnoreCase("button")){
			return true; //We do this to avoid reporting differences in native browser form element styles
		}
		//Calculates alpha
		//boxi and boxj have x0,y0,w,h
		double alpha=-1;
		String boxiStr=boxi[0]+","+boxi[1]+","+boxi[2]+","+boxi[3];
		if(alphaCache.containsKey(boxiStr)){
			alpha=alphaCache.get(boxiStr);
		}
		else{
			String tmp="colorspp.exe "+WDConstants.SC_DIR+refBrowserTestId+".png "+boxi[0]+" "+boxi[1]+" "+boxi[2]+" "+boxi[3];
			alpha=Double.parseDouble(sysCall(tmp));
			alphaCache.put(boxiStr,alpha);
		}
		//Choose threshold based on color density and size
		double threshold=chooseThreshold(alpha,boxi[2],boxi[3])*m;
		//Earth movers distance
		String tmp = "get_emd.exe "+WDConstants.SC_DIR+refBrowserTestId+".png "+WDConstants.SC_DIR+browserTestId+".png "+boxi[0]+" "+boxi[1]+" "+boxi[2]+" "+boxi[3]+" "+boxj[0]+" "+boxj[1]+" "+boxj[2]+" "+boxj[3];
		double emd=Double.parseDouble(sysCall(tmp));
		emd=Math.floor(emd);
		if(emd<=threshold)
			return true;
		else
		{
			//System.out.println(emd+" AREA"+(boxi[2]*boxi[3])+"CD"+alpha+"Threshold"+threshold);
			return false;
		}
	}
	
	private double chooseThreshold(double alpha, int w, int h) {
		double area=w*h;
		//Small Image
		if(area<10000){
			//Small CD
			if(alpha<1)
				return 1;
			//Large CD
			else
				return 0.5;
		}
		//Large Image
		else{
			//Small CD
			if(alpha<1)
				return 2;
			//Large CD
			else
				return 1;
		}
	}

	public void visualAnalysis(int[] browserTestId,int refBrowserTestId) throws SQLException{
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
				if(node.isNonDet()){
					int nodeX0=Math.max(0, nodej.getX0());
					int nodeX1=Math.max(0, nodej.getX1());
					int nodeY0=Math.max(0, nodej.getY0());
					int nodeY1=Math.max(0, nodej.getY1());
					int nodeW=nodeX1-nodeX0;
					int nodeH=nodeY1-nodeY0;
					if(nodeW>0 && nodeH>0){
					//Grey out the browser stuff
					String tmp = "grey_out.exe "+WDConstants.SC_DIR+browserTestId[i-1]+".png "+nodeX0+" "+nodeY0+" "+nodeX1+" "+nodeY1;
					sysCall(tmp);
					}
					//Ignore current node
					continue;
				}
				//Check for positional shifts
				double relDist1=relDistToContainer(node);
				double relDist2=relDistToContainer(nodej);
				double diff=Math.abs(relDist1-relDist2);
				if(diff>5){
					Node parent1=node.getParent();
					Node parent2=nodej.getParent();
					
					int p1Width=Math.max(0, parent1.getX1())-Math.max(0, parent1.getX0());
					int p1Height=Math.max(0, parent1.getY1())-Math.max(0, parent1.getY0());
					int[] boxi={Math.max(0, parent1.getX0()),Math.max(0, parent1.getY0()),p1Width,p1Height};
					
					int p2Width=Math.max(0, parent2.getX1())-Math.max(0, parent2.getX0());
					int p2Height=Math.max(0, parent2.getY1())-Math.max(0, parent2.getY0());
					int[] boxj={Math.max(0, parent2.getX0()),Math.max(0, parent2.getY0()),p2Width,p2Height};
					if(p1Width>0 && p2Width>0 && p1Height>0 && p2Height>0){
						if(!VChk(refBrowserTestId,browserTestId[i-1],boxi,boxj,1,nodej)){
								mismatch[i-1].addToPosIssues(parent2);
								mismatch[i-1].addToPosIssuesP2(parent1);
						}
					}
				}
				int nodeWidth=Math.max(0, node.getX1())-Math.max(0, node.getX0());
				int nodeHeight=Math.max(0, node.getY1())-Math.max(0, node.getY0());
				int[] boxi={Math.max(0, node.getX0()),Math.max(0, node.getY0()),nodeWidth,nodeHeight};
				
				int nodejWidth=Math.max(0, nodej.getX1())-Math.max(0, nodej.getX0());
				int nodejHeight=Math.max(0, nodej.getY1())-Math.max(0, nodej.getY0());
				int[] boxj={Math.max(0, nodej.getX0()),Math.max(0, nodej.getY0()),nodejWidth,nodejHeight};
				
				int widthDiff=Math.abs(nodeWidth-nodejWidth);
				int heightDiff=Math.abs(nodeHeight-nodejHeight);
				if(node.isVisible()!=nodej.isVisible()){
					mismatch[i-1].addToVisIssues(nodej);
					mismatch[i-1].addToVisIssuesP2(node);
				}
				else if(widthDiff>5 || heightDiff>5){
					if(!VChk(refBrowserTestId, browserTestId[i-1], boxi, boxj, 1, nodej)){
						mismatch[i-1].addToSizeIssues(nodej);
						mismatch[i-1].addToSizeIssuesP2(node);
					}
				}
				else if(nodeWidth>0 && nodejWidth>0 && nodeHeight>0 && nodejHeight>0){
					if(!VChk(refBrowserTestId, browserTestId[i-1], boxi, boxj, 2, nodej)){
						mismatch[i-1].addToAppearanceIssues(nodej);
						mismatch[i-1].addToAppearanceIssuesP2(node);
					}
				}
			}
			
			//Write to issues db the issues at given spot pos1 corresponds 2 nodej and pos2 corresponds 2 node = refbrowserz
			for(int m=0;m<mismatch[i-1].getPosIssues().size();m++){
				this.db.insQuery("INSERT INTO issues (testid,id,position1,position2,xpath1,type) VALUES("+browserTestId[i-1]+","+mismatch[i-1].getPosIssues().get(m).getId()+",'("+mismatch[i-1].getPosIssues().get(m).getX0()+","+mismatch[i-1].getPosIssues().get(m).getY0()+","+mismatch[i-1].getPosIssues().get(m).getX1()+","+mismatch[i-1].getPosIssues().get(m).getY1()+")','("+mismatch[i-1].getPosIssuesP2().get(m).getX0()+","+mismatch[i-1].getPosIssuesP2().get(m).getY0()+","+mismatch[i-1].getPosIssuesP2().get(m).getX1()+","+mismatch[i-1].getPosIssuesP2().get(m).getY1()+")','"+mismatch[i-1].getPosIssues().get(m).getXpath()+"',4)");
			}
			for(int m=0;m<mismatch[i-1].getVisIssues().size();m++){
				this.db.insQuery("INSERT INTO issues (testid,id,position1,position2,xpath1,type) VALUES("+browserTestId[i-1]+","+mismatch[i-1].getVisIssues().get(m).getId()+",'("+mismatch[i-1].getVisIssues().get(m).getX0()+","+mismatch[i-1].getVisIssues().get(m).getY0()+","+mismatch[i-1].getVisIssues().get(m).getX1()+","+mismatch[i-1].getVisIssues().get(m).getY1()+")','("+mismatch[i-1].getVisIssuesP2().get(m).getX0()+","+mismatch[i-1].getVisIssuesP2().get(m).getY0()+","+mismatch[i-1].getVisIssuesP2().get(m).getX1()+","+mismatch[i-1].getVisIssuesP2().get(m).getY1()+")','"+mismatch[i-1].getVisIssues().get(m).getXpath()+"',5)");
			}
			for(int m=0;m<mismatch[i-1].getSizeIssues().size();m++){
				this.db.insQuery("INSERT INTO issues (testid,id,position1,position2,xpath1,type) VALUES("+browserTestId[i-1]+","+mismatch[i-1].getSizeIssues().get(m).getId()+",'("+mismatch[i-1].getSizeIssues().get(m).getX0()+","+mismatch[i-1].getSizeIssues().get(m).getY0()+","+mismatch[i-1].getSizeIssues().get(m).getX1()+","+mismatch[i-1].getSizeIssues().get(m).getY1()+")','("+mismatch[i-1].getSizeIssuesP2().get(m).getX0()+","+mismatch[i-1].getSizeIssuesP2().get(m).getY0()+","+mismatch[i-1].getSizeIssuesP2().get(m).getX1()+","+mismatch[i-1].getSizeIssuesP2().get(m).getY1()+")','"+mismatch[i-1].getSizeIssues().get(m).getXpath()+"',6)");
			}
			for(int m=0;m<mismatch[i-1].getAppearanceIssues().size();m++){
				this.db.insQuery("INSERT INTO issues (testid,id,position1,position2,xpath1,type) VALUES("+browserTestId[i-1]+","+mismatch[i-1].getAppearanceIssues().get(m).getId()+",'("+mismatch[i-1].getAppearanceIssues().get(m).getX0()+","+mismatch[i-1].getAppearanceIssues().get(m).getY0()+","+mismatch[i-1].getAppearanceIssues().get(m).getX1()+","+mismatch[i-1].getAppearanceIssues().get(m).getY1()+")','("+mismatch[i-1].getAppearanceIssuesP2().get(m).getX0()+","+mismatch[i-1].getAppearanceIssuesP2().get(m).getY0()+","+mismatch[i-1].getAppearanceIssuesP2().get(m).getX1()+","+mismatch[i-1].getAppearanceIssuesP2().get(m).getY1()+")','"+mismatch[i-1].getAppearanceIssues().get(m).getXpath()+"',7)");
			}
			
			//Cluster Issues
			new ClusterIssues(browserTestId[i-1]);
			
		}
		
	}
}
