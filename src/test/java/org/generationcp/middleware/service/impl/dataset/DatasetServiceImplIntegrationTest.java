package org.generationcp.middleware.service.impl.dataset;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by clarysabel on 11/13/17.
 */
public class DatasetServiceImplIntegrationTest extends IntegrationTestBase {

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

	private DaoFactory daoFactory;
	private Project commonTestProject;
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private DataSetupTest dataSetupTest;

	private DatasetServiceImpl datasetService;


	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.datasetService = new DatasetServiceImpl(this.sessionProvder);

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
	}

	@Test
	public void testGetInstanceObservationUnitRowsMap() {
		Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, "PREFF", parentGermplasm);

		final int nurseryId = this.dataSetupTest.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), gids, "ABCD", false);
		final List<Integer> instanceIds = new ArrayList<>(this.studyDataManager.getInstanceGeolocationIdsMap(nurseryId).values());

		final DatasetDTO datasetDTO = this.datasetService.generateSubObservationDataset(nurseryId, "TEST NURSERY SUB OBS", 10094, instanceIds,8206, 2,nurseryId+2);
		final String traitName = "GW_DW_g1000grn";
		this.datasetService.addVariable(datasetDTO.getDatasetId(), 20451, VariableType.TRAIT, traitName);
		final String selectionVarName  = "NPSEL";
		this.datasetService.addVariable(datasetDTO.getDatasetId(), 8263, VariableType.SELECTION_METHOD, selectionVarName);

		Map<Integer, List<ObservationUnitRow>> instanceObsUnitRowMap = this.datasetService.getInstanceObservationUnitRowsMap(nurseryId, datasetDTO.getDatasetId(), instanceIds);
		List<ObservationUnitRow> observationUnitRows = instanceObsUnitRowMap.get(instanceIds.get(0));
		Assert.assertNotNull(instanceObsUnitRowMap.get(instanceIds.get(0)));
		Assert.assertEquals(40, observationUnitRows.size()); //The number of germplasm in the study(20) multiplied by numberOfSubObservationUnits(2)
		Assert.assertNotNull(observationUnitRows.get(0).getVariables().get(traitName));
		Assert.assertNotNull(observationUnitRows.get(0).getVariables().get(selectionVarName));
	}

	@Test
	public void testGetAllDatasetVariables() {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, "PREFF", parentGermplasm);

		final int nurseryId = this.dataSetupTest.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), gids, "ABCD", false);
		final List<Integer> instanceIds = new ArrayList<>(this.studyDataManager.getInstanceGeolocationIdsMap(nurseryId).values());

		final DatasetDTO datasetDTO = this.datasetService.generateSubObservationDataset(nurseryId, "TEST NURSERY SUB OBS", 10094, instanceIds,8206, 2,nurseryId+2);
		final List<MeasurementVariable> measurementVariables = this.datasetService.getAllDatasetVariables(nurseryId, datasetDTO.getDatasetId());

		final String traitName = "GW_DW_g1000grn";
		this.datasetService.addVariable(datasetDTO.getDatasetId(), 20451, VariableType.TRAIT, traitName);
		final String selectionVarName  = "NPSEL";
		this.datasetService.addVariable(datasetDTO.getDatasetId(), 8263, VariableType.SELECTION_METHOD, selectionVarName);

		final List<MeasurementVariable> measurementVariablesWithAddedTraitAndSelectionVariables = this.datasetService.getAllDatasetVariables(nurseryId, datasetDTO.getDatasetId());
		Assert.assertEquals(measurementVariables.size(), measurementVariablesWithAddedTraitAndSelectionVariables.size()-2);
		final List<String> variableNames = this.getVariableNames(measurementVariablesWithAddedTraitAndSelectionVariables);
		Assert.assertTrue(variableNames.contains(traitName));
		Assert.assertTrue(variableNames.contains(selectionVarName));
	}

	public List<String> getVariableNames(final List<MeasurementVariable> measurementVariables) {
		final List<String> variableNames = new ArrayList<>();
		for(MeasurementVariable mvar: measurementVariables) {
			variableNames.add(mvar.getName());
		}
		return variableNames;
	}

}
