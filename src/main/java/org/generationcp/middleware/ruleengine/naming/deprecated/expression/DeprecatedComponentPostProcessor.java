package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.ruleengine.RulesPostProcessor;
import org.generationcp.middleware.ruleengine.naming.impl.ProcessCodeFactory;
import org.springframework.beans.BeansException;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Daniel Villafuerte on 6/15/2015.
 */
@Deprecated
public class DeprecatedComponentPostProcessor extends RulesPostProcessor{

    private ProcessCodeFactory processCodeFactory;

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        super.postProcessAfterInitialization(o, s);
        if (o instanceof DeprecatedExpression) {
            processCodeFactory.addExpression((DeprecatedExpression) o);
        }

        return o;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return super.postProcessBeforeInitialization(o, s);
    }

    @Autowired
    public void setProcessCodeFactory(ProcessCodeFactory processCodeFactory) {
        this.processCodeFactory = processCodeFactory;
    }
}
