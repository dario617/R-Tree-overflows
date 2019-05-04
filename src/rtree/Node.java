package rtree;

import java.io.Serializable;
import java.util.ArrayList;

import util.nRectangle;

public class Node implements Serializable {
	private static StringBuilder sb = new StringBuilder();
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
	
	public Node(String s) {
		String sep = ",";
		String[] tokens = s.split(sep);
		
		this.isLeaf = tokens[0] == "1"? true : false;
		this.myId = Long.parseLong(tokens[1]);
		this.parent = Long.parseLong(tokens[2]);
		int numDims = Integer.parseInt(tokens[3]);
		
		this.coords = new float[numDims][2];
		for(int i = 0; i < numDims; i++) {
			coords[i][0] = Float.parseFloat(tokens[4+i*2]);
			coords[i][1] = Float.parseFloat(tokens[4+i*2+1]);
		}
		int baseOffset = 4+numDims*2;
		int numChilds = Integer.parseInt(tokens[baseOffset]);
		
		this.childIds = new ArrayList<Long>();
		this.childRectangles = new ArrayList<float[][]>();

		int count = 1;
		for(int c = 0; c < numChilds; c++) {
			this.childIds.add(Long.parseLong(tokens[baseOffset+count]));
			count++;
			float[][] ref = new float[numDims][2];
			for(int i = 0; i < numDims; i++) {
				ref[i][0] = Float.parseFloat(tokens[baseOffset+count++]);
				ref[i][1] = Float.parseFloat(tokens[baseOffset+count++]);
			}
			this.childRectangles.add(ref);
		}
	}
	
	public void addChildIds(ArrayList<Long> list) {
		this.childIds.addAll(list);
	}
	public void addChildRectangles(ArrayList<float[][]> list) {
		this.childRectangles.addAll(list);
	}
	public void recalculateMBR() {
		this.coords = nRectangle.MBR(childRectangles);
	}
	
	public void setMyID(Long newId) {
		this.myId = newId;
	}
	
	/**
	 * Write node as string in the following format:
	 * isLeaf,myId,parentId,nbDims,Coords ... (2*nbDims),
	 * nbChilds, per child Id and Coords (Id + 2*nbDims)
	 * @return node as file String 
	 */
	public String toFileString() {
		char sep = ',';
		float[][] ref;
		// Append if leaf
		if(isLeaf) {
			sb.append('1');
		}else {
			sb.append('0');
		}
		sb.append(sep);
		sb.append(myId); // Append id
		sb.append(sep);
		sb.append(parent); // Append parent id
		sb.append(sep);
		sb.append(coords.length); // Number dims
		sb.append(sep);
		for(int i = 0; i < coords.length; i++) {
			sb.append(coords[i][0]);
			sb.append(sep);
			sb.append(coords[i][1]);
			sb.append(sep);
		}
		sb.append(childIds.size());
		sb.append(sep);
		for(int c = 0; c < childIds.size(); c++) {
			sb.append(childIds.get(c));
			sb.append(sep);
			ref = childRectangles.get(c);
			for(int i = 0; i < coords.length; i++) {
				sb.append(ref[i][0]);
				sb.append(sep);
				sb.append(ref[i][1]);
				sb.append(sep);
			}
		}
		sb.append("ok\n");
		return sb.toString();
	} 
}
