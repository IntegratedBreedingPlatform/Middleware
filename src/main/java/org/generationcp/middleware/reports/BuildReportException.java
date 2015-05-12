
package org.generationcp.middleware.reports;

public class BuildReportException extends Exception {

	private static final long serialVersionUID = 1L;
	private String key;

	@Override
	public String getMessage() {
		return "Datasource for report with key [" + key + "] has not been set or report is not built yet.";
	}

	public BuildReportException(String key) {
		this.key = key;
	}
}
