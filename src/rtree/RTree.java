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
			int minorEnlargementIndex = checkEnlargement(r, N.childRectangles);
			//N = getNodeById(N.childIds[minorEnlargementIndex]);
		}
		return N;
	}

	public void insertRect(float[][] r) {
		Node leaf = this.chooseLeaf(r);
		return;
	}
	
	public boolean findRect(float[][] r) {
		return true;
	}
	
	public boolean deleteRect(float[][] r) {
		return true;
	}
}
