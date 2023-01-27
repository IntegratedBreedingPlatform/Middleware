package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.RuleFactory;
import org.generationcp.middleware.ruleengine.naming.impl.ProcessCodeFactory;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * Created by Daniel Villafuerte on 6/16/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ComponentPostProcessorTest {

    @Mock
    private RuleFactory ruleFactory;

    @Mock
    private ProcessCodeFactory processCodeFactory;

    @InjectMocks
    private ComponentPostProcessor dut;

    @Test
    public void testProcessCodeAdd() {
        Expression testExpression = new Expression() {
            @Override
            public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
                // do nothing
            }

            @Override
            public String getExpressionKey() {
                return "TEST";
            }
        };

        dut.postProcessAfterInitialization(testExpression, testExpression.getExpressionKey());
        verify(processCodeFactory).addExpression(testExpression);
    }
}
