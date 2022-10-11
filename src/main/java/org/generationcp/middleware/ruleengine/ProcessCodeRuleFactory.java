package org.generationcp.middleware.ruleengine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Villafuerte on 6/6/2015.
 */
public class ProcessCodeRuleFactory extends RuleFactory{

    private Map<String, ProcessCodeOrderedRule> rulesByProcessCode;

    public ProcessCodeRuleFactory() {
        rulesByProcessCode = new HashMap<>();
    }

    @Override
    public void addRule(Rule rule) {
        if (rule instanceof ProcessCodeOrderedRule) {
            ProcessCodeOrderedRule processCodeRule = (ProcessCodeOrderedRule) rule;
            rulesByProcessCode.put(processCodeRule.getProcessCode(), processCodeRule);
        }

        super.addRule(rule);
    }

    public ProcessCodeOrderedRule getRuleByProcessCode(String processCode) {
        return rulesByProcessCode.get(processCode);
    }
}
