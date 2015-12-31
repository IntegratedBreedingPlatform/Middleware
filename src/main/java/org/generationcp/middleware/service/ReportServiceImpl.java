
package org.generationcp.middleware.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.reports.AbstractReporter;
import org.generationcp.middleware.reports.BuildReportException;
import org.generationcp.middleware.reports.Reporter;
import org.generationcp.middleware.reports.ReporterFactory;
import org.generationcp.middleware.service.api.ReportService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ReportServiceImpl extends Service implements ReportService {

	private final ReporterFactory factory = ReporterFactory.instance();

	public ReportServiceImpl() {
		super();
	}

	public ReportServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public ReportServiceImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);

	}

	@Override
	public JasperPrint getPrintReport(String code, Integer studyId) throws MiddlewareException, JRException, IOException,
			BuildReportException {

		Reporter reporter = this.factory.createReporter(code);
		Map<String, Object> dataBeans = this.extractFieldbookData(studyId, reporter.isParentsInfoRequired());

		return reporter.buildJRPrint(dataBeans);

	}

	@Override
	public Reporter getStreamReport(String code, Integer studyId, String programName, OutputStream output) throws MiddlewareException,
			JRException, IOException, BuildReportException {

		Reporter reporter = this.factory.createReporter(code);
		Map<String, Object> dataBeans = this.extractFieldbookData(studyId, reporter.isParentsInfoRequired());
		dataBeans.put(AbstractReporter.PROGRAM_NAME_ARG_KEY, programName);

		reporter.buildJRPrint(dataBeans);
		reporter.asOutputStream(output);

		return reporter;
	}

	/**
	 * Creates a Map containing all information needed to generate a report.
	 * 
	 * @param studyId The study Id to which extract information from.
	 * @return a Map containing information of the study.
	 */
	private Map<String, Object> extractFieldbookData(Integer studyId, boolean parentsInfoRequireed) throws MiddlewareException {

		StudyType studyType = this.getStudyDataManager().getStudyType(studyId);
		Workbook wb = this.getWorkbookBuilder().create(studyId, studyType);
		List<MeasurementRow> observations = wb.getObservations();
		List<MeasurementVariable> studyConditions = appendCountryInformation(wb.getConditions());

		if (parentsInfoRequireed) {
			this.appendParentsInformation(studyId, observations);
		}

		Map<String, Object> dataBeans = new HashMap<>();
		dataBeans.put("studyConditions", studyConditions); // List<MeasurementVariable>
		dataBeans.put("dataSource", observations); // list<measurementRow>
		dataBeans.put("studyObservations", wb.getTrialObservations());// list<measurementRow>
		dataBeans.put("studyId", studyId);// list<measurementRow>

		return dataBeans;
	}

	protected List<MeasurementVariable> appendCountryInformation(List<MeasurementVariable> originalConditions) {
		Integer locationId = null;

		for (MeasurementVariable condition : originalConditions) {
			TermId term = TermId.getById(condition.getTermId());
			if (term == TermId.LOCATION_ID) {
				locationId = Integer.parseInt(condition.getValue());
			}
		}

		if (locationId == null) {
			return originalConditions;
		} else {
			List<MeasurementVariable> variables = new ArrayList<>(originalConditions);

			Location location = getLocationDataManager().getLocationByID(locationId);

			if ((location.getCntryid() != null && location.getCntryid() != 0)) {
				Country country = getCountryDao().getById(location.getCntryid());

				MeasurementVariable countryInfo = new MeasurementVariable();
				countryInfo.setName(AbstractReporter.COUNTRY_VARIABLE_NAME);
				countryInfo.setValue(country.getIsofull());

				variables.add(countryInfo);
			}

			MeasurementVariable abbrevInfo = new MeasurementVariable();
			abbrevInfo.setName(AbstractReporter.LOCATION_ABBREV_VARIABLE_NAME);
			abbrevInfo.setValue(location.getLabbr());

			variables.add(abbrevInfo);

			return variables;

		}
	}

	@Override
	public Set<String> getReportKeys() {
		return this.factory.getReportKeys();
	}

	/**
	 * Local method to add information about male and female parents. The information is appended in the form of new {@link MeasurementData}
	 * elements for each {@link MeasurementRow} provided
	 * 
	 * @param studyId the id for the study which the observations belong to.
	 * @param observations List of rows representing entries in a study, in which parent information will be appended
	 */
	private void appendParentsInformation(Integer studyId, List<MeasurementRow> observations) throws MiddlewareQueryException {
		// put germNodes extraction
		Map<Integer, GermplasmPedigreeTreeNode> germNodes = this.getGermplasmDataManager().getDirectParentsForStudy(studyId);

		for (MeasurementRow row : observations) {
			int gid = Integer.valueOf(row.getMeasurementDataValue("GID"));
			GermplasmPedigreeTreeNode germNode = germNodes.get(gid);
			if (germNode != null) {
				Germplasm germplasm = germNode.getGermplasm();

				if (germplasm.getGrplce() >= 0 & germNode.getLinkedNodes().size() == 2) { // is geneative and has parents
					Germplasm female = germNode.getLinkedNodes().get(0).getGermplasm();
					Germplasm male = germNode.getLinkedNodes().get(1).getGermplasm();

					// TODO: pending values for origin of the entries
					row.getDataList().add(new MeasurementData("f_selHist", female.getSelectionHistory()));
					row.getDataList().add(new MeasurementData("f_cross_name", female.getSelectionHistory()));
					row.getDataList().add(new MeasurementData("f_tabbr", "NA")); // put source trial abbreviation
					row.getDataList().add(new MeasurementData("f_locycle", "NA")); // put source trial cycle
					row.getDataList().add(new MeasurementData("f_ent", "-99")); // put source trial entry
					row.getDataList().add(new MeasurementData("f_lid", "-99")); // put source location id

					row.getDataList().add(new MeasurementData("m_selHist", male.getSelectionHistory()));
					row.getDataList().add(new MeasurementData("m_cross_name", male.getSelectionHistory()));
					row.getDataList().add(new MeasurementData("m_tabbr", "NA")); // put source trial abbreviation
					row.getDataList().add(new MeasurementData("m_locycle", "NA")); // put source trial cycle
					row.getDataList().add(new MeasurementData("m_ent", "-99")); // put source trial entry
					row.getDataList().add(new MeasurementData("m_lid", "-99")); // put source location id
				}
			}
		}
	}
}
