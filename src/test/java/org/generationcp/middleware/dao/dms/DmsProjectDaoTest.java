
package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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

		this.dao = new DmsProjectDao(this.mockSession);

		this.mockQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(this.mockSession.createSQLQuery(DmsProjectDao.GET_CHILDREN_OF_FOLDER)).thenReturn(this.mockQuery);
		Mockito.when(this.mockQuery.addScalar(Mockito.anyString())).thenReturn(this.mockQuery);
		Mockito.when(this.mockQuery.addScalar(Mockito.anyString(), Mockito.any(Type.class))).thenReturn(this.mockQuery);

	}

	/**
	 * Unit test (mocking the DB layer) to check the logic of adding appropriate type of reference (Study or Folder) based on DB data and
	 * the data mapping.
	 */
	@Test
	public void testGetStudyFolderMetadata() throws Exception {

		final List<Object[]> mockQueryResult = new ArrayList<Object[]>();

		final Object[] mockDBRow1 = new Object[] {1, "Templates", "Trial and Nursery Templates", 0, null, null};
		mockQueryResult.add(mockDBRow1);

		final Object[] mockDBRow2 = new Object[] {2, "My Folder", "My Folder Desc", 0, PROG_UUID, null};
		mockQueryResult.add(mockDBRow2);

		final Integer ownerId1 = 100;
		final String ownerName1 = RandomStringUtils.randomAlphabetic(20);
		final Object[] mockDBRow3 =
			new Object[] {
				3, "My Nursery", "My Nursery Desc", 1, PROG_UUID, 1, StudyTypeDto.NURSERY_LABEL, StudyTypeDto.NURSERY_NAME,
				Byte.valueOf("1"), 200, true, ownerId1};
		mockQueryResult.add(mockDBRow3);

		final Integer ownerId2 = 110;
		final String ownerName2 = RandomStringUtils.randomAlphabetic(20);
		final Object[] mockDBRow4 =
			new Object[] {
				4, "My Trial", "My Trial Desc", 1, PROG_UUID, 2, StudyTypeDto.TRIAL_LABEL, StudyTypeDto.TRIAL_NAME,
				Byte.valueOf("1"), 201, false, ownerId2};
		mockQueryResult.add(mockDBRow4);

		Mockito.when(this.mockQuery.list()).thenReturn(mockQueryResult);

		final List<Reference> result = this.dao.getRootFolders(PROG_UUID, null);
		Assert.assertNotNull(result);
		Assert.assertEquals(mockQueryResult.size(), result.size());

		final Reference templates = result.get(0);
		Assert.assertTrue(templates.isFolder());
		this.assertCommonDataMapping(mockDBRow1, templates);

		final Reference myFolder = result.get(1);
		Assert.assertTrue(myFolder.isFolder());
		this.assertCommonDataMapping(mockDBRow2, myFolder);

		final Reference myNursery = result.get(2);
		Assert.assertTrue(myNursery.isStudy());
		final StudyReference nurseryResult = (StudyReference) myNursery;
		Assert.assertEquals(StudyTypeDto.NURSERY_NAME, nurseryResult.getStudyType().getName());
		Assert.assertTrue(nurseryResult.getIsLocked());
		Assert.assertEquals(ownerId1, nurseryResult.getOwnerId());
		this.assertCommonDataMapping(mockDBRow3, myNursery);

		final Reference myTrial = result.get(3);
		Assert.assertTrue(myTrial.isStudy());
		final StudyReference trialResult = (StudyReference) myTrial;
		Assert.assertEquals(StudyTypeDto.TRIAL_NAME, trialResult.getStudyType().getName());
		Assert.assertFalse(trialResult.getIsLocked());
		Assert.assertEquals(ownerId2, trialResult.getOwnerId());
		this.assertCommonDataMapping(mockDBRow4, myTrial);
	}

	@Test
	public void testCountCalculatedVariablesInDatasets() {
		final BigInteger expectedCount = BigInteger.valueOf(new Random().nextInt());
		Mockito.when(this.mockSession.createSQLQuery(DmsProjectDao.COUNT_CALCULATED_VARIABLES_IN_DATASETS)).thenReturn(this.mockQuery);
		Mockito.when(this.mockQuery.uniqueResult()).thenReturn(expectedCount);
		assertThat(dao.countCalculatedVariablesInDatasets(new HashSet<Integer>(Arrays.asList(1))), equalTo(expectedCount.intValue()));
	}

	private void assertCommonDataMapping(final Object[] expected, final Reference actual) {
		Assert.assertEquals(expected[0], actual.getId());
		Assert.assertEquals(expected[1], actual.getName());
		Assert.assertEquals(expected[2], actual.getDescription());
		Assert.assertEquals(expected[4], actual.getProgramUUID());
	}
}
