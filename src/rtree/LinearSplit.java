package rtree;

import java.util.ArrayList;
import java.util.Random;

import javafx.util.Pair;

public class LinearSplit {
	
	
	
	public static Node[] split(Node nodo, int m){
		ArrayList<float[][]> brother_rectangles = new ArrayList<float[][]>();
		ArrayList<Long> brother_ids = new ArrayList<Long>();
		ArrayList<Pair<float[][],Long>> seeds = pickSeeds(nodo);	
		brother_rectangles.add(seeds.get(1).getKey());
		brother_ids.add(seeds.get(1).getValue());	
		nodo.childRectangles.remove(seeds.get(0).getKey());
		nodo.childRectangles.remove(seeds.get(1).getKey());
		nodo.childIds.remove(seeds.get(0).getValue());
		nodo.childIds.remove(seeds.get(1).getValue());		
		while(nodo.childRectangles.size()>m+1) {
			Pair<float[][], Long> siguiente = pickNext(nodo);
			brother_rectangles.add(siguiente.getKey());
			brother_ids.add(siguiente.getValue());
			nodo.childRectangles.remove(siguiente.getKey());
			nodo.childIds.remove(siguiente.getValue());					
		}		
		nodo.childRectangles.add(seeds.get(0).getKey());
		nodo.childIds.add(seeds.get(0).getValue());
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
		int randNum = randGen.nextInt(nodo.childRectangles.size()-1);			
		float[][] rect = nodo.childRectangles.get(randNum);
		Long id = nodo.childIds.get(randNum);
		return new Pair<float[][], Long>(rect, id);
	}
	
	public static ArrayList<Pair<float[][],Long>> pickSeeds(Node nodo){
		int dimension = nodo.childRectangles.get(0)[0].length;
		//candidates are pairs with highest coeficient inner_distance/width.
		float[][] candidateRect1 = {};
		float[][] candidateRect2 = {};
		Long candidateId1 = new Long(0);
		Long candidateId2 = new Long(0);
		float best_coefficient = 0f;
		//in Each Dim
		for(int i = 0; i<dimension;i++) {
			//distance calculation.
			float LHS = (float) Float.MAX_VALUE;
			float[][] LHS_rect = {};
			Long LHS_childId = new Long(0);
			float HLS = -1.0f * (float) Float.MAX_VALUE;
			float[][] HLS_rect = {};
			Long HLS_childId = new Long(0);
			//width calculation;
			float lowest_side = (float) Float.MAX_VALUE;
			float highest_side = -1.0f * (float) Float.MAX_VALUE;
			int counter = 0;
			for(float[][] rect : nodo.childRectangles) {
				float low_side = Math.min(rect[0][i], rect[1][i]+rect[0][i]);
				float high_side = Math.max(rect[0][i], rect[1][i]+rect[0][i]);
				if(low_side > HLS) {
					HLS_rect = rect;
					HLS = low_side;
					HLS_childId = nodo.childIds.get(counter);
					
				}
				if(high_side < LHS) {
					LHS_rect = rect;
					LHS = high_side;
					LHS_childId = nodo.childIds.get(counter);
				}
				
				lowest_side = Math.min(lowest_side, low_side);
				highest_side = Math.max(highest_side, high_side);	
				counter++;
			}
			if(Math.abs(HLS-LHS)/Math.abs(lowest_side-highest_side) > best_coefficient ) {
				candidateRect1 = LHS_rect;
				candidateRect2 = HLS_rect;
				candidateId1 = LHS_childId;
				candidateId2 = HLS_childId;
				best_coefficient = Math.abs(HLS-LHS)/Math.abs(lowest_side-highest_side);
			}
		}
		ArrayList<Pair<float[][], Long>> candidates = new ArrayList<Pair<float[][], Long>>();
		candidates.add(new Pair<float[][], Long>(candidateRect1, candidateId1));
		candidates.add(new Pair<float[][], Long>(candidateRect2, candidateId2));
		return candidates;
	}
}
