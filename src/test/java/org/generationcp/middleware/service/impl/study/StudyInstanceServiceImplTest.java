package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StudyInstanceServiceImplTest extends IntegrationTestBase {

	public static final String PROGRAM_UUID = UUID.randomUUID().toString();
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String LOCATION_NAME = "LOCATION_NAME";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private DaoFactory daoFactory;
	private StudyDataManagerImpl studyDataManager;
	private StudyTestDataInitializer studyTestDataInitializer;
	private StudyInstanceService studyInstanceService;
	private Project commonTestProject;
	private CropType cropType;
	private StudyReference studyReference;
	private DatasetReference environmentDataset;

	@Before
	public void setup() throws Exception {

		this.studyInstanceService = new StudyInstanceServiceImpl(this.sessionProvder);
		this.studyDataManager = new StudyDataManagerImpl(this.sessionProvder);
		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		this.studyTestDataInitializer =
			new StudyTestDataInitializer(this.studyDataManager, this.ontologyManager, this.commonTestProject, this.germplasmDataManager,
				this.locationManager);

		this.cropType = this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.name());

		this.studyReference = this.studyTestDataInitializer.addTestStudy();

		// Create an environment dataset with TRIAL_INSTANCE and LOCATION_NAME properties.
		final VariableTypeList environmentVariables = new VariableTypeList();
		environmentVariables
			.add(new DMSVariableType(TRIAL_INSTANCE, "Trial instance - enumerated (number)", this.ontologyDataManager.getStandardVariable(
				TermId.TRIAL_INSTANCE_FACTOR.getId(), null), 1));
		environmentVariables.add(new DMSVariableType(LOCATION_NAME, "Location Name", this.ontologyDataManager.getStandardVariable(
			TermId.LOCATION_ID.getId(), null), 2));
		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(RandomStringUtils.randomAlphanumeric(10));
		datasetValues.setDescription(RandomStringUtils.randomAlphanumeric(10));
		this.environmentDataset =
			this.studyDataManager
				.addDataSet(this.studyReference.getId(), environmentVariables, datasetValues, null, DatasetTypeEnum.SUMMARY_DATA.getId());

	}

	@Test
	public void testCreateStudyInstance() {

		final int instanceNumber = 1;
		this.studyInstanceService.createStudyInstance(this.cropType, this.environmentDataset.getId(), String.valueOf(instanceNumber));

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<StudyInstance> studyInstances =
			this.studyInstanceService.getStudyInstances(this.studyReference.getId());

		final StudyInstance studyInstance = studyInstances.get(0);
		assertEquals(instanceNumber, studyInstance.getInstanceNumber());
		assertNotNull(studyInstance.getInstanceDbId());
		assertNotNull(studyInstance.getExperimentId());
		assertNotNull(studyInstance.getLocationId());
		assertFalse(studyInstance.isHasFieldmap());
		assertEquals("Unspecified Location", studyInstance.getLocationName());
		assertEquals("NOLOC", studyInstance.getLocationAbbreviation());
		assertNull(studyInstance.getCustomLocationAbbreviation());
	}

}
