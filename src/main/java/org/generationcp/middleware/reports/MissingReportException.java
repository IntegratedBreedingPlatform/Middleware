package org.generationcp.middleware.reports;

public class MissingReportException extends Exception {

	private static final long serialVersionUID = 1L;
	private String key;

	@Override
	public String getMessage() {
		return "Factory cannot find reporter with code: "+key;
	}
	
	public MissingReportException(String key) {
		this.key = key;
	}
}
