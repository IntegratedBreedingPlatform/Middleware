/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class GeolocationDaoTest {

	private static final String LOCATION_DESCRIPTION = "LOCATION_DESCRIPTION";
	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String ALGERIA = "ALGERIA";
	private static final String ADRAR = "ADRAR";
	private static final String STUDY_DESCRIPTION = "STUDY DESCRIPTION";
	private static final Integer STUDY_ID = 1001;
	private static final Integer LOCATION_ID = 3005;
	private static final String STUDY_NAME = "STUDY NAME";
	private static final String AFRICA_RICE_CENTER = "AFRICA RICE CENTER";
	private static final int ENVIRONMENT_ID = 5822;
	private GeolocationDao dao;
	private Session session;

	@Before
	public void setUp() throws Exception {
		this.session = Mockito.mock(Session.class);
		this.dao = new GeolocationDao();
		this.dao.setSession(this.session);
	}

	@Test
	public void testGetStudyEnvironmentDetails() {
		final Set<Integer> environmentIds = new HashSet<Integer>();
		environmentIds.add(5822);
		final SQLQuery mockQuery = Mockito.mock(SQLQuery.class);
		final List<Object[]> mockQueryResult = new ArrayList<Object[]>();
		final Object[] result = new Object[] { GeolocationDaoTest.ENVIRONMENT_ID, GeolocationDaoTest.AFRICA_RICE_CENTER,
				GeolocationDaoTest.LOCATION_ID, GeolocationDaoTest.STUDY_ID, GeolocationDaoTest.STUDY_NAME,
				GeolocationDaoTest.STUDY_DESCRIPTION, GeolocationDaoTest.ADRAR, GeolocationDaoTest.ALGERIA };
		mockQueryResult.add(result);
		Mockito.when(mockQuery.list()).thenReturn(mockQueryResult);

		Mockito.when(this.session.createSQLQuery(Matchers.anyString())).thenReturn(mockQuery);
		final List<TrialEnvironment> environments = this.dao.getTrialEnvironmentDetails(environmentIds);
		Assert.assertEquals("The environments should contain 1 trial environment.", 1, environments.size());

		final TrialEnvironment trialEnvironment = environments.get(0);
		Assert.assertEquals("The environment id should be " + GeolocationDaoTest.ENVIRONMENT_ID,
				GeolocationDaoTest.ENVIRONMENT_ID, trialEnvironment.getId());

		final LocationDto locationDto = trialEnvironment.getLocation();
		Assert.assertEquals(Integer.valueOf(GeolocationDaoTest.LOCATION_ID), locationDto.getId());
		Assert.assertEquals(GeolocationDaoTest.AFRICA_RICE_CENTER, locationDto.getLocationName());
		Assert.assertEquals(GeolocationDaoTest.ADRAR, locationDto.getProvinceName());
		Assert.assertEquals(GeolocationDaoTest.ALGERIA, locationDto.getCountryName());

		final StudyReference studyReference = trialEnvironment.getStudy();
		Assert.assertEquals(GeolocationDaoTest.STUDY_ID, studyReference.getId());
		Assert.assertEquals(GeolocationDaoTest.STUDY_NAME, studyReference.getName());
		Assert.assertEquals(GeolocationDaoTest.STUDY_DESCRIPTION, studyReference.getDescription());
	}

	@Test
	public void testGetPropertiesForStudyEnvironments() {
		final SQLQuery mockQuery = Mockito.mock(SQLQuery.class);
		final List<Object[]> mockQueryResult = new ArrayList<Object[]>();
		final Object[] result = new Object[] {
			GeolocationDaoTest.LOCATION_ID, GeolocationDaoTest.LOCATION_NAME,
			GeolocationDaoTest.LOCATION_DESCRIPTION, GeolocationDaoTest.ENVIRONMENT_ID,
			GeolocationDaoTest.AFRICA_RICE_CENTER};
		mockQueryResult.add(result);
		Mockito.when(mockQuery.list()).thenReturn(mockQueryResult);
		Mockito.when(this.session.createSQLQuery(Matchers.anyString())).thenReturn(mockQuery);
		final List<TrialEnvironmentProperty> properties = this.dao
			.getPropertiesForTrialEnvironments(Arrays.asList(GeolocationDaoTest.ENVIRONMENT_ID));
		final TrialEnvironmentProperty property = properties.get(0);
		Assert.assertEquals(GeolocationDaoTest.LOCATION_ID, property.getId());
		Assert.assertEquals(GeolocationDaoTest.LOCATION_NAME, property.getName());
		Assert.assertEquals(GeolocationDaoTest.LOCATION_DESCRIPTION, property.getDescription());
		final Map<Integer, String> environmentValuesMap = property.getEnvironmentValuesMap();
		Assert.assertEquals(
			GeolocationDaoTest.AFRICA_RICE_CENTER,
			environmentValuesMap.get(GeolocationDaoTest.ENVIRONMENT_ID));
	}

	@Test
	public void testGetInstanceMetadataByInstanceId() {

		final Random random = new Random();
		final int studyId = random.nextInt();
		final int instanceId = random.nextInt();
		final SQLQuery mockQuery = Mockito.mock(SQLQuery.class);
		final InstanceMetadata mockQueryResult = new InstanceMetadata();

		Mockito.when(mockQuery.uniqueResult()).thenReturn(mockQueryResult);
		Mockito.when(this.session.createSQLQuery(Matchers.anyString())).thenReturn(mockQuery);

		final Optional<InstanceMetadata> result = this.dao.getInstanceMetadataByInstanceId(studyId, instanceId);

		Assert.assertSame(result.get(), mockQueryResult);
		Mockito.verify(mockQuery).setParameter("studyId", studyId);
		Mockito.verify(mockQuery).setParameter("instanceId", instanceId);

	}
}
