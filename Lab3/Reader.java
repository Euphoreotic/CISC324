

// This file defines class "Reader".

// This code uses
//      class Semaphore, from the java.util.concurrent package in Java 5.0 which defines the behaviour of a 
//                           semaphore, including acquire and release operations.
//      class Synch, which defines the semaphores and variables 
//                   needed for synchronizing the readers and writers.
//      class RandomSleep, which defines the doSleep method.

public class Reader extends Thread {
	int myName; // The variable myName stores the name of this thread.
				// It is initialized in the constructor for class Reader.

	RandomSleep rSleep; // rSleep can hold an instance of class RandomSleep.

	// This is the constructor for class Reader. It has an integer parameter,
	// which is the name that is given to this thread.
	public Reader(int name) {
		myName = name; // copy the parameter value to local variable "MyName"
		rSleep = new RandomSleep(); // Create an instance of RandomSleep.
	} // end of the constructor for class "Reader"

	public void run() {
		for (int I = 0; I < 5; I++) {
			System.out
					.println("Reader " + myName + " wants to read.  " + "Beforehand, readcount is " + Synch.readCount);

			// Do acquire on the "read" semaphore, to simulate the queue of readers
			try {
				Synch.read.acquire();
			} catch (Exception e) {
			}

			// acquire the "mutexRC" semaphore to gain exclusive access to "readCount"
			try {
				Synch.mutexRC.acquire();
			} catch (Exception e) {
				// TODO: handle exception
			}

			// If a writer is active, the first reader waits for "wrt" while still
			// holding on to "read". Other readers, who arrive later, will get
			// held up waiting for "read".
			Synch.readCount++;
			if (Synch.readCount == 1) {
				try {
					Synch.wrt.acquire();
				} catch (Exception e) {
				}
			}
			// Now we have permission to start reading.
			// Print a message and release "read".
			System.out.println("Reader " + myName + " is now reading.  " + "Readcount is " + Synch.readCount);
			Synch.mutexRC.release();
			Synch.read.release();

			// Simulate the time taken for reading
			rSleep.doSleep(1, 200);

			// We're finished reading. Decrement readcount. If we are the last
			// reader, then signal "wrt". The signal to "wrt" will either wake up
			// a waiting writer, or set the semaphore value to 1, so that a future
			// writer or reader can go without waiting.
			try {
				Synch.mutexRC.acquire();
			} catch (Exception e) {
			}
			Synch.readCount--;
			System.out.println(
					"Reader " + myName + " is finished reading.  " + "Readcount decremented to " + Synch.readCount);
			if (Synch.readCount == 0)
				Synch.wrt.release();
			Synch.mutexRC.release();

			// Simulate "doing something else".
			rSleep.doSleep(1, 1000);
		} // end of "for" loop
	} // end of "run" method
} // end of class "Reader"
