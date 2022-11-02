package org.generationcp.middleware.ruleengine.coding;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.coding.expression.CodingExpressionResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CodingCountRuleTest {

	@Mock
	private CodingExpressionResolver codingExpressionResolver;

	@InjectMocks
	private CodingCountRule codingCountRule;

	@Test
	public void testRunRule() throws RuleException {

		final List<String> sequenceOrder = new ArrayList<>();
		final NamingConfiguration namingConfiguration = new NamingConfiguration();
		namingConfiguration.setCount("[SEQUENCE]");
		final CodingRuleExecutionContext context = new CodingRuleExecutionContext(sequenceOrder, namingConfiguration);
		context.setCurrentData("CML");

		final String resolvedValue = "CML1";
		Mockito.when(codingExpressionResolver.resolve(context.getCurrentData(), namingConfiguration.getCount(), namingConfiguration))
				.thenReturn(Arrays.asList(resolvedValue));

		assertEquals(resolvedValue, this.codingCountRule.runRule(context));
	}

}
