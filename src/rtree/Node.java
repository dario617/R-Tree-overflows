package rtree;

public class Node {
	private boolean isLeaf;
	private float[][] coords; 
	private Node[] childs;
	private int M;
	
	public Node(boolean isLeaf,float[][] coords, int M) {
		this.isLeaf = isLeaf;
		this.coords = coords;
		if(!isLeaf) {
			this.childs = new Node[M];
		}
		this.M = M;
	}
}
