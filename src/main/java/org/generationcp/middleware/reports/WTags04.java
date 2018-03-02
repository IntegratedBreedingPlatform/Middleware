
package org.generationcp.middleware.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WTags04 extends AbstractReporter {

	private static final Logger LOG = LoggerFactory.getLogger(WTags04.class);

	protected List<List<String>> dataSource = new ArrayList<>();
	protected Map<String, String> studyMeta = new HashMap<>();

	protected WTags04() {
	}

	@Override
	public Reporter createReporter() {
		Reporter r = new WTags04();
		r.setFileNameExpression("TAGS04_{trialName}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WTAG04";
	}

	@Override
	public String getTemplateName() {
		return null;
	}

	@Override
	public String getFileExtension() {
		return "txt";
	}

	@Override
	@SuppressWarnings("unchecked")
	public JasperPrint buildJRPrint(Map<String, Object> args, String studyName) throws JRException {

		Map<String, Object> jrParams = null;

		if (null != args) {
			jrParams = this.buildJRParams(args, studyName);
			this.setFileName(super.buildOutputFileName(jrParams));

		}

		MeasurementRow[] entries = {};
		entries = ((Collection<MeasurementRow>) args.get("dataSource")).toArray(entries);

		this.dataSource.clear();
		this.studyMeta.clear();

		for (MeasurementVariable var : (List<MeasurementVariable>) args.get("studyConditions")) {
			this.studyMeta.put(var.getName(), var.getValue());
		}

		// add headers in first row of dataSource
		List<String> row = new ArrayList<>();
		for (MeasurementData data : entries[0].getDataList()) {
			row.add(data.getLabel());
		}
		this.dataSource.add(row);

		for (MeasurementRow measurementRow : entries) {
			row = new ArrayList<>();
			for (MeasurementData data : measurementRow.getDataList()) {
				row.add(data.getValue());
			}
			this.dataSource.add(row);
		}

		return null;

	}

	@Override
	public Map<String, Object> buildJRParams(Map<String, Object> args, String studyName) {
		Map<String, Object> params = super.buildJRParams(args, studyName);

		@SuppressWarnings("unchecked")
		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get("studyConditions");

		for (MeasurementVariable var : studyConditions) {
			String trialName = null;
			switch (var.getName()) {
				case "STUDY_TITLE":
					trialName = var.getValue();
					break;
			}

			if (null != trialName) {
				params.put("trial_name", trialName);
				break;
			}
		}

		return params;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> args) {
		return null;
	}

	@Override
	public void asOutputStream(OutputStream output) throws BuildReportException {
		try {
			StringBuilder sb = new StringBuilder();

			int rowSpan = 40;
			int rowSize = 40;

			sb.append(this.buildPrintTestRecord(rowSpan, rowSize));
			sb.append(this.buildPrintTestRecord(rowSpan, rowSize));

			for (int i = 1; i < this.dataSource.size(); i++) {
				sb.append(this.buildRecord(this.dataSource.get(i), this.dataSource.get(0), rowSpan, rowSize));
			}
			output.write(sb.toString().getBytes());
		} catch (IOException e) {
			WTags04.LOG.error("Unable to write to output stream", e);
			throw new BuildReportException(this.getReportCode());
		}
	}

	protected String buildRecord(List<String> row, List<String> headers, int rowSpan, int rowSize) {
		String study = null;
		String occ = null;
		String subProg = null;
		String type = null;
		String season = null;
		String entry = null;
		String pedigreeA = null;
		String pedigreeB = null;
		String selHistA = null;
		String selHistB = null;
		String entryType = null;

		study = this.studyMeta.get("STUDY_NAME");
		occ = this.studyMeta.get("TRIAL_INSTANCE");
		subProg = this.studyMeta.get("BreedingProgram");
		type = this.studyMeta.get("STUDY_TYPE"); // a type for nal,int, etc
		season = this.studyMeta.get("CROP_SEASON");

		for (int i = 0; i < headers.size(); i++) {
			switch (headers.get(i)) {
				case "ENTRY_NO":
					entry = row.get(i);
					break;
				case "CROSS":
					pedigreeA = row.get(i);
					pedigreeB = pedigreeA;
					pedigreeA =
							pedigreeA.length() > 40 ? pedigreeA.substring(0, pedigreeA.substring(0, 40).lastIndexOf("/") + 1) : pedigreeA;
					pedigreeB = pedigreeB.length() > 40 ? pedigreeB.substring(pedigreeB.lastIndexOf("/", 40) + 1, pedigreeB.length()) : "";
					break;
				case "DESIGNATION":
					selHistA = row.get(i);
					selHistB = selHistA;
					selHistA = selHistA.length() > 36 ? selHistA.substring(0, selHistA.substring(0, 36).lastIndexOf("-") + 1) : selHistA;
					selHistB = selHistB.length() > 36 ? selHistB.substring(selHistB.lastIndexOf("-", 36) + 1, selHistB.length()) : "";
					break;
				case "ENTRY_TYPE":
					entryType = row.get(i);
					break;
			}
		}

		// now format
		StringBuilder sb = new StringBuilder();

		sb.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.format(study, 30, true)).append(" OCC: ")
				.append(StringUtil.format(occ, 4, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
				.append(StringUtil.format(subProg, 3, true)).append(" ").append(StringUtil.format(type, 5, true)).append(" ")
				.append(StringUtil.format(season, 13, true)).append(StringUtil.format("ENTRY", 7, false)).append(" ")
				.append(StringUtil.format(entry, 6, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan - 15))
				.append(StringUtil.format("CIMMYT", 6, false)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan));

		if (entryType == null || !entryType.equals("T")) { // test entry, meaning Not-a-check.
			sb.append(StringUtil.format(pedigreeA, rowSize, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
					.append(StringUtil.format(pedigreeB, rowSize, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
					.append(StringUtil.format("", 4, true)).append(StringUtil.format(selHistA, 36, true)).append("\r\n")
					.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.format("", 4, true))
					.append(StringUtil.format(selHistB, 36, true));
		} else {
			sb.append(StringUtil.format("LOCAL CHECK ", rowSize, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan))
					.append(StringUtil.format("** CHECK **", rowSize, true)).append("\r\n").append(StringUtil.stringOf(" ", rowSpan * 2))
					.append("\r\n").append(StringUtil.stringOf(" ", rowSpan * 2));
		}

		return sb.append("\r\n\r\n").toString();
	}

	private String buildPrintTestRecord(int rowSpan, int rowSize) {
		return new StringBuilder().append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.stringOf("X", rowSize)).append("\r\n")
				.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.stringOf("X", rowSize)).append("\r\n")
				.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.stringOf("X", rowSize)).append("\r\n")
				.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.stringOf("X", rowSize)).append("\r\n")
				.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.stringOf("X", rowSize)).append("\r\n")
				.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.stringOf("X", rowSize)).append("\r\n")
				.append(StringUtil.stringOf(" ", rowSpan)).append(StringUtil.stringOf("X", rowSize)).append("\r\n\r\n").toString();
	}

}
