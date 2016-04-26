
package org.generationcp.middleware.reports;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDynamicReporter extends AbstractReporter {

	public static final String COL_EXPR_PREFIX = "col";
	public static final String COL_HEADER_EXPR_PREFIX = "header";
	private static final int TOTAL_PAGE_WIDTH = 800;

	// The whitespace between columns in pixels
	private static final int SPACE_BETWEEN_COLS = 0;

	// The height in pixels of an element in a row and column
	private static final int COLUMN_HEIGHT = 12;

	// The total height of the column header or detail band
	private static final int BAND_HEIGHT = 12;

	// The left and right margin in pixels
	private static final int MARGIN = 10;
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDynamicReporter.class);

	List<String> columnHeaders = null;

	@Override
	public JasperPrint buildJRPrint(Map<String, Object> args) throws JRException {

		Map<String, Object> jrParams = null;
		JRDataSource jrDataSource = null;
		JasperReport jasperReport = null;
		Collection<?> collectionDataSource = null;

		if (null != args) {
			jrParams = this.buildJRParams(args);
			this.setFileName(this.buildOutputFileName(jrParams));

			if (args.containsKey("dataSource")) {
				collectionDataSource = (Collection<?>) args.get("dataSource");
				this.columnHeaders = this.buildColumnHeaders(collectionDataSource.iterator().next());

				jrDataSource = this.buildJRDataSource(collectionDataSource);
				// needed to change to input stream since code can not parse if its inside jar file
				// for MFbNur and MFbTrial
				InputStream jasperInputStream = this.getTemplateCompileInputStream();
				JasperDesign jasperReportDesign = JRXmlLoader.load(jasperInputStream);

				this.addDynamicColumns(jasperReportDesign, this.columnHeaders.size());

				jasperReport = JasperCompileManager.compileReport(jasperReportDesign);
			}
			this.jrPrint = JasperFillManager.fillReport(jasperReport, jrParams, jrDataSource);

		}

		return this.jrPrint;

	}

	/**
	 * Creates the list of columns headers to be used when generating a dynamic Jasper Design.
	 *
	 * @param param any source object that contains the column headers
	 * @return The plains String List with the header's names.
	 */
	protected List<String> buildColumnHeaders(Object param) {

		MeasurementRow row = (MeasurementRow) param;
		List<String> columnHeaders = new ArrayList<>();

		for (MeasurementData rowData : row.getDataList()) {
			columnHeaders.add(rowData.getLabel());
		}

		return columnHeaders;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> dataRecords) {

		return new DynamicColumnDataSource(this.columnHeaders, this.convertBeanCollectionToStringLists(dataRecords));
	}

	/**
	 * Converts a Collection of beans (usually passed via 'dataSource' parameter) to a List containing the records to be used as datasource,
	 * where each record is defined by a String List.
	 *
	 * @param dataRecords A collection of beans to be parsed
	 * @return A
	 */
	@SuppressWarnings("unchecked")
	protected List<List<String>> convertBeanCollectionToStringLists(Collection<?> dataRecords) {
		List<List<String>> dataSource = new ArrayList<>();

		for (MeasurementRow row : (Collection<MeasurementRow>) dataRecords) {
			List<String> sourceItem = new ArrayList<>();
			for (MeasurementData dataItem : row.getDataList()) {
				sourceItem.add(dataItem.getValue());
			}

			dataSource.add(sourceItem);
		}

		return dataSource;
	}

	/**
	 * Regenerates a JasperDesign, by adding it a Header Band for columns headers; and a detail band for displaying data records. The column
	 * width is uniformly distributed among columns.
	 *
	 * @param jasperDesign the design where both bands(header and detail) will be added to.
	 * @param numColumns the number of columns to generate.
	 * @throws JRException If the Jasper Design modification fails.
	 */
	private void addDynamicColumns(JasperDesign jasperDesign, int numColumns) throws JRException {

		JRDesignBand detailBand = new JRDesignBand();
		JRDesignBand headerBand = new JRDesignBand();

		JRDesignStyle normalStyle = this.getNormalStyle();
		JRDesignStyle columnHeaderStyle = this.getColumnHeaderStyle();
		jasperDesign.addStyle(normalStyle);
		jasperDesign.addStyle(columnHeaderStyle);

		int xPos = AbstractDynamicReporter.MARGIN;
		int columnWidth =
				(AbstractDynamicReporter.TOTAL_PAGE_WIDTH - AbstractDynamicReporter.SPACE_BETWEEN_COLS * (numColumns - 1)) / numColumns;

		for (int i = 0; i < numColumns; i++) {

			// Create a Column Field
			JRDesignField field = new JRDesignField();
			field.setName(AbstractDynamicReporter.COL_EXPR_PREFIX + i);
			field.setValueClass(java.lang.String.class);
			jasperDesign.addField(field);

			// Create a Header Field
			JRDesignField headerField = new JRDesignField();
			headerField.setName(AbstractDynamicReporter.COL_HEADER_EXPR_PREFIX + i);
			headerField.setValueClass(java.lang.String.class);
			jasperDesign.addField(headerField);

			// Add a Header Field to the headerBand
			headerBand.setHeight(AbstractDynamicReporter.BAND_HEIGHT);
			JRDesignTextField colHeaderField = new JRDesignTextField();
			colHeaderField.setX(xPos);
			colHeaderField.setY(0);
			colHeaderField.setWidth(columnWidth);
			colHeaderField.setHeight(AbstractDynamicReporter.COLUMN_HEIGHT);
			colHeaderField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
			colHeaderField.setStyle(columnHeaderStyle);
			JRDesignExpression headerExpression = new JRDesignExpression();
			headerExpression.setValueClass(java.lang.String.class);
			headerExpression.setText("$F{" + AbstractDynamicReporter.COL_HEADER_EXPR_PREFIX + i + "}");
			colHeaderField.setExpression(headerExpression);
			headerBand.addElement(colHeaderField);

			// Add text field to the detailBand
			detailBand.setHeight(AbstractDynamicReporter.BAND_HEIGHT);
			JRDesignTextField textField = new JRDesignTextField();
			textField.setX(xPos);
			textField.setY(0);
			textField.setWidth(columnWidth);
			textField.setHeight(AbstractDynamicReporter.COLUMN_HEIGHT);
			textField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
			textField.setStyle(normalStyle);
			JRDesignExpression expression = new JRDesignExpression();
			expression.setValueClass(java.lang.String.class);
			expression.setText("$F{" + AbstractDynamicReporter.COL_EXPR_PREFIX + i + "}");
			textField.setExpression(expression);
			detailBand.addElement(textField);

			xPos = xPos + columnWidth + AbstractDynamicReporter.SPACE_BETWEEN_COLS;
		}

		jasperDesign.setColumnHeader(headerBand);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

	}

	/**
	 * Helper method definig the predefined style to be used in Jasper elements.
	 *
	 * @return A default style with small font size.
	 */
	private JRDesignStyle getNormalStyle() {
		JRDesignStyle normalStyle = new JRDesignStyle();
		normalStyle.setName("Sans_Normal");
		normalStyle.setDefault(true);
		normalStyle.setFontName("SansSerif");
		normalStyle.setFontSize(8);
		normalStyle.setPdfFontName("Helvetica");
		normalStyle.setPdfEncoding("UTF-8");
		normalStyle.setPdfEmbedded(false);
		return normalStyle;
	}

	/**
	 * Helper method definig the predefined style to be used in Jasper columns headers.
	 *
	 * @return A default style with bold and small font size.
	 */
	private JRDesignStyle getColumnHeaderStyle() {
		JRDesignStyle columnHeaderStyle = new JRDesignStyle();
		columnHeaderStyle.setName("Sans_Header");
		columnHeaderStyle.setDefault(false);
		columnHeaderStyle.setFontName("SansSerif");
		columnHeaderStyle.setFontSize(10);
		columnHeaderStyle.setBold(true);
		columnHeaderStyle.setPdfFontName("Helvetica");
		columnHeaderStyle.setPdfEncoding("UTF-8");
		columnHeaderStyle.setPdfEmbedded(false);
		return columnHeaderStyle;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> buildJRParams(Map<String, Object> args) {
		Map<String, Object> params = super.buildJRParams(args);

		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get(STUDY_CONDITIONS_KEY);

		for (MeasurementVariable var : studyConditions) {
			switch (var.getName()) {
				case "SITE_NAME":
					params.put("site", var.getValue());
					break;
				case "STUDY_NAME":
					params.put("nursery", var.getValue());
					break;
				case "CROP_SEASON":
					params.put("season", var.getValue());
					break;
				case "BreedingProgram":
					params.put("seedPrep", "???????");
					break; // add seed prep. Condition
				case "TRIAL_INSTANCE":
					params.put("siteNum", var.getValue());
					break;
			}
		}

		return params;
	}

	@Override
	public String getFileExtension() {
		return "xlsx";
	}
}
