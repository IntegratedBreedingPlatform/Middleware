package org.generationcp.middleware.reports;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

public class WFieldTags extends AbstractReporter{

	@Override
	public Reporter createReporter() {
		return new WFieldTags();
	}

	@Override
	public String getReportCode() {
		return "WFieldTags";
	}

	@Override
	public String getTemplateName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> dataRecords) {
		// TODO Auto-generated method stub
		return null;
	}
	
}