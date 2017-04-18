package operatingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MTOPS extends HypoMachine{
	int processID;
	
	long WQ = 0;
	long RQ = 0;
	long OSFreeList = 0;
	long userFreeList = 0;
	
	//Index of PCB array
	private static final int NEXT_PCB_PTR = 0;
	private static final int PID = 1;
	private static final int STATE = 2;
	private static final int REASON_FOR_WAITING = 3;
	private static final int PRIORITY = 4;
	private static final int STACK_START_ADDR = 5;
	private static final int PCB_STACK_SIZE = 6;
	private static final int MQ_START_ADDR = 7;
	private static final int MQ_SIZE = 8;
	private static final int NUM_OF_MS_IN_MQ = 9;
	private static final int GPR0 = 11;
	private static final int GPR1 = 12;
	private static final int GPR2 = 13;
	private static final int GPR3 = 14;
	private static final int GPR4 = 15;
	private static final int GPR5 = 16;
	private static final int GPR6 = 17;
	private static final int GPR7 = 18;
	private static final int SP = 19;
	private static final int PC = 20;
	private static final int PSR = 21;
	
	//PSR mode
	private static final long USER_MODE = 2;
	private static final long OS_MODE = 1;
	
	private static final int ERROR_NO_FREE_MEMORY = -10;
	private static final int ERROR_INVALID_MEM_SIZE = -11;
	
	private static final long DEFAULT_PRIORITY = 128;
	private static final long READY_STATE = 1;
	private static final long END_OF_LIST = 10000;
	
	//Constants of PCB states
	private static final long WAITING = 1;
	private static final long READY = 0;
	
	private static final int PCB_SIZE = 22;	
	//Initial stack size assigned to each process
	private static final int STACK_SIZE = 32;
	
	long createProcess(String fileName,long priority) {
		long PCBptr = allocateOSmemory(PCB_SIZE);
		initializePCB(PCBptr);
		int PCBvalue = absoluteLoader(fileName);
		//Error happens when loading the program
		if(PCBvalue < OKAY) {
			return PCBvalue;
		}
		pc = PCBvalue;
		
		//Allocate stack space from user free list
		long stackPtr = allocateUserMemory(STACK_SIZE);
		if(stackPtr < OKAY) {//Check for error
			//Free allocated PCB space 
			freeOSmemory(PCBptr, PCB_SIZE);
			//Return error code
			return stackPtr;
		}
		
		//Store stack information in the PCB - SP, ptr and size
		memory[(int)PCBptr + SP] = stackPtr + STACK_SIZE;
		memory[(int)PCBptr + STACK_START_ADDR] = stackPtr;
		memory[(int)PCBptr + PCB_STACK_SIZE] = STACK_SIZE;
		
		//Set priority in the PCB
		memory[(int)PCBptr + PRIORITY] = priority;
		
		//Dump program area
		printPCB(PCBptr);
		
		//Insert PCB into ReadyQueue
		insertIntoReadyQ(PCBptr);
		
		return OKAY;
	}
	
	void initializePCB(long PCBptr) {
		
		//Set entire PCB area to 0
		memory[(int)PCBptr + NEXT_PCB_PTR] = 0;
		memory[(int)PCBptr + PID] = 0;
		memory[(int)PCBptr + STATE] = 0;
		memory[(int)PCBptr + REASON_FOR_WAITING] = 0;
		memory[(int)PCBptr + PRIORITY] = 0;
		memory[(int)PCBptr + STACK_START_ADDR] = 0;
		memory[(int)PCBptr + PCB_STACK_SIZE] = 0;
		memory[(int)PCBptr + MQ_START_ADDR] = 0;
		memory[(int)PCBptr + MQ_SIZE] = 0;
		memory[(int)PCBptr + NUM_OF_MS_IN_MQ] = 0;
		memory[(int)PCBptr + GPR0] = 0;
		memory[(int)PCBptr + GPR1] = 0;
		memory[(int)PCBptr + GPR2] = 0;
		memory[(int)PCBptr + GPR3] = 0;
		memory[(int)PCBptr + GPR4] = 0;
		memory[(int)PCBptr + GPR5] = 0;
		memory[(int)PCBptr + GPR6] = 0;
		memory[(int)PCBptr + GPR7] = 0;
		memory[(int)PCBptr + SP] = 0;
		memory[(int)PCBptr + PC] = 0;
		memory[(int)PCBptr + PSR] = 0;
		
		//Allocate PID and set it in the PCB
		processID++;
		
		//Set PID, Priority and next PCB pointer fields in PCB
		memory[(int)PCBptr + PID] = processID;
		memory[(int)PCBptr + PRIORITY] = DEFAULT_PRIORITY;
		memory[(int)PCBptr + NEXT_PCB_PTR] = END_OF_LIST;
	}
	
	void printPCB(long PCBptr) {
		System.out.println("PCB address = " + PCBptr);
		System.out.println("Next PCB ptr = " + NEXT_PCB_PTR);
		System.out.println("PID = " + PID);
		System.out.println("State = " + STATE);
		System.out.println("Reason for waiting code = " + REASON_FOR_WAITING);
		System.out.println("Priority = " + PRIORITY);
		System.out.println("Stack start address = " + STACK_START_ADDR);
		System.out.println("Stack size = " + PCB_STACK_SIZE);
		System.out.println("Message queue start address = " + MQ_START_ADDR);
		System.out.println("Message queue size = " + MQ_SIZE);
		System.out.println("Number of messages in queue = " + NUM_OF_MS_IN_MQ);
		System.out.println("GPR0 = " + GPR0);
		System.out.println("GPR1 = " + GPR1);
		System.out.println("GPR2 = " + GPR2);
		System.out.println("GPR3 = " + GPR3);
		System.out.println("GPR4 = " + GPR4);
		System.out.println("GPR5 = " + GPR5);
		System.out.println("GPR6 = " + GPR6);
		System.out.println("GPR7 = " + GPR7);
		System.out.println("SP = " + SP);
		System.out.println("PC = " + PC);
		System.out.println("PSR = " + PSR);
	}
	
	long printQueue(long Qptr) {
		long currentPCBptr = Qptr;
		if (currentPCBptr == END_OF_LIST) {
			System.out.println("Empty list message");
			return OKAY;
		}
		while(currentPCBptr < END_OF_LIST) {
			printPCB(currentPCBptr);
			currentPCBptr = memory[(int)currentPCBptr];
		}
		return OKAY;
	}
	
	long insertIntoReadyQ(long PCBptr) {
		
		//Insert PCB according to Round Robin.
		long previousPtr = END_OF_LIST;
		long currentPtr = RQ;
		if(PCBptr < 0) {
			System.out.println("Invalid memory address!");
			return ERROR_INVALID_ADDR;
		}
		memory[(int)PCBptr + STATE] = READY_STATE; 
		memory[(int)PCBptr + NEXT_PCB_PTR] = END_OF_LIST;
		
		//RQ is empty
		if(RQ == END_OF_LIST) {
			RQ = PCBptr;
			return OKAY;
		}
		
		//Walk through RQ and find the place to insert
		//PCB will be inserted at the end of its priority
		while(currentPtr != END_OF_LIST) {
			if(memory[(int)(PCBptr + PRIORITY)] > memory[(int)(currentPtr + PRIORITY)]) {
				//found the place to insert
				if(previousPtr == END_OF_LIST) {
					//Enter the PCB in the front of the list as first entry
					memory[(int)PCBptr + NEXT_PCB_PTR] = RQ;
					RQ = PCBptr;
					return OKAY;
				}
				
				//Enter PCB in the middle of the list
				memory[(int)PCBptr + NEXT_PCB_PTR] = memory[(int)previousPtr + NEXT_PCB_PTR];
				memory[(int)previousPtr + NEXT_PCB_PTR] = PCBptr;
				return OKAY;
			} else {
				//PCB to be inserted has lower or equal priority to the current PCB in RQ
				previousPtr = currentPtr;
				currentPtr = memory[(int)currentPtr + NEXT_PCB_PTR];
			}
		}
		//Insert PCB at the end of RQ
		memory[(int)previousPtr + NEXT_PCB_PTR] = PCBptr;
		return OKAY;
	}
	
	long insertIntoWQ(long PCBptr) {
		if(PCBptr < 0 || PCBptr > MEMORY_LIMIT) {
			System.out.println("Invalid PCB error message!");
			return ERROR_INVALID_ADDR;
		}
		memory[(int)PCBptr + STATE] = WAITING;
		memory[(int)PCBptr + NEXT_PCB_PTR] = WQ;
		WQ = PCBptr;
		return OKAY;
	}
	
	long selectProcessFromRQ() {
		long PCBptr = RQ;
		if(RQ != END_OF_LIST) {
			RQ = memory[(int)PCBptr + NEXT_PCB_PTR]; 
		}
		memory[(int)PCBptr + NEXT_PCB_PTR] = END_OF_LIST;
		return PCBptr;
	}
	
	void saveContext(long PCBptr) {
		
		memory[(int)PCBptr + GPR0] = cpuRegisters[0];
		memory[(int)PCBptr + GPR1] = cpuRegisters[1];
		memory[(int)PCBptr + GPR2] = cpuRegisters[2];
		memory[(int)PCBptr + GPR3] = cpuRegisters[3];
		memory[(int)PCBptr + GPR4] = cpuRegisters[4];
		memory[(int)PCBptr + GPR5] = cpuRegisters[5];
		memory[(int)PCBptr + GPR6] = cpuRegisters[6];
		memory[(int)PCBptr + GPR7] = cpuRegisters[7];
		//Set SP field in the PCB
		memory[(int)PCBptr + SP] = sp;
		//Set PC field in the PCB
		memory[(int)PCBptr + PC] = pc;
	}
	
	void dispatcher(long PCBptr) {
		
		//Copy CPU GPR register values from PCB to CPU registers
		cpuRegisters[0] = memory[(int)PCBptr + GPR0];
		cpuRegisters[1] = memory[(int)PCBptr + GPR1];
		cpuRegisters[2] = memory[(int)PCBptr + GPR2];
		cpuRegisters[3] = memory[(int)PCBptr + GPR3];
		cpuRegisters[4] = memory[(int)PCBptr + GPR4];
		cpuRegisters[5] = memory[(int)PCBptr + GPR5]; 
		cpuRegisters[6] = memory[(int)PCBptr + GPR6];
		cpuRegisters[7] = memory[(int)PCBptr + GPR7];
		
		//Restore SP and PC from given PCB
		sp = memory[(int)PCBptr + SP];
		pc = memory[(int)PCBptr + PC];
		
		//Set system mode to user mode
		psr = USER_MODE;
	}
	
	//Recover all resources allocated to the process
	void terminateProcess(long PCBptr) {
		//Return stack memory using stack start address and stack size
		long i = memory[(int)PCBptr + STACK_START_ADDR];
		while(i < memory[(int)PCBptr + STACK_SIZE]) {
			memory[(int)i] = 0;
		}
		//Return PCB memory using the PCBptr
		memory[(int)PCBptr + NEXT_PCB_PTR] = 0;
	}
	
	long allocateOSmemory(long requestedSize) {
		if(OSFreeList == END_OF_LIST) {
			System.out.println("No free OS memory!");
			return ERROR_NO_FREE_MEMORY;
		}
		if(requestedSize < 0) {
			System.out.println("Invalid size error!");
			return ERROR_INVALID_MEM_SIZE;
		}
		//Minimum allocated memory is 2
		if(requestedSize == 1)
			requestedSize = 2;
		long currentPtr = OSFreeList;
		long previousPtr = END_OF_LIST;
		while(currentPtr != END_OF_LIST) {
			//Check each block in the link list until block with requested 
			//memory size is found
			if(memory[(int)currentPtr + 1] == requestedSize) {
				//Found block with requested size
				if(currentPtr == OSFreeList) {
					//First entry is pointer to next week
					OSFreeList = memory[(int)currentPtr];
					//Reset next pointer in the allocated block
					memory[(int)currentPtr] = END_OF_LIST;
					//Return memory address
					return currentPtr;
				} else {
					//Not first black
					memory[(int)previousPtr] = memory[(int)currentPtr];
					memory[(int)currentPtr] = END_OF_LIST;
					return currentPtr;
				}
			} else if (memory[(int)currentPtr + 1] > requestedSize) {
				//Found block with size greater than the requested size
				
				//First block
				if(currentPtr == OSFreeList) {
					memory[(int)(currentPtr + requestedSize)] = memory[(int)currentPtr];
					memory[(int)(currentPtr + requestedSize + 1)] = 
							memory[(int)(currentPtr + 1)] - (int)requestedSize;
					//Address of reduced block
					OSFreeList = currentPtr + requestedSize;
					memory[(int)currentPtr] = END_OF_LIST;
					return currentPtr;
				} else {
					//Not first block
					
					//Move next block ptr
					memory[(int)(currentPtr + requestedSize)] 
							= memory[(int)currentPtr];
					memory[(int)(currentPtr + requestedSize + 1)] 
							= memory[(int)(currentPtr + 1)] - requestedSize;
					
					//Address of reduced block
					memory[(int)previousPtr] = currentPtr + requestedSize;
					
					//Reset next pointer in the allocated block
					memory[(int)currentPtr] = END_OF_LIST;
					
					return currentPtr;	
				}
			//Small block
			} else {
				//Look at next block
				previousPtr = currentPtr;
				currentPtr = memory[(int)currentPtr];
			}
			
			
		}
		//Display no free OS memory error
		System.out.println("No free OS memory error!");
		return ERROR_NO_FREE_MEMORY;
	}
	
	//Return OK or error code
	long freeOSmemory(long ptr, long size) {
		if(ptr < 0 || ptr > OSFreeList) {
			System.out.println("Invalid errmr message");
			return ERROR_INVALID_ADDR;
		}
		//Check for minimum allocated size, which is 2 even if a user asks for 1 location
		if(size == 1) {
			size = 2;
		//Invalid size
		} else if (size < 1 || (ptr + size) >= MEMORY_LIMIT) {
			System.out.println("Invalid size or address error.");
			return ERROR_INVALID_ADDR;
		}
		//Return the memory to OS free space and insert at the beginning of the linked list
		//**What is this linked list?**
		
		
		//Make the given free block point to free block pointed by the OS free list
		//Set the free block size in the given free block
		OSFreeList = ptr;
		
		return OKAY;
	}
	
	long allocateUserMemory(long requestedSize) {
		if(userFreeList == END_OF_LIST) {
			System.out.println("No free OS memory!");
			return ERROR_NO_FREE_MEMORY;
		}
		if(requestedSize < 0) {
			System.out.println("Invalid size error!");
			return ERROR_INVALID_MEM_SIZE;
		}
		//Minimum allocated memory is 2
		if(requestedSize == 1)
			requestedSize = 2;
		long currentPtr = userFreeList;
		long previousPtr = END_OF_LIST;
		while(currentPtr != END_OF_LIST) {
			//Check each block in the link list until block with requested 
			//memory size is found
			if(memory[(int)currentPtr + 1] == requestedSize) {
				//Found block with requested size
				if(currentPtr == userFreeList) {
					//First entry is pointer to next block
					userFreeList = memory[(int)currentPtr];
					//Reset next pointer in the allocated block
					memory[(int)currentPtr] = END_OF_LIST;
					//Return memory address
					return currentPtr;
				} else {
					//Not first black
					memory[(int)previousPtr] = memory[(int)currentPtr];
					memory[(int)currentPtr] = END_OF_LIST;
					return currentPtr;
				}
			} else if (memory[(int)currentPtr + 1] > requestedSize) {
				//Found block with size greater than the requested size
				
				//First block
				if(currentPtr == userFreeList) {
					memory[(int)(currentPtr + requestedSize)] = memory[(int)currentPtr];
					memory[(int)(currentPtr + requestedSize + 1)] = 
							memory[(int)(currentPtr + 1)] - (int)requestedSize;
					//Address of reduced block
					userFreeList = currentPtr + requestedSize;
					memory[(int)currentPtr] = END_OF_LIST;
					return currentPtr;
				} else {
					//Not first block
					
					//Move next block ptr
					memory[(int)(currentPtr + requestedSize)] 
							= memory[(int)currentPtr];
					memory[(int)(currentPtr + requestedSize + 1)] 
							= memory[(int)(currentPtr + 1)] - requestedSize;
					
					//Address of reduced block
					memory[(int)previousPtr] = currentPtr + requestedSize;
					
					//Reset next pointer in the allocated block
					memory[(int)currentPtr] = END_OF_LIST;
					
					return currentPtr;	
				}
			//Small block
			} else {
				//Look at next block
				previousPtr = currentPtr;
				currentPtr = memory[(int)currentPtr];
			}		
		}
		//Display no free User memory error
		System.out.println("No free OS memory error!");
		return ERROR_NO_FREE_MEMORY;
	}
	
	
	//This is similar to FreeOSmemory
	long freeUserMemory(long ptr, long size) {
		return 0;
	}
	
	void checkAndProcessInterrupt() {
		int interruptId = 0;
		
		//Prompt and read interrupt ID
		System.out.println("Please input the interrput ID: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			interruptId = Integer.parseInt(br.readLine());
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("The interrupt ID is: " + interruptId);
		
		switch(interruptId) {
			case 0: //No interrupt
				break;
			case 1: //Run program
				ISRrunProgramInterrupt();
				break;
			case 2: // Shut down system
				ISRshutdownSystem();
				break;
			case 3: //Input operation completion - io_getc
				ISRinputCompletionInterrupt();
				break;
			case 4: //Output operation completion - io_putc
				ISRoutputCompletionInterrupt();
				break;
			default: //Invalid interrupt ID
				System.out.println("Invalid interrupt ID!");
				break;
		}
		
	}
	
	void ISRrunProgramInterrupt() {
		System.out.println("Please input file name: ");
		String fileName = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			fileName = br.readLine();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createProcess(fileName,DEFAULT_PRIORITY);
	}
	
	void ISRinputCompletionInterrupt() {
		System.out.println("Please input a process ID: ");
		int processID = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			processID = Integer.parseInt(br.readLine());
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Search WQ
		long currentPtr = WQ;
		long previousPtr = 0;
		while(currentPtr != END_OF_LIST) {
			if(currentPtr == processID) {
				//Remove PCB from the WQ
				removeProcessFromWQ(currentPtr);
				memory[(int)currentPtr + STATE] = READY;
				insertIntoReadyQ(currentPtr);
				return;
			}
			currentPtr = memory[(int)currentPtr];
		}
		
		//If no match is found in WQ, then search RQ
		
		System.out.println("Invalid process ID!");
	}
	
	void removeProcessFromWQ(long processID) {
		
	}
	
	void ISRoutputCompletionInterrupt() {
		
	}
	
	void ISRshutdownSystem() {
		
	}
	
	long searchAndRemovePCBfromWQ(long pid) {
		return 0;
	}
	
	public void initializeSystem() {
		
	}
	
	long CPUexecuteProgram() {
		return 0;
	}
	
	long SystemCall(long systemCallID) {
		return 0;
	}
	
	long memAllocSystemCall() {
		return 0;
	}
	
	long memFreeSystemCall() {
		return 0;
	}
	
	long io_getCSystemCall() {
		return 0;
	}
	
	long io_putcSystemCall() {
		return 0;
	}
}
