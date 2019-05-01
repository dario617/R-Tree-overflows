package rtree;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable{
	public boolean isLeaf;
	public float[][] coords; 
	public ArrayList<Long> childs;
	public ArrayList<float[][]> childDims;
	public long parent;
	public long myId;
	
	public Node(boolean isLeaf,float[][] coords, long parent) {
		this.isLeaf = isLeaf;
		this.coords = coords;
		this.childs = new ArrayList<Long>();
		this.childDims = new ArrayList<float[][]>();
		this.parent = parent;
	}
}
