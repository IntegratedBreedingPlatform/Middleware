package org.generationcp.middleware.ruleengine;

/**
 * Created by Daniel Villafuerte on 6/6/2015.
 */
public abstract class ProcessCodeOrderedRule<T extends OrderedRuleExecutionContext> extends OrderedRule<T> {
    public abstract String getProcessCode();
}
