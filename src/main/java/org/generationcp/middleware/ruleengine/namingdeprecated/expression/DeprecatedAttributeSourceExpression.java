package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Deprecated
@Component
public class DeprecatedAttributeSourceExpression extends DeprecatedAttributeExpression {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	public static final String ATTRIBUTE_KEY = "ATTRSC";
	public static final String PATTERN_KEY = "\\[" + ATTRIBUTE_KEY + "\\.([^\\.]*)\\]"; // Example: ATTRSC.1010

	@Override
	public void apply(final List<StringBuilder> values, final DeprecatedAdvancingSource source, final String capturedText) {
		for (final StringBuilder value : values) {
			String newValue = "";
			final Integer variableId = Integer.valueOf(capturedText.substring(1, capturedText.length() - 1).split("\\.")[1]);
			if (METHOD_TYPE_DER.equals(source.getBreedingMethod().getMtype())
				|| METHOD_TYPE_MAN.equals(source.getBreedingMethod().getMtype())) {
				newValue = germplasmDataManager.getAttributeValue(Integer.parseInt(source.getGermplasm().getGid()), variableId);
			}
			this.replaceAttributeExpressionWithValue(value, ATTRIBUTE_KEY, variableId, newValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return DeprecatedAttributeSourceExpression.PATTERN_KEY;
	}

}
