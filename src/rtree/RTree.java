package rtree;

import java.io.IOException;
import java.util.LinkedList;

import rtree.Node;
import util.MemoryManager;
import util.nRectangle;
import rtree.LinearSplit;
public class RTree {
	public enum OverflowMethod {
		LINEAR, QUADRATIC, GREENE, EXTRA_HEURISTIC
	}

	private int M;
	private int m;
	private int ndims;
	private Node root;
	private final OverflowMethod overflowMethod;
	public MemoryManager memManager;
	private boolean debug = true;

	public RTree(int m, int M, int dims, OverflowMethod o, int maxbuffered) {
		assert (m <= M / 2);
		this.m = m;
		this.M = M;
		this.ndims = dims;
		this.root = createRoot(dims);
		this.memManager = new MemoryManager(maxbuffered);
		this.root.setMyID(memManager.getNewId());
		this.overflowMethod = o;
		
		this.memManager.insertNode(root);
	}

	// DEBUGGING FUNCTION HAY QUE BORRARLA
	public int rootSize(){
		return this.root.childRectangles.size();
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	private Node createRoot(int dims) {
		float[][] coords = new float[dims][2];
		for (int i = 0; i < dims; i++) {
			coords[i][0] = (float) (-1.0 * Math.sqrt(Float.MAX_VALUE));
			// To escape overflow on max value
			coords[i][1] = (float) (2.0 * Math.sqrt(Float.MAX_VALUE));
		}
		return new Node(true, coords, 0);
	}

	/**
	 *
	 * @param r rectangle to insert into leaf
	 * @return Leaf Node where to insert rectangle.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Node chooseLeaf(float[][] r) throws IOException, ClassNotFoundException {
		Node N = this.root;
		assert(N != null) : "Root is null!";
		while(!N.isLeaf){
			assert(N != null) : "Null node was caught in the search!";
			int minorIndex = nRectangle.checkEnlargement(r, N.childRectangles);
			N = this.memManager.loadNode(N.childIds.get(minorIndex));
		}
		return N;
	}

	/**
	 *
	 * @param l
	 * @param ll
	 */
	private void adjustTree(Node l, Node ll) throws IOException, ClassNotFoundException {
		assert(l!=null) : "Null pointer to first node to adjust!";
		if(l == this.root){
			// La ra�z se parti� en dos partes
			if(ll != null){
				// Generamos una nueva ra�z
				this.root = createRoot(this.ndims);
				this.root.setMyID(l.myId);
				this.root.isLeaf = false;
				// Generamos nuevas IDs para los nodos nuevos
				l.setMyID(this.memManager.getNewId());
				ll.setMyID(this.memManager.getNewId());
				// Referenciamos los nuevos nodos en la nueva ra�z
				this.root.childRectangles.add(l.coords);
				this.root.childIds.add(l.myId);
				this.root.childRectangles.add(ll.coords);
				this.root.childIds.add(ll.myId);
				// Seteamos el padre de ambos nodos
				l.parent = this.root.myId;
				ll.parent = this.root.myId;
				// Guardamos los nodos nuevos en el buffer
				memManager.insertNode(l);
				memManager.insertNode(ll);
				memManager.insertNode(root); // Tambien es nuevo
				// Actualizo el MBR de la ra�z
				this.root.recalculateMBR();
				return;

			}
			else{
				// No hubo split de la ra�z, solo se actualiza su MBR
				l.recalculateMBR();
				return;
			}
		}
		else{
			// Llamo al padre del nodo l
			if(debug) {
				System.out.println("hola");
				System.out.println(l);	
			}
			memManager.insertNode(l);
			Node P;
			if(l.parent == 0) {
				P = this.root;
			}else {
				P = this.memManager.loadNode(l.parent);	
			}
			int lIndex = P.childIds.indexOf(l.myId);
			if(ll != null) {
				// Hubo split de nodos, ambos vienen con su MBR ya calculado
				P.childRectangles.set(lIndex, l.coords);
				ll.setMyID(this.memManager.getNewId());
				memManager.insertNode(ll);
				P.childRectangles.add(ll.coords);
				P.childIds.add(ll.myId);
				if (P.childIds.size() > this.M) {
					// El padre se llen�, hago split
					Node[] parentSplits = splitNode(P);
					this.adjustTree(parentSplits[0], parentSplits[1]);
				}
			}
			else{
				l.recalculateMBR();
				P.childRectangles.set(lIndex, l.coords);
				P.recalculateMBR();
				return;
			}
		}

	}

	/**
	 * Splits the given node using quadratic or linear split
	 * @param n
	 * @return
	 */
	private Node[] splitNode(Node n){
		if(this.overflowMethod.equals(OverflowMethod.LINEAR)) {
			return LinearSplit.split(n, this.m);
		} else {
			return QuadraticSplit.split(n, this.m);
		}		
	}

	/**
	 * Insert a rectangle in the RTree
	 * @param r rectangle coordinates
	 */
	public void insertRect(float[][] r) {
		if(debug) {
			System.out.print("inserting rect [[");
			System.out.print(r[0][0]);
			System.out.print(" , ");
			System.out.print(r[0][1]);
			System.out.print(" ],[ ");
			System.out.print(r[1][0]);
			System.out.print(" , ");
			System.out.print(r[1][1]);
			System.out.println(" ]]");			
		}

		Node leafNode = null;
		try {
			leafNode = this.chooseLeaf(r);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error in insertRect");
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error in insertRect");
			return;
		}
		leafNode.childRectangles.add(r);
		leafNode.childIds.add((long)-1); //Agregamos una id -1 pues estamos en una hoja.
		//Si el nodo sobrepaso los M registros, hay que hacer split
		if(leafNode.childIds.size() > this.M){
			Node[] splitNodes = splitNode(leafNode);
			try {
				this.adjustTree(splitNodes[0], splitNodes[1]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		else{
			try {
				this.adjustTree(leafNode, null);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Search for a rectangle in the RTree and returns true if the exact rectangle
	 * is in a leaf.
	 * @param r rectangle coordinates
	 * @return if the rectangle is in a leaf
	 */
	public boolean search(float[][] r) {

		// Search subtrees like a DFS
		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(this.root);

		while (!queue.isEmpty()) {
			Node n = queue.poll();
			for (int i = 0; i < n.childIds.size(); i++) {
				// Check if the dimensions fit inside
				if (nRectangle.overlaps(r, n.childRectangles.get(i))) {

					// If is leaf then it's not in memory
					// We should just return it as a valid response
					if (n.isLeaf) {
						return true;
					}
					// Ask for node to memory manager
					try {
						Node c = this.memManager.loadNode(n.childIds.get(i));
						// Add at the bottom
						queue.add(c);
					}catch (Exception e) {
						// TODO: handle exception
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
