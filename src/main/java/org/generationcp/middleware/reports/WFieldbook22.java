
package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook22 extends AbstractReporter {

	/**
	 * Enforces obtaining instances through the Factory
	 */
	protected WFieldbook22() {
	}

	@Override
	public Reporter createReporter() {
		return new WFieldbook22();
	}

	@Override
	public String getReportCode() {
		return "WFb22";
	}

	@Override
	public String getTemplateName() {
		return "WFb22_header.jasper";
	}

	@Override
	public Map<String, Object> buildJRParams(Map<String, Object> args, String studyName) {
		Map<String, Object> params = super.buildJRParams(args, studyName);

		Integer dummyInt = new Integer(111);

		params.put("tid", dummyInt);
		params.put("occ", 123);
		params.put("lid", "lid");
		params.put("program", "dummy program");
		params.put("trialAbbr", "trial_abbr");
		params.put("cycle", "dummy cycleA");

		return params;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> args) {
		JRDataSource dataSource = new JRBeanCollectionDataSource(args);
		return dataSource;
	}

}
