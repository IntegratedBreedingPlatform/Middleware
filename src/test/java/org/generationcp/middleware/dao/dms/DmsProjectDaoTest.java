
package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DmsProjectDaoTest {

	private DmsProjectDao dao;
	private Session mockSession;
	private SQLQuery mockQuery;

	@Before
	public void beforeEachTest() {
		this.mockSession = Mockito.mock(Session.class);

		this.dao = new DmsProjectDao();
		this.dao.setSession(this.mockSession);

		this.mockQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.mockSession.createSQLQuery(Mockito.anyString())).thenReturn(this.mockQuery);
	}

	/**
	 * Unit test (mocking the DB layer) to check the logic of adding appropriate type of reference (Study or Folder) based on DB data and
	 * the data mapping.
	 */
	@Test
	public void testGetStudyFolderMetadata() throws Exception {
		String programUUID = UUID.randomUUID().toString();

		List<Object[]> mockQueryResult = new ArrayList<Object[]>();

		Object[] mockDBRow1 = new Object[] {1, "Templates", "Trial and Nursery Templates", 0, null, null};
		mockQueryResult.add(mockDBRow1);

		Object[] mockDBRow2 = new Object[] {2, "My Folder", "My Folder Desc", 0, programUUID, null};
		mockQueryResult.add(mockDBRow2);

		Object[] mockDBRow3 = new Object[] {3, "My Nursery", "My Nursery Desc", 1, programUUID, String.valueOf(TermId.NURSERY.getId())};
		mockQueryResult.add(mockDBRow3);

		Object[] mockDBRow4 = new Object[] {4, "My Trial", "My Trial Desc", 1, programUUID, String.valueOf(TermId.TRIAL.getId())};
		mockQueryResult.add(mockDBRow4);

		Mockito.when(this.mockQuery.list()).thenReturn(mockQueryResult);

		List<Reference> result = dao.getRootFolders(programUUID);
		Assert.assertNotNull(result);
		Assert.assertEquals(mockQueryResult.size(), result.size());

		Reference templates = result.get(0);
		Assert.assertTrue(templates.isFolder());
		assertCommonDataMapping(mockDBRow1, templates);

		Reference myFolder = result.get(1);
		Assert.assertTrue(myFolder.isFolder());
		assertCommonDataMapping(mockDBRow2, myFolder);

		Reference myNursery = result.get(2);
		Assert.assertTrue(myNursery.isStudy());
		Assert.assertEquals(StudyType.N, ((StudyReference) myNursery).getStudyType());
		assertCommonDataMapping(mockDBRow3, myNursery);

		Reference myTrial = result.get(3);
		Assert.assertTrue(myTrial.isStudy());
		Assert.assertEquals(StudyType.T, ((StudyReference) myTrial).getStudyType());
		assertCommonDataMapping(mockDBRow4, myTrial);
	}

	private void assertCommonDataMapping(Object[] expected, Reference actual) {
		Assert.assertEquals(expected[0], actual.getId());
		Assert.assertEquals(expected[1], actual.getName());
		Assert.assertEquals(expected[2], actual.getDescription());
		Assert.assertEquals(expected[4], actual.getProgramUUID());
	}
}
