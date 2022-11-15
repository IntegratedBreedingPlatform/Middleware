
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NumberExpressionTest extends TestExpression {

	private static final String ROOT_NAME = "GERMPLASM_TEST";
	private static final String SEPARATOR = "-";
	private static final String PREFIX = "IBX";
	private static final String SUFFIX = "RS";
	private static final String NUMBER = "[NUMBER]";

	private DeprecatedAdvancingSource source;
	private NumberExpression expression;

	@Before
	public void setup() {
		this.expression = new NumberExpression();
		this.source = this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, NUMBER, SUFFIX, true);
	}

	@Test
	public void testNumber() {
		final int plantsSelected = 2;
		this.source.setPlantsSelected(plantsSelected);
		List<StringBuilder> values = this.createInitialValues(this.source);

		this.expression.apply(values, this.source, null);
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + plantsSelected + SUFFIX, values.get(0).toString());
	}


	@Test
	public void testNegativeNumber() {
		this.source.setPlantsSelected(-2);
		List<StringBuilder> values = this.createInitialValues(this.source);
		this.expression.apply(values, this.source, null);
		// The NUMBER expression will be replaced with blank string
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testNumberEqualToOne() {
		this.source.setPlantsSelected(1);
		List<StringBuilder> values = this.createInitialValues(this.source);
		this.expression.apply(values, this.source, null);
		// The NUMBER expression will be replaced with blank string
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testCaseSensitive() {
		final int plantsSelected = 3;
		this.source.setPlantsSelected(plantsSelected);
		List<StringBuilder> values = this.createInitialValues(this.source);
		this.expression.apply(values, this.source, null);
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + plantsSelected + SUFFIX, values.get(0).toString());
	}

}
