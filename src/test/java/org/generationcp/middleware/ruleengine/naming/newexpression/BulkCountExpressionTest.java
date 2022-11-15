
package org.generationcp.middleware.ruleengine.naming.newexpression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;

import java.util.List;

public class BulkCountExpressionTest extends TestExpression {

	@Test
	public void testNonBulkingSource() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAt1() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAt2() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-2B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAtInvalidNumber() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-11a22B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAtMultipleAdvanced() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B-4B-3B", "[BCOUNT]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B-4B-3B", "[bcount]", null, null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process code in lower case");
		this.printResult(values, source);
	}
}
