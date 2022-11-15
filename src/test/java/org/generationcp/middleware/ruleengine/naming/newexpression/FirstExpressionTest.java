
package org.generationcp.middleware.ruleengine.naming.newexpression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;

import java.util.List;

public class FirstExpressionTest extends TestExpression {

	@Test
	public void testFirst() throws Exception {
		FirstExpression expression = new FirstExpression();
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData("GERMPLASM_TEST-12B:13C-14D:15E", "[FIRST]:", "12C", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testFirst2() throws Exception {
		FirstExpression expression = new FirstExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "[FIRST]:", "12C", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		FirstExpression expression = new FirstExpression();
		DeprecatedAdvancingSource
			source = this.createAdvancingSourceTestData("GERMPLASM_TEST-12B:13C-14D:15E", "[first]:", "12C", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process is in lower case");
		this.printResult(values, source);
	}

}
