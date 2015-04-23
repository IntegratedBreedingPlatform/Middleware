package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.study.Measurement;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.Trait;
import org.hibernate.Session;
import org.junit.*;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.tmatesoft.sqljet.core.internal.lang.SqlParser.when_expr_return;

import static org.junit.Assert.*;

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
}