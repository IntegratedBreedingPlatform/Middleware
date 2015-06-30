
package org.generationcp.middleware.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerWatch {

	private long startTime;
	private String title;
	private static final Logger LOG = LoggerFactory.getLogger(TimerWatch.class);

	public TimerWatch(String title) {
		this.title = title;
		this.startTime = System.nanoTime();
	}

	public void stop() {
		double elapsedTime = ((double) System.nanoTime() - this.startTime) / 1000000000;
		if (TimerWatch.LOG != null) {
			TimerWatch.LOG.debug("##" + this.title + " = " + elapsedTime + " sec");
		} else {
			Debug.println(0, "##" + this.title + " = " + elapsedTime + " sec");
		}
	}

	public void restart(String title) {
		if (this.startTime > 0) {
			this.stop();
		}
		this.title = title;
		this.startTime = System.nanoTime();
	}

}
