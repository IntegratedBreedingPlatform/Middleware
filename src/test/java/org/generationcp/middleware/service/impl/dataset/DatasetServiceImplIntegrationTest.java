package org.generationcp.middleware.service.impl.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by clarysabel on 11/13/17.
 */
public class DatasetServiceImplIntegrationTest extends IntegrationTestBase {

	private static final String SELECTION_NAME = "NPSEL";

	private static final String TRAIT_NAME = "GW_DW_g1000grn";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	private Project commonTestProject;
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private DataSetupTest dataSetupTest;

	private DatasetServiceImpl datasetService;
	private Integer studyId;
	private List<Integer> instanceIds;
	private Integer subObsDatasetId;


	@Before
	public void setUp() {
		this.datasetService = new DatasetServiceImpl(this.sessionProvder);
		this.datasetService.setWorkbenchDataManager(this.workbenchDataManager);

		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

		if (this.workbenchTestDataUtil == null) {
			this.workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
			this.workbenchTestDataUtil.setUpWorkbench();
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmDataManager);
		}
		
		if (this.studyId == null) {
			this.createTestStudyWithSubObservations();
		}
	}

	@Test
	public void testGetInstanceObservationUnitRowsMap() {
		Map<Integer, List<ObservationUnitRow>> instanceObsUnitRowMap = this.datasetService.getInstanceIdToObservationUnitRowsMap(this.studyId, this.subObsDatasetId, this.instanceIds);
		List<ObservationUnitRow> observationUnitRows = instanceObsUnitRowMap.get(instanceIds.get(0));
		Assert.assertNotNull(observationUnitRows);
		Assert.assertEquals(40, observationUnitRows.size()); //The number of germplasm in the study(20) multiplied by numberOfSubObservationUnits(2)
		final ObservationUnitRow observationUnitRow = observationUnitRows.get(0);
		this.verifyObservationUnitRowValues(observationUnitRow);
		// Check for study and environment values
		Assert.assertNotNull(observationUnitRow.getVariables().get("STUDY_INSTITUTE"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("STUDY_BM_CODE"));
	}
	
	@Test
	public void testGetObservationUnitRows() {
		final ObservationUnitsSearchDTO searchDto = new ObservationUnitsSearchDTO();
		searchDto.setInstanceId(this.instanceIds.get(0));
		final List<ObservationUnitRow> observationUnitRows = this.datasetService.getObservationUnitRows(this.studyId, this.subObsDatasetId,
			searchDto);
		Assert.assertNotNull(observationUnitRows);
		Assert.assertEquals(40, observationUnitRows.size()); //The number of germplasm in the study(20) multiplied by numberOfSubObservationUnits(2)
		final ObservationUnitRow observationUnitRow = observationUnitRows.get(0);
		this.verifyObservationUnitRowValues(observationUnitRow);
	}

	private void verifyObservationUnitRowValues(final ObservationUnitRow observationUnitRow) {
		Assert.assertNotNull(observationUnitRow.getVariables().get(TRAIT_NAME));
		Assert.assertNotNull(observationUnitRow.getVariables().get(SELECTION_NAME));
		Assert.assertNotNull(observationUnitRow.getVariables().get("LOCATION_ID"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("TRIAL_INSTANCE"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("ENTRY_NO"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("ENTRY_CODE"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("GID"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("DESIGNATION"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("CROSS"));
		Assert.assertNotNull(observationUnitRow.getVariables().get("PLOT_NO"));
		Assert.assertNotNull(observationUnitRow.getObsUnitId());
		Assert.assertNotNull(observationUnitRow.getObservationUnitId());
		Assert.assertNotNull(observationUnitRow.getAction());
		Assert.assertNotNull(observationUnitRow.getGid());
		Assert.assertNotNull(observationUnitRow.getDesignation());
	}

	private void createTestStudyWithSubObservations() {
		Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, "PREFF", parentGermplasm);

		this.studyId = this.dataSetupTest.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), gids, "ABCD");
		this.instanceIds = new ArrayList<>(this.studyDataManager.getInstanceGeolocationIdsMap(this.studyId).values());

		final DatasetDTO datasetDTO = this.datasetService.generateSubObservationDataset(this.studyId, "TEST NURSERY SUB OBS", 10094, instanceIds,8206, 2, this.studyId+2);
		this.subObsDatasetId = datasetDTO.getDatasetId();
		this.datasetService.addDatasetVariable(datasetDTO.getDatasetId(), 20451, VariableType.TRAIT, TRAIT_NAME);
		this.datasetService.addDatasetVariable(datasetDTO.getDatasetId(), 8263, VariableType.SELECTION_METHOD, SELECTION_NAME);
	}

	public List<String> getVariableNames(final List<MeasurementVariable> measurementVariables) {
		final List<String> variableNames = new ArrayList<>();
		for(MeasurementVariable mvar: measurementVariables) {
			variableNames.add(mvar.getName());
		}
		return variableNames;
	}

}
