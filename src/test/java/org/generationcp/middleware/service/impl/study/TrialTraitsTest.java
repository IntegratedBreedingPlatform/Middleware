package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * The class <code>TrialTraitsTest</code> contains tests for the class <code>{@link TrialTraits}</code>.
 *
 */
public class TrialTraitsTest {
	/**
	 * Run the {@link TrialTraits}.getTraits() method and makes sure the query returns appropriate values.
	 *
	 */
	@Test
	public void traitsQueryRetrievesTrialRelatedTraits()
		throws Exception {
		final Session session = Mockito.mock(Session.class);

		final TrialTraits trailTraits = new TrialTraits(session);
		
		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);
		when(session.createSQLQuery((new TraitNamesQuery()).generateQuery())).thenReturn(mockSqlQuery);
		final List<String> sampleTraits = Collections.<String>singletonList("TestTrait");
		when(mockSqlQuery.list()).thenReturn(sampleTraits);

		int traitId = 2019;
		List<String> returnedTraits = trailTraits.getTraits(traitId);
		
		verify(mockSqlQuery).setParameter(0, traitId);
		// add additional test code here
		assertEquals("Make sure that the traits returned are equal", returnedTraits, sampleTraits);
	}


}