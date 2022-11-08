package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SelectionTraitExpression extends BaseExpression {

    public static final String KEY = "[SELTRAIT]";

    public SelectionTraitExpression() {
    }

    @Override
    public void apply(List<StringBuilder> values, DeprecatedAdvancingSource source, final String capturedText) {
        for (StringBuilder container : values) {
            this.replaceExpressionWithValue(container, source.getSelectionTraitValue());
        }

    }

    @Override
    public String getExpressionKey() {
        return KEY;
    }
}
