import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class HypoMachine {
	
	//Error code constants
	private static final int ERROR_FILE_OPEN = 1;
	private static final int ERROR_INVALID_ADDR = 2;
	private static final int ERROR_NO_END_OF_PROGRAM = 3;
	
	private static final int MEMORY_LIMIT = 10000;
	private static final int END_OF_PROGRAM = -1;
	
	//Hpyo memory array
	long[] memory;

	//Hypo memory register MAR, MBR
	long mar, mbr;		
	
	//CPU Clock
	long clock;
	
	//An array of 8 general purpose registers
	long[] cpuRegisters;
	
	//CPU registers:Instruction register, Processor status register,
	//Program counter and Status counter
	long ir, psr, pc, sp;
	
	
	
	//********************************
	//Function: InitializeSystem
	//
	//Task Description:
	//  Set all global system hardware components to 0
	//
	//Input Parameters:
	//	None
	//
	//Output Parameters:
	//	None
	//
	//Function Return Value:
	//	None:
	//********************************
	public void initializeSystem() {
		memory = new long[MEMORY_LIMIT];
		mar = mbr = 0;
		cpuRegisters = new long[8];
		ir = psr = pc = sp = 0;
	}
	
	// ********************************************************************
	// Function: AbsoluteLoader
	//
	// Task Description:
	// Open the file containing HYPO machine user program and
	// load the content into HYPO memory.
	// On successful load, return the PC value in the End of Program line.
	// On failure, display appropriate error message and return appropriate error code
	//
	// Input Parameters
	// filename Name of the Hypo Machine executable file
	//
	// Output Parameters
	// Code of status
	//
	// Function Return Value will be one of the following:
	// ErrorFileOpen Unable to open the file
	// ErrorInvalidAddress Invalid address error
	// ErrorNoEndOfProgram Missing end of program indicator
	// ErrorInvalidPCValue invalid PC value
	// 0 to Valid address range Successful Load, valid PC value
	// ************************************************************
	public int absoluteLoader(String fileName) {
		int addr = 0;
		int content = 0;
		Scanner in = null;
		try {
			in = new Scanner(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			return ERROR_FILE_OPEN;
		};
		while(in.hasNext()) {			
			String word = in.next();
			addr = Integer.valueOf(word.substring(0, word.indexOf(" ") + 1));
			content = Integer.valueOf(word.substring(word.indexOf(" ")));
			if(addr == END_OF_PROGRAM) {
				in.close();
			}
			//If address is in the valid range
			else if (addr >= 0 && addr < memory.length) {
				memory[addr] = content;
			
			} else {
				//Address is not in the valid range
				in.close();
				return ERROR_INVALID_ADDR;
			}
		}
		in.close();
		return addr;
	}
	
	// ********************************************************************
	// Function: CPU
	//
	// Task Description:
	// Executes the program that is already loaded in the main memory
	// load the content into HYPO memory.
	// On successful load, return the PC value in the End of Program line.
	// On failure, display appropriate error message and return appropriate error code
	//
	// Input Parameters
	// None
	//
	// Output Parameters
	// Status of the execution
	//
	// Function Return Value will be one of the following:
	// ErrorFileOpen Unable to open the file
	// ErrorInvalidAddress Invalid address error
	// ErrorNoEndOfProgram Missing end of program indicator
	// ErrorInvalidPCValue invalid PC value
	// 0 to Valid address range Successful Load, valid PC value
	// ************************************************************
	public long CPU() {
		int opCode = -1;
		
		//While the program does not halt nor error out
		while(opCode != 0) {
			//Fetch Cycle
			if(0 <= pc && pc <= MEMORY_LIMIT) {
				mar = pc++;
				mbr = memory[(int)mar];
			} else {
				
			}
		}
		
		ir = mbr;
		
		//Decode cycle
		
		return 0;
	}
}
