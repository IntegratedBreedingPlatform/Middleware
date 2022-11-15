
package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;

import java.util.List;

public class FirstExpressionTest extends TestExpression {

	@Test
	public void testFirst() throws Exception {
		FirstExpression expression = new FirstExpression();
		AdvancingSource
			source = this.createAdvancingSourceTestData("GERMPLASM_TEST-12B:13C-14D:15E", "[FIRST]:", "12C", null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST-12B:13C-14D:15E", source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testFirst2() throws Exception {
		FirstExpression expression = new FirstExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "[FIRST]:", "12C", null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST", source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		FirstExpression expression = new FirstExpression();
		AdvancingSource
			source = this.createAdvancingSourceTestData("GERMPLASM_TEST-12B:13C-14D:15E", "[first]:", "12C", null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST-12B:13C-14D:15E", source);
		expression.apply(values, source, null);
		System.out.println("process is in lower case");
		this.printResult(values, source);
	}

}
