
package org.generationcp.middleware.service;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class FieldbookServiceTest {

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNull() throws MiddlewareQueryException {
		FieldbookServiceImpl impl = Mockito.mock(FieldbookServiceImpl.class);
		Assert.assertFalse("Should return false since the workbook is null", impl.setOrderVariableByRank(null));
	}

	@Test
	public void testSetOrderVariableByRankIfWorkbookIsNotNull() throws MiddlewareQueryException {
		FieldbookServiceImpl impl = Mockito.spy(new FieldbookServiceImpl());
		Mockito.when(impl.getWorkbookBuilder()).thenReturn(Mockito.mock(WorkbookBuilder.class));
		Mockito.when(impl.getProjectPropertyDao()).thenReturn(Mockito.mock(ProjectPropertyDao.class));
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setId(1);
		studyDetails.setStudyName("Test Name");
		workbook.setStudyDetails(studyDetails);
		Assert.assertTrue("Should return true since the workbook is not null", impl.setOrderVariableByRank(workbook));
	}

	@Test
	public void testGetCompleteDataset() throws MiddlewareQueryException {
		FieldbookServiceImpl impl = Mockito.spy(new FieldbookServiceImpl());
		Mockito.when(impl.getWorkbookBuilder()).thenReturn(Mockito.mock(WorkbookBuilder.class));
		Mockito.when(impl.getProjectPropertyDao()).thenReturn(Mockito.mock(ProjectPropertyDao.class));
		DataSetBuilder builder = Mockito.mock(DataSetBuilder.class);
		Mockito.when(impl.getDataSetBuilder()).thenReturn(builder);
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setId(1);
		studyDetails.setStudyName("Test Name");
		workbook.setStudyDetails(studyDetails);
		Mockito.when(builder.buildCompleteDataset(Matchers.anyInt(), Matchers.anyBoolean())).thenReturn(workbook);
		impl.getCompleteDataset(1, true);
		Mockito.verify(impl, Mockito.times(1)).setOrderVariableByRank(Matchers.any(Workbook.class), Matchers.anyInt());

	}
}
