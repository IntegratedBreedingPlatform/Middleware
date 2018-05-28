
package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DmsProjectDaoTest {

	private DmsProjectDao dao;
	private Session mockSession;
	private SQLQuery mockQuery;
	private static final String PROG_UUID = UUID.randomUUID().toString();

	@Before
	public void beforeEachTest() {
		this.mockSession = Mockito.mock(Session.class);

		this.dao = new DmsProjectDao();
		this.dao.setSession(this.mockSession);

		this.mockQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.mockSession.createSQLQuery(DmsProjectDao.GET_CHILDREN_OF_FOLDER)).thenReturn(this.mockQuery);

	}

	/**
	 * Unit test (mocking the DB layer) to check the logic of adding appropriate type of reference (Study or Folder) based on DB data and
	 * the data mapping.
	 */
	@Test
	public void testGetStudyFolderMetadata() throws Exception {

		List<Object[]> mockQueryResult = new ArrayList<Object[]>();

		Object[] mockDBRow1 = new Object[] {1, "Templates", "Trial and Nursery Templates", 0, null, null};
		mockQueryResult.add(mockDBRow1);

		Object[] mockDBRow2 = new Object[] {2, "My Folder", "My Folder Desc", 0, PROG_UUID, null};
		mockQueryResult.add(mockDBRow2);

		Object[] mockDBRow3 = new Object[] {3, "My Nursery", "My Nursery Desc", 1, PROG_UUID, StudyTypeDto.NURSERY_NAME};
		mockQueryResult.add(mockDBRow3);

		Object[] mockDBRow4 = new Object[] {4, "My Trial", "My Trial Desc", 1, PROG_UUID, StudyTypeDto.TRIAL_NAME};
		mockQueryResult.add(mockDBRow4);

		Mockito.when(this.mockQuery.list()).thenReturn(mockQueryResult);

		List<Reference> result = this.dao.getRootFolders(PROG_UUID);
		Assert.assertNotNull(result);
		Assert.assertEquals(mockQueryResult.size(), result.size());

		Reference templates = result.get(0);
		Assert.assertTrue(templates.isFolder());
		this.assertCommonDataMapping(mockDBRow1, templates);

		Reference myFolder = result.get(1);
		Assert.assertTrue(myFolder.isFolder());
		this.assertCommonDataMapping(mockDBRow2, myFolder);

		Reference myNursery = result.get(2);
		Assert.assertTrue(myNursery.isStudy());
		Assert.assertEquals(StudyTypeDto.NURSERY_NAME, ((StudyReference) myNursery).getStudyType());
		this.assertCommonDataMapping(mockDBRow3, myNursery);

		Reference myTrial = result.get(3);
		Assert.assertTrue(myTrial.isStudy());
		Assert.assertEquals(StudyTypeDto.TRIAL_NAME, ((StudyReference) myTrial).getStudyType());
		this.assertCommonDataMapping(mockDBRow4, myTrial);
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testNullStudyTypeParameter1() {
		this.dao.getRootFolders(PROG_UUID);
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testEmptyStudyTypeParameter1() {
		this.dao.getRootFolders(PROG_UUID);
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testNullStudyTypeParameter2() {
		this.dao.getChildrenOfFolder(1, PROG_UUID);
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testEmptyStudyTypeParameter2() {
		this.dao.getChildrenOfFolder(1, PROG_UUID);
	}

	@Test
	public void testGetStudyMetadata() {
 		Mockito.when(this.mockSession.createSQLQuery(DmsProjectDao.GET_STUDY_METADATA_BY_ID)).thenReturn(this.mockQuery);

		final Object[] mockDBRow1 = new Object[] {"31", 2088, "TR", StudyTypeDto.TRIAL_NAME, "10300", "2088", "TR", "20161212", "", "9006", "2"};
		Mockito.when(this.mockQuery.uniqueResult()).thenReturn(mockDBRow1);
		StudyMetadata studyMetadata = this.dao.getStudyMetadata(31);

		assertThat(studyMetadata.getStudyDbId(), equalTo(Integer.parseInt((String)mockDBRow1[0])));
		assertThat(studyMetadata.getNurseryOrTrialId(), equalTo(mockDBRow1[1]));
		assertThat(studyMetadata.getStudyName(), equalTo(mockDBRow1[2]));
		assertThat(studyMetadata.getStudyType(), equalTo(mockDBRow1[3]));
		assertThat(studyMetadata.getSeasons().get(0), equalTo(TermId.getById(Integer.parseInt((String) mockDBRow1[4])).toString()));
		assertThat(studyMetadata.getTrialDbId(), equalTo(Integer.parseInt( (String) mockDBRow1[5])));
		assertThat(studyMetadata.getTrialName(), equalTo(mockDBRow1[6]));
		assertThat(studyMetadata.getStartDate(), equalTo((mockDBRow1[7])));
		assertThat(studyMetadata.getEndDate(), equalTo(mockDBRow1[8]));
		assertThat(studyMetadata.getActive(), equalTo(Boolean.FALSE));
		assertThat(studyMetadata.getLocationId(), equalTo(Integer.parseInt((String) mockDBRow1[10])));

	}

	private void assertCommonDataMapping(Object[] expected, Reference actual) {
		Assert.assertEquals(expected[0], actual.getId());
		Assert.assertEquals(expected[1], actual.getName());
		Assert.assertEquals(expected[2], actual.getDescription());
		Assert.assertEquals(expected[4], actual.getProgramUUID());
	}
}
