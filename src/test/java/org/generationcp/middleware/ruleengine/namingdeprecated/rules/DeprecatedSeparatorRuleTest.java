
package org.generationcp.middleware.ruleengine.namingdeprecated.rules;

import junit.framework.Assert;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedSeparatorRuleTest extends DeprecatedBaseNamingRuleTest {

	private DeprecatedSeparatorRule rule;
	private Method breedingMethod;
	private String testGermplasmName;
	private Integer breedingMethodSnameType;

	@Before
	public void setUp() {
		this.breedingMethodSnameType = 5;
		this.breedingMethod = new Method();
		this.breedingMethod.setSnametype(this.breedingMethodSnameType);
		this.breedingMethod.setSeparator("-");
		this.row = new DeprecatedAdvancingSource();
		this.row.setBreedingMethod(this.breedingMethod);
		this.testGermplasmName = "CMT1234";
		this.rule = new DeprecatedSeparatorRule();

	}

	@Test
	public void testGetGermplasmRootNameWithTheSameSnameTypeWithMethod() {

		List<String> input = new ArrayList<String>();
		input.add(this.testGermplasmName);

		try {
			input = (List<String>) this.rule.runRule(this.createExecutionContext(input));
		} catch (RuleException re) {
			Assert.fail("Rule failed to run for Separator" + this.row.getBreedingMethod().getSeparator());
		}

		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct name appended with a separator", this.testGermplasmName
				+ this.row.getBreedingMethod().getSeparator(), input.get(0));
	}

	@Test
	public void testGetGermplasmRootNameWithNullSeparator() {

		List<String> input = new ArrayList<String>();
		input.add(this.testGermplasmName);

		this.breedingMethod.setSeparator(null);

		try {
			input = (List<String>) this.rule.runRule(this.createExecutionContext(input));
		} catch (RuleException re) {
			Assert.fail("Rule failed to run for Separator" + this.row.getBreedingMethod().getSeparator());
		}

		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct name appended with a blank separator", this.testGermplasmName, input.get(0));
	}

}
