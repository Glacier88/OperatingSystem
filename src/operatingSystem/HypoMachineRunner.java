package operatingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HypoMachineRunner {

	public static void main(String[] args) {	
		
		HypoMachine hypo = new HypoMachine();
		System.out.println("Please enter the name of the program.");
		String hypoUserTestProgramFilename = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			hypoUserTestProgramFilename = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Load the program
		long returnValue = hypo.absoluteLoader(hypoUserTestProgramFilename);
		
		//Set pc counter to return value
		hypo.pc = returnValue;
		
		//Dump memory from 0 to 99;
		hypo.dumpMemory("After loading the user program", 0, 100);
		
		//Execute HYPO machine program by calling CPU method
		long executionCompletionStatus = hypo.CPU();
		
		//Dump memory from 0 to 99;
		hypo.dumpMemory("After loading the user program", 0, 100);
		
		System.out.println("Return value is: " + executionCompletionStatus);
	}

}
