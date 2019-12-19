
// This file defines class "writer".

// This code uses
//      class Semaphore, from the java.util.concurrent package in Java 5.0 which defines the behaviour of a 
//                           semaphore, including acquire and release operations.
//      class Synch, which defines the semaphores and variables
//                   needed for synchronizing the readers and writers.
//      class RandomSleep, which defines the doSleep method.

public class Writer extends Thread {
	int myName; // The variable myName stores the name of this thread.
				// It is initialized in the constructor for class Reader.

	RandomSleep rSleep; // rSleep can hold an instance of class RandomSleep.

	// This is the constructor for class Writer. It has an integer parameter,
	// which is the name that is given to this thread.
	public Writer(int name) {
		myName = name; // copy the parameter value to local variable "MyName"
		rSleep = new RandomSleep(); // Create and instance of RandomSleep.
	} // end of the constructor for class "Writer"

	public void run() {
		for (int I = 0; I < 5; I++) {

			// acquire "mutexWC" to gain exclusive access to "writeCount"
			System.out.println("Writer " + myName + " wants to write");
			try {
				Synch.mutexWC.acquire();
			} catch (Exception e) {
			}

			// the first writer will wait to acquire "read" so no other readers will start.
			// Other writers arriving later will be held up by "mutexWC"
			if (Synch.writeCount == 0) {
				try {
					Synch.read.acquire();
				} catch (Exception e) {
				}
			}

			// release "mutexWC" after gaining permission to write
			Synch.writeCount++;
			Synch.mutexWC.release();

			// acquire "wrt" to gain exclusive writing permission
			try {
				Synch.wrt.acquire();
			} catch (Exception e) {
			}

			// Simulate the time taken by writing.
			System.out.println("Writer " + myName + " is now writing");
			rSleep.doSleep(1, 200);

			// We're done writing.
			// acquire "mutexWC" to gain exclusive control over writeCount
			System.out.println("Writer " + myName + " is finished writing");
			try {
				Synch.mutexWC.acquire();
			} catch (Exception e) {
				// TODO: handle exception
			}
			Synch.writeCount--;
			
			// if there are no more waiting writers, release "read" to wake up any waiting readers
			if (Synch.writeCount == 0)
				Synch.read.release();

			Synch.mutexWC.release();

			Synch.wrt.release();

			// Simulate "doing something else"
			rSleep.doSleep(1, 1000);
		} // end of "for" loop
	} // end of "run" method
} // end of class "Writer"
