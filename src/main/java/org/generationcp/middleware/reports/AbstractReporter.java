
package org.generationcp.middleware.reports;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

/**
 * Defines the base class for all Reporters. Each reporter pretending to be discovered by ReporterFactory must extend this class
 * 
 * @author jarojas
 *
 */
abstract class AbstractReporter implements Reporter {

	private String fileNameExpr = this.getReportCode() + "-{tid}";
	private String fileName = null;
	private final Pattern fileNameParamsPattern = Pattern.compile("\\{[\\w-_]*\\}");
	private boolean isParentsInfoRequired = false;
	JasperPrint jrPrint;

	private static final Logger LOG = LoggerFactory.getLogger(AbstractReporter.class);
	
	@Override
	public String toString() {
		return String.format("Report[%s : %s]", this.getReportCode(), this.getClass().getSimpleName());
	}

	/**
	 * Uses the data passed to build a JasperPrint, which can be used to generate a file, an outputStream or any other format for
	 * distribution.
	 */
	@Override
	public JasperPrint buildJRPrint(Map<String, Object> args) throws JRException {

		Map<String, Object> jrParams = null;
		JRDataSource jrDataSource = null;

		if (null != args) {
			jrParams = this.buildJRParams(args);
			this.fileName = this.buildOutputFileName(jrParams);

			if (args.containsKey("dataSource")) {
				jrDataSource = this.buildJRDataSource((Collection<?>) args.get("dataSource"));
			}

		}
		InputStream jasperReport = this.getTemplateInputStream();
		this.jrPrint = JasperFillManager.fillReport(jasperReport, jrParams, jrDataSource);

		return this.jrPrint;

	}

	/**
	 * Returns a Map with the parameters required for creating a JasperPrint for this Reporter. This method configures some basic jasper
	 * options like language and text formatting; subclasses extending AbstractReporter may add extra parameters to fill in its particular
	 * template.
	 * 
	 * @return Map of parameters for a JarperPrint
	 */
	@Override
	public Map<String, Object> buildJRParams(Map<String, Object> args) {
		Map<String, Object> params = new HashMap<String, Object>();

		if (args.containsKey("datePattern")) {
			params.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, args.get("datePattern"));
		}

		if (args.containsKey("numberPattern")) {
			params.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, args.get("numberPattern"));
		}

		if (args.containsKey("locale")) {
			if (args.get("locale") instanceof Locale) {
				params.put(JRParameter.REPORT_LOCALE, args.get("locale"));
			} else {
				params.put(JRParameter.REPORT_LOCALE, new Locale(args.get("locale").toString()));
			}
		} else {
			params.put(JRParameter.REPORT_LOCALE, new Locale("en_US"));
		}
		ClassLoader loader = AbstractReporter.class.getClassLoader();
		params.put("SUBREPORT_DIR", loader.getResource("jasper/").toExternalForm());
		return params;
	}

	/**
	 * Obtains the input stream to the .jasper file, specified by getFileName() The reason behind using input stream is that so it can work
	 * even inside a jar file
	 * 
	 * @param jasperFileName The name of the compiled .jasper file.
	 * @return
	 */
	public InputStream getTemplateInputStream() {
		String baseJasperDirectory = "jasper/";
		String jasperFileName = this.getTemplateName();
		ClassLoader loader = AbstractReporter.class.getClassLoader();

		if (!jasperFileName.endsWith(".jasper")) {
			jasperFileName = jasperFileName + ".jasper";
		}

		return loader.getResourceAsStream(baseJasperDirectory + jasperFileName);
	}

	/**
	 * Obtains the input stream of the .jrxml file for dynamic compiling of the report The reason behind using input stream is that so it
	 * can work even inside a jar file
	 *
	 * @return
	 */
	public InputStream getTemplateCompileInputStream() {
		String baseJasperDirectory = "jasper/";
		String jasperFileName = this.getTemplateName();
		ClassLoader loader = AbstractReporter.class.getClassLoader();

		if (!jasperFileName.endsWith(".jasper")) {
			jasperFileName = jasperFileName + ".jrxml";
		}

		return loader.getResourceAsStream(baseJasperDirectory + jasperFileName);
	}

	@Override
	public void setFileNameExpression(String fileNameExpr) {
		this.fileNameExpr = fileNameExpr;
	}

	protected String buildOutputFileName(Map<String, Object> jrParams) {
		String fileName = this.fileNameExpr;

		Matcher paramsMatcher = this.fileNameParamsPattern.matcher(this.fileNameExpr);

		while (paramsMatcher.find()) {
			String paramName = paramsMatcher.group().replaceAll("[\\{\\}]", "");

			if (null == jrParams || null == jrParams.get(paramName)) {
				fileName = fileName.replace(paramsMatcher.group(), "");
			} else {
				fileName = fileName.replace(paramsMatcher.group(), jrParams.get(paramName).toString());
			}
		}

		return fileName;
	}

	@Override
	public void asOutputStream(OutputStream output) throws BuildReportException {
		if (null != this.jrPrint) {
			try {
				JasperExportManager.exportReportToPdfStream(this.jrPrint, output);
			} catch (JRException e) {
				AbstractReporter.LOG.error("Exporting report to PDF was not successful", e);
			}
		} else {
			throw new BuildReportException(this.getReportCode());
		}
	}

	/**
	 * Does not set the input and output of this exporter, only returns a pre-configured xlsx exporter.
	 * 
	 * @return
	 */
	protected JRXlsxExporter createDefaultExcelExporter() {
		JRXlsxExporter ex = new JRXlsxExporter();

		SimpleXlsReportConfiguration jrConfig = new SimpleXlsReportConfiguration();
		jrConfig.setOnePagePerSheet(false);
		jrConfig.setDetectCellType(true);
		jrConfig.setIgnoreCellBorder(true);
		jrConfig.setWhitePageBackground(true);

		return ex;
	}

	@Override
	public String getFileName() {
		return this.fileName + "." + this.getFileExtension();
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getFileExtension() {
		return "pdf";
	}

	@Override
	public boolean isParentsInfoRequired() {
		return this.isParentsInfoRequired;
	}

	protected void setParentInfoRequired(boolean isParentsInfoRequired) {
		this.isParentsInfoRequired = isParentsInfoRequired;
	}
}
