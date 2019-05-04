package util;

import java.util.ArrayList;

public class nRectangle {
	static final float sum_epsilon = 0.001f;
	static final float min_value = -1.0f * (float) Float.MAX_VALUE;
	static final float max_value = (float) Float.MAX_VALUE;

	/*
	 * overlaps: Returns true if two rectangles overlap, false otherwise.
	 * 
	 * */
	public static boolean overlaps(float[][] nRect1, float[][] nRect2) {
		boolean overlaps = true;
		for(int i = 0; i<nRect1[0].length; i++) {
			float lower_bound = Math.min(nRect1[0][i], nRect1[0][i] + nRect1[1][i]);
			float upper_bound = Math.max(nRect1[0][i], nRect1[0][i] + nRect1[1][i]);
			float lower_toCompare = Math.min(nRect2[0][i], nRect2[0][i] + nRect2[1][i]);
			float upper_toCompare = Math.max(nRect2[0][i], nRect2[0][i] + nRect2[1][i]);
			
			boolean axis_overlaps = (lower_bound <= lower_toCompare && upper_bound >= lower_toCompare)
					|| (upper_bound >= upper_toCompare && lower_bound <= upper_toCompare);
					
			overlaps = overlaps && axis_overlaps;
			if(!overlaps) break;
		}
		return overlaps;
	}
	
	/*
	 * returns the Minimum Bounding (n)Rectangle of a set of (n)Rectangles as a float array.
	 * 
	 */
	
	public static float[][] MBR(float[][] nRect1, float[][] nRect2){
		ArrayList<float[][]> twoRects = new ArrayList<float[][]>();
		twoRects.add(nRect1);
		twoRects.add(nRect2);
		return MBR(twoRects);
	}
	public static float[][] MBR(ArrayList<float[][]> nRects){
		float max_coord = min_value;
		float min_coord = max_value;
		
		//generate initial MBR with full length sides
		int mbr_len = nRects.get(0)[0].length;
		float[][] coords = new float[2][mbr_len];
		for(int i = 0; i<mbr_len; i++) {
			coords[0][i] = min_coord;
			coords[1][i] = max_coord;
		}
		
		for(float[][] nRect : nRects) {
			for(int i = 0; i<nRect[0].length; i++) {
				float lower_bound = Math.min(nRect[0][i], nRect[0][i] + nRect[1][i]);
				float upper_bound = Math.max(nRect[0][i], nRect[0][i] + nRect[1][i]);
				coords[0][i] = Math.min(lower_bound, coords[0][i]);				
				coords[1][i] = Math.max(upper_bound, coords[1][i]);
				
			}
		}
		//transform into [coord, dim] system:
		for(int i = 0; i<coords[0].length; i++) {
			coords[1][i] -= coords[0][i];
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

	public static float enlargementArea(float[][] rect, ArrayList<float[][]> group){
		float originalArea = area(MBR(group));
		group.add(rect);
		float newArea = area(MBR(group));
		group.remove(rect);
		return newArea - originalArea;
	}
}
