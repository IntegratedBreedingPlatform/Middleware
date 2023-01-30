
package org.generationcp.middleware.ruleengine;

import org.generationcp.middleware.ruleengine.provider.RuleConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RuleFactory {

	private static Logger LOG = LoggerFactory.getLogger(RuleFactory.class);

	private Map<RuleExecutionNamespace, Map<String, Rule>> availableRules;

	private Map<String, String[]> ruleOrder;

	@Resource
	private RuleConfigurationProvider configProvider;

	public RuleFactory() {
		this.availableRules = new HashMap<>();
		this.ruleOrder = new HashMap<>();
	}

	public void init() {
		this.ruleOrder = this.configProvider.getRuleSequenceConfiguration();
	}

	public void setAvailableRules(final Map<RuleExecutionNamespace, Map<String, Rule>> availableRulesMap) {
		this.availableRules = availableRulesMap;
	}

	public void addRule(final Rule rule) {
		this.availableRules.putIfAbsent(rule.getRuleExecutionNamespace(), new HashMap<>());
		final Map<String, Rule> rules = this.availableRules.get(rule.getRuleExecutionNamespace());
		rules.put(rule.getKey(), rule);
	}

	public Rule getRule(final RuleExecutionNamespace ruleExecutionNamespace, final String key) {
		if (ruleExecutionNamespace == null || key == null) {
			return null;
		}

		final Map<String, Rule> rules = this.availableRules.get(ruleExecutionNamespace);
		if (CollectionUtils.isEmpty(rules)) {
			return null;
		}

		return rules.get(key);
	}

	public int getAvailableRuleCount() {
		return this.availableRules.size();
	}

	public String[] getRuleSequenceForNamespace(final RuleExecutionNamespace namespace) {
		return this.ruleOrder.get(namespace.getName());
	}

	public Collection<String> getAvailableConfiguredNamespaces() {
		return this.ruleOrder.keySet();
	}

}
