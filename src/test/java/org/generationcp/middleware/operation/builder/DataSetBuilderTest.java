
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
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

	DataSetBuilder dataSetBuilder;

	@Before
	public void setUp() throws MiddlewareQueryException {
		MockitoAnnotations.initMocks(this);

		this.dataSetBuilder = Mockito.spy(new DataSetBuilder(this.hibernateSessionProvider));
		Mockito.doReturn(this.generateDmsProject()).when(this.dataSetBuilder).getDmsProjectById(DataSetBuilderTest.STUDY_ID);
	}

	@Test
	public void testDataSetBuilderGetTrialDataSet() throws MiddlewareQueryException {

		DmsProject project = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID, 1);

		Assert.assertTrue("The Trial Dataset's project id should be 1", "1".equals(project.getProjectId().toString()));
	}

	private DmsProject generateDmsProject() {

		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(DataSetBuilderTest.STUDY_ID);

		List<ProjectRelationship> relatedBys = new ArrayList<>();
		relatedBys.add(this.createProjectRelationship(1));
		relatedBys.add(this.createProjectRelationship(2));
		relatedBys.add(this.createProjectRelationship(3));
		dmsProject.setRelatedBys(relatedBys);

		return dmsProject;
	}

	private ProjectRelationship createProjectRelationship(int projectId) {

		ProjectRelationship relationship = new ProjectRelationship();
		DmsProject project = new DmsProject();
		project.setProjectId(projectId);
		relationship.setSubjectProject(project);
		return relationship;

	}

}
