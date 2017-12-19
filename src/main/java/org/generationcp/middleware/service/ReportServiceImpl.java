
package org.generationcp.middleware.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
public class ReportServiceImpl extends Service implements ReportService {

	public static final String DEFAULT_STRING_VALUE = "NA";
	public static final String BLANK_STRING_VALUE = "";
	public static final String DEFAULT_INTEGER_STRING_VALUE = "0";
	private final ReporterFactory factory = ReporterFactory.instance();

	public ReportServiceImpl() {
		super();
	}

	public ReportServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public ReportServiceImpl(final HibernateSessionProvider sessionProvider, final String databaseName) {
		super(sessionProvider, databaseName);

	}

	@Override
	public JasperPrint getPrintReport(final String code, final Integer studyId) throws JRException, IOException,
			BuildReportException {

		final Reporter reporter = this.factory.createReporter(code);
		final Map<String, Object> dataBeans = this.extractFieldbookData(studyId, reporter.isParentsInfoRequired());

		return reporter.buildJRPrint(dataBeans);

	}

	@Override
	public Reporter getStreamReport(final String code, final Integer studyId, final String programName, final OutputStream output)
			throws JRException, IOException, BuildReportException {

		final Reporter reporter = this.factory.createReporter(code);
		final Map<String, Object> dataBeans = this.extractFieldbookData(studyId, reporter.isParentsInfoRequired());
		dataBeans.put(AbstractReporter.PROGRAM_NAME_ARG_KEY, programName);

		reporter.buildJRPrint(dataBeans);
		reporter.asOutputStream(output);

		return reporter;
	}

	@Override
	public Reporter getStreamGermplasmListReport(final String code, final Integer germplasmListID, final String programName,
			final OutputStream output) throws JRException, IOException, BuildReportException {
		final Reporter reporter = this.factory.createReporter(code);
		final Map<String, Object> data = this.extractGermplasmListData();
		data.put(AbstractReporter.PROGRAM_NAME_ARG_KEY, programName);

		reporter.buildJRPrint(data);
		reporter.asOutputStream(output);

		return reporter;
	}

	/**
	 * Creates a Map containing all information needed to generate a report.
	 * 
	 * @param studyId The study Id to which extract information from.
	 * @return a Map containing information of the study.
	 */
	private Map<String, Object> extractFieldbookData(final Integer studyId, final boolean parentsInfoRequireed) {

		final StudyType studyType = this.getStudyDataManager().getStudyType(studyId);
		final Workbook wb = this.getWorkbookBuilder().create(studyId, studyType);
		// getWorkbookBuilder().create no longer loads observations collection by default. Load only when needed. Like here.
		this.getWorkbookBuilder().loadAllObservations(wb);
		final List<MeasurementRow> observations = wb.getObservations();
		final List<MeasurementVariable> studyConditions = this.appendCountryInformationFromCondition(wb.getConditions());
		final List<MeasurementRow> trialObservations = wb.getTrialObservations();

		if (!trialObservations.isEmpty()) {
			for (final MeasurementRow trialObservation : trialObservations) {
				trialObservation.setDataList(this.appendCountryInformationFromObservation(trialObservation.getDataList()));
			}
		}

		if (parentsInfoRequireed) {
			this.appendParentsInformation(studyId, observations);
		}

		final Map<String, Object> dataBeans = new HashMap<>();
		dataBeans.put(AbstractReporter.STUDY_CONDITIONS_KEY, studyConditions);
		dataBeans.put(AbstractReporter.DATA_SOURCE_KEY, observations);
		dataBeans.put(AbstractReporter.STUDY_OBSERVATIONS_KEY, wb.getTrialObservations());
		dataBeans.put("studyId", studyId);
		dataBeans.put(AbstractReporter.STUDY_TITLE_REPORT_KEY, wb.getStudyDetails().getDescription());

		return dataBeans;
	}

	protected Map<String, Object> extractGermplasmListData() {
		// currently, only a blank map is returned as the current requirements for germplasm reports do not require dynamic data
		final Map<String, Object> params = new HashMap<>();
		params.put(AbstractReporter.STUDY_CONDITIONS_KEY, new ArrayList<MeasurementVariable>());
		params.put(AbstractReporter.DATA_SOURCE_KEY, new ArrayList());
		return params;
	}

	protected List<MeasurementVariable> appendCountryInformationFromCondition(final List<MeasurementVariable> originalConditions) {
		final Integer locationId = this.retrieveLocationIdFromCondition(originalConditions);

		if (locationId == null) {
			return originalConditions;
		} else {
			final List<MeasurementVariable> variables = new ArrayList<>(originalConditions);

			final Location location = this.getLocationDataManager().getLocationByID(locationId);

			if ((location.getCntryid() != null && location.getCntryid() != 0)) {
				final Country country = this.getCountryDao().getById(location.getCntryid());

				variables.add(createPlaceholderCountryMeasurementVariable(country.getIsofull()));
			}

			final MeasurementVariable abbrevInfo = new MeasurementVariable();
			abbrevInfo.setName(AbstractReporter.LOCATION_ABBREV_VARIABLE_NAME);
			abbrevInfo.setProperty(BLANK_STRING_VALUE);
			abbrevInfo.setValue(location.getLabbr());

			variables.add(abbrevInfo);

			return variables;

		}
	}

	protected List<MeasurementData> appendCountryInformationFromObservation(final List<MeasurementData> observations) {
		final Integer locationId = this.retrieveLocationIdFromObservations(observations);

		if (locationId == null) {
			return observations;
		} else {
			final List<MeasurementData> variables = new ArrayList<>(observations);

			final Location location = this.getLocationDataManager().getLocationByID(locationId);

			if (location.getCntryid() != null && location.getCntryid() != 0) {
				final Country country = this.getCountryDao().getById(location.getCntryid());

				variables.add(createPlaceholderCountryMeasurementData(country.getIsofull()));
			}

			final MeasurementData abbrevData = new MeasurementData();
			final MeasurementVariable abbrevInfo = new MeasurementVariable();
			abbrevInfo.setName(AbstractReporter.LOCATION_ABBREV_VARIABLE_NAME);
			abbrevInfo.setProperty(BLANK_STRING_VALUE);
			abbrevData.setValue(location.getLabbr());
			abbrevData.setMeasurementVariable(abbrevInfo);

			variables.add(abbrevData);

			return variables;

		}
	}

	protected MeasurementVariable createPlaceholderCountryMeasurementVariable(final String countryISO) {
		final MeasurementVariable countryInfo = new MeasurementVariable();
		countryInfo.setName(AbstractReporter.COUNTRY_VARIABLE_NAME);
		countryInfo.setValue(countryISO);
		countryInfo.setProperty(BLANK_STRING_VALUE);

		return countryInfo;
	}

	protected MeasurementData createPlaceholderCountryMeasurementData(final String countryISO) {
		final MeasurementData countryData = new MeasurementData();
		final MeasurementVariable countryVariable = createPlaceholderCountryMeasurementVariable(countryISO);
		countryData.setValue(countryISO);
		countryData.setMeasurementVariable(countryVariable);

		return countryData;
	}

	/***
	 * Retrieves the Location ID from the list of condition variables; Returns null if the condition value is an empty string
	 *
	 * @param condition
	 * @param termId
	 * @return
	 */
	Integer retrieveLocationIdFromCondition(final List<MeasurementVariable> originalConditions) {
		Integer locationId = null;
		for (final MeasurementVariable condition : originalConditions) {
			final TermId term = TermId.getById(condition.getTermId());
			if (term == TermId.LOCATION_ID && !StringUtils.isEmpty(condition.getValue())) {
				locationId = Integer.parseInt(condition.getValue());
			}
		}

		return locationId;
	}

	/***
	 * Retrieves the Location ID from the list of trial observations; Returns null if the condition value is an empty string
	 *
	 * @return
	 */
	Integer retrieveLocationIdFromObservations(final List<MeasurementData> observations) {
		Integer locationId = null;
		for (final MeasurementData observation : observations) {
			final TermId term = TermId.getById(observation.getMeasurementVariable().getTermId());
			if (term == TermId.LOCATION_ID && !StringUtils.isEmpty(observation.getValue())) {
				locationId = Integer.parseInt(observation.getValue());
			}
		}

		return locationId;
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
	protected void appendParentsInformation(final Integer studyId, final List<MeasurementRow> observations) {
		// put germNodes extraction
		final Map<Integer, GermplasmPedigreeTreeNode> germNodes = this.getGermplasmDataManager().getDirectParentsForStudy(studyId);

		for (final MeasurementRow row : observations) {
			final int gid = Integer.parseInt(row.getMeasurementDataValue("GID"));
			final GermplasmPedigreeTreeNode germNode = germNodes.get(gid);

			if (germNode == null || (germNode.getFemaleParent() == null && germNode.getMaleParent() == null)) {
				provideBlankParentInformationValues(row.getDataList());
			} else {
				provideParentInformation(germNode, row.getDataList());
			}

		}
	}

	void provideParentInformation(final GermplasmPedigreeTreeNode germNode, final List<MeasurementData> dataRow) {
		final GermplasmPedigreeTreeNode femaleNode = germNode.getFemaleParent();
		final GermplasmPedigreeTreeNode maleNode = germNode.getMaleParent();
		final Germplasm female = femaleNode == null ? null : femaleNode.getGermplasm();
		final Germplasm male = maleNode == null ? null : maleNode.getGermplasm();

		// TODO: pending values for origin of the entries (most likely resolved in BMS-2211)
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SELECTION_HISTORY_KEY, female == null ? BLANK_STRING_VALUE : female
				.getSelectionHistory()));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_CROSS_NAME_KEY, female == null ? BLANK_STRING_VALUE : female
				.getSelectionHistory()));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_TRIAL_ABBREVIATION_KEY, DEFAULT_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SOURCE_TRIAL_CYCLE_KEY, DEFAULT_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SOURCE_TRIAL_ENTRY_KEY, DEFAULT_INTEGER_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SOURCE_TRIAL_LOCATION_ID_KEY, DEFAULT_INTEGER_STRING_VALUE));

		dataRow.add(new MeasurementData(AbstractReporter.MALE_SELECTION_HISTORY_KEY, male == null ? BLANK_STRING_VALUE : male
				.getSelectionHistory()));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_CROSS_NAME_KEY, male == null ? BLANK_STRING_VALUE : male
				.getSelectionHistory()));

		dataRow.add(new MeasurementData(AbstractReporter.MALE_TRIAL_ABBREVIATION_KEY, DEFAULT_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_SOURCE_TRIAL_CYCLE_KEY, DEFAULT_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_SOURCE_TRIAL_ENTRY_KEY, DEFAULT_INTEGER_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_SOURCE_TRIAL_LOCATION_ID_KEY, DEFAULT_INTEGER_STRING_VALUE));
	}

	void provideBlankParentInformationValues(final List<MeasurementData> dataRow) {
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SELECTION_HISTORY_KEY, BLANK_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_CROSS_NAME_KEY, BLANK_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_TRIAL_ABBREVIATION_KEY, BLANK_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SOURCE_TRIAL_CYCLE_KEY, BLANK_STRING_VALUE));

		dataRow.add(new MeasurementData(AbstractReporter.MALE_SELECTION_HISTORY_KEY, BLANK_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_CROSS_NAME_KEY, BLANK_STRING_VALUE));

		dataRow.add(new MeasurementData(AbstractReporter.MALE_TRIAL_ABBREVIATION_KEY, BLANK_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_SOURCE_TRIAL_CYCLE_KEY, BLANK_STRING_VALUE));

		// TODO : the value used here is 0 instead of a proper blank because the report design only supports integer values for this field
		// and changing it would involve several changes more fit to be done in a separate ticket
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SOURCE_TRIAL_ENTRY_KEY, DEFAULT_INTEGER_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.FEMALE_SOURCE_TRIAL_LOCATION_ID_KEY, DEFAULT_INTEGER_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_SOURCE_TRIAL_ENTRY_KEY, DEFAULT_INTEGER_STRING_VALUE));
		dataRow.add(new MeasurementData(AbstractReporter.MALE_SOURCE_TRIAL_LOCATION_ID_KEY, DEFAULT_INTEGER_STRING_VALUE));
	}
}
