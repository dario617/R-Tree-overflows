package experiments;

import java.io.IOException;
import java.util.ArrayList;

import rtree.RTree;
import rtree.RTree.OverflowMethod;

public class Experiments {

	private static DatasetGenerator dg;
	private static long pageSize = 4096L;
	private static boolean DEBUG = false;
	private static long timeStamp;

	/**
	 * Runs Insertion, Page used space and Search tests using random rectangles.
	 * Results are saved to secondary memory
	 * 
	 * @param exp      			number of rectangles = 2^exp
	 * @param overflow          heuristic to use on split
	 * @param maxBuffered       nodes to keep on memory
	 * @param nbRectangleSearch rectangles to search on RTree
	 */
	private static void runSynteticExperiment(long exp, RTree.OverflowMethod overflow, int maxBuffered,
			int nbRectangleSearch) {
		try {
			
			
			// Log on unique file
			Logger logs = new Logger("./" + timeStamp + "SynteticData-2^" + exp + ".txt",
					"SynteticData-2^" + exp, overflow.toString());

			long nbRectangles = (long)Math.pow(2, exp);
			
			RTree rtree = new RTree(29, 73, 2, overflow, maxBuffered);
			rtree.setDebug(DEBUG);
			ArrayList<float[][]> cachedRect = new ArrayList<float[][]>();

			// ----- Insert test
			logs.startTest("InsertTest");
			for (long i = 0; i < (nbRectangles - (long) (nbRectangleSearch)); i++) {
				rtree.insertRect(dg.getNewRectangle());
			}
			// Insert Test - positive search results
			for (long i = 0; i < nbRectangleSearch; i++) {
				float[][] ref = dg.getNewRectangle();
				rtree.insertRect(ref);
				cachedRect.add(ref);
			}
			logs.stopTest("InsertTest");

			// ----- Search test
			logs.startTest("Search");
			// Negative results
			logs.startTest("NegativeResults");
			for (int i = 0; i < nbRectangleSearch; i++) {
				rtree.search(dg.getNewRectangle());
			}
			logs.stopTest("NegativeResults");

			// Positive results
			logs.startTest("PositiveResults");
			for (int i = 0; i < nbRectangleSearch; i++) {
				rtree.search(cachedRect.get(i));
			}
			logs.stopTest("PositiveResults");
			logs.stopTest("Search");
			
			// ----- Get page average used space
			long totalSpaceUsed = 0L;
			long requiredSpace = pageSize*rtree.memManager.createdNodes; // Page size
			long fullCriteria = (long)(pageSize*0.9);
			long nbFullNodes = 0L; // Nodes close to pagesize
			long tmp;
			logs.startTest("Statistics");
			for(long id = 0; id < rtree.memManager.createdNodes; id++) {
				tmp = rtree.memManager.getFileSizeAndDelete(id);
				if(tmp >= fullCriteria) {nbFullNodes++;}
				totalSpaceUsed += tmp;
			}
			logs.logInfo("Statistics", "Total Data Size "+totalSpaceUsed+"Bytes");
			logs.logInfo("Statistics", "Total Data Required Size "+requiredSpace+"Bytes");
			logs.logInfo("Statistics", "Page use ratio "+ totalSpaceUsed*100.0/requiredSpace+"%");
			logs.logInfo("Statistics", "Number of full nodes "+nbFullNodes);
			logs.stopTest("Statistics");

			logs.close();
		} catch (IOException e) {
			System.err.println("Failed to initiate logger");
			e.printStackTrace();
		}
	}

	private static void synteticDataExperiment() {
		RTree.OverflowMethod[] om = { OverflowMethod.LINEAR, OverflowMethod.QUADRATIC };
		for (int o = 0; o < om.length; o++) {
			System.out.print("Inserting with Method ");
			System.out.println(OverflowMethod.values()[o]);
			for (int e = 9; e < 29; e++) {
				runSynteticExperiment(e, om[o], 10, 100);
			}
		}
	}

	public static void main(String[] args) {		
		timeStamp = System.currentTimeMillis();
		// Instantiate experiment tools
		dg = new DatasetGenerator(1, 100, 2);
		// Start experiments
		synteticDataExperiment();
	}

}
