
package org.generationcp.middleware.ruleengine.namingdeprecated.rules;

import org.generationcp.middleware.ruleengine.namingdeprecated.service.DeprecatedProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/13/2015 Time: 6:02 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml", "classpath:DeprecatedBaseNamingRuleTest-context.xml"})
public abstract class DeprecatedBaseNamingRuleTest {

	@Resource
	protected DeprecatedProcessCodeService processCodeService;

	protected DeprecatedAdvancingSource row;

	protected DeprecatedNamingRuleExecutionContext createExecutionContext(List<String> input) {
		return new DeprecatedNamingRuleExecutionContext(null, this.processCodeService, this.row, null, input);
	}
}
