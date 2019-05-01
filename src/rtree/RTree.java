package rtree;

import java.util.LinkedList;

import rtree.Node;
import util.MemoryManager;
import util.nRectangle;

public class RTree {
	public enum OverflowMethod {
		LINEAR, QUADRATIC, GREENE, EXTRA_HEURISTIC
	}

	private int M;
	private int m;
	private int ndims;
	private Node root;
	private final OverflowMethod overflowMethod;
	private MemoryManager memManager;

	public RTree(int m, int M, int dims, OverflowMethod o) {
		assert (m <= M / 2);
		this.m = m;
		this.M = M;
		this.ndims = dims;
		this.root = createRoot(dims);
		this.overflowMethod = o;
	}

	private Node createRoot(int dims) {
		float[][] coords = new float[dims][2];
		for (int i = 0; i < dims; i++) {
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

	/**
	 * Search for a rectangle in the RTree and returns true if the exact rectangle
	 * is in a leaf.
	 * 
	 * @param r rectangle coordinates
	 * @return if the rectangle is in a leaf
	 */
	public boolean search(float[][] r) {

		// Search subtrees like a DFS
		if (!this.root.isLeaf) {
			LinkedList<Node> queue = new LinkedList<Node>();
			queue.add(this.root);

			while (!queue.isEmpty()) {
				Node n = queue.poll();
				for (int i = 0; i < n.childs.size(); i++) {
					// Check if the dimensions fit inside
					if (nRectangle.overlaps(r, n.childDims.get(i))) {

						// If is leaf then it's not in memory
						// We should just return it as a valid response
						if (n.isLeaf) {
							return true;
						}
						// Ask for node to memory manager
						try {
							Node c = this.memManager.loadNode(n.childs.get(i));
							// Add at the bottom
							queue.add(c);
						}catch (Exception e) {
							// TODO: handle exception
						}
						
					}
				}
			}
		}
		return false;
	}
	
	public boolean deleteRect(float[][] r) {
		return true;
	}
}
