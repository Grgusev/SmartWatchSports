package dk.reflevel.viewpager;

import com.squareup.otto.Bus;

public class EventBus {
	private static final Bus sBus = new Bus();

	public static Bus getInstance() {
		return sBus;
	}

	private EventBus() {
		// do nothing here, EventBus is just a keeper for real a Bus instance
	}

}
