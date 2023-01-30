package org.generationcp.middleware.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.namingdeprecated.service.DeprecatedGermplasmNamingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CodingExpressionResolverTest {

	private static final String SEQUENCE_CODE = "[SEQUENCE]";

	@Mock
	private CodingExpressionFactory factory;

	@Mock
	private DeprecatedGermplasmNamingService germplasmNamingService;

	@InjectMocks
	private final CodingExpressionResolver codingExpressionResolver = new CodingExpressionResolver();

	@Test
	public void testResolve() {

		final int startingSequenceNumber = 11;
		final NamingConfiguration namingConfiguration = new NamingConfiguration();
		final String prefix = "IBC";
		namingConfiguration.setPrefix(prefix);
		final String currentInput = "CML";

		final CodingSequenceExpression codingSequenceExpression = new CodingSequenceExpression();
		codingSequenceExpression.setGermplasmNamingService(this.germplasmNamingService);
		Mockito.when(this.factory.lookup(SEQUENCE_CODE)).thenReturn(codingSequenceExpression);
		Mockito.when(this.germplasmNamingService.getNextNumberAndIncrementSequence(prefix)).thenReturn(startingSequenceNumber);
		Mockito.when(this.germplasmNamingService.getNumberWithLeadingZeroesAsString(startingSequenceNumber, 1)).thenReturn(String.valueOf(startingSequenceNumber));

		final List<String> result = this.codingExpressionResolver.resolve(currentInput, SEQUENCE_CODE, namingConfiguration);
		assertEquals(currentInput + startingSequenceNumber, result.get(0));
	}

}
