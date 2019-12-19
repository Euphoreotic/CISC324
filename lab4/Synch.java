
//This file defines class "Synch".  This class contains all the semaphores
//and variables needed to coordinate the car simulation.

public class Synch {

	public static TimeSim timeSim; // this class provides an accurate "sleep"
									// function.

//*** Declare your variables and semaphores here.  You might want to have two semaphores, one for use
//*** by waiting eastbound cars, and the other for use by waiting westbound cars.
//*** You might also want to have counters for the number of cars waiting in each direction.  You
//*** also need some variable to represent whether the light is green westbound, green eastbound, or
//*** red in both directions.
//*** If you use counters, you need to have a mutex semaphore to protect access to the counters.
//
//Which variables/semaphores you need depends on how you decide to code your solution.

	// semaphores used by eastbound and westbound cars
	public static Semaphore WBsema;
	public static Semaphore EBsema;
	
	//semaphores used to protect access to the traffic lights and counters
	public static Semaphore lightSema;
	public static Semaphore carsWaitingSema;

	public static int debug; // set this to 1 or 2 to get extra output for debugging TimeSim.java
	
	// variable to represent the current status of the light, the value is false if the light is red, green if the light is green
	public static boolean westboundGreenLight = false;
	public static boolean eastboundGreenLight = false;
	
	// counter for the number of cars waiting to travel each way
	public static int carsWaitingWB = 0;
	public static int carsWaitingEB = 0;

}