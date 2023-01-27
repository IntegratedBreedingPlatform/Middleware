package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PaddedSequenceExpressionTest extends TestExpression {

	private static final String ROOT_NAME = "TESTING";
	private static final String SEPARATOR = "-";
	private static final String PREFIX = "ABC";
	private static final String SUFFIX = "XYZ";
	private static final String PADSEQ = "[PADSEQ]";
	private static final String PADSEQ_WITH_NUMBER = "[PADSEQ.%s]";
	private static final Integer PLANTS_SELECTED = 5;
	private static final Integer NEXT_NUMBER_FROM_DB = 22;

	private PaddedSequenceExpression expression;

	@Mock
	private GermplasmNamingService germplasmNamingService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.expression = new PaddedSequenceExpression();
		this.expression.setGermplasmNamingService(this.germplasmNamingService);

		Mockito.doReturn(NEXT_NUMBER_FROM_DB, NEXT_NUMBER_FROM_DB + 1, NEXT_NUMBER_FROM_DB + 2, NEXT_NUMBER_FROM_DB + 3,
			NEXT_NUMBER_FROM_DB + 4).when(this.germplasmNamingService).getNextNumberAndIncrementSequenceUsingNativeSQL(
			ArgumentMatchers.anyString());
		Mockito
			.doReturn("0" + NEXT_NUMBER_FROM_DB, "0" + (NEXT_NUMBER_FROM_DB + 1), "0" + (NEXT_NUMBER_FROM_DB + 2),
				"0" + (NEXT_NUMBER_FROM_DB + 3), "0" + (NEXT_NUMBER_FROM_DB + 4)).when(this.germplasmNamingService)
			.getNumberWithLeadingZeroesAsString(
				ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ExpressionUtils.DEFAULT_LENGTH));
	}

	@Test
	public void testExpressionRegex() {
		Assert.assertTrue(PaddedSequenceExpressionTest.PADSEQ.matches(this.expression.getExpressionKey()));
		Assert.assertTrue("[PADSEQ.2]".matches(this.expression.getExpressionKey()));
		Assert.assertTrue("[PADSEQ.23]".matches(this.expression.getExpressionKey()));
		Assert.assertFalse("[PADSEQ.AB2]".matches(this.expression.getExpressionKey()));
		Assert.assertFalse("[PADSEQ.ab2]".matches(this.expression.getExpressionKey()));
	}

	@Test
	public void testWithNegativeNumberPlantsSelected() {
		final Integer plantSelected = -2;
		final AdvancingSource source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, PADSEQ, SUFFIX, true, plantSelected);
		final List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);

		this.expression.apply(values, source, null);
		// The expression will be replaced with blank string
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
		Mockito.verifyZeroInteractions(this.germplasmNamingService);
	}

	@Test
	public void testCaseSensitiveKey() {
		final AdvancingSource
			source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, PADSEQ.toLowerCase(), SUFFIX, true, PLANTS_SELECTED);
		final List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);

		this.expression.apply(values, source, null);
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "0" + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testWithNullPlantsSelected() {
		// final false refers to nonBulking
		final Integer plantSelected = null;
		final AdvancingSource source =
			this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, PADSEQ.toLowerCase(), SUFFIX, false, plantSelected);
		final int currentMaxSequence = 10;
		source.setCurrentMaxSequence(currentMaxSequence);
		final List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);

		this.expression.apply(values, source, null);
		Mockito.verifyZeroInteractions(this.germplasmNamingService);
		assertEquals(1, values.size());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testBulkingWithPlantsSelected() {
		final AdvancingSource source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, PADSEQ, SUFFIX, true, PLANTS_SELECTED);
		final List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);

		this.expression.apply(values, source, null);
		// Expecting only one iteration for bulking method
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "0" + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testNonBulkingWithPlantsSelected() {
		// final false refers to nonBulking
		final AdvancingSource source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, PADSEQ, SUFFIX, false, PLANTS_SELECTED);
		final int currentMaxSequence = 13;
		source.setCurrentMaxSequence(currentMaxSequence);
		final List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);

		this.expression.apply(values, source, null);
		assertEquals(PLANTS_SELECTED.intValue(), values.size());
		Mockito.verify(this.germplasmNamingService, Mockito.times(PLANTS_SELECTED))
			.getNextNumberAndIncrementSequenceUsingNativeSQL(ROOT_NAME + SEPARATOR + PREFIX);
		Mockito.verify(this.germplasmNamingService, Mockito.times(PLANTS_SELECTED))
			.getNumberWithLeadingZeroesAsString(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ExpressionUtils.DEFAULT_LENGTH));

		// If non-bulking, name is generated for each plant selected
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "0" + (NEXT_NUMBER_FROM_DB) + SUFFIX, values.get(0).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "0" + (NEXT_NUMBER_FROM_DB + 1) + SUFFIX, values.get(1).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "0" + (NEXT_NUMBER_FROM_DB + 2) + SUFFIX, values.get(2).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "0" + (NEXT_NUMBER_FROM_DB + 3) + SUFFIX, values.get(3).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "0" + (NEXT_NUMBER_FROM_DB + 4) + SUFFIX, values.get(4).toString());
	}

	@Test
	public void testApplyWithNumberOfDigitsSpecified() {
		final Integer numberofDigits = 5;
		final String processCode = String.format(PaddedSequenceExpressionTest.PADSEQ_WITH_NUMBER, numberofDigits);
		final AdvancingSource source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, processCode, SUFFIX, false, PLANTS_SELECTED);

		final List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);
		Mockito.doReturn("000" + NEXT_NUMBER_FROM_DB, "000" + (NEXT_NUMBER_FROM_DB + 1), "000" + (NEXT_NUMBER_FROM_DB + 2),
			"000" + (NEXT_NUMBER_FROM_DB + 3), "000" + (NEXT_NUMBER_FROM_DB + 4))
			.when(this.germplasmNamingService).getNumberWithLeadingZeroesAsString(
			ArgumentMatchers.anyInt(), ArgumentMatchers.eq(numberofDigits));

		this.expression.apply(values, source, null);
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "000" + (NEXT_NUMBER_FROM_DB) + SUFFIX, values.get(0).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "000" + (NEXT_NUMBER_FROM_DB + 1) + SUFFIX, values.get(1).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "000" + (NEXT_NUMBER_FROM_DB + 2) + SUFFIX, values.get(2).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "000" + (NEXT_NUMBER_FROM_DB + 3) + SUFFIX, values.get(3).toString());
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + "000" + (NEXT_NUMBER_FROM_DB + 4) + SUFFIX, values.get(4).toString());
	}

}
