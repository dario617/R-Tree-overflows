package rtree;

import java.util.ArrayList;
import java.util.Random;

import javafx.util.Pair;

public class LinearSplit {
	
	
	
	public static Node[] split(Node nodo, int m){
		ArrayList<float[][]> brother_rectangles = new ArrayList<float[][]>();
		ArrayList<Long> brother_ids = new ArrayList<Long>();
		Pair<float[][],Long>[] seeds = pickSeeds(nodo);	
		brother_rectangles.add(seeds[1].getKey());
		brother_ids.add(seeds[1].getValue());	
		nodo.childRectangles.remove(seeds[0].getKey());
		nodo.childRectangles.remove(seeds[1].getKey());
		nodo.childIds.remove(seeds[0].getValue());
		nodo.childIds.remove(seeds[1].getValue());		
		Random randGen = new Random();
		while(nodo.childRectangles.size()>m+1) {
			// **** ESTA ES LA LOGICA DEL PICKNEXT
			int randNum = randGen.nextInt(nodo.childRectangles.size());			
			float[][] rect = nodo.childRectangles.get(randNum);
			Long id = nodo.childIds.get(randNum);
			//
			brother_rectangles.add(rect);
			brother_ids.add(id);
			nodo.childRectangles.remove(randNum);
			nodo.childIds.remove(randNum);					
		}		
		nodo.childRectangles.add(seeds[0].getKey());
		nodo.childIds.add(seeds[0].getValue());
		nodo.recalculateMBR();			
		float[][] mtCoords = {{0,0},{0,0}}; 
		Node second = new Node(nodo.isLeaf,mtCoords, nodo.parent);
		second.addChildRectangles(brother_rectangles);
		second.addChildIds(brother_ids);
		second.recalculateMBR();
		Node[] output = {nodo, second};
		return output;
	}
	private static Pair<float[][], Long> pickNext(int size){
		//TODO: Move Logic Here
		Random randGen = new Random();
		return null;
	}
	
	private static Pair<float[][],Long>[] pickSeeds(Node nodo){
		//TODO: All
		return null;
	}
}
