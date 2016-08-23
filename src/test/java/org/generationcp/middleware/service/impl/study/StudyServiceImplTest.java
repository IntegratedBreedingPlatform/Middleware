
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * The class <code>StudyServiceImplTest</code> contains tests for the class <code>{@link StudyServiceImpl}</code>.
 *
 * @author Akhil
 */
public class StudyServiceImplTest {

	/**
	 * Run the StudyServiceImpl(HibernateSessionProvider) constructor test.
	 *
	 */
	@Test
	public void listMeasurementData() throws Exception {
		final TraitService mockTrialTraits = Mockito.mock(TraitService.class);
		final StudyMeasurements mockTrailMeasurements = Mockito.mock(StudyMeasurements.class);
		final StudyGermplasmListService mockStudyGermplasmListService = Mockito.mock(StudyGermplasmListService.class);

		final StudyServiceImpl result = new StudyServiceImpl(mockTrialTraits, mockTrailMeasurements, mockStudyGermplasmListService);

		final List<TraitDto> projectTraits = Arrays.<TraitDto>asList(new TraitDto(1, "Trait1"), new TraitDto(1, "Trait2"));
		Mockito.when(mockTrialTraits.getTraits(1234)).thenReturn(projectTraits);
		final List<MeasurementDto> traits = new ArrayList<MeasurementDto>();
		traits.add(new MeasurementDto(new TraitDto(1, "traitName"), 9999, "triatValue"));
		final ObservationDto measurement =
				new ObservationDto(1, "trialInstance", "entryType", 1234, "designation", "entryNo", "seedSource", "repitionNumber",
						"plotNumber", traits);
		final List<ObservationDto> testMeasurements = Collections.<ObservationDto>singletonList(measurement);
		Mockito.when(mockTrailMeasurements.getAllMeasurements(1234, projectTraits, 1, 1, 100)).thenReturn(testMeasurements);
		result.getObservations(1234, 1, 1, 100);

		final List<ObservationDto> allMeasurements = mockTrailMeasurements.getAllMeasurements(1234, projectTraits, 1, 1, 100);
		Assert.assertEquals(allMeasurements, testMeasurements);
	}

	@Test
	public void testlistAllStudies() throws MiddlewareQueryException {
		final Session mockSession = Mockito.mock(Session.class);
		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);

		final HibernateSessionProvider mockSessionProvider = Mockito.mock(HibernateSessionProvider.class);
		Mockito.when(mockSessionProvider.getSession()).thenReturn(mockSession);
		Mockito.when(mockSession.createSQLQuery(Matchers.anyString())).thenReturn(mockSqlQuery);
		Mockito.when(mockSqlQuery.addScalar(Matchers.anyString())).thenReturn(mockSqlQuery);

		final Object[] testDBRow =
			{2007, "Wheat Trial 1", "Wheat Trial 1 Title", "c996de54-3ebb-41ca-8fed-160a33ffffd4", "10010", "Wheat Trial 1 Objective",
				"20150417", "20150422", "Mr. Breeder", "Auckland", "Summer"};
		final List<Object[]> testResult = Arrays.<Object[]>asList(testDBRow);

		Mockito.when(mockSqlQuery.list()).thenReturn(testResult);

		final StudyServiceImpl studyServiceImpl = new StudyServiceImpl(mockSessionProvider);
		StudySearchParameters searchParameters = new StudySearchParameters();
		searchParameters.setProgramUniqueId("c996de54-3ebb-41ca-8fed-160a33ffffd4");
		final List<StudySummary> studySummaries = studyServiceImpl.search(searchParameters);
		Assert.assertNotNull(studySummaries);
		Assert.assertEquals(1, studySummaries.size());

		final StudySummary studySummary = studySummaries.get(0);

		Assert.assertEquals(testDBRow[0], studySummary.getId());
		Assert.assertEquals(testDBRow[1], studySummary.getName());
		Assert.assertEquals(testDBRow[2], studySummary.getTitle());
		Assert.assertEquals(testDBRow[3], studySummary.getProgramUUID());
		Assert.assertEquals(testDBRow[4], String.valueOf(studySummary.getType().getId()));
		Assert.assertEquals(testDBRow[5], studySummary.getObjective());
		Assert.assertEquals(testDBRow[6], studySummary.getStartDate());
		Assert.assertEquals(testDBRow[7], studySummary.getEndDate());
		Assert.assertEquals(testDBRow[8], studySummary.getPrincipalInvestigator());
		Assert.assertEquals(testDBRow[9], studySummary.getLocation());
		Assert.assertEquals(testDBRow[10], studySummary.getSeason());

	}

	@Test
	public void testGetStudyInstances() throws Exception {
		final Session mockSession = Mockito.mock(Session.class);
		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);

		final HibernateSessionProvider mockSessionProvider = Mockito.mock(HibernateSessionProvider.class);
		Mockito.when(mockSessionProvider.getSession()).thenReturn(mockSession);
		Mockito.when(mockSession.createSQLQuery(Matchers.anyString())).thenReturn(mockSqlQuery);
		Mockito.when(mockSqlQuery.addScalar(Matchers.anyString())).thenReturn(mockSqlQuery);

		final Object[] testDBRow = {12345, "Gujarat, India", "GUJ", 1};
		final List<Object[]> testResult = Arrays.<Object[]>asList(testDBRow);
		Mockito.when(mockSqlQuery.list()).thenReturn(testResult);

		final StudyServiceImpl studyServiceImpl = new StudyServiceImpl(mockSessionProvider);

		final List<StudyInstance> studyInstances = studyServiceImpl.getStudyInstances(123);

		Assert.assertTrue(studyInstances.size() == 1);
		Assert.assertEquals(testDBRow[0], studyInstances.get(0).getInstanceDbId());
		Assert.assertEquals(testDBRow[1], studyInstances.get(0).getLocationName());
		Assert.assertEquals(testDBRow[2], studyInstances.get(0).getLocationAbbreviation());
		Assert.assertEquals(testDBRow[3], studyInstances.get(0).getInstanceNumber());
	}
}
