package org.generationcp.middleware.util;

import java.util.Date;

public class CustomClock implements Clock {

	private Date now;

	public CustomClock() {
		this.now = new Date();
	}

	public Date now() {
		return now;
	}

	public void set(Date date) {
		now = date;
	}
}
