package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Deprecated
@Component
public class DeprecatedSelectionTraitExpression extends DeprecatedBaseExpression {

    public static final String KEY = "[SELTRAIT]";

    public DeprecatedSelectionTraitExpression() {
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
