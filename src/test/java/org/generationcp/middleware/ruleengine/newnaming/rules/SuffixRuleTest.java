
package org.generationcp.middleware.ruleengine.newnaming.rules;

import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.newnaming.impl.ProcessCodeServiceImpl;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class SuffixRuleTest extends BaseNamingRuleTest {

	private SuffixRule rule;
	private Method breedingMethod;
	private String testGermplasmName;
	private Integer breedingMethodSnameType;

	@Before
	public void setUp() {
		this.processCodeService = new ProcessCodeServiceImpl();
		this.breedingMethodSnameType = 5;
		this.breedingMethod = new Method();
		this.breedingMethod.setSnametype(this.breedingMethodSnameType);
		this.breedingMethod.setSuffix("test-suffix");
		this.row = Mockito.mock(AdvancingSource.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(this.row.getBreedingMethod()).thenReturn(this.breedingMethod);
		this.testGermplasmName = "CMT1234-B-3-";
		this.rule = new SuffixRule();
	}

	@Test
	public void testPrefixGenerationSimple() {

		List<String> input = new ArrayList();
		input.add(this.testGermplasmName);

		try {
			input = (List<String>) this.rule.runRule(this.createExecutionContext(input));
		} catch (org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct name appended with prefix text", this.testGermplasmName
				+ this.row.getBreedingMethod().getSuffix(), input.get(0));
	}

}
