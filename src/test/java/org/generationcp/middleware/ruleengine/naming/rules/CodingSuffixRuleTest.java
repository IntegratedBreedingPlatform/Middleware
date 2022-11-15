
package org.generationcp.middleware.ruleengine.naming.rules;

import junit.framework.Assert;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.naming.newimpl.ProcessCodeServiceImpl;
import org.generationcp.middleware.ruleengine.naming.newrules.SuffixRule;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CodingSuffixRuleTest extends BaseNamingRuleTest {

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
		this.row = new DeprecatedAdvancingSource();
		this.row.setBreedingMethod(this.breedingMethod);
		this.testGermplasmName = "CMT1234-B-3-";
		this.rule = new SuffixRule();
	}

	@Test
	public void testPrefixGenerationSimple() {

		List<String> input = new ArrayList<String>();
		input.add(this.testGermplasmName);

		try {
			input = (List<String>) this.rule.runRule(this.createExecutionContext(input));
		} catch (org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(1, input.size());
		;
		Assert.assertEquals("Should return the correct name appended with prefix text", this.testGermplasmName
				+ this.row.getBreedingMethod().getSuffix(), input.get(0));
	}

}
