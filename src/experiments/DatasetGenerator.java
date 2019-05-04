package experiments;

import java.util.Random;

public class DatasetGenerator {
	private float lowerBound;
	private float upperBound;
	private float diff;
	private int nbDims;
	private Random nbGenerator;
	
	/**
	 * 
	 * @param lowerBound
	 * @param upperBound
	 * @param dims
	 */
	public DatasetGenerator(float lowerBound, float upperBound, int dims) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.nbDims = dims;
		this.diff = upperBound-lowerBound; //Ensure that its computed once
		this.nbGenerator = new Random(System.currentTimeMillis());
	}
	
	/**
	 * Creates rectangles bounded by the generator's
	 * lower and upper bound.
	 * @return rectangle coordinates as a float[dims][2]
	 * 			(point and segment length)
	 */
	public float[][] getNewRectangle(){
		
		float[][] rect = new float[nbDims][2];
		float nextPoint;
		for(int i = 0; i < this.nbDims; i++) {
			rect[i][0] = nbGenerator.nextFloat()*diff + lowerBound;
			nextPoint = nbGenerator.nextFloat()*diff + lowerBound;
			// Check that they are not the same point
			while(nextPoint == rect[i][0]) {
				nextPoint = nbGenerator.nextFloat()*diff + lowerBound;
			}
			rect[i][1] = nextPoint - rect[i][0];
		}
		return rect;
	}
}
