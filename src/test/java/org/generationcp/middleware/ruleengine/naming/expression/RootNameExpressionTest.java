package org.generationcp.middleware.ruleengine.naming.expression;

import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
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
	public void testCrossNameEnclosing() {
		final List<String> input = Arrays.asList("a(b/c)d", "a/bc", "/abc", "(ab)/(de)", "(abc)a/e", "((a/b))", "b/e(a/b)", "(b/e)/(a/b)",
			"(CML146/CLQ-6203)/CML147", "(CLQ-6203/CML150)/CML144", "(L-133/LSA-297)/PA-1", "((P 47/MPSWCB 4) 11//(MPSWCB", "(a//b)",
			"(a/b/c/d)");

		final List<String> expectedOutput =
			Arrays.asList("a(b/c)d", "(a/bc)", "(/abc)", "((ab)/(de))", "((abc)a/e)", "((a/b))", "(b/e(a/b))", "((b/e)/(a/b))",
				"((CML146/CLQ-6203)/CML147)", "((CLQ-6203/CML150)/CML144)", "((L-133/LSA-297)/PA-1)",
				"(((P 47/MPSWCB 4) 11//(MPSWCB)", "(a//b)", "(a/b/c/d)");

		final AdvancingSource source = this.createAdvancingSourceTestData("Germplasm", null, null, null, null, true, 2);
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
	public void testIfThereIsNoMatchingName() {
		final List<String> input = Arrays.asList("a(b/c)d", "a/bc", "/abc", "(ab)/(de)", "(abc)a/e", "((a/b))", "b/e(a/b)", "(b/e)/(a/b)",
			"(CML146/CLQ-6203)/CML147", "(CLQ-6203/CML150)/CML144", "(L-133/LSA-297)/PA-1", "((P 47/MPSWCB 4) 11//(MPSWCB", "(a//b)",
			"(a/b/c/d)");

		final BasicNameDTO name = new BasicNameDTO();
		name.setTypeId(11);
		name.setNstat(2);
		final List<BasicNameDTO> names = new ArrayList<>();
		names.add(name);

		final AdvancingSource source =
			new AdvancingSource(new BasicGermplasmDTO(), names, new ObservationUnitRow(), new ObservationUnitRow(),
				new Method(), new Method(), null,
				RandomStringUtils.randomAlphabetic(10), 2);
		source.getBreedingMethod().setSnametype(10);
		for (final String nameString : input) {
			System.out.println("INPUT = " + nameString);
			final List<StringBuilder> builders = new ArrayList<>();
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

		final AdvancingSource source =
			new AdvancingSource(new BasicGermplasmDTO(), names, new ObservationUnitRow(), new ObservationUnitRow(),
				new Method(), new Method(), null,
				RandomStringUtils.randomAlphabetic(10), 2);

		// Set the snametype to null
		source.getBreedingMethod().setSnametype(null);

		final List<StringBuilder> builders = new ArrayList<>();
		builders.add(new StringBuilder());

		this.rootNameExpression.apply(builders, source, null);

		final String output = builders.get(0).toString();
		Assert.assertEquals("If snametype of the breeding method is null, then the preferred name should be used as a root name.",
			lineNameValue, output);
	}

	public AdvancingSource createAdvancingSourceTestData(final List<BasicNameDTO> names) {

		final Method method = new Method();
		method.setGeneq(TermId.BULKING_BREEDING_METHOD_CLASS.getId());

		final AdvancingSource source =
			new AdvancingSource(new BasicGermplasmDTO(), names, new ObservationUnitRow(), new ObservationUnitRow(),
				method, new Method(), null,
				RandomStringUtils.randomAlphabetic(10), 2);
		source.setRootName("Germplasm");
		return source;
	}

}
