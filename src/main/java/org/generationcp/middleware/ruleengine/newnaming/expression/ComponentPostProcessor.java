package org.generationcp.middleware.ruleengine.newnaming.expression;

import org.generationcp.middleware.ruleengine.RulesPostProcessor;
import org.generationcp.middleware.ruleengine.newnaming.impl.ProcessCodeFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Daniel Villafuerte on 6/15/2015.
 */
public class ComponentPostProcessor extends RulesPostProcessor{

    private ProcessCodeFactory processCodeFactory;

    @Override
    public Object postProcessAfterInitialization(final Object o, final String s) throws BeansException {
        super.postProcessAfterInitialization(o, s);
        if (o instanceof Expression) {
            processCodeFactory.addExpression((Expression) o);
        }

        return o;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object o, final String s) throws BeansException {
        return super.postProcessBeforeInitialization(o, s);
    }

    @Autowired
    public void setProcessCodeFactory(final ProcessCodeFactory processCodeFactory) {
        this.processCodeFactory = processCodeFactory;
    }
}
