import java.util.ArrayList;


public class Node {
	private String domid,xpath,contenthash,name;
	private int id,type,x0,y0,x1,y1,zindex,testid;
	boolean clickable,visible,mapped,nondet;
	private Node parent;
	private ArrayList <Node> children=new ArrayList<Node>();
	public Node(int testid,String domid,String xpath,String contenthash,String name,int id,int type,int x0,int y0,int x1,int y1, int zindex, boolean clickable,boolean visible,Node parent){
		this.testid=testid;
		this.domid=domid;
		this.xpath=xpath;
		this.contenthash=contenthash;
		this.name=name;
		this.id=id;
		this.type=type;
		this.x0=x0;
		this.x1=x1;
		this.y1=y1;
		this.y0=y0;
		this.zindex=zindex;
		this.clickable=clickable;
		this.visible=visible;
		this.parent=parent;
		this.mapped=false;
		this.nondet=false;
	}

	public String toCSVString(){
		return clickable+","+contenthash+","+domid+","+id+","+name+","+type+","+visible+","+x0+","+y0+","+x1+","+y1+","+xpath+","+zindex;

	}
	public String toString() {
			return "Node [" +"clickable=" + clickable
					+ ", contenthash=" + contenthash + ", domid=" + domid + ", id="
					+ id + ", name=" + name  + ", type="
					+ type + ", visible=" + visible + ", x0=" + x0 + ", x1=" + x1
					+ ", xpath=" + xpath + ", y0=" + y0 + ", y1=" + y1
					+ ", zindex=" + zindex + "]\n";
		 
		}
	public String getDomid() {
		return domid;
	}
	public void setDomid(String domid) {
		this.domid = domid;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getContenthash() {
		return contenthash;
	}
	public void setContenthash(String contenthash) {
		this.contenthash = contenthash;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getX0() {
		return x0;
	}
	public void setX0(int x0) {
		this.x0 = x0;
	}
	public int getY0() {
		return y0;
	}
	public void setY0(int y0) {
		this.y0 = y0;
	}
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getY1() {
		return y1;
	}
	public void setY1(int y1) {
		this.y1 = y1;
	}
	public int getZindex() {
		return zindex;
	}
	public void setZindex(int zindex) {
		this.zindex = zindex;
	}
	public boolean isClickable() {
		return clickable;
	}
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public void addChild(Node n){
		children.add(n);
	}
	public ArrayList<Node> getChildren(){
		return children;
		
	}
	public void setMapped(){
		this.mapped=true;
	}
	public boolean isMapped(){
		return mapped;	
	}
	public void setNonDet(){
		this.nondet=true;
	}
	public boolean isNonDet() {
		// TODO Auto-generated method stub
		return nondet;
	}
	public void setTestid(int testid) {
		this.testid = testid;
	}
	public int getTestid() {
		return testid;
	}
	public boolean equals(Node n){
		if(this.domid.equals(n.getDomid()) && this.name==n.getName() && this.type==n.getType() && this.x0==n.getX0() && this.y0==n.getY0() && this.x1==n.getX1() && this.y1==n.getY1() && this.contenthash.equals(n.getContenthash()) && this.zindex==n.getZindex() && this.clickable==n.isClickable() && this.visible==n.isVisible()){
			return true;
		}
			
		return false;
		
	}
	
}
