
//This file defines class "TrafficLights".

//This code uses
// class Synch, which defines the semaphores and variables
//              needed for synchronizing the traffic lights.

public class TrafficLights extends Thread {

	// constants for the time that the traffic lights will be active for
	int greenLightTime = 500;
	int redLightTime = 100;

	public TrafficLights() {
		// Call threadStart to let the timeSim scheduler know that another
		// thread is starting. timeSim needs to know how many threads there
		// are, so that it can accurately judge when all threads have finished
		// their current computation, so that simulated time can be advanced.
		Synch.timeSim.threadStart();
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println("The westbound light is now green.");

			// acquire lightSema and carsWaitingSema to get exclusive access to westboundGreenLight and carsWaitingWB  
			// change the westbound light to green
			Synch.lightSema.acquire();
			Synch.westboundGreenLight = true;

			// for each car waiting to go westbound, release WBsema once, so the car is able to cross
			Synch.carsWaitingSema.acquire();
			for (int carsWaiting = Synch.carsWaitingWB; carsWaiting > 0; carsWaiting--) {
				Synch.carsWaitingWB--;
				Synch.WBsema.release();
			}
			Synch.carsWaitingSema.release();
			Synch.lightSema.release();

			// the green light is active for the specified amount of time
			Synch.timeSim.doSleep(greenLightTime);

			System.out.println("The westbound light is now red.");
			
			// change the westbound light to red, now both lights are red for the specified amount of time
			Synch.lightSema.acquire();
			Synch.westboundGreenLight = false;
			Synch.lightSema.release();
			Synch.timeSim.doSleep(redLightTime);

			System.out.println("The eastbound light is now green.");

			// acquire lightSema and carsWaitingSema to get exclusive access to eastboundGreenLight and carsWaitingEB  
			// change the eastbound light to green
			Synch.lightSema.acquire();
			Synch.eastboundGreenLight = true;

			// for each car waiting to go eastbound, release EBsema once, so the car is able to cross
			Synch.carsWaitingSema.acquire();
			for (int carsWaiting = Synch.carsWaitingEB; carsWaiting > 0; carsWaiting--) {
				Synch.carsWaitingEB--;
				Synch.EBsema.release();
			}
			Synch.carsWaitingSema.release();
			Synch.lightSema.release();

			// the green light is active for the specified amount of time
			Synch.timeSim.doSleep(greenLightTime);

			System.out.println("The eastbound light is now red.");
			
			// change the eastbound light to red, now both lights are red for the specified amount of time
			Synch.lightSema.acquire();
			Synch.eastboundGreenLight = false;
			Synch.lightSema.release();
			Synch.timeSim.doSleep(redLightTime);
		}
		Synch.timeSim.threadEnd();
	}

}
