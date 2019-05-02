package rtree;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {
	public boolean isLeaf;
	public float[][] coords; 
	public ArrayList<Long> childIds;
	public ArrayList<float[][]> childRectangles;
	public long parent;
	public long myId;

	public Node(boolean isLeaf, float[][] coords, long parent) {
		this.isLeaf = isLeaf;
		this.coords = coords;
		this.childIds = new ArrayList<Long>();
		this.childRectangles = new ArrayList<float[][]>();
		this.parent = parent;
	}
}
