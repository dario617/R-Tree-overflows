package experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Logger {

	private String outfile;
	private String testName;
	private HashMap<String, Long> testTimes;
	private BufferedWriter bw;
	
	public Logger(String outFile, String testName, String testHeader) throws IOException {
		this.outfile = outFile;
		this.testName = testName;
		this.testTimes = new HashMap<String, Long>();
		this.bw =  new BufferedWriter(new FileWriter(outfile));
		this.bw.write(testName+"::"+testHeader);
		this.bw.newLine();
	}
	
	public void startTest(String test) {
		try {
			this.bw.write(testName+"::Starting::"+test);
			this.bw.newLine();
			testTimes.put(test, System.currentTimeMillis());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to write Log begining: "+test);
			e.printStackTrace();
		}
	}
	
	public void logInfo(String test, String info) {
		try {
			this.bw.write(testName+"::Info::"+test+"::"+info);
			this.bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to write Log info: "+test);
			e.printStackTrace();
		}
	}
	
	public void stopTest(String test) {
		try {
			long endTime = System.currentTimeMillis();
			long diff = endTime - testTimes.get(test);
			this.bw.write(testName+"::Finished::"+test+"::on::"+diff+"ms");
			this.bw.newLine();
			this.bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to write Log ending: "+test);
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			this.bw.close();	
		} catch (Exception e) {
			System.err.println("Error closing I/O of logger");
		}
	}
}
