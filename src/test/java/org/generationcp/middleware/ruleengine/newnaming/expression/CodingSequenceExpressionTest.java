
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CodingSequenceExpressionTest extends TestExpression {

	private static final String ROOT_NAME = "GERMPLASM_TEST";
	private static final String SEPARATOR = "-";
	private static final String PREFIX = "IBX";
	private static final String SUFFIX = "P";
	private static final String SEQUENCE = "[SEQUENCE]";
	private static final Integer PLANTS_SELECTED = 5;
	private static final Integer NEXT_NUMBER_FROM_DB = 22;

	private SequenceExpression expression;

	@Mock
	private GermplasmNamingService germplasmNamingService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.expression = new SequenceExpression();
		this.expression.setGermplasmNamingService(this.germplasmNamingService);

		Mockito.doReturn(NEXT_NUMBER_FROM_DB, NEXT_NUMBER_FROM_DB + 1, NEXT_NUMBER_FROM_DB + 2, NEXT_NUMBER_FROM_DB + 3,
			NEXT_NUMBER_FROM_DB + 4).when(this.germplasmNamingService).getNextNumberAndIncrementSequence(
			ArgumentMatchers.anyString());
		Mockito
			.doReturn(String.valueOf(NEXT_NUMBER_FROM_DB), String.valueOf(NEXT_NUMBER_FROM_DB + 1), String.valueOf(NEXT_NUMBER_FROM_DB + 2),
				String.valueOf(NEXT_NUMBER_FROM_DB + 3), String.valueOf(NEXT_NUMBER_FROM_DB + 4)).when(this.germplasmNamingService)
			.getNumberWithLeadingZeroesAsString(
				ArgumentMatchers.anyInt(), ArgumentMatchers.eq(1));
	}

	@Test
	public void testWithNegativeNumberPlantsSelected() {
		final DeprecatedAdvancingSource source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, SEQUENCE, SUFFIX, true);
		source.setPlantsSelected(-2);
		final List<StringBuilder> values = this.createInitialValues(source);

		this.expression.apply(values, source, null);
		// The SEQUENCE expression will be replaced with blank string
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
		Mockito.verifyZeroInteractions(this.germplasmNamingService);
	}

	@Test
	public void testCaseSensitiveSequence() {
		final DeprecatedAdvancingSource source =
			this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, SEQUENCE.toLowerCase(), SUFFIX, true);
		source.setPlantsSelected(PLANTS_SELECTED);
		final List<StringBuilder> values = this.createInitialValues(source);

		this.expression.apply(values, source, null);
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testWithNullPlantsSelected() {
		// final false refers to nonBulking
		final DeprecatedAdvancingSource source =
			this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, SEQUENCE.toLowerCase(), SUFFIX, false);
		source.setPlantsSelected(null);
		final int currentMaxSequence = 10;
		source.setCurrentMaxSequence(currentMaxSequence);
		final List<StringBuilder> values = this.createInitialValues(source);

		this.expression.apply(values, source, null);
		Mockito.verifyZeroInteractions(this.germplasmNamingService);
		assertEquals(1, values.size());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testBulkingWithPlantsSelected() {
		final DeprecatedAdvancingSource source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, SEQUENCE, SUFFIX, true);
		source.setPlantsSelected(PLANTS_SELECTED);
		final List<StringBuilder> values = this.createInitialValues(source);

		this.expression.apply(values, source, null);
		// Expecting only one iteration for bulking method
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testNonBulkingWithPlantsSelected() {
		// final false refers to nonBulking
		final DeprecatedAdvancingSource source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, SEQUENCE, SUFFIX, false);
		source.setPlantsSelected(PLANTS_SELECTED);
		final int currentMaxSequence = 13;
		source.setCurrentMaxSequence(currentMaxSequence);
		final List<StringBuilder> values = this.createInitialValues(source);

		this.expression.apply(values, source, null);
		assertEquals(PLANTS_SELECTED.intValue(), values.size());
		Mockito.verify(this.germplasmNamingService, Mockito.times(PLANTS_SELECTED))
			.getNextNumberAndIncrementSequence(ROOT_NAME + SEPARATOR + PREFIX);
		// If non-bulking, name is generated for each plant selected
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + (NEXT_NUMBER_FROM_DB) + SUFFIX, values.get(0).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + (NEXT_NUMBER_FROM_DB + 1) + SUFFIX, values.get(1).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + (NEXT_NUMBER_FROM_DB + 2) + SUFFIX, values.get(2).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + (NEXT_NUMBER_FROM_DB + 3) + SUFFIX, values.get(3).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + (NEXT_NUMBER_FROM_DB + 4) + SUFFIX, values.get(4).toString());
	}
}
