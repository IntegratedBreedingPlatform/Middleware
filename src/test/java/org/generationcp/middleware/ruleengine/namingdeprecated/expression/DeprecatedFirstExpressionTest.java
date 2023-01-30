
package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;

import java.util.List;

public class DeprecatedFirstExpressionTest extends DeprecatedTestExpression {

	@Test
	public void testFirst() throws Exception {
		DeprecatedFirstExpression expression = new DeprecatedFirstExpression();
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData("GERMPLASM_TEST-12B:13C-14D:15E", "[FIRST]:", "12C", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testFirst2() throws Exception {
		DeprecatedFirstExpression expression = new DeprecatedFirstExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "[FIRST]:", "12C", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		DeprecatedFirstExpression expression = new DeprecatedFirstExpression();
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData("GERMPLASM_TEST-12B:13C-14D:15E", "[first]:", "12C", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process is in lower case");
		this.printResult(values, source);
	}

}
