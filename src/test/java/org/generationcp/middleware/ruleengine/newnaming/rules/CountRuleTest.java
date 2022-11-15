
package org.generationcp.middleware.ruleengine.newnaming.rules;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.Rule;
import org.generationcp.middleware.ruleengine.RuleExecutionContext;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class CountRuleTest extends BaseNamingRuleTest {

	private Rule rule;
	private Method breedingMethod;
	private Integer breedingMethodSnameType;
	private String name;

	@Before
	public void setUp() {
		this.breedingMethodSnameType = 5;
		this.breedingMethod = new Method();
		this.breedingMethod.setSnametype(this.breedingMethodSnameType);
		this.breedingMethod.setSeparator("-");
		this.breedingMethod.setCount("");
		this.row = Mockito.mock(AdvancingSource.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(this.row.getBreedingMethod()).thenReturn(this.breedingMethod);
		this.rule = new CountRule();

		this.name = "TestGP";
	}

	@Test
	public void testNoCountMethodInRule() {
		this.breedingMethod.setCount(null);

		final List<String> testCurrentInput = new ArrayList<>();
		testCurrentInput.add(this.name);

		final RuleExecutionContext context = this.createExecutionContext(testCurrentInput);

		try {
			this.rule.runRule(context);
		} catch (final org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}
		final List<String> output = (List<String>) context.getRuleExecutionOutput();
		Assert.assertEquals(testCurrentInput.size(), output.size());

		Assert.assertEquals("No changes should be made to current input if no count method is available", this.name, output.get(0));

	}

	@Test
	public void testNumberCount() {
		Mockito.when(this.row.getPlantsSelected()).thenReturn(3);
		this.setBulking(this.breedingMethod, true);

		List<String> input = new ArrayList<String>();

		input.add(this.name);
		final RuleExecutionContext context = this.createExecutionContext(input);

		try {
			this.rule.runRule(context);
		} catch (org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}
		input = (List<String>) context.getRuleExecutionOutput();
		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct countr", "TestGP", input.get(0));

	}

	@Test
	public void testNumberCountNoPlantsSelected() {
		Mockito.when(this.row.getPlantsSelected()).thenReturn(0);
		this.setBulking(this.breedingMethod, true);

		List<String> input = new ArrayList<String>();
		input.add(this.name);

		final RuleExecutionContext context = this.createExecutionContext(input);

		try {
			this.rule.runRule(context);
		} catch (org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}

		input = (List<String>) context.getRuleExecutionOutput();

		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct countr", "TestGP", input.get(0));

	}

	@Test
	public void testSequenceCountNoPlantsSelected() {
		Mockito.when(this.row.getPlantsSelected()).thenReturn(0);
		this.setBulking(this.breedingMethod, false);
		List<String> input = new ArrayList<String>();
		input.add(this.name);

		final RuleExecutionContext context = this.createExecutionContext(input);
		try {
			input = (List<String>) this.rule.runRule(context);
		} catch (org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct countr", "TestGP", input.get(0));

	}

	@Test
	public void testSequenceCount() {
		Mockito.when(this.row.getPlantsSelected()).thenReturn(0);
		Mockito.when(this.row.getBreedingMethod()).thenReturn(this.setBulking(this.breedingMethod, false));
		List<String> input = new ArrayList<String>();
		input.add(this.name);

		final RuleExecutionContext context = this.createExecutionContext(input);
		try {
			input = (List<String>) this.rule.runRule(context);
		} catch (org.generationcp.middleware.ruleengine.RuleException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(1, input.size());
		Assert.assertEquals("Should return the correct countr", "TestGP", input.get(0));

	}

	private Method setBulking(final Method method, final boolean isBulking) {

		if (isBulking) {
			method.setGeneq(TermId.BULKING_BREEDING_METHOD_CLASS.getId());
		} else {
			method.setGeneq(TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId());
		}
		return method;

	}
}
