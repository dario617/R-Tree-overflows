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
		memManager.insertNode(this.root);
		this.overflowMethod = o;
	}
	// FUNCION PARA DEBUGUEAR, HAY QUE SACARLA DESPUES
	public int rootSize(){
		return this.root.childRectangles.size();
	}

	// DEBUGGING FUNCTION
	public void printTree(){
		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(this.root);
		int counter = 1;
		while(!queue.isEmpty()){
			Node node = queue.poll();
			System.out.println("==============");
			System.out.printf("Numero de nodo: %d\n", counter++);
			System.out.printf("ID: %d, Parent: %d\n", node.myId, node.parent);
			System.out.printf("childIds: %d, childRects: %d\n", node.childIds.size(), node.childRectangles.size());
			System.out.printf("isLeaf: %s\n", node.isLeaf);
			System.out.println("MBR:");
			for(int j = 0; j < this.ndims; j++){
				System.out.printf("[%f\t%f]\n",node.coords[j][0], node.coords[j][1]);
			}
			System.out.println("==============");
			if(node.isLeaf){
				continue;
			}
			for(int i = 0; i < node.childIds.size(); i++){
				try {
					Node n = this.memManager.loadNode(node.childIds.get(i));
					queue.add(n);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	private Node createRoot(int dims) {
		float[][] coords = new float[2][dims];
		for (int i = 0; i < dims; i++) {
			coords[0][i] = (float) (-1.0 * Math.sqrt(Float.MAX_VALUE));
			// To escape overflow on max value
			coords[1][i] = (float) (2.0 * Math.sqrt(Float.MAX_VALUE));
		}
		return new Node(true, coords, -1);
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
		if(l.myId == this.root.myId){
			// La raiz se partio en dos partes
			if(ll != null){
				// Generamos una nueva ra�z
				this.root = createRoot(this.ndims);
				this.root.isLeaf=false;
				// Le damos una nueva ID a la raiz
				this.root.setMyID(memManager.getNewId());
				// Generamos una nueva ID para el nodo nuevo
				ll.setMyID(this.memManager.getNewId());
				// Referenciamos los nodos en la nueva raiz
				this.root.childRectangles.add(l.coords);
				this.root.childIds.add(l.myId);
				this.root.childRectangles.add(ll.coords);
				this.root.childIds.add(ll.myId);
				// Seteamos el padre de ambos nodos
				l.parent = this.root.myId;
				ll.parent = this.root.myId;
				// Actualizamos el padre de los hijos del nuevo nodo
				if(!ll.isLeaf) {
					for (Long f : ll.childIds) {
						Node child = memManager.loadNode(f);
						child.parent = ll.myId;
					}
				}
				// Actualizo el MBR de la raiz
				this.root.recalculateMBR();
				// Guardamos los nodos nuevos en el buffer
				memManager.insertNode(this.root);
				memManager.insertNode(ll);
				memManager.insertNode(l);
				return;
			}
			else{
				// No hubo split de la raiz, solo se actualiza su MBR
				l.recalculateMBR();
				memManager.insertNode(l);
				return;
			}
		}
		else{
			// Llamo al padre del nodo l
			if(debug) {
				System.out.println("hola");
				System.out.println(l);	
			}
			Node P = this.memManager.loadNode(l.parent);
			int lIndex = P.childIds.indexOf(l.myId);
			if(lIndex == -1) {
				System.err.println("NOOOOOOOOOOOOOOO");
			}
			if(ll != null) {
				// Hubo split de nodos, ambos vienen con su MBR ya calculado
				// Le damos un id al nodo nuevo
				ll.setMyID(this.memManager.getNewId());
				// Seteo el padre de los hijos del nuevo nodo
				if(!ll.isLeaf) {
					for (Long f : ll.childIds) {
						Node child = memManager.loadNode(f);
						child.parent = ll.myId;
					}
				}
				memManager.insertNode(ll);
				memManager.insertNode(l);
				P.childRectangles.set(lIndex, l.coords);
				P.childRectangles.add(ll.coords);
				P.childIds.add(ll.myId);
				P.recalculateMBR();
				memManager.insertNode(P);
				if (P.childIds.size() > this.M) {
					// El padre se llen�, hago split
					Node[] parentSplits = splitNode(P);
					this.adjustTree(parentSplits[0], parentSplits[1]);
				}
				return;
			}
			else{
				l.recalculateMBR();
				P.childRectangles.set(lIndex, l.coords);
				P.recalculateMBR();
				memManager.insertNode(l);
				memManager.insertNode(P);
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
		leafNode.childIds.add(new Long(-1)); //Agregamos una id -1 pues estamos en una hoja.
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
			if(debug) {
				System.out.printf("Tamaño de rectangulos del nodo %d\n", n.childRectangles.size());
			}
			for (int i = 0; i < n.childRectangles.size(); i++) {
				// Check if the dimensions fit inside
				if(debug) {
					System.out.print("Searching ");
					for(int j = 0; j<2; j++){				
						System.out.printf("[ %f %f ]\n", r[j][0], r[j][1]);
					}
					System.out.println("In Rect");
					for(int j = 0; j<2; j++){
						System.out.printf("[ %f %f ]\n", n.childRectangles.get(i)[j][0], n.childRectangles.get(i)[j][1]);
					}
				}
				if (nRectangle.overlaps(r, n.childRectangles.get(i))) {

					// If is leaf then it's not in memory
					// We should just return it as a valid response
					if (n.isLeaf) {
						if(debug) {
							System.out.println("found");
						}
						return true;
					}
					// Ask for node to memory manager
					try {
						Node c = this.memManager.loadNode(n.childIds.get(i));
						//System.out.println("ayuda");
						// Add at the bottom
						queue.add(c);
						if(debug) {
							System.out.println("Succesfully added to queue");	
						}
					} catch (Exception e) {
						// TODO: handle exception						
						System.out.println("Exception en search");
						e.printStackTrace();
						System.out.println("=============================================");
					}

				}
			}
		}
		if(debug) {
			System.out.println("Not Found");	
		}
		return false;
	}
	
	public boolean deleteRect(float[][] r) {
		return true;
	}
}
