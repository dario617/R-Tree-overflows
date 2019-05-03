package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import rtree.Node;

public class MemoryManager {

	private long createdNodes;
	public int loadedNodes;
	private HashMap<Long, Node> bufferedNodes;
	private HashMap<Long, Boolean> nodeUpdated;
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
		this.createdNodes = 0;
		this.loadedNodes = 0;
		this.maxBuffered = maxBufferedNodes;
		this.bufferedNodes = new HashMap<Long, Node>();
		this.nodeUpdated = new HashMap<Long, Boolean>();
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
		if(loadedNodes == maxBuffered) {
			freeNode();
		}
		this.bufferedNodes.put(n.myId, n);
		this.nodeUpdated.put(n.myId, false);
		this.loadedOn.put(System.nanoTime(), n.myId);
		loadedNodes++;
	}
	
	public void saveNode(Node n) throws IOException {
		saveNode(n, this.createdNodes);
		this.createdNodes++;
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
		oos.flush();
		oos.close();
	}

	/**
	 * Remove the oldest node from buffer and save it to memory
	 */
	private void freeNode() {
		try {
			Entry<Long, Long> oldestId = loadedOn.pollFirstEntry();
			long id = oldestId.getValue();
			saveNode(bufferedNodes.get(id), id);
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

	public void updatedNode(long id) {
		this.nodeUpdated.put(id, true);
	}

	public boolean nodeWasModified(long id) {
		return this.nodeUpdated.get(id);
	}

	/**
	 * Loads node from memory or buffer
	 * @param id of the Node
	 * @return Node recovered from memory or buffer
	 * @throws ClassNotFoundException
	 * @throws IOException in case the caller asks for an unknown node 
	 */
	public Node loadNode(long id) throws ClassNotFoundException, IOException {
		// Check if in buffer
		if (this.bufferedNodes.containsKey(id)) {
			return this.bufferedNodes.get(id);
			// Look for it in secondary memory
		} else {

			String filename = this.nodeDir + id + this.fileExtension;
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Node n = (Node) ois.readObject();
			ois.close();

			// Put node in buffer
			// If buffer is full
			if (loadedNodes == maxBuffered) {
				// Select a random not modified node and delete it
				freeNode();
			}

			this.bufferedNodes.put(id, n);
			this.nodeUpdated.put(id, false);
			this.loadedOn.put(System.nanoTime(), id);
			loadedNodes++;

			return n;
		}
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
}
