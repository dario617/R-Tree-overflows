package rtree;
import rtree.Node;
import util.nRectangle;

public class RTree {
	public enum OverflowMethod {LINEAR, QUADRATIC, GREENE, EXTRA_HEURISTIC}
	private int M;
	private int m;
	private int ndims;
	private Node root;
	private final OverflowMethod overflowMethod;
	
	public RTree(int m, int M, int dims, OverflowMethod o) {
		assert (m <= M/2);
		this.m = m;
		this.M = M;
		this.ndims = dims;
		this.root = createRoot(dims);
		this.overflowMethod = o;
	}
	
	private Node createRoot(int dims) {
		float[][] coords = new float[dims][2];
		for(int i = 0; i < dims; i++) {
			coords[i][0] = Float.MIN_VALUE;
			coords[i][1] = Float.MAX_VALUE;
		}
		return new Node(true, coords, M);
	}

	private Node chooseLeaf(float[][] r){
		Node N = this.root;
		while(!N.isLeaf){
			int minorEnlargementIndex = nRectangle.checkEnlargement(r, N.childRectangles);
			//N = getNodeById(N.childIds[minorEnlargementIndex]);
		}
		return N;
	}

	private void adjustTree(Node l, Node ll){
		return;
	}

	private Node[] splitNode(Node n){
		Node[] as = {null, null};
		return as;
	}

	public void insertRect(float[][] r) {
		Node leaf = this.chooseLeaf(r);
		leaf.childRectangles.add(r);
		leaf.childIds.add((long)-1);
		if(leaf.childIds.size() > this.M){
			Node[] splitNodes = splitNode(leaf);
			this.adjustTree(splitNodes[0], splitNodes[1]);
		}
		else{
			adjustTree(leaf, null);
		}
	}
	
	public boolean findRect(float[][] r) {
		return true;
	}
	
	public boolean deleteRect(float[][] r) {
		return true;
	}
}
