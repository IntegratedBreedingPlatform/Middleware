package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SelectionTraitExpression extends BaseExpression {

    public static final String KEY = "[SELTRAIT]";

    public SelectionTraitExpression() {
    }

    @Override
    public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {
        for (final StringBuilder container : values) {
            this.replaceExpressionWithValue(container, source.getSelectionTraitValue());
        }

    }

    @Override
    public String getExpressionKey() {
        return KEY;
    }
}
