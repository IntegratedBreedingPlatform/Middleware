
package org.generationcp.middleware.ruleengine.stockid;

import org.generationcp.middleware.ruleengine.RuleException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class BreederIdentifierRuleTest {

	private static final String TEST_BREEDER_IDENTIFIER = "DV";

	@Mock
	private BreederIdentifierGenerationStrategy generationStrategy;

	@InjectMocks
	private BreederIdentifierRule unitUnderTest;
	private StockIDGenerationRuleExecutionContext ruleContext;

	@Before
	public void setUp() {
		ruleContext = new StockIDGenerationRuleExecutionContext(new ArrayList<String>());
	}

	@Test
	public void testPresetBreederIdentifierAvailable() throws RuleException {
		ruleContext.setBreederIdentifier(TEST_BREEDER_IDENTIFIER);

		unitUnderTest.runRule(ruleContext);

		Mockito.verify(generationStrategy, Mockito.never()).generateBreederIdentifier();
		Assert.assertEquals("Expected rule to provide the breeder identifier present in context", TEST_BREEDER_IDENTIFIER,
				ruleContext.getRuleExecutionOutput());

	}

	@Test(expected = IllegalStateException.class)
	public void testBreederIdentifierNotAvailable() throws RuleException {
		unitUnderTest.runRule(ruleContext);
	}

	@Test
	public void testKey() {
		Assert.assertEquals(BreederIdentifierRule.KEY, unitUnderTest.getKey());
	}
}
