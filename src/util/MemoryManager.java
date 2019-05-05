package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import rtree.Node;

public class MemoryManager {

	public long createdNodes;
	public int loadedNodes;
	private HashMap<Long, Node> bufferedNodes;
	private HashMap<Long, Long> nodeUpdated;
	// Tree Maps sort a HashMap on a descending key value
	private TreeMap<Long, Long> loadedOn; 
	private String nodeDir = "./Rtree/";
	private String fileExtension = ".node";
	private int maxBuffered;

	/**
	 * Create a Memory Manager to contain
	 * the given number of nodes
	 * @param maxBufferedNodes
	 */
	public MemoryManager(int maxBufferedNodes) {
		this.createdNodes = 1;
		this.loadedNodes = 0;
		this.maxBuffered = maxBufferedNodes;
		this.bufferedNodes = new HashMap<Long, Node>();
		this.nodeUpdated = new HashMap<Long, Long>();
		this.loadedOn = new TreeMap<Long, Long>();
	}

	/**
	 * Returns a valid ID for a new node
	 * @return
	 */
	public long getNewId() {
		return this.createdNodes++;
	}

	/**
	 * Inserts node to the buffer and removes a node 
	 * if there's not enough space
	 * @param n Node
	 */
	public void insertNode(Node n) {
		insertNode(n, false);
	}
	
	public void insertNode(Node n, boolean freeString) {
		if(bufferedNodes.containsKey(n.myId)){
			// Put node and remove previous timestamp
			this.bufferedNodes.put(n.myId, n);
			long usedTS = this.nodeUpdated.get(n.myId);
			this.loadedOn.remove(usedTS);
			// Get a new timestamp and update value
			long newTS = System.nanoTime();
			this.loadedOn.put(newTS, n.myId);
			this.nodeUpdated.put(n.myId,newTS);
		}else {
			if(loadedNodes == maxBuffered) {
				freeNode(freeString);
			}
			this.bufferedNodes.put(n.myId, n);
			long newTS = System.nanoTime();
			this.loadedOn.put(newTS, n.myId);
			this.nodeUpdated.put(n.myId,newTS);
			loadedNodes++;
		}
	}
	
	/**
	 * Saves node string format to secondary memory
	 * @param n Node to save
	 * @throws IOException
	 */
	public void saveNodeAsString(Node n) throws IOException{
		String s = n.toFileString();
		String filename = this.nodeDir + n.myId + this.fileExtension;
		BufferedWriter fos = new BufferedWriter(new FileWriter(filename));
		fos.write(s);
		fos.flush();
		fos.close();
	}

	/**
	 * Saves node to secondary memory
	 * @param n Node to save
	 * @param id to be used as a filename
	 * @throws IOException
	 */
	public void saveNode(Node n, long id) throws IOException {
		String filename = this.nodeDir + id + this.fileExtension;
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(n);
		oos.close();
	}

	/**
	 * Remove the oldest node from buffer and save it to memory
	 * @param useString to save instead of binary
	 */
	private void freeNode(boolean useString) {
		try {
			Entry<Long, Long> oldestId = loadedOn.pollFirstEntry();
			
			long id = oldestId.getValue();
			if(useString) {
				saveNodeAsString(bufferedNodes.get(id));
			}else {
				saveNode(bufferedNodes.get(id), id);
			}
			// Delete internal references
			bufferedNodes.remove(id);
			nodeUpdated.remove(id);
			// Reduce count
			loadedNodes--;
		} catch (Exception iOException) {
			// TODO: handle exception
			System.err.println("Failed to save the oldest node");
		}
	}

	/**
	 * Loads node from memory or buffer
	 * @param id of the Node
	 * @return Node recovered from memory or buffer
	 * @throws ClassNotFoundException
	 * @throws IOException in case the caller asks for an unknown node 
	 */
	public Node loadNode(long id) throws ClassNotFoundException, IOException {
		return this.loadNode(id, false);
	}
	
	/**
	 * Load node from memory or buffer
	 * @param id of the Node
	 * @param readString if the node is saved as a string
	 * @return Node recovered from memory or buffer
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Node loadNode(long id, boolean readString) throws ClassNotFoundException, IOException {
		if(id == 0L) {
			System.err.println(" Hola");
		}
		// Check if in buffer
		if (this.bufferedNodes.containsKey(id)) {
			return this.bufferedNodes.get(id);
		// Look for it in secondary memory
		} else {

			Node n;
			if(readString) {
				n = loadStringFromMemory(id);
			}else {
				n = loadFromMemory(id);
			}
			insertNode(n, readString);
			return n;
		}
	}
	
	private Node loadStringFromMemory(long id) throws IOException {
		String filename = this.nodeDir + id + this.fileExtension;
		BufferedReader bf = new BufferedReader(new FileReader(filename));
		String read = bf.readLine();
		Node n = new Node(read);
		return n;
	}

	private Node loadFromMemory(long id) throws IOException, ClassNotFoundException {
		String filename = this.nodeDir + id + this.fileExtension;
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Node n = (Node) ois.readObject();
		if(n == null) {
			System.err.println("WTF OMG WTF");
		}
		fis.close();
		ois.close();
		return n;
	}

	/**
	 * Delete node from secondary memory.
	 * If Id is not found no error is thrown
	 * @param id of the node to delete
	 */
	public void deleteNode(long id) {
		String filename = this.nodeDir + id + this.fileExtension;
		try {
			File f = new File(filename);
			if(!f.delete()) {
				System.err.println("Coudn't delete file "+id);
			}
		}catch (Exception e) {
			System.err.println("File not found");
		}
	}
	
	/**
	 * Get File size and delete it.
	 * Used for statistics and clean up.
	 * @param id
	 * @return number of bytes
	 */
	public long getFileSizeAndDelete(long id) {
		String filename = this.nodeDir + id + this.fileExtension;
		try {
			File f = new File(filename);
			long size = f.length();
			if(!f.delete()) {
				System.err.println("Couldn't delete file "+id);
			}
			return size;
		}catch (Exception e) {
			System.err.println("File not found");
			return 0L;
		}
	}
	
	/**
	 * Print file size in a human readable way
	 * @param id
	 */
	public void printFileSize(long id) {
		String filename = this.nodeDir + id + this.fileExtension;
		try {
			File f = new File(filename);
			System.out.println(getFileSizeBytes(f));
			System.out.println(getFileSizeKiloBytes(f));
			System.out.println(getFileSizeMegaBytes(f));
		}catch (Exception e) {
			System.err.println("File not found");
		}
	}
	
	private static String getFileSizeMegaBytes(File file) {
		return (double) file.length() / (1024 * 1024) + " mb";
	}
	
	private static String getFileSizeKiloBytes(File file) {
		return (double) file.length() / 1024 + "  kb";
	}

	private static String getFileSizeBytes(File file) {
		return file.length() + " bytes";
	}
}
