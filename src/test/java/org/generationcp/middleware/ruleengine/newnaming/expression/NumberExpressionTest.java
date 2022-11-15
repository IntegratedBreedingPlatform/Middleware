
package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
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

	private NumberExpression expression;

	@Before
	public void setup() {
		this.expression = new NumberExpression();
	}

	@Test
	public void testNumber() {
		final int plantsSelected = 2;
		final AdvancingSource source =
			this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, NUMBER, SUFFIX, true, plantsSelected);

		List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);

		this.expression.apply(values, source, null);
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + plantsSelected + SUFFIX, values.get(0).toString());
	}


	@Test
	public void testNegativeNumber() {
		final AdvancingSource source =
			this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, NUMBER, SUFFIX, true, -2);
		List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);
		this.expression.apply(values, source, null);
		// The NUMBER expression will be replaced with blank string
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testNumberEqualToOne() {
		final AdvancingSource source =
			this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, NUMBER, SUFFIX, true, 1);
		List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);
		this.expression.apply(values, source, null);
		// The NUMBER expression will be replaced with blank string
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + SUFFIX, values.get(0).toString());
	}

	@Test
	public void testCaseSensitive() {
		final int plantsSelected = 3;
		final AdvancingSource source =
			this.createAdvancingSourceTestData(ROOT_NAME, SEPARATOR, PREFIX, NUMBER, SUFFIX, true, plantsSelected);
		List<StringBuilder> values = this.createInitialValues(ROOT_NAME, source);
		this.expression.apply(values, source, null);
		assertEquals(ROOT_NAME + SEPARATOR + PREFIX + plantsSelected + SUFFIX, values.get(0).toString());
	}

}
