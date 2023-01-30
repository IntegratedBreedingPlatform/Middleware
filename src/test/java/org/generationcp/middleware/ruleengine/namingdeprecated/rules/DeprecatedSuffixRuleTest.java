
package org.generationcp.middleware.ruleengine.namingdeprecated.rules;

import junit.framework.Assert;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.namingdeprecated.impl.DeprecatedProcessCodeServiceImpl;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedSuffixRuleTest extends DeprecatedBaseNamingRuleTest {

	private DeprecatedSuffixRule rule;
	private Method breedingMethod;
	private String testGermplasmName;
	private Integer breedingMethodSnameType;

	@Before
	public void setUp() {
		this.processCodeService = new DeprecatedProcessCodeServiceImpl();
		this.breedingMethodSnameType = 5;
		this.breedingMethod = new Method();
		this.breedingMethod.setSnametype(this.breedingMethodSnameType);
		this.breedingMethod.setSuffix("test-suffix");
		this.row = new DeprecatedAdvancingSource();
		this.row.setBreedingMethod(this.breedingMethod);
		this.testGermplasmName = "CMT1234-B-3-";
		this.rule = new DeprecatedSuffixRule();
	}

	@Test
	public void testPrefixGenerationSimple() {

		List<String> input = new ArrayList<String>();
		input.add(this.testGermplasmName);

		try {
			input = (List<String>) this.rule.runRule(this.createExecutionContext(input));
		} catch (RuleException re) {
			Assert.fail("Rule failed to run for Prefix" + this.row.getBreedingMethod().getSuffix());
		}

		Assert.assertEquals(1, input.size());
		;
		Assert.assertEquals("Should return the correct name appended with prefix text", this.testGermplasmName
				+ this.row.getBreedingMethod().getSuffix(), input.get(0));
	}

}
