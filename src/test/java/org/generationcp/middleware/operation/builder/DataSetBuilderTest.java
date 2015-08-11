
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
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

	private static int STUDY_ID = 1;

	@Mock
	HibernateSessionProvider hibernateSessionProvider;
	
	// Class to be tested - DO NOT MOCK
	DataSetBuilder dataSetBuilder;
	
	// Mock Services
	StudyDataManager studyDataManager; 
	DmsProjectDao dmsProjectDao;

	@Before
	public void setUp() throws MiddlewareQueryException {
		
		this.studyDataManager = Mockito.mock(StudyDataManagerImpl.class);
		this.dmsProjectDao = Mockito.mock(DmsProjectDao.class);
		Mockito.when((this.studyDataManager).getDatasetReferences(STUDY_ID)).thenReturn(generateDatasetReferences());
		Mockito.when(this.dmsProjectDao.getById(4)).thenReturn(generateDmsProject(4));
		
		MockitoAnnotations.initMocks(this);
		
		// Inject the mock services into the test class
		dataSetBuilder = new DataSetBuilder(this.hibernateSessionProvider, dmsProjectDao, studyDataManager);

	}

	@Test
	public void testDataSetBuilderGetTrialDataSet() throws MiddlewareQueryException {
		DmsProject trialDataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID);
		Assert.assertTrue("The Trial Dataset's project id should be 4", "4".equals(trialDataset.getProjectId().toString()));
	}

	private List<DatasetReference> generateDatasetReferences() {
		
		List<DatasetReference> dsRefs = new ArrayList<DatasetReference>();

		DatasetReference ref1 = new DatasetReference(1, "TestTrial");
		DatasetReference ref2 = new DatasetReference(4, "TestTrial-ENVIRONMENT");
		DatasetReference ref3 = new DatasetReference(3, "TestTrial-DUMMY");
		DatasetReference ref4 = new DatasetReference(2, "TestTrial-PLOTDATA");
		
		dsRefs.add(ref1);
		dsRefs.add(ref2);
		dsRefs.add(ref3);
		dsRefs.add(ref4);

		return dsRefs;
	}
	
	private DmsProject generateDmsProject(int id) {
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(id);
		return dmsProject;
	}

}
