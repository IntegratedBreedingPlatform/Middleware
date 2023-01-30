package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
public class GroupCountExpressionTest extends TestExpression {

	GroupCountExpression dut = new GroupCountExpression();

    @Test
    public void verifyTestScenarios() {
        testCountExpression("CML451 / ABC1234-B*3-B-B", "B*[COUNT]", "CML451 / ABC1234-B*3-B-B*2");
        testCountExpression("CML451 / ABC1234-B*5-B*2", "B*[COUNT]", "CML451 / ABC1234-B*5-B*3");
        testCountExpression("CML451 / ABC1234-B*3-4-B", "B*[COUNT]", "CML451 / ABC1234-B*3-4-B*2");
        testCountExpression("TEST-BBB", "B*[COUNT]", "TEST-BBB*2");
        testCountExpression("TEST-B-B-B", "B*[COUNT]", "TEST-B-B-B*2");
        testCountExpression("CML451 / ABC1234", "B*[COUNT]", "CML451 / ABC1234-B");
        testCountExpression("TEST-B-2-B-3-B", "B*[COUNT]", "TEST-B-2-B-3-B*2");
        testCountExpression("TEST-1-B-2-B*3-B", "B*[COUNT]", "TEST-1-B-2-B*3-B*2");
        testCountExpression("TEST-B-1-B*4-B*3", "B*[COUNT]", "TEST-B-1-B*4-B*4");
        testCountExpression("TEST-B-B*3-B", "B*[COUNT]", "TEST-B-B*3-B*2");
        testCountExpression("TEST-B-1B-2B-B", "B*[COUNT]", "TEST-B-1B-2B-B*2");
        testCountExpression("TEST-B-1B-2BBB", "B*[COUNT]", "TEST-B-1B-2BBB*2");
        testCountExpression("TEST-B-1B-2B", "B*[COUNT]", "TEST-B-1B-2B*2");


    }

    protected void testCountExpression(final String sourceName, final String countExpression, final String expectedValue) {
        final AdvancingSource source = this.createAdvancingSourceTestData(sourceName, "-", null, null, null, true, 2);
        final List<StringBuilder> values = new ArrayList<>();
        values.add(new StringBuilder(source.getRootName() + countExpression));

        this.dut.apply(values, source, null);
        final String value = values.get(0).toString();

        Assert.assertEquals(expectedValue, value);
    }
}
