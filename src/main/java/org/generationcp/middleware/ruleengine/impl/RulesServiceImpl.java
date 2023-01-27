
package org.generationcp.middleware.ruleengine.impl;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.generationcp.middleware.ruleengine.Rule;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.RuleExecutionContext;
import org.generationcp.middleware.ruleengine.RuleFactory;
import org.generationcp.middleware.ruleengine.service.RulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

public class RulesServiceImpl implements RulesService {

	@Resource
	private RuleFactory ruleFactory;
	
	public static final Logger LOG = LoggerFactory.getLogger(RulesServiceImpl.class);

	public RulesServiceImpl() {
	}

	// FIXME : catch RuleExceptions here?
	@Override
	public Object runRules(final RuleExecutionContext context) throws RuleException {

		final Monitor monitor = MonitorFactory.start(this.getClass().getName() + ".runRules");
		
		try{

  		final List<String> sequenceOrder = context.getExecutionOrder();
  
  		assert !sequenceOrder.isEmpty();
  		Rule rule = this.ruleFactory.getRule(context.getRuleExecutionNamespace(), sequenceOrder.get(0));
  
  		while (rule != null) {
  			rule.runRule(context);
  			rule = this.ruleFactory.getRule(context.getRuleExecutionNamespace(), rule.getNextRuleStepKey(context));
  		}
		} finally {
		  monitor.stop();
		}

		return context.getRuleExecutionOutput();

	}

	public void setRuleFactory(final RuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}
}
