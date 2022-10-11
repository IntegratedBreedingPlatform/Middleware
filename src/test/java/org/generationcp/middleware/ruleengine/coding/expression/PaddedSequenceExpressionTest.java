package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.ExpressionUtils;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PaddedSequenceExpressionTest {

	private static final Integer NEXT_NUMBER_FROM_DB = 43;
	private static final String PREFIX = "ABC";
	private static final String SUFFIX = "XYZ";
	private static final String PADSEQ = "[PADSEQ]";
	private static final String PADSEQ_WITH_NUMBER = "[PADSEQ.%s]";
	private final PaddedSequenceExpression padSeqExpression = new PaddedSequenceExpression();
	private final NamingConfiguration namingConfiguration = new NamingConfiguration();

	@Mock
	private GermplasmNamingService germplasmNamingService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.padSeqExpression.setGermplasmNamingService(this.germplasmNamingService);

		this.namingConfiguration.setPrefix(PREFIX);
		this.namingConfiguration.setCount(PADSEQ);
		this.namingConfiguration.setSuffix(SUFFIX);
		Mockito.doReturn(NEXT_NUMBER_FROM_DB, NEXT_NUMBER_FROM_DB + 1, NEXT_NUMBER_FROM_DB + 2).when(this.germplasmNamingService)
			.getNextNumberAndIncrementSequence(PREFIX);
		Mockito.doReturn("0" + NEXT_NUMBER_FROM_DB, "0" + (NEXT_NUMBER_FROM_DB + 1), "0" + (NEXT_NUMBER_FROM_DB + 2))
			.when(this.germplasmNamingService).getNumberWithLeadingZeroesAsString(
			ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ExpressionUtils.DEFAULT_LENGTH));
	}

	@Test
	public void testExpressionRegex() {
		Assert.assertTrue(PaddedSequenceExpressionTest.PADSEQ.matches(this.padSeqExpression.getExpressionKey()));
		Assert.assertTrue("[PADSEQ.2]".matches(this.padSeqExpression.getExpressionKey()));
		Assert.assertTrue("[PADSEQ.23]".matches(this.padSeqExpression.getExpressionKey()));
		Assert.assertFalse("[PADSEQ.AB2]".matches(this.padSeqExpression.getExpressionKey()));
		Assert.assertFalse("[PADSEQ.ab2]".matches(this.padSeqExpression.getExpressionKey()));
	}

	@Test
	public void testApplyNoNumberOfDigitsSpecified() {
		final Integer count = 3;
		final List<StringBuilder> values = this.createInitialValues(this.namingConfiguration, count);

		this.padSeqExpression.apply(values, "", this.namingConfiguration);
		assertEquals(count.intValue(), values.size());
		assertEquals(PREFIX + "0" + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
		assertEquals(PREFIX + "0" + (NEXT_NUMBER_FROM_DB + 1) + SUFFIX, values.get(1).toString());
		assertEquals(PREFIX + "0" + (NEXT_NUMBER_FROM_DB + 2) + SUFFIX, values.get(2).toString());
	}

	@Test
	public void testApplyWithNumberOfDigitsSpecified() {
		final Integer numberofDigits = 5;
		this.namingConfiguration.setCount(String.format(PaddedSequenceExpressionTest.PADSEQ_WITH_NUMBER, numberofDigits));
		final Integer count = 3;
		final List<StringBuilder> values = this.createInitialValues(this.namingConfiguration, count);
		Mockito.doReturn("000" + NEXT_NUMBER_FROM_DB, "000" + (NEXT_NUMBER_FROM_DB + 1), "000" + (NEXT_NUMBER_FROM_DB + 2))
			.when(this.germplasmNamingService).getNumberWithLeadingZeroesAsString(
			ArgumentMatchers.anyInt(), ArgumentMatchers.eq(numberofDigits));

		this.padSeqExpression.apply(values, "", this.namingConfiguration);
		assertEquals(count.intValue(), values.size());
		assertEquals(PREFIX + "000" + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
		assertEquals(PREFIX + "000" + (NEXT_NUMBER_FROM_DB + 1) + SUFFIX, values.get(1).toString());
		assertEquals(PREFIX + "000" + (NEXT_NUMBER_FROM_DB + 2) + SUFFIX, values.get(2).toString());
	}

	private List<StringBuilder> createInitialValues(final NamingConfiguration config, final Integer count) {
		final List<StringBuilder> builders = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			final StringBuilder builder = new StringBuilder();
			builder.append(this.getNonNullValue(config.getPrefix()))
				.append(this.getNonNullValue(config.getCount()))
				.append(this.getNonNullValue(config.getSuffix()));
			builders.add(builder);
		}

		return builders;
	}

	private String getNonNullValue(final String value) {
		return value != null ? value : "";
	}

}
