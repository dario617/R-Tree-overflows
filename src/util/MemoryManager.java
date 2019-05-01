package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import rtree.Node;

public class MemoryManager {

	private long createdNodes;
	private int loadedNodes;
	private HashMap<Long, Node> bufferedNodes;
	private HashMap<Long, Boolean> nodeUpdated;
	private String nodeDir = "./Rtree/";
	private String fileExtension = ".node";
	private int maxBuffered;

	public MemoryManager(int maxBufferedNodes) {
		this.createdNodes = 0;
		this.loadedNodes = 0;
		this.maxBuffered = maxBufferedNodes;
	}

	public long getNewId() {
		return this.createdNodes++;
	}
	
	public void saveNode(Node n) throws IOException {
		saveNode(n, this.createdNodes);
		this.createdNodes++;
	}

	public void saveNode(Node n, long id) throws IOException {
		String filename = id + this.fileExtension;
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(n);
		oos.flush();
		oos.close();
	}
	
	public void updatedNode(long id){
		this.nodeUpdated.put(id, true);
	}
	
	public boolean nodeWasModified(long id) {
		return this.nodeUpdated.get(id);
	}

	public Node loadNode(long id) throws ClassNotFoundException, IOException {
		// Check if in buffer
		if(this.bufferedNodes.containsKey(id)) {
			return this.bufferedNodes.get(id);
		// Look for it in secondary memory
		}else {
			
			String filename = id+this.fileExtension;
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Node n = (Node) ois.readObject();
			ois.close();
			
			// Put node in buffer
			// If buffer is full
			if(loadedNodes == maxBuffered) {
				// Select a random not modified node and delete it
				
			}else {
				loadedNodes++;
				this.bufferedNodes.put(id,n);
			}
			
			return n;
		}
	}
	
	
}
