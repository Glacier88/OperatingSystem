package operatingSystem;

public class MTOPSRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MTOPS mtops = new MTOPS();
		long status = 0;
		
		//Initialize system function
		mtops.initializeSystem();
		
		//While the system is not shut down. 
		while(mtops.RQ != mtops.END_OF_LIST 
				|| mtops.WQ != mtops.END_OF_LIST) {
			
			//Check and process interrupt
			mtops.checkAndProcessInterrupt();
			
			//Dump RQ and WQ
			mtops.printQueue(mtops.RQ);
			mtops.printQueue(mtops.WQ);
			
			//Dump dynamic memory area before CPU scheduling
			//Starting from memory location 0 with a size of 100
			mtops.dumpMemory("Dynamic Memory Area before CPU scheduling", 0, 100);
			
			//Select next process from RQ to give CPU
			long runningPCBptr = mtops.selectProcessFromRQ();
			
			//Perform restore context using Dispatcher
			mtops.dispatcher(runningPCBptr);
			
			//Dump RQ
			mtops.printQueue(mtops.RQ);
			
			//Dump running PCB and CPU context 
			mtops.printPCB(runningPCBptr);
			
			//Execute instructions of the running process using the CPU
			status = mtops.CPUexecuteProgram();
			
			//Dump dynamic memory area after executing the program
			//Starting from memory location 0 with a size of 100
			mtops.dumpMemory("Dynamic Memory Area after executing the program", 0, 100);
			
			//Check return status - reason for giving up CPU
			
			//Status is time slice expired
			if(status == mtops.TIME_SLICE_EXPIRED) {
				//running process is losing CPU
				mtops.saveContext(runningPCBptr);
				
				//Insert running process PCB into RQ
				mtops.insertIntoReadyQ(runningPCBptr);
				
				//Set running PCB ptr to End of list
				runningPCBptr = mtops.END_OF_LIST;				
			} else if (status == mtops.HALT || status < 0){//Halt or run-time error
				//Terminate running process 
				mtops.terminateProcess(runningPCBptr);
				runningPCBptr = mtops.END_OF_LIST;
			} else if (status == mtops.INPUT_OPERATION_EVENT) {//io_getc
				
				System.out.println("Waiting in the running PCB to input completion event.");
				
				//Insert running process into WQ
				mtops.insertIntoReadyQ(runningPCBptr);
				
				//Set running PCB ptr to End of List
				runningPCBptr = mtops.END_OF_LIST;
			} else if (status == mtops.OUTPUT_OPERATION_EVENT) { //io_putc
				
				System.out.println("Waiting in the running PCB to output completion event.");
				
				//Insert running process into WQ
				mtops.insertIntoReadyQ(runningPCBptr);
				
				//Set running PCB ptr to End of List
				runningPCBptr = mtops.END_OF_LIST;
			} else {
				System.out.println("Unknown programming error message.");
			}			
		}
		
		//OS is shutting down
		System.out.println("The operating system is shutting down.");
		
	}

}
