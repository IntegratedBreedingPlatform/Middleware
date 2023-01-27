
package org.generationcp.middleware.ruleengine.naming.rules;

import junit.framework.Assert;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class PrefixRuleTest extends BaseNamingRuleTest {

	private PrefixRule rule;
	private Method breedingMethod;
	private String testGermplasmName;
	private Integer breedingMethodSnameType;

	@Before
	public void setUp() {

		this.breedingMethodSnameType = 5;
		this.breedingMethod = new Method();
		this.breedingMethod.setSnametype(this.breedingMethodSnameType);
		this.row = Mockito.mock(AdvancingSource.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(this.row.getBreedingMethod()).thenReturn(this.breedingMethod);
		this.testGermplasmName = "CMT1234-";
		this.rule = new PrefixRule();
	}

	@Test
	public void testPrefixGenerationSimple() {
		this.breedingMethod.setPrefix("B");
		List<String> input = new ArrayList();
		input.add(this.testGermplasmName);

		try {
			input = (List<String>) this.rule.runRule(this.createExecutionContext(input));
		} catch (final org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}
		System.out.println(input.get(0));
		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct name appended with prefix text", this.testGermplasmName
				+ this.row.getBreedingMethod().getPrefix(), input.get(0));
	}

	@Test
	public void testSeasonCodePrefix() {
		this.breedingMethod.setPrefix("Wet");
		this.row.setSeason("Wet");
		List<String> input = new ArrayList();
		input.add(this.testGermplasmName);
		try {
			input = (List<String>) this.rule.runRule(this.createExecutionContext(input));
		} catch (final org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}
		System.out.println(input.get(0));
		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct name appended with prefix text", this.testGermplasmName + this.row.getSeason(),
				input.get(0));
	}

}
