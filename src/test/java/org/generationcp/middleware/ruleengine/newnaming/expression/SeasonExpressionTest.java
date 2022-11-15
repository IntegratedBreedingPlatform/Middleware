
package org.generationcp.middleware.ruleengine.naming.expression;

import junit.framework.Assert;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeasonExpressionTest extends TestExpression {

	public static final Logger LOG = LoggerFactory.getLogger(SeasonExpressionTest.class);

	@Test
	public void testSeasonAsPrefix() throws Exception {
		LOG.debug("Testing Season As Prefix");
		SeasonExpression expression = new SeasonExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[SEASON]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TESTDry", this.buildResult(values));
	}

	@Test
	public void testSeasonAsSuffix() throws Exception {
		LOG.debug("Testing Season As Suffix");
		SeasonExpression expression = new SeasonExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", ":", null, null, "[SEASON]", true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TEST:Dry", this.buildResult(values));
	}

	@Test
	public void testNoSeason() throws Exception {
		SimpleDateFormat f = new SimpleDateFormat("YYYYMM");
		String defSeason = f.format(new Date());
		LOG.debug("Testing No Season");
		SeasonExpression expression = new SeasonExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "-", null, null, "[SEASON]", true);
		source.setSeason(null);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TEST-" + defSeason, this.buildResult(values));
	}

	@Test
	public void testCaseSensitive() throws Exception {
		SeasonExpression expression = new SeasonExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[seasOn]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		LOG.debug("Testing process code is in lower case");
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TESTDry", this.buildResult(values));
	}

}
