
package org.generationcp.middleware.ruleengine.stockid;

import junit.framework.Assert;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class StockNotationNumberRuleTest {

	private static final int TEST_NOTATION_NUMBER = 3;

	private StockNotationNumberRule unitUnderTest;
	private StockIDGenerationRuleExecutionContext ruleContext;
	private LotService lotService;

	@Before
	public void setUp() throws Exception {
		unitUnderTest = new StockNotationNumberRule();

		lotService = Mockito.mock(LotService.class);
		ruleContext = new StockIDGenerationRuleExecutionContext(null, lotService);
		ruleContext.setBreederIdentifier("DV");
	}

	@Test
	public void testStockNotation() throws RuleException {
		Mockito.when(this.lotService.getCurrentNotationNumberForBreederIdentifier(Mockito.anyString())).thenReturn(TEST_NOTATION_NUMBER);

		unitUnderTest.runRule(ruleContext);
		Assert.assertEquals("Unable to output the incremented value of the current notation number for input", new Integer(
				TEST_NOTATION_NUMBER + 1).toString(), ruleContext.getRuleExecutionOutput());
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(StockNotationNumberRule.KEY, unitUnderTest.getKey());
	}
}
