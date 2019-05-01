package util;

public class nRectangle {
	float sum_epsilon;
	float prod_epsilon;
	private nRectangle() {
		super();
		sum_epsilon = 0.001f;
		prod_epsilon = 1.001f;
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
		for(float[][] nRect : nRects) {
			
		}
		return null;
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
