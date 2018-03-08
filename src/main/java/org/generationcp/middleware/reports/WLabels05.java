
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

public class WLabels05 extends AbstractReporter {

	private static final Logger LOG = LoggerFactory.getLogger(WLabels05.class);

	protected List<List<String>> dataSource = new ArrayList<>();
	protected Map<String, String> studyMeta = new HashMap<>();

	protected WLabels05() {
	}

	@Override
	public Reporter createReporter() {
		final Reporter r = new WLabels05();
		r.setFileNameExpression("LABEL05_{trialName}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WLBL05";
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
	public JasperPrint buildJRPrint(final Map<String, Object> args, String studyName) throws JRException {

		Map<String, Object> jrParams = null;

		if (null != args) {
			jrParams = this.buildJRParams(args, studyName);
			this.setFileName(super.buildOutputFileName(jrParams));

		}

		MeasurementRow[] entries = {};
		entries = ((Collection<MeasurementRow>) args.get("dataSource")).toArray(entries);

		this.dataSource.clear();
		this.studyMeta.clear();

		for (final MeasurementVariable var : (List<MeasurementVariable>) args.get("studyConditions")) {
			this.studyMeta.put(var.getName(), var.getValue());
		}

		// add headers in first row of dataSource
		List<String> row = new ArrayList<>();
		for (final MeasurementData data : entries[0].getDataList()) {
			row.add(data.getLabel());
		}
		this.dataSource.add(row);

		for (final MeasurementRow measurementRow : entries) {
			row = new ArrayList<>();
			for (final MeasurementData data : measurementRow.getDataList()) {
				row.add(data.getValue());
			}
			this.dataSource.add(row);
		}

		return null;

	}

	@Override
	public Map<String, Object> buildJRParams(final Map<String, Object> args, String studyName) {
		final Map<String, Object> params = super.buildJRParams(args, studyName);

		@SuppressWarnings("unchecked")
		final List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get("studyConditions");

		for (final MeasurementVariable var : studyConditions) {
			String trialName = null;
			if ("STUDY_TITLE".equalsIgnoreCase(var.getName())) {
				trialName = var.getValue();
			}

			if (trialName != null) {
				params.put("trialName", trialName);
				break;
			}
		}

		return params;
	}

	@Override
	public JRDataSource buildJRDataSource(final Collection<?> args) {
		return null;
	}

	@Override
	public void asOutputStream(final OutputStream output) throws BuildReportException {
		try {
			final int columns = 3;
			final int colSpan = 20;
			final StringBuilder sb = new StringBuilder();
			sb.append(this.buildPrintTestRecord(columns, colSpan));

			for (int i = 1; i < this.dataSource.size() + 1; i = i + columns) {
				final List<List<String>> items =
						this.dataSource.subList(i, i + columns < this.dataSource.size() ? i + columns : this.dataSource.size());

				sb.append(this.buildRecord(items, this.dataSource.get(0), colSpan));
			}
			output.write(sb.toString().getBytes());
		} catch (final IOException e) {
			WLabels05.LOG.error("Unable to write to output stream", e);
			throw new BuildReportException(this.getReportCode());
		}
	}

	protected String buildRecord(final List<List<String>> rows, final List<String> headers, final int colSpan) {

		final List<Map<String, String>> records = this.extractRecordData(rows, headers);

		final StringBuilder sb = new StringBuilder();

		final int columns = rows.size();

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
					.append(StringUtil.format(records.get(i).get("season"), 13, true)).append(StringUtil.format("ENTRY", 7, false))
					.append(" ").append(StringUtil.format(records.get(i).get("entry"), 9, true))
					.append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		// CIMMYT text must be have less left margin from the other lines
		sb.append(StringUtil.stringOf(" ", colSpan - 18));
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

		sb.append(StringUtil.stringOf(" ", colSpan));
		for (int i = 0; i < columns; i++) {
			sb.append(StringUtil.format("Local Check", 11, true)).append(StringUtil.stringOf(" ", 29))
					.append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
		}

		return sb.append("\r\n").toString();
	}

	private String buildPrintTestRecord(final int columns, final int colSpan) {
		final StringBuilder sb = new StringBuilder();
		final int colSize = 40;
		final int rows = 7;

		for (int r = 0; r < rows; r++) {
			sb.append(StringUtil.stringOf(" ", colSpan));
			for (int i = 0; i < columns; i++) {
				sb.append(StringUtil.stringOf("X", colSize)).append(i + 1 == columns ? "\r\n" : StringUtil.stringOf(" ", colSpan));
			}
		}

		return sb.append("\r\n").toString();
	}

	protected List<Map<String, String>> extractRecordData(final List<List<String>> rows, final List<String> headers) {
		final List<Map<String, String>> mapRows = new ArrayList<>();

		for (int j = 0; j < rows.size(); j++) {

			final Map<String, String> record = new HashMap<>();
			final List<String> row = rows.get(j);

			String pedigreeA = null;
			String pedigreeB = null;
			String selHistA = null;
			String selHistB = null;

			// retrieve trial instance no from study condition when the study is in nursery type
			// otherwise, from the measurement row
			if ("Nursery".equalsIgnoreCase(this.studyMeta.get("STUDY_TYPE"))) {
				record.put("occ", this.studyMeta.get("TRIAL_INSTANCE"));
			}

			record.put("study", this.studyMeta.get("STUDY_NAME"));

			// TODO: Needs to finalize the variable needed for the following fields
			// subProg
			// type - a type for nal,int, etc
			// season
			record.put("subProg", this.studyMeta.get("BreedingProgram"));
			record.put("type", this.studyMeta.get("STUDY_TYPE"));
			record.put("season", this.studyMeta.get("CROP_SEASON"));

			for (int i = 0; i < headers.size(); i++) {
				switch (headers.get(i)) {
					case "TRIAL_INSTANCE":
						record.put("occ", row.get(i));
						break;
					case "ENTRY_NO":
						record.put("entry", row.get(i));
						break;
					case "CROSS":
						pedigreeA = row.get(i);
						pedigreeB = pedigreeA;
						pedigreeA =
								pedigreeA.length() > 40 ? pedigreeA.substring(0, pedigreeA.substring(0, 40).lastIndexOf("/") + 1)
										: pedigreeA;
						pedigreeB =
								pedigreeB.length() > 40 ? pedigreeB.substring(pedigreeB.lastIndexOf("/", 40) + 1, pedigreeB.length()) : "";
						record.put("pedigreeA", pedigreeA);
						record.put("pedigreeB", pedigreeB);
						break;
					case "DESIGNATION":
						selHistA = row.get(i);
						selHistB = selHistA;
						selHistA =
								selHistA.length() > 36 ? selHistA.substring(0, selHistA.substring(0, 36).lastIndexOf("-") + 1) : selHistA;
						selHistB = selHistB.length() > 36 ? selHistB.substring(selHistB.lastIndexOf("-", 36) + 1, selHistB.length()) : "";
						record.put("selHistA", selHistA);
						record.put("selHistB", selHistB);
						break;
					case "PLOT_NO":
						record.put("plot", row.get(i));
						break;
					default:
						break;
				}
			}
			mapRows.add(record);
		}

		return mapRows;
	}

	public void setStudyMeta(final Map<String, String> studyMeta) {
		this.studyMeta = studyMeta;
	}

}
