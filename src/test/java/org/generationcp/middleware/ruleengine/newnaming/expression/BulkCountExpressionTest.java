
package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;

import java.util.List;

public class BulkCountExpressionTest extends TestExpression {

	@Test
	public void testNonBulkingSource() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "[BCOUNT]", null, null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST", source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAt1() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B", "[BCOUNT]", null, null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST-B", source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAt2() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-2B", "[BCOUNT]", null, null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST-2B", source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAtInvalidNumber() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-11a22B", "[BCOUNT]", null, null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST-11a22B", source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testBulkSourceAtMultipleAdvanced() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B-4B-3B", "[BCOUNT]", null, null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST-B-4B-3B", source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		BulkCountExpression expression = new BulkCountExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST-B-4B-3B", "[bcount]", null, null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues("GERMPLASM_TEST-B-4B-3B", source);
		expression.apply(values, source, null);
		System.out.println("process code in lower case");
		this.printResult(values, source);
	}
}
