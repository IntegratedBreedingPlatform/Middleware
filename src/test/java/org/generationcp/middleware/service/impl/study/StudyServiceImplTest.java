package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.study.Measurement;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.Trait;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * The class <code>StudyServiceImplTest</code> contains tests for the class
 * <code>{@link StudyServiceImpl}</code>.
 *
 * @author Akhil
 */
public class StudyServiceImplTest {
	/**
	 * Run the StudyServiceImpl(HibernateSessionProvider) constructor test.
	 *
	 */
	@Test
	public void listTrialData() throws Exception {
		TraitServiceImpl mockTrialTraits = Mockito.mock(TraitServiceImpl.class);
		TrialMeasurements mockTrailMeasurements = Mockito.mock(TrialMeasurements.class);
		StudyServiceImpl result = new StudyServiceImpl(mockTrialTraits, mockTrailMeasurements);

		List<String> projectTraits = Arrays.<String> asList("Trait1", "Trait2");
		when(mockTrialTraits.getTraits(1234)).thenReturn(projectTraits);
		final List<Trait> traits = new ArrayList<Trait>();
		traits.add(new Trait("traitName", 1, "triatValue"));
		final Measurement measurement = new Measurement(1, "trialInstance", "entryType", 1234,
				"designation", "entryNo", "seedSource", "repitionNumber", "plotNumber", traits);
		final List<Measurement> testMeasurements = Collections.<Measurement> singletonList(measurement);
		when(mockTrailMeasurements.getAllMeasurements(1234, projectTraits)).thenReturn(
				testMeasurements);
		result.getMeasurements(1234);

		final List<Measurement> allMeasurements = mockTrailMeasurements.getAllMeasurements(1234, projectTraits);
		assertEquals(allMeasurements,testMeasurements);
	}
	
	@Test
	public void testlistAllStudies() throws MiddlewareQueryException {
		Session mockSession = Mockito.mock(Session.class);
		SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);
		
		HibernateSessionProvider mockSessionProvider = Mockito.mock(HibernateSessionProvider.class);
		Mockito.when(mockSessionProvider.getSession()).thenReturn(mockSession);
		Mockito.when(mockSession.createSQLQuery(Mockito.anyString())).thenReturn(mockSqlQuery);
		Mockito.when(mockSqlQuery.addScalar(Mockito.anyString())).thenReturn(mockSqlQuery);
		
		final Object[] testDBRow =
				{2007, "Wheat Trial 1", "Wheat Trial 1 Title", "c996de54-3ebb-41ca-8fed-160a33ffffd4", "10010", "Wheat Trial 1 Objective", "20150417", "20150422"};
		final List<Object[]> testResult = Arrays.<Object[]>asList(testDBRow);
		
		Mockito.when(mockSqlQuery.list()).thenReturn(testResult);
		
		StudyServiceImpl studyServiceImpl = new StudyServiceImpl(mockSessionProvider);
		List<StudySummary> studySummaries = studyServiceImpl.listAllStudies("c996de54-3ebb-41ca-8fed-160a33ffffd4");
		Assert.assertNotNull(studySummaries);
		Assert.assertEquals(1, studySummaries.size());
		
		StudySummary studySummary = studySummaries.get(0);
		
		Assert.assertEquals(testDBRow[0], studySummary.getId());
		Assert.assertEquals(testDBRow[1], studySummary.getName());
		Assert.assertEquals(testDBRow[2], studySummary.getTitle());
		Assert.assertEquals(testDBRow[3], studySummary.getProgramUUID());
		Assert.assertEquals(testDBRow[4], String.valueOf(studySummary.getType().getId()));
		Assert.assertEquals(testDBRow[5], studySummary.getObjective());
		Assert.assertEquals(testDBRow[6], studySummary.getStartDate());
		Assert.assertEquals(testDBRow[7], studySummary.getEndDate());
		
	}
}