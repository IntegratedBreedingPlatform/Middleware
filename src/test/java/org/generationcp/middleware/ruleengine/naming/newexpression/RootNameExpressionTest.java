package org.generationcp.middleware.ruleengine.naming.newexpression;

import junit.framework.Assert;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RootNameExpressionTest extends TestExpression {

	private RootNameExpression rootNameExpression;

	@Before
	public void init() {

		this.rootNameExpression = new RootNameExpression();

	}

	@Test
	public void testCrossNameEnclosing() throws Exception {
		final List<String> input = Arrays.asList("a(b/c)d", "a/bc", "/abc", "(ab)/(de)", "(abc)a/e", "((a/b))", "b/e(a/b)", "(b/e)/(a/b)",
				"(CML146/CLQ-6203)/CML147", "(CLQ-6203/CML150)/CML144", "(L-133/LSA-297)/PA-1", "((P 47/MPSWCB 4) 11//(MPSWCB", "(a//b)",
				"(a/b/c/d)");

		final List<String> expectedOutput =
				Arrays.asList("a(b/c)d", "(a/bc)", "(/abc)", "((ab)/(de))", "((abc)a/e)", "((a/b))", "(b/e(a/b))", "((b/e)/(a/b))",
						"((CML146/CLQ-6203)/CML147)", "((CLQ-6203/CML150)/CML144)", "((L-133/LSA-297)/PA-1)",
						"(((P 47/MPSWCB 4) 11//(MPSWCB)", "(a//b)", "(a/b/c/d)");

		final DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("Germplasm", null, null, null, null, true);
		int i = 0;
		final BasicNameDTO name = new BasicNameDTO();
		name.setTypeId(10);
		source.getNames().add(name);
		source.getBreedingMethod().setSnametype(10);
		for (final String nameString : input) {
			System.out.println("INPUT = " + nameString);
			final List<StringBuilder> builders = new ArrayList<StringBuilder>();
			builders.add(new StringBuilder());
			name.setNval(nameString);
			this.rootNameExpression.apply(builders, source, null);
			final String output = builders.get(0).toString();
			Assert.assertEquals(expectedOutput.get(i), output);
			i++;
		}
	}

	@Test
	public void testIfThereIsNoMatchingName() throws Exception {
		final List<String> input = Arrays.asList("a(b/c)d", "a/bc", "/abc", "(ab)/(de)", "(abc)a/e", "((a/b))", "b/e(a/b)", "(b/e)/(a/b)",
				"(CML146/CLQ-6203)/CML147", "(CLQ-6203/CML150)/CML144", "(L-133/LSA-297)/PA-1", "((P 47/MPSWCB 4) 11//(MPSWCB", "(a//b)",
				"(a/b/c/d)");

		final DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("Germplasm", null, null, null, null, true);
		final BasicNameDTO name = new BasicNameDTO();
		name.setTypeId(11);
		name.setNstat(2);
		final List<BasicNameDTO> names = new ArrayList<>();
		names.add(name);
		source.setNames(names);
		source.getBreedingMethod().setSnametype(10);
		for (final String nameString : input) {
			final List<StringBuilder> builders = new ArrayList<StringBuilder>();
			builders.add(new StringBuilder());
			name.setNval(nameString);
			this.rootNameExpression.apply(builders, source, null);
			final String output = builders.get(0).toString();
			Assert.assertEquals("", output);
		}
	}

	@Test
	public void testSnametypeIsNull() {

		final String lineNameValue = "DESIGNATION1";
		final String derivativeNameValue = "DERIVATIVE NAME1";

		final DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("Germplasm", null, null, null, null, true);

		// Create lineName Name and set is as preferred
		final BasicNameDTO lineName = new BasicNameDTO();
		lineName.setTypeId(GermplasmNameType.LINE_NAME.getUserDefinedFieldID());
		lineName.setNval(lineNameValue);
		lineName.setNstat(1);

		// Create derivative name
		final BasicNameDTO derivativeName = new BasicNameDTO();
		derivativeName.setTypeId(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID());
		derivativeName.setNval(derivativeNameValue);
		derivativeName.setNstat(2);

		final List<BasicNameDTO> names = new ArrayList<>();
		names.add(lineName);
		names.add(derivativeName);

		source.setNames(names);

		// Set the snametype to null
		source.getBreedingMethod().setSnametype(null);

		final List<StringBuilder> builders = new ArrayList<StringBuilder>();
		builders.add(new StringBuilder());

		this.rootNameExpression.apply(builders, source, null);

		final String output = builders.get(0).toString();
		Assert.assertEquals("If snametype of the breeding method is null, then the preferred name should be used as a root name.",
				lineNameValue, output);
	}

}
