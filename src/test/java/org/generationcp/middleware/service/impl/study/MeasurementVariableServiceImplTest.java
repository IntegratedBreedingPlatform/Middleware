package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;

/**
 * The class <code>MeasurementVariableServiceImplTest</code> contains tests for the class <code>{@link MeasurementVariableServiceImpl}</code>.
 */
// FIXME IBP-2716 Convert to IntegrationTest
public class MeasurementVariableServiceImplTest {

	private static final int TEST_STUDY_ID = 4062;
	private static final int TEST_TRAIT_ID = 2019;

	private static final String TEST_TRAIT = "TestTrait";

	/**
	 * Run the {@link MeasurementVariableServiceImpl}.getTraits() method and makes sure the query returns appropriate values.
	 */
	@Test
	public void traitsQueryRetrievesStudyRelatedTraits() throws Exception {
		final Session session = Mockito.mock(Session.class);

		final MeasurementVariableServiceImpl trailTraits = new MeasurementVariableServiceImpl(session);

		final SQLQuery mockSqlQuery = Mockito.mock(SQLQuery.class);

		Mockito.when(session.createSQLQuery(MeasurementVariableServiceImpl.STUDY_VARIABLES_QUERY)).thenReturn(mockSqlQuery);

		final Object[] sampleTraits = new Object[] {TEST_TRAIT_ID, TEST_TRAIT};

		Mockito.when(mockSqlQuery.list()).thenReturn(Arrays.<Object[]>asList(sampleTraits));
		final List<MeasurementVariableDto> returnedTraits = trailTraits.getVariables(TEST_STUDY_ID, VariableType.TRAIT.getId());

		Mockito.verify(mockSqlQuery).setParameter("studyId", TEST_STUDY_ID);
		Mockito.verify(mockSqlQuery).setParameterList("variablesTypes", new Integer[] {VariableType.TRAIT.getId()});

		assertThat(returnedTraits, not(empty()));
		assertThat(returnedTraits, hasSize(1));
		assertThat(returnedTraits.get(0).getId(), is(equalTo(TEST_TRAIT_ID)));
		assertThat(returnedTraits.get(0).getName(), is(equalTo(TEST_TRAIT)));
	}

}
