
package org.generationcp.middleware.reports;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public interface Reporter {

	/**
	 * Factory method, used to create new instances of this class.
	 * 
	 * @return An instance of this class
	 */
	Reporter createReporter();

	/**
	 * Gets the unique key associated to this reporter, and registered in ReportFactory
	 * 
	 * @return The key defining this reporter type.
	 */
	String getReportCode();

	/**
	 * Creates a Datasource for this concrete Report, if needed.
	 * 
	 * @param dataRecords a Collection with information to generate a data source.
	 * @return
	 */
	JRDataSource buildJRDataSource(Collection<?> dataRecords);

	/**
	 * Returns a Map with the parameters required for creating a JasperPrint for a concrete Reporter.
	 * 
	 * @return Map of parameters for a JarperPrint
	 */
	Map<String, Object> buildJRParams(Map<String, Object> args, String studyName);

	/**
	 * Creates a JarperPrint using beans and raw data passed in to this method. Concrete classes need to define the extraction process.
	 * 
	 * @param args a Map with a variety of objects with all information needed to fill in a jasper template.
	 * @return The processed JasperPrint (template + data)
	 * @throws JRException when method cannot create a valid JasperPrint with current inputs.
	 */
	JasperPrint buildJRPrint(Map<String, Object> args, String studyName) throws JRException;

	/**
	 * Retrieves the name of the jasper file to be used for constructing this report. The extension '.jasper' in the name is optional, but
	 * the file must exist in the classpath.
	 *
	 * @return
	 */
	String getTemplateName();

	/**
	 * Defines the expression for the file name generated for this Reporter. Examples of file name expressions and possible outputs
	 * (depending on output file format):
	 * 
	 * <pre>
	 *   report1: report1.pdf / report1.xls / report1.txt
	 *   fieldbook14: fieldbook14.pdf / fieldbook14.xls / fieldbook14.txt
	 *   CB-{trialId}: CB-25644.pdf / CB-889211.xls / CB-112.txt
	 *   Summer-{trialAbbr}_{cycle}: Summer-Obr_2014A.pdf / Summer-BW-14A.xls / Summer-Nur1_14B.xls
	 * </pre>
	 * 
	 * Parameters can be passed in expressions, and they will be replaced for the value of such parameter, defined in the Map returned from
	 * buildJRParams() method. If no parameter is found, it is just omitted.
	 *
	 * @param fileNameExpr the name expression. The default is {reportCode}-{tid}, where {reportCode} is the code of a concrete subclass,
	 *        and {tid} is a parameter in the parameters Map passed to the JRPrint, ignored if not found.
	 */
	void setFileNameExpression(String fileNameExpr);

	/**
	 * Returns a flag indicating a reporter will need information about direct progenitors (male and female). This flag just indicates if
	 * such information is required, proper extraction is responsibility of implementations. Data providers for this class should read this
	 * flag to avoid making unnecessary queries to the Database, improving performance.
	 */
	boolean isParentsInfoRequired();

	/**
	 * Creates a file name for the specified reporter by using the report name expression and applying proper parameters.
	 * 
	 * @return the constructed file name
	 */
	String getFileName();

	/**
	 * Returns the specific file extension for this reporter, without separator at the beginning; that is, it returns 'pdf' instead of
	 * '.pdf'.
	 * 
	 * @return the file extension for this reporter.
	 */
	String getFileExtension();

	/**
	 * Sends the file to the specified output stream
	 * 
	 * @param output Out where the report has to be sent. This can be a servlet, file or any other output stream.
	 * @throws BuildReportException If the JasperPrint is null or cannot generate a proper document.
	 */
	void asOutputStream(OutputStream output) throws BuildReportException;

}
