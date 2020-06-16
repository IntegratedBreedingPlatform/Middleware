package org.generationcp.middleware.service.impl.dataset;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
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

    private static final String SELECTION_NAME = "NPSEL";

    private static final String TRAIT_NAME = "GW_DW_g1000grn";

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

    @Autowired
    private WorkbenchTestDataUtil workbenchTestDataUtil;

    private Project commonTestProject;

    private GermplasmTestDataGenerator germplasmTestDataGenerator;
    private DataSetupTest dataSetupTest;

    @Autowired
    private DatasetService datasetService;

    private Integer studyId;
    private List<Integer> instanceIds;
    private Integer subObsDatasetId;


    @Before
    public void setUp() {
        this.dataSetupTest = new DataSetupTest();
        this.dataSetupTest.setDataImportService(this.dataImportService);
        this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
        this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

        this.workbenchTestDataUtil.setUpWorkbench();

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
        final Map<Integer, List<ObservationUnitRow>> instanceObsUnitRowMap = this.datasetService.getInstanceIdToObservationUnitRowsMap(this.studyId, this.subObsDatasetId, this.instanceIds);
        final List<ObservationUnitRow> observationUnitRows = instanceObsUnitRowMap.get(this.instanceIds.get(0));
        Assert.assertNotNull(observationUnitRows);
        Assert.assertEquals(80,
			observationUnitRows
				.size()); //The number of germplasm in the study(20) multiplied by numberOfSubObservationUnits(2)  multiplied by the number of reps (2)
        final ObservationUnitRow observationUnitRow = observationUnitRows.get(0);
        this.verifyObservationUnitRowValues(observationUnitRow);
        // Check for study and environment values
        Assert.assertNotNull(observationUnitRow.getVariables().get("STUDY_INSTITUTE"));
        Assert.assertNotNull(observationUnitRow.getVariables().get("STUDY_BM_CODE"));
        Assert.assertNotNull(observationUnitRow.getEnvironmentVariables().get("SITE_ALT"));
        Assert.assertNotNull(observationUnitRow.getEnvironmentVariables().get("SITE_LAT"));
        Assert.assertNotNull(observationUnitRow.getEnvironmentVariables().get("SITE_LONG"));
        Assert.assertNotNull(observationUnitRow.getEnvironmentVariables().get("SITE_DATUM"));
    }

    @Test
    public void testGetObservationUnitRows() {
        final ObservationUnitsSearchDTO searchDto = new ObservationUnitsSearchDTO();
        searchDto.setInstanceId(this.instanceIds.get(0));
        final List<ObservationUnitRow> observationUnitRows = this.datasetService.getObservationUnitRows(this.studyId, this.subObsDatasetId,
                searchDto);
        Assert.assertNotNull(observationUnitRows);
        Assert.assertEquals(80,
			observationUnitRows
				.size()); //The number of germplasm in the study(20) multiplied by numberOfSubObservationUnits(2) multiplied by the number of reps (2)
        final ObservationUnitRow observationUnitRow = observationUnitRows.get(0);
        this.verifyObservationUnitRowValues(observationUnitRow);
    }

    @Test
    public void testGetObservationUnitRowsAsMapList() {
        final ObservationUnitsSearchDTO searchDto = new ObservationUnitsSearchDTO();
        searchDto.setInstanceId(this.instanceIds.get(0));
        searchDto.getFilterColumns().add("TRIAL_INSTANCE");
        searchDto.getFilterColumns().add(TRAIT_NAME);
        final List<Map<String, Object>> rowsAsListMap = this.datasetService.getObservationUnitRowsAsMapList(this.studyId, this.subObsDatasetId,
            searchDto);
        Assert.assertNotNull(rowsAsListMap);
        Assert
			.assertEquals(80, rowsAsListMap
				.size()); //The number of germplasm in the study(20) multiplied by numberOfSubObservationUnits(2) multiplied by the number of reps (2)
        final Map<String, Object> dataMap = rowsAsListMap.get(0);
        Assert.assertEquals(searchDto.getFilterColumns().size(), dataMap.size());
        Assert.assertNotNull(dataMap.get("TRIAL_INSTANCE"));
        Assert.assertNull(dataMap.get(TRAIT_NAME));
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
        final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

        final Integer[] gids = this.germplasmTestDataGenerator
                .createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, "PREFF", parentGermplasm);

        this.studyId = this.dataSetupTest.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), gids, "ABCD");
        this.instanceIds = new ArrayList<>(this.studyDataManager.getInstanceGeolocationIdsMap(this.studyId).values());

        final DatasetDTO datasetDTO = this.datasetService.generateSubObservationDataset(this.studyId, "TEST NURSERY SUB OBS",
            DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(), this.instanceIds, 8206, 2, this.studyId + 2);
        this.subObsDatasetId = datasetDTO.getDatasetId();
        this.datasetService.addDatasetVariable(datasetDTO.getDatasetId(), 20451, VariableType.TRAIT, TRAIT_NAME);
        this.datasetService.addDatasetVariable(datasetDTO.getDatasetId(), 8263, VariableType.SELECTION_METHOD, SELECTION_NAME);
    }

    public List<String> getVariableNames(final List<MeasurementVariable> measurementVariables) {
        final List<String> variableNames = new ArrayList<>();
        for (final MeasurementVariable mvar : measurementVariables) {
            variableNames.add(mvar.getName());
        }
        return variableNames;
    }

}
