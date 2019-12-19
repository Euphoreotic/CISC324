
//This is a wrapper class for java.util.concurrent.Semaphore.  It provides acquire()
//and release() methods that work as usual, but that also have the side-effect of 
//calling timeSim routines, so that timeSim can keep track of how many threads
//are still computing at the current simulated time.

public class Semaphore {
	java.util.concurrent.Semaphore s; // A semaphore from the Java concurrency package
	int semCounter; // This is the integer value of the semaphore.
	java.util.concurrent.Semaphore semMutex;

	// --------------- constructor -------------
	Semaphore(int permit, boolean fifo) {
		s = new java.util.concurrent.Semaphore(permit, fifo);
		semCounter = permit;
		semMutex = new java.util.concurrent.Semaphore(1, true);
	}

	// --------------- acquire -------------------
	void acquire() {
		try {
			semMutex.acquire();
		} catch (Exception e) {
		}

		if (semCounter >= 1) { // this threas is not going to be waiting
			semCounter--;
			try {
				s.acquire();
			} catch (Exception e) {
				System.out.println(e);
			}
			semMutex.release();
		} else {
			// This thread will have to wait on the acquire.
			// Decrement semCounter before the acquire(), so that another thread
			// cannot jump in and sample semCounter when it is >= 1.
			semCounter--;
			semMutex.release();

			Synch.timeSim.threadComputationDoneForNow();

			try {
				s.acquire();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	// ----------------- release ---------------
	void release() {
		try {
			semMutex.acquire();
		} catch (Exception e) {
		}

		if (semCounter < 0) {
			// semCounter is less than zero, so there are definitely threads waiting.
			// Call timesim to let it know that one thread will be woken up.
			Synch.timeSim.threadComputationStarting();
			semCounter++;
			s.release();
		} else { // No threads will be awakened. Just call release().
			semCounter++;
			s.release();
		}
		semMutex.release();
	}
}