package operatingSystem;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class HypoMachine {
	
	public static final int OKAY = 0;
	
	//Error code constants
	public static final int ERROR_FILE_OPEN = -1;
	public static final int ERROR_INVALID_ADDR = -2;
	public static final int ERROR_NO_END_OF_PROGRAM = -3;
	public static final int ERROR_IMMEDIATE_MODE = -4;	
	public static final int END_OF_PROGRAM = -1;
	public static final int RUNTIME_ERROR = -5;
	public static final int ERROR_STACK_OVERFLOW = -6;
	public static final int ERROR_STACK_UNDERFLOW = -7;
	public static final int ERROR_INVALID_OPCODE = -8;
	public static final int ERROR_INVALID_OPMODE = -9;
	
	public static final int MEMORY_LIMIT = 10000;
	public static final int STACK_UPPER_BOUND = 10000;
	
	public static final int HALT = 0;
	
	//Addressing modes constants
	public static final long INVALID_MODE = 0;
	public static final long REGISTER_MODE = 1;
	public static final long REGISTER_DEFERRED = 2;
	public static final long AUTO_INCREMENT = 3;
	public static final long AUTO_DECREMENT = 4;
	public static final long DIRECT_MODE = 5;
	public static final long IMMEDIATE_MODE = 6;
	
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
		long startTime = System.nanoTime();
		long opCode = -1;
		long result = 0;
		long remainder;
		long status;
		long op1Mode, op1Gpr, op2Mode, op2Gpr;
		//While the program does not halt nor error out
		while(opCode != 0) {
			//Fetch Cycle
			if(0 <= pc && pc <= MEMORY_LIMIT) {
				mar = pc++;
				mbr = memory[(int)mar];
			} else {
				System.err.println("Invalid memory address!");
				return ERROR_INVALID_ADDR;
			}
		
		
		ir = mbr;
		
		//Decode cycle
		opCode = ir / 10000;
		remainder = ir % 10000;
		
		op1Mode = remainder / 1000;
		remainder = ir % 1000;
		
		op1Gpr = remainder / 100;
		remainder = ir % 100;
		
		op2Mode = remainder / 10;
		op2Gpr = remainder % 10;
		
		LongWrapper op1Address = new LongWrapper();
		LongWrapper op1Value = new LongWrapper();
		LongWrapper op2Address = new LongWrapper();
		LongWrapper op2Value = new LongWrapper();
		
		//Execute cycle
		switch((int)opCode) {
			case 0: //Halt
				System.out.println("Halt instruction is encounterred.");
				return HALT;
			case 1: //Add
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				status = fetchOperand(op2Mode, op2Gpr, op2Address, op2Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				result = op1Value.value + op2Value.value;
				if (op1Mode == REGISTER_MODE) {
					cpuRegisters[(int)op1Gpr] = result;
				} else if (op1Mode == IMMEDIATE_MODE) {
					System.out.println("Destination operand cannot be immediate value.");
					return ERROR_IMMEDIATE_MODE;
				} else {
					memory[(int)op1Address.value] = result;
				}
				clock += System.nanoTime() - startTime;
				break;
			case 2: // Subtract
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				status = fetchOperand(op2Mode, op2Gpr, op2Address, op2Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				result = op1Value.value - op2Value.value;
				if (op1Mode == REGISTER_MODE) {
					cpuRegisters[(int)op1Gpr] = result;
				} else if (op1Mode == IMMEDIATE_MODE) {
					System.out.println("Destination operand cannot be immediate value.");
					return ERROR_IMMEDIATE_MODE;
				} else {
					memory[(int)op1Address.value] = result;
				}
				clock += System.nanoTime() - startTime;
				break;
			case 3: //Multiply
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				status = fetchOperand(op2Mode, op2Gpr, op2Address, op2Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				result = op1Value.value * op2Value.value;
				if (op1Mode == REGISTER_MODE) {
					cpuRegisters[(int)op1Gpr] = result;
				} else if (op1Mode == IMMEDIATE_MODE) {
					System.out.println("Destination operand cannot be immediate value.");
					return ERROR_IMMEDIATE_MODE;
				} else {
					memory[(int)op1Address.value] = result;
				}
				clock += System.nanoTime() - startTime;
				break;
			case 4: //Divide
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				status = fetchOperand(op2Mode, op2Gpr, op2Address, op2Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				if (op2Value.value == 0) {
					System.out.println("Divide by 0!");
					return RUNTIME_ERROR;
				}
				result = op1Value.value / op2Value.value;
				if (op1Mode == REGISTER_MODE) {
					cpuRegisters[(int)op1Gpr] = result;
				} else if (op1Mode == IMMEDIATE_MODE) {
					System.out.println("Destination operand cannot be immediate value.");
					return ERROR_IMMEDIATE_MODE;
				} else {
					memory[(int)op1Address.value] = result;
				}
				clock += System.nanoTime() - startTime;
				break;
			case 5: 
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				status = fetchOperand(op2Mode, op2Gpr, op2Address, op2Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				result = op2Value.value;
				if (op1Mode == REGISTER_MODE) {
					cpuRegisters[(int)op1Gpr] = result;
				} else if (op1Mode == IMMEDIATE_MODE) {
					System.out.println("Destination operand cannot be immediate value.");
					return ERROR_IMMEDIATE_MODE;
				} else {
					memory[(int)op1Address.value] = result;
				}
				clock += System.nanoTime() - startTime;
				break;
			case 6: //Branch on jump instruction 
				if (pc >= 0 && pc <= MEMORY_LIMIT) {
					pc = memory[(int)pc];
					clock += System.nanoTime() - startTime; 
					break;
				} else {
					System.out.println("Memory is out of range!");
					return ERROR_INVALID_ADDR;
				}
			case 7: //Branch on Minus
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				if(op1Value.value < 0) {
					if(pc >= 0 && pc <= MEMORY_LIMIT) {
						pc = memory[(int)pc];
						clock += System.nanoTime();
						break;
					} else {
						System.out.println("Memory is out of range!");
						return ERROR_INVALID_ADDR;
					}
				} else {
					pc++;
				}
				break;
			case 8: //Branch on plus
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				if(op1Value.value > 0) {
					if(pc >= 0 && pc <= MEMORY_LIMIT) {
						pc = memory[(int)pc];
						clock += System.nanoTime();
						break;
					} else {
						System.out.println("Memory is out of range!");
						return ERROR_INVALID_ADDR;
					}
				} else {
					pc++;
				}
				break;
			case 9: //Branch on zero
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if(status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				if(op1Value.value == 0) {
					if(pc >= 0 && pc <= MEMORY_LIMIT) {
						pc = memory[(int)pc];
						clock += System.nanoTime();
						break;
					} else {
						System.out.println("Memory is out of range!");
						return ERROR_INVALID_ADDR;
					}
				} else {
					pc++;
				}
				break;
			case 10: //push - if stack is not full
				status = fetchOperand(op1Mode, op1Gpr, op1Address, op1Value);
				if (status != OKAY) {
					System.out.println("Error while fetching operands!");
					return status;
				}
				if(sp >= STACK_UPPER_BOUND) {
					System.out.println("Stack overflow error.");
					return ERROR_STACK_OVERFLOW;
				} else {
					sp++;
					memory[(int)sp] = op1Value.value;
					break;
				}
			case 11: //Pop - if stack is not empty
				if(sp < 0) {
					System.out.println("Stack underflow error.");
					return ERROR_STACK_UNDERFLOW;
				} else {
					sp--;
					memory[(int)op1Address.value] = memory[(int)sp];
				}
				break;
			case 12: //System call
				if(pc < 0 || pc >= MEMORY_LIMIT) {
					System.out.println("Memory is out of range.");
					return ERROR_INVALID_ADDR;
				}
				long systemCallID = memory[(int)pc++];
				status = SystemCall(op1Value);
				break;
			default:
				System.out.println("Invalid opcode!");
				return ERROR_INVALID_OPCODE;
			}
		
		}
		return OKAY;
	}
	
	//**************************************************
	//Function: FetchOperand
	//
	//Task Description:
	//Fetch operands from memory and put them to registers
	//
	//Input parameters:
	//	OpMode: Operand mode value
	//	OpValue: Operand value when mode and GPR are valid
	//
	//Function return value:
	// OKAY: On successful fetch
	// All possible error codes:
	// ERROR_INVALID_OPMODE: Invalid operand mode
	// ERROR_INVALID_ADDR: Invalid memory address
	//**************************************************
	public long fetchOperand(long opMode, long opReg, LongWrapper opAddress, 
			LongWrapper opValue) {
		//Fetch operand value based on the operand mode
		switch((int)opMode) {
			case 1: //Register mode
				opAddress.value = -1;
				opValue.value = cpuRegisters[(int)opReg];
				break;
			case 2: //Register deferred mode - Op addr in in GPR & value in memory
				opAddress.value = cpuRegisters[(int)opReg];
				if(opAddress.value >= 0 && opAddress.value < MEMORY_LIMIT) {
					opValue.value = memory[(int)opAddress.value];
				} else {
					System.out.println("Invalid address error!");
					return ERROR_INVALID_ADDR;
				}
				break;
			case 3: //Auto-increment mode - Op addr in GPR & Op value in memory
				opAddress.value = cpuRegisters[(int)opReg];
				if(opAddress.value >= 0 && opAddress.value < MEMORY_LIMIT) {
					opValue.value = memory[(int)opAddress.value];
				} else {
					System.out.println("Invalid address error!");
					return ERROR_INVALID_ADDR;
				}
				cpuRegisters[(int)(opReg)]++;
				break;
			case 4: //Auto-decrement mode
				opAddress.value = cpuRegisters[(int)opReg];
				if(opAddress.value >= 0 && opAddress.value < MEMORY_LIMIT) {
					opValue.value = memory[(int)opAddress.value];
				} else {
					System.out.println("Invalid address error!");
					return ERROR_INVALID_ADDR;
				}
				cpuRegisters[(int)(opReg)]--;
				break;
			case 5: //Direct mode - Op address is in the instruction pointed by PC
				opAddress.value = memory[(int)pc++];
				if(opAddress.value >= 0 && opAddress.value < MEMORY_LIMIT) {
					opValue.value = memory[(int)opAddress.value];
				} else {
					System.out.println("Invalid address error!");
					return ERROR_INVALID_ADDR;
				}
				break;
			case 6: //Immediate mode - Operand value is in the instruction
				opAddress.value = memory[(int)pc++];
				if(opAddress.value >= 0 && opAddress.value < MEMORY_LIMIT) {
					opAddress.value = -1;
					opValue.value = memory[(int)pc++];
				} else {
					System.out.println("Invalid address error!");
					return ERROR_INVALID_ADDR;
				}
				break;
			default:
				System.out.println("Invalid mode1");
				return ERROR_INVALID_OPMODE;			
		}
		return OKAY;
	}
	
	//**********************************************************
	//Function: Dump memory
	//
	//Task description:
	//	Display a string passed as one of the input parameter.
	//	Display content of GPRs, SP, PC, PSR, System clock and
	//	the content of specified memory locations in a specific format
	//
	//Input parameters:
	//	String         String to be displayed
	//	StarAddress    Start address of memory location
	//	Size		   Number of locations to dump
	//
	//Output parameters
	//	None
	//
	//Function return value:
	//	None
	//
	//************************************************************
	public void dumpMemory(String string, long startAddress, long size) {
		//Display input the input parameter string
		System.out.println(string);
		if(startAddress < 0 || startAddress >= MEMORY_LIMIT) {
			System.out.println("Invalid start address error!");
			return;
		} else if (size <= 0) {
			System.out.println("Invalid memory size specified");
			return;
		} else if (startAddress + size >= MEMORY_LIMIT) {
			System.out.println("End memory address overflows");
			return;
		}
		System.out.println("GPRs: G0 G1 G2 G3 G5 G5 G6 G7 SP PC");
		System.out.println(cpuRegisters[0] + " "
				+ cpuRegisters[1] + " "
				+ cpuRegisters[2] + " "
				+ cpuRegisters[3] + " "
				+ cpuRegisters[4] + " "
				+ cpuRegisters[5] + " "
				+ cpuRegisters[6] + " "
				+ cpuRegisters[7] + " "
				+ sp + " " + pc);
		System.out.println("Address +0 +1 +2 +3 +4 +5 +6 +7 +8 +9");
		long addr = startAddress;
		long endAddr = startAddress + size;
		while(addr < endAddr) {
			//Display address of the first value in the line
			System.out.println(addr);
			for(int i = 1; i < 10 ;i++) {//Display 10 values of memory from addr to addr +9
				if(addr < endAddr) {
					System.out.println(memory[(int)addr++]);
				} else {
					break;
				}
			}
		}
		//Display clock value and PSR value
		System.out.println("Clock value: " + clock);
		System.out.println("PSR value: " + psr);
	}
	
	//*****************************************
	//To be implemented
	//*****************************************
	public long SystemCall(LongWrapper op1Value) {
		return 0;
	}
}

//**************************************************************
// This wrap is used to mimic the pass by pointer attribute in C
// It will be used to change values of input arguments
//**************************************************************
class LongWrapper {
	long value;
	
	LongWrapper() {
		value = 0;
	}
	LongWrapper(long value) {
		this.value = value;
	}
}
