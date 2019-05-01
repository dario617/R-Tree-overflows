package util;

import java.util.ArrayList;

public class nRectangle {
	static float sum_epsilon;
	static float prod_epsilon;
	static float min_value;
	static float max_value;
	
	private nRectangle() {
		super();
		sum_epsilon = 0.001f;
		prod_epsilon = 1.001f;
		min_value = -1.0f * (float) Float.MAX_VALUE;
		max_value = (float) Float.MAX_VALUE;
	}
	
	/*
	 * overlaps: Returns true if two rectangles overlap, false otherwise.
	 * 
	 * */
	public static boolean overlaps(float[][] nRect1, float[][] nRect2) {
		boolean overlaps = false;
		for(int i = 0; i<nRect1[0].length; i++) {
			float lower_bound = Math.min(nRect1[i][0], nRect1[i][0] + nRect1[i][1]);
			float upper_bound = Math.max(nRect1[i][0], nRect1[i][0] + nRect1[i][1]);
			float lower_toCompare = Math.min(nRect2[i][0], nRect2[i][0] + nRect2[i][1]);
			float upper_toCompare = Math.max(nRect2[i][0], nRect2[i][0] + nRect2[i][1]);
			
			overlaps = lower_bound <= lower_toCompare || lower_bound <= upper_toCompare
					|| upper_bound >= lower_toCompare || upper_bound >= upper_toCompare;
					
			if(overlaps) break;
		}
		return overlaps;
	}
	
	/*
	 * returns the Minimum Bounding (n)Rectangle of a set of (n)Rectangles as a float array.
	 * 
	 */
	
	public static float[][] MBR(float[][] nRect1, float[][] nRect2){
		ArrayList<float[][]> twoRects = new ArrayList();
		twoRects.add(nRect1);
		twoRects.add(nRect2);
		return MBR(twoRects);
	}
	public static float[][] MBR(ArrayList<float[][]> nRects){
		float max_coord = min_value;
		float min_coord = max_value;
		
		//generate initial MBR with full length sides
		int mbr_len = nRects.get(0)[0].length;
		float[][] coords = new float[mbr_len][2];
		for(int i = 0; i<mbr_len; i++) {
			coords[i][0] = min_coord;
			coords[i][1] = max_coord;
		}
		
		for(float[][] nRect : nRects) {
			for(int i = 0; i<nRect[0].length; i++) {
				float lower_bound = Math.min(nRect[i][0], nRect[i][0] + nRect[i][1]);
				float upper_bound = Math.max(nRect[i][0], nRect[i][0] + nRect[i][1]);
				coords[i][0] = Math.min(lower_bound, coords[i][0]);				
				coords[i][1] = Math.max(upper_bound, coords[i][1]);
				
			}
		}
		//transform into [coord, dim] system:
		for(int i = 0; i<coords[0].length; i++) {
			coords[i][1] -= coords[i][0];
		}
		return coords;
	}
	
	/*
	 * returns the area of a rectangle.
	 */
	public static float area(float[][] nRect) {
		float accumulator = 1;
		for(int i=0; i<nRect[1].length; i++) {
			accumulator *= Math.abs(nRect[i][1]);
		}
		return accumulator;
	}
	
	/* 
	 * Return the index of the best rectangle based in enlargement.
	 * 
	 */
	public static int checkEnlargement(float[][] newRect, ArrayList<float[][]> nRects){
		int actual_index = 0;
		float best_area = (float) Float.MAX_VALUE; //for tie checking.
		float best_enlargement = (float) Float.MAX_VALUE;
		int saved_index = 0;
		for(float[][] nRect : nRects) {
			float[][] mbr = MBR(newRect, nRect);
			float enlargement = area(mbr)-area(nRect);
			if(Math.abs(enlargement - best_enlargement) < sum_epsilon ) { //enlargements son "iguales"
				//Check which area is lower.
				if(area(nRect)<best_area) {
					saved_index = actual_index;
					best_enlargement = enlargement;
				} else {
					//the other rectangle is the best rectangle.
				}
			} else if(enlargement<best_enlargement) {
				saved_index = actual_index;
				best_enlargement = enlargement;
			} else {
				//Rectangle is not good, do nothing.
			}
			actual_index++;
		}		
		
		return saved_index;
	}
	
}
