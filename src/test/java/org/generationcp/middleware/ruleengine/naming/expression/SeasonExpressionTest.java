
package org.generationcp.middleware.ruleengine.naming.expression;

import junit.framework.Assert;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeasonExpressionTest extends TestExpression {

	public static final Logger LOG = LoggerFactory.getLogger(SeasonExpressionTest.class);

	private static final String DESIGNATION = "GERMPLASM_TEST";

	@Test
	public void testSeasonAsPrefix() throws Exception {
		LOG.debug("Testing Season As Prefix");
		final SeasonExpression expression = new SeasonExpression();
		final AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, "[SEASON]", null, null, true, 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TESTDry", this.buildResult(values));
	}

	@Test
	public void testSeasonAsSuffix() throws Exception {
		LOG.debug("Testing Season As Suffix");
		final SeasonExpression expression = new SeasonExpression();
		final AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, ":", null, null, "[SEASON]", true, 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TEST:Dry", this.buildResult(values));
	}

	@Test
	public void testNoSeason() throws Exception {
		final SimpleDateFormat f = new SimpleDateFormat("YYYYMM");
		final String defSeason = f.format(new Date());
		LOG.debug("Testing No Season");
		final SeasonExpression expression = new SeasonExpression();
		final AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, "-", null, null, "[SEASON]", true, 2);
		source.setSeason(null);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TEST-" + defSeason, this.buildResult(values));
	}

	@Test
	public void testCaseSensitive() throws Exception {
		final SeasonExpression expression = new SeasonExpression();
		final AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, "[seasOn]", null, null, true, 2);
		final List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		LOG.debug("Testing process code is in lower case");
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TESTDry", this.buildResult(values));
	}

}
