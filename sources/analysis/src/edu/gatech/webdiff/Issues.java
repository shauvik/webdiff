package edu.gatech.webdiff;
import java.util.ArrayList;


public class Issues {
	//xP2 list contain the node and the corresponding nodej is in x list
	//4=Content Shifted
    private  ArrayList<Node> iPos;
    private  ArrayList<Node> iPosP2;
    //5=Visibility Changed
    private  ArrayList<Node> iVis;
    private  ArrayList<Node> iVisP2;
    //6=Size Changed
    private  ArrayList<Node> iSize;
    private  ArrayList<Node> iSizeP2;
    //7=Appearance Changed
    private  ArrayList<Node> iAppear;
    private  ArrayList<Node> iAppearP2;
	public Issues(){
		this.iPos=new ArrayList<Node>();
		this.iVis=new ArrayList<Node>();
		this.iSize=new ArrayList<Node>();
		this.iAppear=new ArrayList<Node>();
		
		this.iPosP2=new ArrayList<Node>();
		this.iVisP2=new ArrayList<Node>();
		this.iSizeP2=new ArrayList<Node>();
		this.iAppearP2=new ArrayList<Node>();
	}

	public ArrayList<Node> getPosIssues() {
		return iPos;
	}
	public void addToPosIssues(Node n){
		iPos.add(n);
	}
	
	public ArrayList<Node> getPosIssuesP2() {
		return iPosP2;
	}
	public void addToPosIssuesP2(Node n){
		iPosP2.add(n);
	}
	public ArrayList<Node> getVisIssues() {
		return iVis;
	}
	public void addToVisIssues(Node n){
		iVis.add(n);
	}
	public ArrayList<Node> getVisIssuesP2() {
		return iVisP2;
	}
	public void addToVisIssuesP2(Node n){
		iVisP2.add(n);
	}
	public ArrayList<Node> getSizeIssues() {
		return iSize;
	}
	public void addToSizeIssues(Node n){
		iSize.add(n);
	}
	public ArrayList<Node> getSizeIssuesP2() {
		return iSizeP2;
	}
	public void addToSizeIssuesP2(Node n){
		iSizeP2.add(n);
	}
	public ArrayList<Node> getAppearanceIssues() {
		return iAppear;
	}
	public void addToAppearanceIssues(Node n){
		iAppear.add(n);
	}
	public ArrayList<Node> getAppearanceIssuesP2() {
		return iAppearP2;
	}
	public void addToAppearanceIssuesP2(Node n){
		iAppearP2.add(n);
	}
	public String iPosToString(){
		String tmp="";
		for(Node n:iPos)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String iVisToString(){
		String tmp="";
		for(Node n:iVis)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String iSizeToString(){
		String tmp="";
		for(Node n:iSize)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String iAppearToString(){
		String tmp="";
		for(Node n:iAppear)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	
	public String iPosP2ToString(){
		String tmp="";
		for(Node n:iPosP2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String iVisP2ToString(){
		String tmp="";
		for(Node n:iVisP2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String iSizeP2ToString(){
		String tmp="";
		for(Node n:iSizeP2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String iAppearP2ToString(){
		String tmp="";
		for(Node n:iAppearP2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	
}
