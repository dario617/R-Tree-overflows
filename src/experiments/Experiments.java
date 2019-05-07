package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import rtree.RTree;
import rtree.RTree.OverflowMethod;
import util.MemoryManager;

public class Experiments {

	private static DatasetGenerator dg;
	private static long pageSize = 4096L;
	private static boolean DEBUG = false;
	private static long timeStamp;

	public static void deleteListFilesOnFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	deleteListFilesOnFolder(fileEntry);
	        }
	        fileEntry.delete();
	    }
	}
	
	/**
	 * Runs Insertion, Page used space and Search tests using random rectangles.
	 * Results are saved to secondary memory
	 * 
	 * @param exp      			number of rectangles = 2^exp
	 * @param overflow          heuristic to use on split
	 * @param maxBuffered       nodes to keep on memory
	 * @param nbRectangleSearch rectangles to search on RTree
	 */
	private static void runSynteticExperiment(long exp, RTree.OverflowMethod overflow, int maxBuffered) {
		try {
			// Log on unique file
			Logger logs = new Logger("./" + timeStamp + "-SynteticData-2^" + exp + "-" + overflow + ".txt",
					"SynteticData-2^" + exp, overflow.toString());

			long nbRectangles = (long)Math.pow(2, exp);
			
			RTree rtree = new RTree(29, 73, 2, overflow, maxBuffered);
			rtree.setDebug(DEBUG);
			ArrayList<float[][]> cachedRect = new ArrayList<float[][]>();

			logs.startTest("OverallTest");
			// ----- Insert test
			logs.startTest("InsertTest");
			for (long i = 0; i < nbRectangles; i++) {
				rtree.insertRect(dg.getNewRectangle());
			}
			logs.stopTest("InsertTest");

			// ----- Search test
			logs.startTest("Search");
			rtree.memManager.diskAccess = 0;
			// Negative results
			for (int i = 0; i < nbRectangles/10; i++) {
				rtree.search(dg.getNewRectangle());
			}
			logs.stopTest("Search");
			
			// ----- Get page average used space
			long totalSpaceUsed = 0L;
			long requiredSpace = pageSize*rtree.memManager.createdNodes; // Page size
			long fullCriteria = (long)(pageSize*0.9);
			long nbFullNodes = 0L; // Nodes close to pagesize
			long tmp;
			rtree.memManager.saveBuffer();
			logs.startTest("Statistics");
			for(long id = 1; id < rtree.memManager.createdNodes; id++) {
				tmp = rtree.memManager.getFileSizeAndDelete(id);
				if(tmp == 0) {
					requiredSpace-=pageSize; // Due to error on id innecessary increase
				}
				if(tmp >= fullCriteria) {nbFullNodes++;}
				totalSpaceUsed += tmp;
			}
			logs.logInfo("Statistics", "Total Data Size "+totalSpaceUsed+"Bytes or " + 
					MemoryManager.getFileSizeMegaBytes(totalSpaceUsed) + "MB");
			logs.logInfo("Statistics", "Total Data Required Size "+requiredSpace+"Bytes or "+ 
					MemoryManager.getFileSizeMegaBytes(requiredSpace) + "MB");
			logs.logInfo("Statistics", "Page use ratio "+ totalSpaceUsed*100.0/requiredSpace+"%");
			logs.logInfo("Statistics", "I/O ops"+rtree.memManager.diskAccess);
			logs.logInfo("Statistics", "Number of full nodes "+nbFullNodes);
			logs.stopTest("Statistics");

			logs.stopTest("OverallTest");
			
			logs.close();
		} catch (IOException e) {
			System.err.println("Failed to initiate logger");
			e.printStackTrace();	
		}
	}

	private static void synteticDataExperiment() {
		
		//int bufferSize = (int)Math.pow(2, 25);
		int bufferSize = 10;
		
		RTree.OverflowMethod[] om = { OverflowMethod.LINEAR, OverflowMethod.QUADRATIC };
		for (int e = 9; e < 26; e++) {
			for (int o = 0; o < om.length; o++) {
				System.out.println(OverflowMethod.values()[o] + " Doing 2^" + e);
				boolean running = true; 
				while(running) {
					try {
						runSynteticExperiment(e, om[o], bufferSize);
						running = false;
					}catch (Exception exc) {
						System.err.println("Error somewhere, cleaning up");
						exc.printStackTrace();
						deleteListFilesOnFolder(new File("./RTree/"));
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		
		Runtime.getRuntime().addShutdownHook( new Thread("app-shutdown-hook") {
			@Override 
			public void run() { 
				System.out.println("Tear down, deleting orphaned nodes");
				deleteListFilesOnFolder(new File("./RTree/"));
		    }
		});
		
		timeStamp = System.currentTimeMillis();
		// Instantiate experiment tools
		dg = new DatasetGenerator(1, 100, 2);
		// Start experiments
		synteticDataExperiment();
	}

}
