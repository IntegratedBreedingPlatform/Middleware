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
public class CodingSuffixRuleTest {

	@Mock
	private CodingExpressionResolver codingExpressionResolver;

	@InjectMocks
	private CodingSuffixRule codingSuffixRule;

	@Test
	public void testRunRule() throws RuleException {

		final String suffix = "XYZ";

		final List<String> sequenceOrder = new ArrayList<>();
		final NamingConfiguration namingConfiguration = new NamingConfiguration();

		namingConfiguration.setSuffix(suffix);
		final CodingRuleExecutionContext context = new CodingRuleExecutionContext(sequenceOrder, namingConfiguration);
		context.setCurrentData("");

		Mockito.when(codingExpressionResolver.resolve(context.getCurrentData(), namingConfiguration.getSuffix(), namingConfiguration))
				.thenReturn(Arrays.asList(suffix));

		assertEquals(suffix, this.codingSuffixRule.runRule(context));
	}

}
