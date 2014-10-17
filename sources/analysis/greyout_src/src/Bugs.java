import java.util.ArrayList;


public class Bugs {
	//bugxp2 contain the node and the corresponding nodej is in bugsx
	//4=Content Shifted
    private  ArrayList<Node> bugs4;
    private  ArrayList<Node> bugs4p2;
    //5=Visibility Changed
    private  ArrayList<Node> bugs5;
    private  ArrayList<Node> bugs5p2;
    //6=Size Changed
    private  ArrayList<Node> bugs6;
    private  ArrayList<Node> bugs6p2;
    //7=Appearance Changed
    private  ArrayList<Node> bugs7;
    private  ArrayList<Node> bugs7p2;
	public Bugs(){
		this.bugs4=new ArrayList<Node>();
		this.bugs5=new ArrayList<Node>();
		this.bugs6=new ArrayList<Node>();
		this.bugs7=new ArrayList<Node>();
		
		this.bugs4p2=new ArrayList<Node>();
		this.bugs5p2=new ArrayList<Node>();
		this.bugs6p2=new ArrayList<Node>();
		this.bugs7p2=new ArrayList<Node>();
	}

	public ArrayList<Node> getBugs4() {
		return bugs4;
	}
	public void addToBugs4(Node n){
		bugs4.add(n);
	}
	
	public ArrayList<Node> getBugs4p2() {
		return bugs4p2;
	}
	public void addToBugs4p2(Node n){
		bugs4p2.add(n);
	}
	public ArrayList<Node> getBugs5() {
		return bugs5;
	}
	public void addToBugs5(Node n){
		bugs5.add(n);
	}
	public ArrayList<Node> getBugs5p2() {
		return bugs5p2;
	}
	public void addToBugs5p2(Node n){
		bugs5p2.add(n);
	}
	public ArrayList<Node> getBugs6() {
		return bugs6;
	}
	public void addToBugs6(Node n){
		bugs6.add(n);
	}
	public ArrayList<Node> getBugs6p2() {
		return bugs6p2;
	}
	public void addToBugs6p2(Node n){
		bugs6p2.add(n);
	}
	public ArrayList<Node> getBugs7() {
		return bugs7;
	}
	public void addToBugs7(Node n){
		bugs7.add(n);
	}
	public ArrayList<Node> getBugs7p2() {
		return bugs7p2;
	}
	public void addToBugs7p2(Node n){
		bugs7p2.add(n);
	}
	public String bugs4ToString(){
		String tmp="";
		for(Node n:bugs4)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String bugs5ToString(){
		String tmp="";
		for(Node n:bugs5)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String bugs6ToString(){
		String tmp="";
		for(Node n:bugs6)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String bugs7ToString(){
		String tmp="";
		for(Node n:bugs7)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	
	public String bugs4p2ToString(){
		String tmp="";
		for(Node n:bugs4p2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String bugs5p2ToString(){
		String tmp="";
		for(Node n:bugs5p2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String bugs6p2ToString(){
		String tmp="";
		for(Node n:bugs6p2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	public String bugs7p2ToString(){
		String tmp="";
		for(Node n:bugs7p2)
			tmp+="("+n.getX0()+","+n.getY0()+","+n.getX1()+","+n.getY1()+");";
		return tmp;
	}
	
}
