
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
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

	private static int STUDY_ID_WITHTRIAL = 1;
	private static int STUDY_ID_NOTRIAL = 2;
	private static int STUDY_ID_NODATASETS = 3;

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
		Mockito.when((this.studyDataManager).getDatasetReferences(STUDY_ID_WITHTRIAL)).thenReturn(generateDatasetReferences(true));
		Mockito.when((this.studyDataManager).getDatasetReferences(STUDY_ID_NOTRIAL)).thenReturn(generateDatasetReferences(false));
		Mockito.when((this.studyDataManager).getDatasetReferences(STUDY_ID_NODATASETS)).thenReturn(new ArrayList<DatasetReference>());
		Mockito.when(this.dmsProjectDao.getById(4)).thenReturn(generateDmsProject(4));
		Mockito.when((this.studyDataManager).findOneDataSetByType(STUDY_ID_NOTRIAL, DataSetType.SUMMARY_DATA)).thenReturn(generateDataset(3));
		Mockito.when(this.dmsProjectDao.getById(3)).thenReturn(generateDmsProject(3));

		MockitoAnnotations.initMocks(this);

		// Inject the mock services into the test class
		dataSetBuilder = new DataSetBuilder(this.hibernateSessionProvider, dmsProjectDao, studyDataManager);

	}

	@Test
	public void testGetTrialDataSetByUsingName() {
		DmsProject trialDataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_WITHTRIAL);
		Assert.assertTrue("The Trial Dataset's project id should be 4", "4".equals(trialDataset.getProjectId().toString()));
	}

	@Test
	public void testGetTrialDataSetByUsingDatasetType() {
		DmsProject trialDataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_NOTRIAL);
		Assert.assertTrue("The Trial Dataset's project id should be 3", "3".equals(trialDataset.getProjectId().toString()));
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testGetTrialDataSetExceptionCase() {
		DmsProject trialDataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_NODATASETS);
	}

	private List<DatasetReference> generateDatasetReferences(boolean includeTrialDataset) {

		List<DatasetReference> dsRefs = new ArrayList<DatasetReference>();

		DatasetReference ref1 = new DatasetReference(1, "TestTrial");
		DatasetReference ref3 = new DatasetReference(3, "TestTrial-DUMMY");
		DatasetReference ref4 = new DatasetReference(2, "TestTrial-PLOTDATA");

		dsRefs.add(ref1);
		dsRefs.add(ref3);
		dsRefs.add(ref4);

		// this allows us to run tests with and without a Trial dataset present
		if (includeTrialDataset) {
			DatasetReference ref2 = new DatasetReference(4, "TestTrial-ENVIRONMENT");
			dsRefs.add(ref2);
		}

		return dsRefs;
	}

	private DmsProject generateDmsProject(int id) {
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(id);
		return dmsProject;
	}

	private List<DmsProject> getListOfDatasets() {
		List<DmsProject> list = new ArrayList<DmsProject>();
		list.add(generateDmsProject(3));
		return list;
	}

	private DataSet generateDataset(int id) {
		DataSet ds = new DataSet();
		ds.setId(3);
		return ds;
	}

}
