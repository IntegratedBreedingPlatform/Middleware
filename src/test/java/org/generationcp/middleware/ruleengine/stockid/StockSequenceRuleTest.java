
package org.generationcp.middleware.ruleengine.stockid;

import org.generationcp.middleware.ruleengine.RuleException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class StockSequenceRuleTest {

	private StockSequenceRule unitUnderTest;
	private StockIDGenerationRuleExecutionContext ruleContext;

	private final static Long NEW_SEQUENCE_NUMBER = new Long(1);

	@Before
	public void setUp() {
		unitUnderTest = new StockSequenceRule();
		ruleContext = new StockIDGenerationRuleExecutionContext(new ArrayList<String>());
	}

	@Test
	public void testSequenceOutputNewSequence() throws RuleException {
		Long sequence = (Long) unitUnderTest.runRule(ruleContext);

		assertEquals(sequence, NEW_SEQUENCE_NUMBER);
		assertEquals("Expected the rule output to use a new sequence if no start sequence provided", NEW_SEQUENCE_NUMBER,
				ruleContext.getSequenceNumber());

	}

	@Test
	public void testSequenceOutputExistingSequence() throws RuleException {
		Long existingSequenceNumber = 5L;
		ruleContext.setSequenceNumber(existingSequenceNumber);

		Long newSequenceNumber = (Long) unitUnderTest.runRule(ruleContext);
		assertEquals("Expected the rule output to increment the value of the existing sequence", new Long(existingSequenceNumber + 1),
				newSequenceNumber);

	}

	@Test
	public void testGetKey() {
		assertEquals(StockSequenceRule.KEY, unitUnderTest.getKey());
	}
}
