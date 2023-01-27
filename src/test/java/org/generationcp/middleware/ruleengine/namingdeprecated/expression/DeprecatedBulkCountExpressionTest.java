
package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;

import java.util.List;

public class DeprecatedBulkCountExpressionTest extends DeprecatedTestExpression {

	@Test
	public void testNonBulkingSource() throws Exception {
		DeprecatedBulkCountExpression expression = new DeprecatedBulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAt1() throws Exception {
		DeprecatedBulkCountExpression expression = new DeprecatedBulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAt2() throws Exception {
		DeprecatedBulkCountExpression expression = new DeprecatedBulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-2B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAtInvalidNumber() throws Exception {
		DeprecatedBulkCountExpression expression = new DeprecatedBulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-11a22B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAtMultipleAdvanced() throws Exception {
		DeprecatedBulkCountExpression expression = new DeprecatedBulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B-4B-3B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		DeprecatedBulkCountExpression expression = new DeprecatedBulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B-4B-3B", "[bcount]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process code in lower case");
		this.printResult(values, source);
	}
}
