
package org.generationcp.middleware.ruleengine.stockid;

import junit.framework.Assert;
import org.generationcp.middleware.ruleengine.RuleException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class StockIDSeparatorRuleTest {

	private static final String TEST_SEPARATOR = "|";

	private StockIDSeparatorRule unitUnderTest;
	private StockIDGenerationRuleExecutionContext ruleContext;

	@Before
	public void setUp() {
		unitUnderTest = new StockIDSeparatorRule();
		ruleContext = new StockIDGenerationRuleExecutionContext(new ArrayList<String>());
	}

	@Test
	public void testNoSuppliedSeparator() throws RuleException {
		unitUnderTest.runRule(ruleContext);

		Assert.assertEquals("Expected rule to output the default separator", StockIDSeparatorRule.DEFAULT_SEPARATOR,
				ruleContext.getRuleExecutionOutput());
	}

	@Test
	public void testSeparatorSupplied() throws RuleException {
		ruleContext.setSeparator(TEST_SEPARATOR);

		unitUnderTest.runRule(ruleContext);

		Assert.assertEquals("Expected the rule to output the separator provided in the context", TEST_SEPARATOR,
				ruleContext.getRuleExecutionOutput());
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(StockIDSeparatorRule.KEY, unitUnderTest.getKey());
	}
}
