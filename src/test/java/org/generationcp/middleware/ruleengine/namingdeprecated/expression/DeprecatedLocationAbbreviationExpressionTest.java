
package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;

import java.util.List;

public class DeprecatedLocationAbbreviationExpressionTest extends DeprecatedTestExpression {

	@Test
	public void testLabbrAsPrefix() throws Exception {
		DeprecatedLocationAbbreviationExpression expression = new DeprecatedLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[LABBR]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testLabbrAsSuffix() throws Exception {
		DeprecatedLocationAbbreviationExpression expression = new DeprecatedLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", ":", null, null, "[LABBR]", true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testNoLabbr() throws Exception {
		DeprecatedLocationAbbreviationExpression expression = new DeprecatedLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, null, null, "[LABBR]", true);
		source.setLocationAbbreviation(null);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		DeprecatedLocationAbbreviationExpression expression = new DeprecatedLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[labbr]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process code is in lower case");
		this.printResult(values, source);
	}

}
