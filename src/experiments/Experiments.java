package experiments;

import java.io.IOException;
import java.util.ArrayList;

import rtree.RTree;
import rtree.RTree.OverflowMethod;

public class Experiments {

	private static DatasetGenerator dg;

	/**
	 * Runs Insertion, Page used space and Search tests using random rectangles.
	 * Results are saved to secondary memory
	 * 
	 * @param nbRectangles      to write in test
	 * @param overflow          heuristic to use on split
	 * @param maxBuffered       nodes to keep on memory
	 * @param nbRectangleSearch rectangles to search on RTree
	 */
	private static void runSynteticExperiment(long nbRectangles, RTree.OverflowMethod overflow, int maxBuffered,
			int nbRectangleSearch) {
		try {
			// Log on unique file
			Logger logs = new Logger("./SynteticData" + nbRectangles + "::" + System.currentTimeMillis() + ".txt",
					"SynteticData" + nbRectangles, overflow.toString());

			RTree rtree = new RTree(29, 73, 2, overflow, maxBuffered);
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
			long requiredSpace = 4096L*rtree.memManager.createdNodes; // Page size
			long nbFullNodes = 0L; // Nodes close to pagesize
			long tmp;
			logs.startTest("Statistics");
			for(long id = 0; id < rtree.memManager.createdNodes; id++) {
				tmp = rtree.memManager.getFileSizeAndDelete(id);
				if(tmp >= 4000L) {nbFullNodes++;}
				totalSpaceUsed += tmp;
			}
			logs.logInfo("Statistics", "Total Data Size"+totalSpaceUsed);
			logs.logInfo("Statistics", "Total Data Required Size"+requiredSpace);
			logs.logInfo("Statistics", "Page use ratio "+ totalSpaceUsed*100L/requiredSpace);
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
			for (int e = 9; e < 10; e++) {
				runSynteticExperiment((long) Math.pow(2, e), om[o], 400, 100);
			}
		}
	}

	public static void main(String[] args) {		
		// Instantiate experiment tools
		dg = new DatasetGenerator(1, 100, 2);
		// Start experiments
		synteticDataExperiment();
	}

}
