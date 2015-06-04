
package org.generationcp.middleware.util;

import org.slf4j.Logger;

public class TimerWatch {

	private long startTime;
	private String title;
	private Logger logger;

	public TimerWatch(String title) {
		this.title = title;
		this.startTime = System.nanoTime();
	}

	public TimerWatch(String title, Logger logger) {
		this.title = title;
		this.logger = logger;
		this.startTime = System.nanoTime();
	}

	public void stop() {
		double elapsedTime = ((double) System.nanoTime() - this.startTime) / 1000000000;
		if (this.logger != null) {
			this.logger.debug("##" + this.title + " = " + elapsedTime + " sec");
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
