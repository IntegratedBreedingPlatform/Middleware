
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.service.api.DataImportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkbookBuilderIntegrationTest extends IntegrationTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookBuilderIntegrationTest.class);

	@Autowired
	private DataImportService dataImportService;

	private WorkbookBuilder workbookBuilder;

	public static final int NUMBER_OF_GERMPLASM = 5;
	public static final String GERMPLSM_PREFIX = "Germplasm-";

	private static final String CHAR = "C";
	private static final String NUMERIC = "N";

	private static final String ASSIGNED = "ASSIGNED";

	private static final String STUDY = "STUDY";
	private static final String ENTRY = "ENTRY";
	private static final String PLOT = "PLOT";
	private static final String PROP_STUDY = "Study";
	private static final String PROP_STUDY_TITLE = "Study Description";

	private static final String DBCV = "DBCV";
	private static final String DBID = "DBID";
	private static final String SCALE_TEXT = "Text";

	private static final String ENUMERATED = "ENUMERATED";

	private static final String GID = "GID";
	private static final String DESIG = "DESIG";
	private static final String NUMBER = "NUMBER";

	private static final String KG_HA = "kg/ha";
	private static final String GRAIN_YIELD = "Grain Yield";
	private static final String DRY_AND_WEIGH = "Dry and weigh";

	private List<MeasurementVariable> constants;
	private List<MeasurementVariable> variates;
	private String programUUID;
	private StudyDetails studyDetails;
	private Workbook workbook;

	@Before
	public void setUp() {
		if (this.workbookBuilder == null) {
			this.workbookBuilder = new WorkbookBuilder(super.sessionProvder);
		}
	}

	private void setUpNursery() {
		// Basic Details
		studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		studyDetails.setStudyName("Test Nursery " + new Random().nextInt(100));
		studyDetails.setDescription(studyDetails.getStudyName() + " Description");
		studyDetails.setParentFolderId(1);
		studyDetails.setCreatedBy("1");

		setUpWorkbook();
	}

	private void setUpTrial() {
		// Basic Details
		studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		studyDetails.setStudyName("Test Trial " + new Random().nextInt(100));
		studyDetails.setDescription(studyDetails.getStudyName() + " Description");
		studyDetails.setParentFolderId(1);
		studyDetails.setCreatedBy("1");

		setUpWorkbook();
	}

	private void setUpWorkbook() {
		// Create a study (workbook) in database.
		workbook = new Workbook();
		workbook.setStudyDetails(studyDetails);

		programUUID = UUID.randomUUID().toString();

		// Conditions
		final List<MeasurementVariable> conditions = new ArrayList<>();
		workbook.setConditions(conditions);

		// Constants
		constants = new ArrayList<>();
		constants.add(this.createMeasurementVariable(8270, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)", "Soil acidity", "Ph meter", "pH",
			WorkbookBuilderIntegrationTest.NUMERIC, "7", WorkbookBuilderIntegrationTest.STUDY, PhenotypicType.VARIATE, false));
		workbook.setConstants(constants);

		// Factors
		final List<MeasurementVariable> factors = new ArrayList<>();
		final MeasurementVariable entryFactor =
			this.createMeasurementVariable(TermId.ENTRY_NO.getId(), "ENTRY_NO", "Germplasm entry - enumerated (number)",
				"Germplasm entry", WorkbookBuilderIntegrationTest.ENUMERATED, WorkbookBuilderIntegrationTest.NUMBER,
				WorkbookBuilderIntegrationTest.NUMERIC, null, WorkbookBuilderIntegrationTest.ENTRY, PhenotypicType.GERMPLASM, true);
		factors.add(entryFactor);

		final MeasurementVariable designationFactor =
			this.createMeasurementVariable(TermId.DESIG.getId(), "DESIGNATION", "Germplasm designation - assigned (DBCV)",
				"Germplasm Designation", WorkbookBuilderIntegrationTest.ASSIGNED, WorkbookBuilderIntegrationTest.DBCV,
				WorkbookBuilderIntegrationTest.CHAR, null, WorkbookBuilderIntegrationTest.DESIG, PhenotypicType.GERMPLASM, true);
		factors.add(designationFactor);

		final MeasurementVariable gidFactor =
			this.createMeasurementVariable(TermId.GID.getId(), "GID", "Germplasm identifier - assigned (DBID)", "Germplasm id",
				WorkbookBuilderIntegrationTest.ASSIGNED, WorkbookBuilderIntegrationTest.DBID,
				WorkbookBuilderIntegrationTest.NUMERIC, null, WorkbookBuilderIntegrationTest.GID, PhenotypicType.GERMPLASM, true);
		factors.add(gidFactor);

		final MeasurementVariable plotFactor = this.createMeasurementVariable(TermId.PLOT_NO.getId(), "PLOT_NO",
			"Field plot - enumerated (number)", "Field plot", WorkbookBuilderIntegrationTest.ENUMERATED,
			WorkbookBuilderIntegrationTest.NUMBER, WorkbookBuilderIntegrationTest.NUMERIC, null, WorkbookBuilderIntegrationTest.PLOT,
			PhenotypicType.TRIAL_DESIGN, true);
		factors.add(plotFactor);

		workbook.setFactors(factors);

		// Variates
		variates = new ArrayList<>();
		final MeasurementVariable variate =
			this.createMeasurementVariable(51570, "GY_Adj_kgha", "Grain yield BY Adjusted GY - Computation IN Kg/ha",
			WorkbookBuilderIntegrationTest.GRAIN_YIELD, WorkbookBuilderIntegrationTest.DRY_AND_WEIGH,
			WorkbookBuilderIntegrationTest.KG_HA, WorkbookBuilderIntegrationTest.NUMERIC, null, WorkbookBuilderIntegrationTest.PLOT,
			PhenotypicType.VARIATE, false);
		variates.add(variate);

		workbook.setVariates(variates);

		// Observations
		final List<MeasurementRow> observations = new ArrayList<>();
		MeasurementRow row;
		List<MeasurementData> dataList;
		for (int i = 0; i < WorkbookBuilderIntegrationTest.NUMBER_OF_GERMPLASM; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<>();
			final MeasurementData entryData = new MeasurementData(entryFactor.getLabel(), String.valueOf(i));
			entryData.setMeasurementVariable(entryFactor);
			dataList.add(entryData);

			final MeasurementData designationData =
				new MeasurementData(designationFactor.getLabel(), WorkbookBuilderIntegrationTest.GERMPLSM_PREFIX + i);
			designationData.setMeasurementVariable(designationFactor);
			dataList.add(designationData);

			final MeasurementData gidData = new MeasurementData(gidFactor.getLabel(), String.valueOf(i));
			gidData.setMeasurementVariable(gidFactor);
			dataList.add(gidData);

			final MeasurementData plotData = new MeasurementData(plotFactor.getLabel(), String.valueOf(i));
			plotData.setMeasurementVariable(plotFactor);
			dataList.add(plotData);

			final MeasurementData variateData = new MeasurementData(variate.getLabel(), String.valueOf(new Random().nextInt(100)));
			variateData.setMeasurementVariable(variate);
			dataList.add(variateData);

			row.setDataList(dataList);
			observations.add(row);
		}
		workbook.setObservations(observations);
	}

	@Test
	public void testWorkbookBuilderLoadsNoObservationsByDefaultNursery() throws MiddlewareException {
		setUpNursery();

		// Save the workbook
		final int studyId = this.dataImportService.saveDataset(workbook, true, false, programUUID, "9CVR");
		WorkbookBuilderIntegrationTest.LOG.info("Study " + studyDetails.getStudyName() + " created, studyId: " + studyId);

		// Now the actual test and assertions. Load the workbook using workbook builder.
		final Workbook studyWorkbook = this.workbookBuilder.create(studyId, StudyType.N);
		Assert.assertNotNull(studyWorkbook);

		// The main assertion.
		Assert.assertEquals("Workbook loaded via WorkbookBuilder.create() must not populate the observations collection by default.", 0,
				studyWorkbook.getObservations().size());

		// Other basic assertions just as sanity check.
		final StudyDetails nurseryStudyDetails = studyWorkbook.getStudyDetails();
		Assert.assertNotNull(nurseryStudyDetails);
		Assert.assertNotNull(nurseryStudyDetails.getId());
		Assert.assertEquals(studyId, nurseryStudyDetails.getId().intValue());
		Assert.assertEquals(studyDetails.getStudyName(), nurseryStudyDetails.getStudyName());
		Assert.assertEquals(studyDetails.getDescription(), nurseryStudyDetails.getDescription());
		Assert.assertEquals(constants.size(), studyWorkbook.getConstants().size());
		Assert.assertEquals(variates.size(), studyWorkbook.getVariates().size());
	}

	@Test
	public void testWorkbookBuilderLoadsNoObservationsByDefaultTrial() throws MiddlewareException {
		setUpTrial();

		// Save the workbook
		final int studyId = this.dataImportService.saveDataset(workbook, true, false, programUUID, "9CVR");
		WorkbookBuilderIntegrationTest.LOG.info("Study " + studyDetails.getStudyName() + " created, studyId: " + studyId);

		// Now the actual test and assertions. Load the workbook using workbook builder.
		final Workbook studyWorkbook = this.workbookBuilder.create(studyId, StudyType.T);
		Assert.assertNotNull(studyWorkbook);

		// The main assertion.
		Assert.assertEquals("Workbook loaded via WorkbookBuilder.create() must not populate the observations collection by default.", 0,
				studyWorkbook.getObservations().size());

		// Other basic assertions just as sanity check.
		final StudyDetails studyDetails = studyWorkbook.getStudyDetails();
		Assert.assertNotNull(studyDetails);
		Assert.assertNotNull(studyDetails.getId());
		Assert.assertEquals(studyId, studyDetails.getId().intValue());
		Assert.assertEquals(this.studyDetails.getStudyName(), studyDetails.getStudyName());
		Assert.assertEquals(this.studyDetails.getDescription(), studyDetails.getDescription());
		Assert.assertEquals(constants.size(), studyWorkbook.getConstants().size());
		Assert.assertEquals(variates.size(), studyWorkbook.getVariates().size());

		int measurementDataSetId = this.workbookBuilder.getMeasurementDataSetId(studyId, studyDetails.getStudyName());
		Workbook workbookCompleteDataset = this.workbookBuilder.getDataSetBuilder().buildCompleteDataset(measurementDataSetId, true);

		Assert.assertTrue(workbookCompleteDataset.getObservations().size() > 0);
	}

	private MeasurementVariable createMeasurementVariable(final int termId, final String name, final String description,
			final String property, final String method, final String scale, final String dataType, final String value, final String label,
			final PhenotypicType role, final boolean isFactor) {

		final MeasurementVariable variable = new MeasurementVariable();

		variable.setTermId(termId);
		variable.setName(name);
		variable.setDescription(description);
		variable.setProperty(property);
		variable.setMethod(method);
		variable.setScale(scale);
		variable.setDataType(dataType);
		variable.setValue(value);
		variable.setLabel(label);
		variable.setFactor(isFactor);
		variable.setRole(role);

		return variable;
	}

	public void setDataImportService(final DataImportService dataImportService) {
		this.dataImportService = dataImportService;
	}
}
