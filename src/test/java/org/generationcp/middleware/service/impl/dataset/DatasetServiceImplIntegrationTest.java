package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.Table;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.ontology.AnalysisVariablesImportRequest;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.analysis.SiteAnalysisService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsParamDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.impl.analysis.MeansImportRequest;
import org.generationcp.middleware.service.impl.analysis.SummaryStatisticsImportRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by clarysabel on 11/13/17.
 */
public class DatasetServiceImplIntegrationTest extends IntegrationTestBase {

    private static final String SELECTION_NAME = "NPSEL";

    private static final String TRAIT_NAME = "GW_DW_g1000grn";
    public static final int TRAIT_ID = 20451;

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

    @Autowired
    private OntologyVariableService ontologyVariableService;

    @Autowired
    private SiteAnalysisService analysisService;

    private Project commonTestProject;

    private GermplasmTestDataGenerator germplasmTestDataGenerator;
    private DataSetupTest dataSetupTest;

    @Autowired
    private DatasetService datasetService;

    private Integer studyId;
    private Integer plotDatasetId;
    private List<Integer> instanceIds;
    private Integer subObsDatasetId;
    private DaoFactory daoFactory;
    private WorkbenchDaoFactory workbenchDaoFactory;


    @Before
    public void setUp() {
        if (this.daoFactory == null) {
            this.daoFactory =new DaoFactory(this.sessionProvder);
        }

        if (this.workbenchDaoFactory == null) {
            this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
        }

        this.dataSetupTest = new DataSetupTest();
        this.dataSetupTest.setDataImportService(this.dataImportService);
        this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
        this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

        this.workbenchTestDataUtil.setUpWorkbench(workbenchDaoFactory);

        if (this.commonTestProject == null) {
            this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
        }

        if (this.daoFactory == null) {
            this.daoFactory =new DaoFactory(this.sessionProvder);
        }
        if (this.germplasmTestDataGenerator == null) {
            this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.sessionProvder, this.daoFactory);
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
    public void testSetValueToVariable() {
        Map<Integer, List<ObservationUnitRow>> instanceObsUnitRowMap = this.datasetService.getInstanceIdToObservationUnitRowsMap(this.studyId, this.subObsDatasetId, this.instanceIds);
        List<ObservationUnitRow> observationUnitRows = instanceObsUnitRowMap.get(this.instanceIds.get(0));
        for(final ObservationUnitRow row: observationUnitRows) {
            Assert.assertNull(row.getVariables().get(TRAIT_NAME).getValue());
        }
        final ObservationUnitsParamDTO param = new ObservationUnitsParamDTO();
        param.setNewValue("2");
        final ObservationUnitsSearchDTO searchDTO = new ObservationUnitsSearchDTO();
        searchDTO.setDatasetId(subObsDatasetId);
        searchDTO.setDraftMode(false);
        searchDTO.setFilter(searchDTO.new Filter());
        searchDTO.getFilter().setVariableId(TRAIT_ID);
        param.setObservationUnitsSearchDTO(searchDTO);
        this.datasetService.setValueToVariable(subObsDatasetId, param, this.studyId);
        this.sessionProvder.getSession().flush();

        instanceObsUnitRowMap = this.datasetService.getInstanceIdToObservationUnitRowsMap(this.studyId, this.subObsDatasetId, this.instanceIds);
        observationUnitRows = instanceObsUnitRowMap.get(this.instanceIds.get(0));
        for(final ObservationUnitRow row: observationUnitRows) {
            Assert.assertEquals(param.getNewValue(), row.getVariables().get(TRAIT_NAME).getValue());
        }
    }

    @Test
    public void testDeleteVariableValues() {
        final ObservationUnitsParamDTO param = new ObservationUnitsParamDTO();
        param.setNewValue("2");
        final ObservationUnitsSearchDTO searchDTO = new ObservationUnitsSearchDTO();
        searchDTO.setDatasetId(subObsDatasetId);
        searchDTO.setDraftMode(false);
        searchDTO.setFilter(searchDTO.new Filter());
        searchDTO.getFilter().setVariableId(TRAIT_ID);
        param.setObservationUnitsSearchDTO(searchDTO);
        this.datasetService.setValueToVariable(subObsDatasetId, param, this.studyId);
        this.sessionProvder.getSession().flush();

        Map<Integer, List<ObservationUnitRow>> instanceObsUnitRowMap = this.datasetService.getInstanceIdToObservationUnitRowsMap(this.studyId, this.subObsDatasetId, this.instanceIds);
        List<ObservationUnitRow> observationUnitRows = instanceObsUnitRowMap.get(this.instanceIds.get(0));
        for(final ObservationUnitRow row: observationUnitRows) {
            Assert.assertEquals(param.getNewValue(), row.getVariables().get(TRAIT_NAME).getValue());
        }

        this.datasetService.deleteVariableValues(this.studyId, this.subObsDatasetId, searchDTO);
        this.sessionProvder.getSession().flush();

        instanceObsUnitRowMap = this.datasetService.getInstanceIdToObservationUnitRowsMap(this.studyId, this.subObsDatasetId, this.instanceIds);
        observationUnitRows = instanceObsUnitRowMap.get(this.instanceIds.get(0));
        for(final ObservationUnitRow row: observationUnitRows) {
            Assert.assertNull(row.getVariables().get(TRAIT_NAME).getValue());
        }
    }

    @Test
    public void testGetObservationUnitRows() {
        final ObservationUnitsSearchDTO searchDto = new ObservationUnitsSearchDTO();
        searchDto.setInstanceIds(this.instanceIds);
        final List<ObservationUnitRow> observationUnitRows = this.datasetService.getObservationUnitRows(this.studyId, this.subObsDatasetId,
                searchDto, new PageRequest(0, Integer.MAX_VALUE));
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
        searchDto.setInstanceIds(this.instanceIds);
        searchDto.getFilterColumns().add("TRIAL_INSTANCE");
        searchDto.getFilterColumns().add(TRAIT_NAME);
        final List<Map<String, Object>> rowsAsListMap = this.datasetService.getObservationUnitRowsAsMapList(this.studyId, this.subObsDatasetId,
            searchDto, new PageRequest(0, Integer.MAX_VALUE));
        Assert.assertNotNull(rowsAsListMap);
        Assert
			.assertEquals(80, rowsAsListMap
				.size()); //The number of germplasm in the study(20) multiplied by numberOfSubObservationUnits(2) multiplied by the number of reps (2)
        final Map<String, Object> dataMap = rowsAsListMap.get(0);
        Assert.assertEquals(searchDto.getFilterColumns().size(), dataMap.size());
        Assert.assertNotNull(dataMap.get("TRIAL_INSTANCE"));
        Assert.assertNull(dataMap.get(TRAIT_NAME));
    }

    @Test
    public void testGetTrialNumberPlotNumberObservationUnitIdTable() {
        final Set<Integer> trialInstances = Collections.singleton(1);
        final Set<Integer> plotNumbers = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20));
        Table<Integer, Integer, Integer> observationUnitIdsPlotNumberTable = this.datasetService.getTrialNumberPlotNumberObservationUnitIdTable(this.plotDatasetId, trialInstances, plotNumbers);
        Assert.assertNotNull(observationUnitIdsPlotNumberTable);
        Assert.assertEquals(plotNumbers.size(), observationUnitIdsPlotNumberTable.size());
        Assert.assertEquals(new HashSet<>(trialInstances), observationUnitIdsPlotNumberTable.rowKeySet());
        Assert.assertEquals(new HashSet<>(plotNumbers), observationUnitIdsPlotNumberTable.columnKeySet());

        // Table should be empty for not-existing plot numbers
        final Set<Integer> nonExistentPlotNumbers = new HashSet<>(Arrays.asList(42, 43, 44, 45));
        observationUnitIdsPlotNumberTable = this.datasetService.getTrialNumberPlotNumberObservationUnitIdTable(this.plotDatasetId, trialInstances, nonExistentPlotNumbers);
        Assert.assertTrue(observationUnitIdsPlotNumberTable.isEmpty());

        // Table should be empty for not-existing trial instance
        final Integer nonExistentTrialInstance = 2;
        observationUnitIdsPlotNumberTable = this.datasetService.getTrialNumberPlotNumberObservationUnitIdTable(this.plotDatasetId, Collections.singleton(nonExistentTrialInstance), plotNumbers);
        Assert.assertTrue(observationUnitIdsPlotNumberTable.isEmpty());
    }

    @Test
    public void testGetObservationSetColumnsForObservations() {
        final List<MeasurementVariable> columns = this.datasetService.getObservationSetColumns(this.studyId, this.subObsDatasetId, false);
        final Map<Integer, MeasurementVariable> columnsMap = columns.stream().collect(Collectors.toMap(MeasurementVariable::getTermId, Function
            .identity()));
        Assert.assertTrue(columnsMap.containsKey(TermId.TRIAL_INSTANCE_FACTOR.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.ENTRY_NO.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.ENTRY_TYPE.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.GID.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.DESIG.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.CROSS.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.PLOT_NO.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.REP_NO.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.ENTRY_CODE.getId()));
        Assert.assertTrue(columnsMap.containsKey(TRAIT_ID));
    }

    @Test
    public void testGetObservationSetColumnsMeans() {
        final VariableFilter variableFilter = this.createVariableFilter(VariableType.ANALYSIS.getName(),
            Arrays.asList("BLUEs", "BLUPs"));

        final Map<Integer, Variable> analysisVariablesMap = this.ontologyVariableService.getVariablesWithFilterById(variableFilter);

        // Create means dataset
        final MeansImportRequest meansImportRequest = new MeansImportRequest();
        final List<Geolocation> environmentGeolocations =
            this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(this.studyId);
        final List<MeansImportRequest.MeansData> meansDataList =
            environmentGeolocations.stream().map(o -> this.createMeansData(Integer.valueOf(o.getDescription()), 1, analysisVariablesMap))
                .collect(Collectors.toList());
        meansImportRequest.setData(meansDataList);

        final int meansDatasetId =
            this.analysisService.createMeansDataset(ContextHolder.getCurrentCrop(), this.studyId, meansImportRequest);

        final List<MeasurementVariable> columns = this.datasetService.getObservationSetColumns(this.studyId, meansDatasetId, false);
        final Map<Integer, MeasurementVariable> columnsMap = columns.stream().collect(Collectors.toMap(MeasurementVariable::getTermId, Function
            .identity()));

        Assert.assertTrue(columnsMap.containsKey(TermId.TRIAL_INSTANCE_FACTOR.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.GID.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.CROSS.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.DESIG.getId()));
        variableFilter.getVariableIds().stream().forEach(variableId -> Assert.assertTrue(columnsMap.containsKey((variableId))));
    }

    @Test
    public void testGetObservationSetColumnsSummaryStatistics() {
        final VariableFilter variableFilter = this.createVariableFilter(VariableType.ANALYSIS_SUMMARY.getName(),
            Arrays.asList("Heritability", "PValue", "CV"));
        final Map<Integer, Variable> analysisSummaryVariablesMap = this.ontologyVariableService.getVariablesWithFilterById(variableFilter);

        // Create summary statistics dataset for the first environment
        final SummaryStatisticsImportRequest summaryStatisticsImportRequest = new SummaryStatisticsImportRequest();
        final List<Geolocation> environmentGeolocations =
            this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(this.studyId);
        final List<SummaryStatisticsImportRequest.SummaryData> summaryDataList =
            Arrays.asList(
                this.createSummaryData(Integer.valueOf(environmentGeolocations.get(0).getDescription()), analysisSummaryVariablesMap));
        summaryStatisticsImportRequest.setData(summaryDataList);

        final int summaryStatisticsDatasetId =
            this.analysisService.createSummaryStatisticsDataset(
                ContextHolder.getCurrentCrop(), this.studyId,
                summaryStatisticsImportRequest);

        final List<MeasurementVariable> columns = this.datasetService.getObservationSetColumns(this.studyId, summaryStatisticsDatasetId, false);
        final Map<Integer, MeasurementVariable> columnsMap = columns.stream().collect(Collectors.toMap(MeasurementVariable::getTermId, Function
            .identity()));

        Assert.assertTrue(columnsMap.containsKey(TermId.TRIAL_INSTANCE_FACTOR.getId()));
        Assert.assertTrue(columnsMap.containsKey(TermId.LOCATION_ID.getId()));
        variableFilter.getVariableIds().stream().forEach(variableId -> Assert.assertTrue(columnsMap.containsKey((variableId))));
    }


    private void verifyObservationUnitRowValues(final ObservationUnitRow observationUnitRow) {
        Assert.assertNotNull(observationUnitRow.getVariables().get(TRAIT_NAME));
        Assert.assertNotNull(observationUnitRow.getVariables().get(SELECTION_NAME));
        Assert.assertNotNull(observationUnitRow.getVariables().get("LOCATION_ID"));
        Assert.assertNotNull(observationUnitRow.getVariables().get("TRIAL_INSTANCE"));
        Assert.assertNotNull(observationUnitRow.getVariables().get("ENTRY_NO"));
        Assert.assertNotNull(observationUnitRow.getVariables().get("ENTRY_CODE"));
        Assert.assertNotNull(observationUnitRow.getVariables().get("ENTRY_TYPE"));
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

        this.studyId = this.dataSetupTest.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), gids, "ABCD", DataSetupTest.NUMBER_OF_GERMPLASM, 2);
        this.instanceIds = new ArrayList<>(this.studyDataManager.getInstanceGeolocationIdsMap(this.studyId).values());

        this.plotDatasetId = this.studyId + 2;
        final DatasetDTO datasetDTO = this.datasetService.generateSubObservationDataset(this.studyId, "TEST NURSERY SUB OBS",
            DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(), this.instanceIds, 8206, 2, this.plotDatasetId);
        this.subObsDatasetId = datasetDTO.getDatasetId();
        this.datasetService.addDatasetVariable(datasetDTO.getDatasetId(), TRAIT_ID, VariableType.TRAIT, TRAIT_NAME);
        this.datasetService.addDatasetVariable(datasetDTO.getDatasetId(), 8263, VariableType.SELECTION_METHOD, SELECTION_NAME);
        this.sessionProvder.getSession().flush();
    }

    private Variable createTestVariable(final String variableName, final String propertyName, final String scaleName,
        final String methodName, final List<VariableType> variableTypes) {
        // Create traitVariable
        final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
            .save(variableName, RandomStringUtils.randomAlphanumeric(10), CvId.VARIABLES);
        final CVTerm property = this.daoFactory.getCvTermDao().save(propertyName, "", CvId.PROPERTIES);
        final CVTerm scale = this.daoFactory.getCvTermDao().save(scaleName, "", CvId.SCALES);
        this.daoFactory.getCvTermRelationshipDao().save(scale.getCvTermId(), TermId.HAS_TYPE.getId(), DataType.NUMERIC_VARIABLE.getId());
        final CVTerm method = this.daoFactory.getCvTermDao().save(methodName, "", CvId.METHODS);
        final CVTerm numericDataType = this.daoFactory.getCvTermDao().getById(DataType.NUMERIC_VARIABLE.getId());

        // Assign Property, Scale, Method
        this.daoFactory.getCvTermRelationshipDao()
            .save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getCvTermId());
        this.daoFactory.getCvTermRelationshipDao()
            .save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getCvTermId());
        this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());

        // Assign TRAIT and SELECTION_METHOD Variable types
        for (final VariableType variableType : variableTypes) {
            this.daoFactory.getCvTermPropertyDao()
                .save(new CVTermProperty(null, cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableType.getName(), 0));
        }

        final VariableFilter variableFilter = new VariableFilter();
        variableFilter.addVariableId(cvTermVariable.getCvTermId());
        return this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter).values().stream().findFirst().get();
    }

    private MeansImportRequest.MeansData createMeansData(final int environmentNumber, final int entryNo,
        final Map<Integer, Variable> analysisVariablesMap) {
        final MeansImportRequest.MeansData meansData = new MeansImportRequest.MeansData();
        meansData.setEntryNo(entryNo);
        meansData.setEnvironmentNumber(environmentNumber);
        final Map<String, Double> valuesMap = new HashMap<>();
        for (final Variable variable : analysisVariablesMap.values()) {
            valuesMap.put(variable.getName(), new Random().nextDouble());
        }
        meansData.setValues(valuesMap);
        return meansData;
    }

    private SummaryStatisticsImportRequest.SummaryData createSummaryData(final int environmentNumber,
        final Map<Integer, Variable> analysisSummaryVariablesMap) {
        final SummaryStatisticsImportRequest.SummaryData summaryData = new SummaryStatisticsImportRequest.SummaryData();
        summaryData.setEnvironmentNumber(environmentNumber);
        final Map<String, Double> valuesMap = new HashMap<>();
        for (final Variable variable : analysisSummaryVariablesMap.values()) {
            valuesMap.put(variable.getName(), new Random().nextDouble());
        }
        summaryData.setValues(valuesMap);
        return summaryData;
    }

    private VariableFilter createVariableFilter(final String variableType, final List<String> analysisMethodNames) {
        final Variable testVariable = this.createTestVariable(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10),
            RandomStringUtils.randomAlphanumeric(10), Arrays.asList(VariableType.TRAIT, VariableType.SELECTION_METHOD));

        // Create SSA Variables to be used in creating SSA dataset
        final AnalysisVariablesImportRequest analysisVariablesImportRequest = new AnalysisVariablesImportRequest();
        analysisVariablesImportRequest.setVariableType(variableType);
        analysisVariablesImportRequest.setVariableIds(Arrays.asList(testVariable.getId()));
        analysisVariablesImportRequest.setAnalysisMethodNames(analysisMethodNames);
        final MultiKeyMap createdAnalysisVariablesMap =
            this.ontologyVariableService.createAnalysisVariables(analysisVariablesImportRequest, new HashMap<>());

        final VariableFilter variableFilter = new VariableFilter();
        createdAnalysisVariablesMap.values().stream().forEach(i -> variableFilter.addVariableId((Integer) i));
        return variableFilter;
    }

}
