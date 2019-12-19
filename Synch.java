

// This file defines class "Synch".  This class contains all the semaphores
// and variables needed to coordinate the instances of the Reader and Writer
// classes.  

import java.util.concurrent.*;

public class Synch {

	// The mutexRC semaphore is used to protect the "readCount" counter
	public static Semaphore mutexRC;
	// The mutexWC semaphore is used to protect the "writeCount" counter
	public static Semaphore mutexWC;
	// semaphore for write operations
	public static Semaphore wrt;
	// semaphore for read operations
	public static Semaphore read;
	// counter for active readers
	public static int readCount = 0;
	// counter for writers
	public static int writeCount = 0;

} // end of class "Synch"
