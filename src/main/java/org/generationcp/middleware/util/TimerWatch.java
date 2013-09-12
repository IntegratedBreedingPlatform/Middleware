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
		if (logger != null) {
			logger.debug("##" + title + " = " + elapsedTime + " sec");
		} else {
			System.out.println("##" + title + " = " + elapsedTime + " sec");
		}
	}
	
	public void restart(String title) {
		if (startTime > 0) {
			stop();
		}
		this.title = title;
		this.startTime = System.nanoTime();
	}
	
}
