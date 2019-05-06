
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DataSetBuilderTest {

	private static final int STUDY_ID_WITH_STUDY = 1;
	private static final int STUDY_ID_NO_STUDY = 2;
	private static final int STUDY_ID_NODATASETS = 3;
	private static final int RANK = 0;
	private static final String DATASET_NAME = "DATASET_NAME";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String NFERT_NO = "NFert_NO";

	@Mock
	HibernateSessionProvider hibernateSessionProvider;

	// Class to be tested
	DataSetBuilder dataSetBuilder;

	// Mock Services
	StudyDataManager studyDataManager;
	DmsProjectDao dmsProjectDao;

	@Before
	public void setUp() throws MiddlewareQueryException {

		this.studyDataManager = Mockito.mock(StudyDataManagerImpl.class);
		this.dmsProjectDao = Mockito.mock(DmsProjectDao.class);
		Mockito.when((this.studyDataManager).getDatasetReferences(STUDY_ID_WITH_STUDY)).thenReturn(generateDatasetReferences(true));
		Mockito.when((this.studyDataManager).getDatasetReferences(STUDY_ID_NO_STUDY)).thenReturn(generateDatasetReferences(false));
		Mockito.when((this.studyDataManager).getDatasetReferences(STUDY_ID_NODATASETS)).thenReturn(new ArrayList<DatasetReference>());
		Mockito.when(this.dmsProjectDao.getById(4)).thenReturn(generateDmsProject(4));
		Mockito.when((this.studyDataManager).findOneDataSetReferenceByType(STUDY_ID_NO_STUDY, DataSetType.SUMMARY_DATA)).thenReturn(generateDatasetReference(3));
		Mockito.when(this.dmsProjectDao.getById(3)).thenReturn(generateDmsProject(3));

		MockitoAnnotations.initMocks(this);

		// Inject the mock services into the test class
		dataSetBuilder = new DataSetBuilder(this.hibernateSessionProvider, dmsProjectDao, studyDataManager);

	}

	@Test
	public void testGetStudyDataSetByUsingName() {
		final DmsProject dataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_WITH_STUDY);
		Assert.assertEquals("The Study Dataset's project id should be 4", "4", dataset.getProjectId().toString());
	}

	@Test
	public void testGetStudyDataSetByUsingDatasetType() {
		final DmsProject dataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_NO_STUDY);
		Assert.assertEquals("The Study Dataset's project id should be 3", "3", dataset.getProjectId().toString());
	}

	@Test
	public void filterVariables_partOfHiddenDatasetColumns_RemoveVariable() {
		final VariableTypeList variables = new VariableTypeList();
		variables.add(new DMSVariableType(DataSetBuilderTest.DATASET_NAME, DataSetBuilderTest.DATASET_NAME,
				StandardVariableTestDataInitializer.createStandardVariable(TermId.DATASET_NAME.getId(), DataSetBuilderTest.DATASET_NAME), DataSetBuilderTest.RANK));
		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, true);
		Assert.assertTrue("Dataset column removed.", newVariables.getVariableTypes().size() == 0);
	}

	@Test
	public void filterVariables_isMeasurementDatasetAndIsTrialFactors_RemoveVariable() {
		final VariableTypeList variables = new VariableTypeList();

		variables
				.add(new DMSVariableType(DataSetBuilderTest.LOCATION_NAME,
						DataSetBuilderTest.LOCATION_NAME,
						StandardVariableTestDataInitializer.createStandardVariableTestData(
								DataSetBuilderTest.LOCATION_NAME, PhenotypicType.TRIAL_ENVIRONMENT), DataSetBuilderTest.RANK));
		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, true);

		Assert.assertTrue("Trial factor removed.", newVariables.getVariableTypes().size() == 0);
	}

	@Test
	public void filterVariables_isTrialAndOcc_AddVariable() {
		final VariableTypeList variables = new VariableTypeList();

		variables.add(new DMSVariableType(DataSetBuilderTest.TRIAL_INSTANCE, DataSetBuilderTest.TRIAL_INSTANCE,
				StandardVariableTestDataInitializer.createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), DataSetBuilderTest.TRIAL_INSTANCE), DataSetBuilderTest.RANK));
		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, false);

		Assert.assertTrue("Trial instance added.", newVariables.size() == variables.size());
		Assert.assertEquals(DataSetBuilderTest.TRIAL_INSTANCE, newVariables.getVariableTypes().get(0).getLocalName());
	}

	@Test
	public void filterVariables_isTreatmentFactorDuplicate_RemoveVariable() {
		// Create treatment factor variable
		final VariableTypeList variables = new VariableTypeList();
		variables.add(DMSVariableTypeTestDataInitializer.createDmsVariableType(DataSetBuilderTest.NFERT_NO, DataSetBuilderTest.NFERT_NO));

		// Create additional prop added to projectprop when adding treatment factors
		DMSVariableType variableType = DMSVariableTypeTestDataInitializer.createDmsVariableType(DataSetBuilderTest.NFERT_NO, DataSetBuilderTest.NFERT_NO, DataSetBuilderTest.RANK);
		variableType.setVariableType(VariableType.TREATMENT_FACTOR);
		variableType.setStandardVariable(StandardVariableTestDataInitializer.createStandardVariable(8241, variableType.getLocalName()));
		variables.add(variableType);

		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, true);

		// Assert that duplicate was removed
		Assert.assertTrue("Duplicate treatment factor variable was removed.", newVariables.getVariableTypes().size() < variables.getVariableTypes().size());
		Assert.assertEquals(DataSetBuilderTest.NFERT_NO, variables.getVariableTypes().get(0).getLocalName());
		Assert.assertNotNull(newVariables.getVariableTypes().get(0).getVariableType());
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testGetStudyDataSetExceptionCase() {
		final DmsProject dataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_NODATASETS);
	}

	private List<DatasetReference> generateDatasetReferences(final boolean includeStudyDataset) {

		final List<DatasetReference> dsRefs = new ArrayList<DatasetReference>();

		final DatasetReference ref1 = new DatasetReference(1, "TestStudy");
		final DatasetReference ref3 = new DatasetReference(3, "TestStudy-DUMMY");
		final DatasetReference ref4 = new DatasetReference(2, "TestStudy-PLOTDATA");

		dsRefs.add(ref1);
		dsRefs.add(ref3);
		dsRefs.add(ref4);

		// this allows us to run tests with and without a Study dataset present
		if (includeStudyDataset) {
			final DatasetReference ref2 = new DatasetReference(4, "TestStudy-ENVIRONMENT");
			dsRefs.add(ref2);
		}

		return dsRefs;
	}

	private DmsProject generateDmsProject(final int id) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(id);
		return dmsProject;
	}

	private DatasetReference generateDatasetReference(final int id) {
		final DatasetReference dsr = new DatasetReference(id, "Name");
		return dsr;
	}

}
