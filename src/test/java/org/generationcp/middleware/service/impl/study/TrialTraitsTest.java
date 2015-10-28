
package org.generationcp.middleware.service.impl.study;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.service.api.study.TraitDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * The class <code>TrialTraitsTest</code> contains tests for the class <code>{@link TraitServiceImpl}</code>.
 *
 */
public class TrialTraitsTest {

	private static final int TEST_TRAIT_ID = 2019;
	private static final String TEST_TRAIT = "TestTrait";

	/**
	 * Run the {@link TraitServiceImpl}.getTraits() method and makes sure the query returns appropriate values.
	 *
	 */
	@Test
	public void traitsQueryRetrievesTrialRelatedTraits() throws Exception {
		final Session session = Mockito.mock(Session.class);

		final TraitServiceImpl trailTraits = new TraitServiceImpl(session);

		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(session.createSQLQuery(TraitServiceImpl.STUDY_TRAITS_QUERY)).thenReturn(mockSqlQuery);

		final Object[] sampleTraits = new Object[] {2019, TrialTraitsTest.TEST_TRAIT};

		Mockito.when(mockSqlQuery.list()).thenReturn(Arrays.<Object[]>asList(sampleTraits));

		final List<TraitDto> returnedTraits = trailTraits.getTraits(TrialTraitsTest.TEST_TRAIT_ID);

		Mockito.verify(mockSqlQuery).setParameter(0, TrialTraitsTest.TEST_TRAIT_ID);
		// add additional test code here
		Assert.assertEquals("Make sure that the traits returned are equal", returnedTraits,
				Collections.<TraitDto>singletonList(new TraitDto(TrialTraitsTest.TEST_TRAIT_ID, TrialTraitsTest.TEST_TRAIT)));
	}

}
