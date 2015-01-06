package org.generationcp.middleware.reports;

import org.generationcp.middleware.exceptions.MiddlewareException;

public class MissingReportException extends MiddlewareException {

	private static final long serialVersionUID = 1L;
	private String key;

	@Override
	public String getMessage() {
		return "Factory cannot find reporter with code: "+key;
	}
	
	public MissingReportException(String key) {
		super(key);
		this.key = key;
	}
}
