package operatingSystem;

public class MTOPS extends HypoMachine{
	int processID;
	//long RQ
	//LONG OSFreeList
	//long UserFreeList

	private static final int PCB_SIZE = 22;	
	
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
		
		return 0;
	}
	
	void initializePCB(long PCBptr) {
		processID++;
	}
	
	void printPCB(long PCBptr) {
		
	}
	
	long printQueue(long Qptr) {
		return 0;
	}
	
	long insertIntoRQ(long PCBptr) {
		return 0;
	}
	
	long insertIntoWQ(long PCBptr) {
		return 0;
	}
	
	long selectProcessFromRQ() {
		return 0;
	}
	
	void saveContext(long PCBptr) {
		
	}
	
	void dispatcher(long PCBptr) {
		
	}
	
	void terminateProcess(long PCBptr) {
		
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
