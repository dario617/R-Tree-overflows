package util;

import java.util.ArrayList;

public class nRectangle {
	float sum_epsilon;
	float prod_epsilon;
	static float min_value;
	static float max_value;
	
	private nRectangle() {
		super();
		sum_epsilon = 0.001f;
		prod_epsilon = 1.001f;
		min_value = -1.0f * (float) Float.MAX_VALUE;
		max_value = (float) Float.MAX_VALUE;
	}
	
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
	
	public static float[][] MBR(float[][]... nRects){
		float max_coord = min_value;
		float min_coord = max_value;
		
		//generate initial MBR with full length sides
		int mbr_len = nRects[0][0].length;
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
	
	public static float area(float[][] nRect) {
		float accumulator = 1;
		for(int i=0; i<nRect[1].length; i++) {
			accumulator *= Math.abs(nRect[i][1]);
		}
		return accumulator;
	}
	
	public static float checkEnlargement(){
		return 0;
	}
	
}
