package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.ruleengine.RuleFactory;
import org.generationcp.middleware.ruleengine.naming.impl.ProcessCodeFactory;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
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
public class DeprecatedComponentPostProcessorTest {

    @Mock
    private RuleFactory ruleFactory;

    @Mock
    private ProcessCodeFactory processCodeFactory;

    @InjectMocks
    private DeprecatedComponentPostProcessor dut;

    @Test
    public void testProcessCodeAdd() {
        DeprecatedExpression testExpression = new DeprecatedExpression() {
            @Override
            public void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText) {
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
