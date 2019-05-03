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
		while(nodo.childRectangles.size()>m+1) {
			Pair<float[][], Long> siguiente = pickNext(nodo);
			brother_rectangles.add(siguiente.getKey());
			brother_ids.add(siguiente.getValue());
			nodo.childRectangles.remove(siguiente.getKey());
			nodo.childIds.remove(siguiente.getValue());					
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
	private static Pair<float[][], Long> pickNext(Node nodo){
		
		Random randGen = new Random(); 
		int randNum = randGen.nextInt(nodo.childRectangles.size());			
		float[][] rect = nodo.childRectangles.get(randNum);
		Long id = nodo.childIds.get(randNum);
		return new Pair<float[][], Long>(rect, id);
	}
	
	private static Pair<float[][],Long>[] pickSeeds(Node nodo){
		for(float[][] rect : nodo.childRectangles) {
			
		}
		return null;
	}
}
