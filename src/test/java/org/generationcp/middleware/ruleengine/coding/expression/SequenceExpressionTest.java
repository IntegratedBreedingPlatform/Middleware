package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SequenceExpressionTest {

	private static final Integer NEXT_NUMBER_FROM_DB = 22;
	private static final String PREFIX = "XYZ";
	private static final String SEQUENCE = "[SEQUENCE]";
	private static final String SUFFIX = "T";
	private final SequenceExpression sequenceExpression = new SequenceExpression();
	private final NamingConfiguration namingConfiguration = new NamingConfiguration();

	@Mock
	private GermplasmNamingService germplasmNamingService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.sequenceExpression.setGermplasmNamingService(this.germplasmNamingService);

		this.namingConfiguration.setPrefix(PREFIX);
		this.namingConfiguration.setCount(SEQUENCE);
		this.namingConfiguration.setSuffix(SUFFIX);
		Mockito.doReturn(NEXT_NUMBER_FROM_DB, NEXT_NUMBER_FROM_DB + 1, NEXT_NUMBER_FROM_DB + 2).when(this.germplasmNamingService)
			.getNextNumberAndIncrementSequence(PREFIX);
		Mockito
			.doReturn(String.valueOf(NEXT_NUMBER_FROM_DB), String.valueOf(NEXT_NUMBER_FROM_DB + 1), String.valueOf(NEXT_NUMBER_FROM_DB + 2))
			.when(this.germplasmNamingService).getNumberWithLeadingZeroesAsString(
			ArgumentMatchers.anyInt(), ArgumentMatchers.eq(1));
	}

	@Test
	public void testApplySingleValue() {
		final Integer count = 1;
		final List<StringBuilder> values = this.createInitialValues(this.namingConfiguration, count);

		this.sequenceExpression.apply(values, "", this.namingConfiguration);
		assertEquals(count.intValue(), values.size());
		assertEquals(PREFIX + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testApplyMultipleValues() {
		final Integer count = 3;
		final List<StringBuilder> values = this.createInitialValues(this.namingConfiguration, count);

		this.sequenceExpression.apply(values, "", this.namingConfiguration);
		assertEquals(count.intValue(), values.size());
		assertEquals(PREFIX + NEXT_NUMBER_FROM_DB + SUFFIX, values.get(0).toString());
		assertEquals(PREFIX + (NEXT_NUMBER_FROM_DB + 1) + SUFFIX, values.get(1).toString());
		assertEquals(PREFIX + (NEXT_NUMBER_FROM_DB + 2) + SUFFIX, values.get(2).toString());
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
