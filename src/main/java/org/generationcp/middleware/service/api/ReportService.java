
package org.generationcp.middleware.service.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.reports.BuildReportException;
import org.generationcp.middleware.reports.Reporter;

public interface ReportService {

	/**
	 * Returns a JasperPrint instance representing the generated report. This may be required when somo post processing is needed. either as
	 * a physical file, or read as in input stream.
	 * 
	 * @param code The code for the report to generate
	 * @param studyId The identifier of the nursery or trial
	 * @return a File reference (PDF/XLSX,TXT) to the generated report file.
	 * @throws MiddlewareException
	 * @throws MiddlewareQueryException
	 * @throws JRException
	 */
	JasperPrint getPrintReport(String code, Integer studyId) throws MiddlewareException, JRException,
			IOException, BuildReportException;

	/**
	 * Sends the file to the specified output stream. Additionally, returns a Reporter instance, in case some extra information about the
	 * report is needed, such as file extension, report file name, etc.
	 * 
	 * @param output Out where the report has to be sent. This can be a servlet, file or any other output stream.
	 * @return a Reporter instance, describirn the report being generated.
	 */
	Reporter getStreamReport(String code, Integer studyId, String programName, OutputStream output) throws MiddlewareException,
			JRException, IOException, BuildReportException;

    Reporter getStreamGermplasmListReport(String code, Integer germplasmListID, String programName, final OutputStream output)
            throws MiddlewareException, JRException, IOException, BuildReportException;

	/**
	 * Returns a Set of keys available to be passed to a ReporterFactory, for generating a particular report. Only keys returned by this
	 * method can be used as valid codes for ReporterFacotry's createReporter() method.
	 * 
	 * @return a Set of valid codes for creating a report.
	 */
	Set<String> getReportKeys();

}
