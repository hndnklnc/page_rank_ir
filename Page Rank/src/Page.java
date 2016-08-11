import java.util.ArrayList;
import java.util.HashMap;


public class Page {

	private ArrayList<Page> outlinks = new ArrayList<Page>();
	private ArrayList<Page> inlinks = new ArrayList<Page>();
	private double rank;
	private String name;
	private double newPR;
	
	public Page( String name) {
		this.name = name;
	}
	
	public ArrayList<Page> getOutlinks() {
		return outlinks;
	}
	public void setOutlinks(ArrayList<Page> outlinks) {
		this.outlinks = outlinks;
	}
	public ArrayList<Page> getInlinks() {
		return inlinks;
	}
	public void setInlinks(ArrayList<Page> inlinks) {
		this.inlinks = inlinks;
	}
	public double getRank() {
		return rank;
	}
	public void setRank(double rank) {
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getNewPR() {
		return newPR;
	}

	public void setNewPR(double newPR) {
		this.newPR = newPR;
	}
	
	
}
