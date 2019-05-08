
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DatasetType;
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
		Mockito.when((this.studyDataManager).findOneDataSetReferenceByType(STUDY_ID_NO_STUDY, DatasetType.SUMMARY_DATA)).thenReturn(generateDatasetReference(3));
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
