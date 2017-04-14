package operatingSystem;

public class MTOPS extends HypoMachine{
	int processID;
	
	long WQ = 0;
	long RQ = 0;
	//LONG OSFreeList
	//long UserFreeList
	
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
	
	private static final long DEFAULT_PRIORITY = 128;
	private static final long READY_STATE = 1;
	private static final long END_OF_LIST = 10000;
	
	//Constants of PCB states
	private static final long WAITING = 1;
	
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
		return 0;
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
	
	void terminateProcess(long PCBptr) {
		//Return stack memory using stack start address and 
		// stack size
		
		//Return PCB memory using the PCBptr
	}
	
	long allocateOSmemory(long requestedSize) {
		return 0;
	}
	
	long freeOSmemory(long ptr, long size) {
		return 0;
	}
	
	long allocateUserMemory(long size) {
		return 0;
	}
	
	long freeUserMemory(long ptr, long size) {
		return 0;
	}
	
	void checkAndProcessInterrupt() {
		
	}
	
	void ISRrunProgramInterrupt() {
		
	}
	
	void ISRinputCompletionInterrupt() {
		
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
