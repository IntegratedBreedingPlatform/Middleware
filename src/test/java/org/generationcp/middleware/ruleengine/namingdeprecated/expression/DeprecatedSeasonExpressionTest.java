
package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import junit.framework.Assert;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DeprecatedSeasonExpressionTest extends DeprecatedTestExpression {

	public static final Logger LOG = LoggerFactory.getLogger(DeprecatedSeasonExpressionTest.class);

	@Test
	public void testSeasonAsPrefix() throws Exception {
		LOG.debug("Testing Season As Prefix");
		DeprecatedSeasonExpression expression = new DeprecatedSeasonExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[SEASON]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TESTDry", this.buildResult(values));
	}

	@Test
	public void testSeasonAsSuffix() throws Exception {
		LOG.debug("Testing Season As Suffix");
		DeprecatedSeasonExpression expression = new DeprecatedSeasonExpression();
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
		DeprecatedSeasonExpression expression = new DeprecatedSeasonExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "-", null, null, "[SEASON]", true);
		source.setSeason(null);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TEST-" + defSeason, this.buildResult(values));
	}

	@Test
	public void testCaseSensitive() throws Exception {
		DeprecatedSeasonExpression expression = new DeprecatedSeasonExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[seasOn]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		LOG.debug("Testing process code is in lower case");
		this.printResult(values, source);
		Assert.assertEquals("GERMPLASM_TESTDry", this.buildResult(values));
	}

}
