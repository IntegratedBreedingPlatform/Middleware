
package org.generationcp.middleware.ruleengine.newnaming.rules;

import org.generationcp.middleware.ruleengine.newnaming.service.ProcessCodeService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/13/2015 Time: 6:02 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:BaseNamingRuleTest-context.xml", "classpath:testContext.xml"})
public abstract class BaseNamingRuleTest {

	@Resource
	protected ProcessCodeService processCodeService;

	protected AdvancingSource row;

	protected NamingRuleExecutionContext createExecutionContext(final List<String> input) {
		return new NamingRuleExecutionContext(null, this.processCodeService, this.row, null, input);
	}
}
