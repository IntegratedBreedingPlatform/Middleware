
package org.generationcp.middleware.ruleengine;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Spring utility class that allows Rule objects managed by the Spring IoC container to be automatically registered into the RuleFactory
 * object
 */
public class RulesPostProcessor implements BeanPostProcessor {

	private RuleFactory ruleFactory;

	@Override
	public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
		// do nothing
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(Object o, String s) throws BeansException {

		if (o instanceof Rule) {
			Rule rule = (Rule) o;
			this.ruleFactory.addRule(rule);
		}

		return o;
	}

	public void setRuleFactory(RuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}
}
