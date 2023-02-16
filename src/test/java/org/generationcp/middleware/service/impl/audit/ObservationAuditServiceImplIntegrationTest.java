package org.generationcp.middleware.service.impl.audit;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.audit.ObservationAuditService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsParamDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.dataset.ObservationAuditDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObservationAuditServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private ObservationAuditService observationAuditService;

	@Autowired
	private DatasetService datasetService;

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

	private Integer studyId;
	private Integer plotDatasetId;
	private List<Integer> instanceIds;
	private Integer subObsDatasetId;
	private DaoFactory daoFactory;
	private WorkbenchDaoFactory workbenchDaoFactory;

	public static final int TRAIT_ID = 20451;
	private static final String SELECTION_NAME = "NPSEL";

	private static final String TRAIT_NAME = "GW_DW_g1000grn";

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
	public void testGetPhenotypeAuditList() {
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

		Map<Integer, List<ObservationUnitRow>> instanceObsUnitRowMap =
			this.datasetService.getInstanceIdToObservationUnitRowsMap(this.studyId, this.subObsDatasetId, this.instanceIds);
		List<ObservationUnitRow> observationUnitRows = instanceObsUnitRowMap.get(this.instanceIds.get(0));
		ObservationUnitRow observation1 = observationUnitRows.get(0);

		final List<ObservationAuditDTO> auditList = this.observationAuditService.getObservationAuditList(observation1.getObsUnitId(),
			TRAIT_ID,
			new PageRequest(0, Integer.MAX_VALUE));
		Assert.assertNotNull(auditList);
		Assert.assertFalse(auditList.isEmpty());
		Assert.assertEquals(auditList.get(0).getRevisionType(), RevisionType.CREATION);
		Assert.assertEquals(observation1.getVariableValueByVariableId(TRAIT_ID), auditList.get(0).getValue());
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
}
