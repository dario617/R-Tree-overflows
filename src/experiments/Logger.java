package experiments;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Logger {

	private String outfile;
	private String testName;
	private HashMap<String, Long> testTimes;
	private PrintWriter bw;
	
	public Logger(String outFile, String testName, String testHeader) throws IOException {
		this.outfile = outFile;
		this.testName = testName;
		this.testTimes = new HashMap<String, Long>();
		this.bw =  new PrintWriter(outfile);
		this.bw.println(testName+"::"+testHeader);
	}
	
	public void startTest(String test) {
		this.bw.println(testName+"::"+test+"::Starting");
		testTimes.put(test, System.currentTimeMillis());
	}
	
	public void logInfo(String test, String info) {
		this.bw.println(testName+"::"+test+"::Info::"+info);
	}
	
	public void stopTest(String test) {
		long endTime = System.currentTimeMillis();
		long diff = endTime - testTimes.get(test);
		this.bw.println(testName+"::"+test+"::Finished::on:: "+diff+"ms");
		this.bw.flush();
	}
	
	public void close() {
		try {
			this.bw.close();	
		} catch (Exception e) {
			System.err.println("Error closing I/O of logger");
		}
	}
}
