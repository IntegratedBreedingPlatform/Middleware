
package org.generationcp.middleware.reports;

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.util.StringUtil;

public class WLabels21 extends WLabels05 {

	@Override
	public Reporter createReporter() {
		Reporter r = new WLabels21();
		r.setFileNameExpression("LABEL21_{trial_name}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WLBL21";
	}

	@Override
	protected String buildRecord(List<List<String>> rows, List<String> headers, int colSpan) {

		List<Map<String, String>> records = this.extractRecordData(rows, headers);

		StringBuilder sb = new StringBuilder();

		int columns = rows.size();

		// now format
		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.format(records.get(i).get("study"), 30, true)).append(" OCC: ")
					.append(StringUtil.format(records.get(i).get("occ"), 4, true))
					.append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.format(records.get(i).get("subProg"), 3, true)).append(" ")
					.append(StringUtil.format(records.get(i).get("type"), 5, true)).append(" ")
					.append(StringUtil.format(records.get(i).get("season"), 6, true)).append(StringUtil.format("PLOT", 5, false))
					.append(" ").append(StringUtil.format(records.get(i).get("plot"), 6, true))
					.append(StringUtil.format("ENTRY", 5, false)).append(" ")
					.append(StringUtil.format(records.get(i).get("entry"), 6, true))
					.append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.format("CIMMYT", 10, false)).append(StringUtil.stringOf(" ", 30))
					.append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.format(records.get(i).get("pedigreeA"), 40, true)).append(
					i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.format(records.get(i).get("pedigreeB"), 40, true)).append(
					i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.stringOf(" ", 4)).append(StringUtil.format(records.get(i).get("selHistA"), 36, true))
					.append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.stringOf(" ", 4)).append(StringUtil.format(records.get(i).get("selHistB"), 36, true))
					.append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		return sb.append("\r\n").toString();
	}

}
