package org.generationcp.middleware.reports;

import org.generationcp.middleware.exceptions.MiddlewareException;

public class BuildReportException extends MiddlewareException {

	private static final long serialVersionUID = 1L;
	private String key;

	@Override
	public String getMessage() {
		return "Datasource for report with key ["+key+"] has not been set.";
	}
	
	public BuildReportException(String key) {
		super(key);
		this.key = key;
	}
}
