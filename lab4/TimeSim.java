
//This file defines class "TimeSim".  This class contains all the
//variables and methods needed to handle the passage of simulated time.
//Threads can call timeSim.doSleep(n) to "sleep" for n time units, or
//timeSim.doSleep(n,m) to "sleep" for k time units, where k is randomly
//chosen in the range n to m.
import java.util.concurrent.*;

public class TimeSim {

	// Class "Event" represents an event that lies in the future. The event
	// consists of a simulated time, as well as a semaphore that should
	// receive a release() at that time.
	class Event {
		public int wakeupTime;
		public Semaphore sem; // This semaphore should receive a
								// release() when the simulated time
								// is equal to wakeupTime
	}

	private int numThreads; // numThreads is the total number of threads
							// that are making use of class TimeSim.

	private int threadsComputing; // This is the number of threads that are
	// still computing at the current time. Wait until all threads are done before
	// advancing time. Threads are done when they are waiting at an acquire(); this
	// happens because the thread calls acquire() or doSleep().

	private int time; // time is the current simulated time. threadComputationDoneForNow()
	// advances time when all threads are waiting for a semaphore. That wakes up
	// those threads that are in the shortest sleep.

	private final int MAX_EVENTS = 100;// this is the maximum number of threads
										// that can be handled, since each
										// thread produces at most one event.
										// (An event is produced by calling
										// timeSim.doSleep)
	private Event[] events; // An array of events, in unsorted order.
							// events[i].wakeupTime = -1 indicates that
							// events[i] is currently unused.

	private java.util.concurrent.Semaphore timeMutex; // This is a normal semaphore
	// (as defined in the Java concurrency package) used only within
	// TimeSim to ensure mutual exclusion

	// ------------------- constructor ---------------
	public TimeSim() {
		numThreads = 0;
		threadsComputing = 0;
		time = 0;

		events = new Event[MAX_EVENTS];

		for (int i = 0; i < MAX_EVENTS; i++) {
			events[i] = new Event();
			events[i].wakeupTime = -1;
			events[i].sem = new Semaphore(0, true);
		}
		timeMutex = new java.util.concurrent.Semaphore(1, true);
	}

	// ------------------- threadStart ---------------
	public void threadStart() {
		try {
			timeMutex.acquire();
		} catch (Exception e) {
			System.out.println(e);
		}
		numThreads++;
		threadsComputing++;
		if (numThreads > MAX_EVENTS) {
			System.out.println("You have called timeSim.threadStart more than " + MAX_EVENTS
					+ " times. This overflows the events array.  ");
			System.out.println("If you need this many threads, then increase MAX_EVENTS in TimeSim.java.");
		}
		timeMutex.release();
	}

	// ------------------- threadEnd ---------------
	public void threadEnd() {
		try {
			timeMutex.acquire();
		} catch (Exception e) {
			System.out.println(e);
		}
		if (numThreads == 0)
			System.out.println("Error: threadEnd called more often than threadStart!");
		numThreads--;
		threadsComputing--;
		if (Synch.debug >= 2)
			System.out.println("A thread has ended.  There are " + numThreads + " threads left, and " + threadsComputing
					+ " of these are computing now, at time " + time);
		if (threadsComputing == 0)
			advanceTime();
		timeMutex.release();
	}

	// ------------------- curTime -----------------------------------
	public int curTime() {
		return time;
	}

	// ------------------- threadComputationDoneForNow ---------------
	// This method is called by acquire. Once all threads are stuck at
	// an acquire, advance time to the next simulated time at which a
	// thread is supposed to wake up.
	public void threadComputationDoneForNow() {
		try {
			timeMutex.acquire();
		} catch (Exception e) {
			System.out.println(e);
		}
		threadsComputing--;
		if (Synch.debug >= 2)
			System.out.println("At time " + time + ", a thread computation is done." + threadsComputing
					+ " threads are still computing");
		if (threadsComputing < 0) {
			System.out.println("Error in timeSim: inconsistent number of threads.  ");
			System.out.println("Maybe some threads forgot to call timeSim.threadStart");
		}
		if (threadsComputing == 0)
			advanceTime();
		timeMutex.release();
	}

	// ------------------- threadComputationStarting ---------------
	// This method is called by release. It indicates that another
	// thread has code to execute at the current simulated time. We have
	// to wait for all of these threads to finish, before advancing time.
	public void threadComputationStarting() {

		threadsComputing++;
		if (Synch.debug >= 2)
			System.out.println("At time " + time + " a thread computation is starting. " + threadsComputing
					+ " threads are now computing");

	}

	// ------------------- doSleep (two parameters) ------------------------
	public void doSleep(int lower, int upper) {
		if ((lower >= 0) && (upper >= lower)) {
			int n = (int) ((upper - lower) * Math.random()) + lower;
			doSleep(n);
		} else
			System.out.println("Invalid parameters to TimeSim.doRandomSleep()");
	}

	// ------------------- doSleep (one parameter) -------------------------
	// This method is called when a thread wants to sleep. Create an
	// event that records when we should wake up. Then acquire() a semaphore
	// associated with the event. This semaphore will be released by advanceTime()
	// once the simulated time has advanced up to the wake-up time for this thread.
	public void doSleep(int n) {
		try {
			timeMutex.acquire();
		} catch (Exception e) {
			System.out.println(e);
		}
		int waketime = time + n;

		// Look through events, to find an unused entry.
		int eventIndex = 0;
		while (events[eventIndex].wakeupTime != -1)
			eventIndex++;

		events[eventIndex].wakeupTime = waketime;
		if (Synch.debug >= 2) {
			System.out.print("List of event wakeupTimes after doSleep(" + n + "):");
			for (int j = 0; j < MAX_EVENTS; j++) {
				if (events[j].wakeupTime != -1)
					System.out.print(" " + events[j].wakeupTime);
			}
			System.out.println(".");
		}
		// Release mutex before making this thread wait.
		timeMutex.release();
		events[eventIndex].sem.acquire();
	}

	// ------------------- advanceTime ------------------------------
	// This is a private method that advances time to the wakeupTime of
	// of the soonest event, which is stored in events[0]. Wake up all threads
	// that have this event time.
	// The caller already holds timeMutex.

	private void advanceTime() {
		if (Synch.debug >= 3)
			System.out.println("starting advanceTime");

		// Find the smallest wakeupTime in events.
		// If events[i].wakeupTime == -1, that event is unused.
		int minTime = -1;
		for (int i = 0; i < MAX_EVENTS; i++)
			if (events[i].wakeupTime != -1)
				if ((minTime == -1) | (events[i].wakeupTime < minTime))
					minTime = events[i].wakeupTime;
		if (minTime == -1) {
			// System.out.println("There are no more events.");
		} else {
			time = minTime;
			for (int i = 0; i < MAX_EVENTS; i++) {
				if (events[i].wakeupTime == time) {
					events[i].wakeupTime = -1;
					events[i].sem.release();
				}
			}
			if (Synch.debug >= 1) {
				System.out.print("Advanced time to " + time + ". Remaining wakeupTimes are:");
				for (int j = 0; j < MAX_EVENTS; j++) {
					if (events[j].wakeupTime != -1)
						System.out.print(" " + events[j].wakeupTime);
				}
				System.out.println(".");
			}
		}
	}

} // end of class "TimeSim"