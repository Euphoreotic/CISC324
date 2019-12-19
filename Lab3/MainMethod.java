

// The main method for the readers and writers program.
// Several readers can access the shared data structure simultaneously, or 
// one writer can have exclusive access.
//
// The reader/writer problem is discussed in the textbook and in the course
// notes.  Three types of solutions exist:
// (1) Readers get priority.  This means that if there is a constant stream
//     of readers, then a writer can wait indefinitely.  (This is called
//     "starvation" of the writer.)
// (2) Writers get priority.  This means that if there is a constant stream
//     of writers, then a reader can wait indefinitley.
// (3) A starvation-free solution, in which neither readers nor writers
//     wait indefinitely.
// The code given here is for solution (1).  This same algorithm is shown in
// Operating System Concepts by Silberschatz, Galvin, and Gagne, in the
// section titled "The Readers-Writers Problem".

// This code uses 
//     class "Reader" from file Reader.java 
//     class "Writer" from file Writer.java 
//     class "Synch" from file Synch.java

import java.util.concurrent.*;

public class MainMethod {
	public static void main(String argv[]) {

		// Initialize the semaphores/variables needed for thread synchronization
		// The constructor of the Semaphore class accepts two parameters. The first is
		// an integer parameter that
		// specifies the initial number of permits available. The second is a boolean
		// parameter that will ensure
		// that permits are granted on a FIFO basis if set to true.
		Synch.mutexRC = new Semaphore(1, true);
		Synch.mutexWC = new Semaphore(1, true);
		Synch.wrt = new Semaphore(1, true);
		Synch.read = new Semaphore(1, true);

		// Now create several instances of Reader and Writer.
		Reader R; // R can hold an instance of class Reader
		Writer W; // W can hold an instance of class Writer

		for (int i = 1; i <= 10; i++) {
			W = new Writer(i);
			W.start();
			R = new Reader(i);
			R.start();
			
			
		}

		System.out.println("This is main speaking");
	} // end of "main"
} // end of "MainMethod"
