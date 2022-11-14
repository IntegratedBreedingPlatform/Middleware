package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Deprecated
@Component
public class DeprecatedAttributeMaleParentExpression extends DeprecatedAttributeExpression {

	// Example: ATTRMP.2000
	public static final String ATTRIBUTE_KEY = "ATTRMP";
	public static final String PATTERN_KEY = "\\[" + ATTRIBUTE_KEY + "\\.([^\\.]*)\\]";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Override
	public void apply(final List<StringBuilder> values, final DeprecatedAdvancingSource source, final String capturedText) {

		final Method breedingMethod = source.getBreedingMethod();
		Integer gpid2 = null;
		if (METHOD_TYPE_GEN.equals(breedingMethod.getMtype())) {
			// If the method is Generative, GPID2 refers to male parent of the cross
			gpid2 = Integer.valueOf(source.getMaleGid());
		} else if (METHOD_TYPE_DER.equals(breedingMethod.getMtype()) || METHOD_TYPE_MAN.equals(breedingMethod.getMtype())) {

			// If the method is Derivative or Maintenance, GPID2 refers to the male parent of the group source
			final Integer groupSourceGid = this.getGroupSourceGID(source);
			gpid2 = this.getSourceParentGID(groupSourceGid);

		}

		final Integer variableId = Integer.valueOf(capturedText.substring(1, capturedText.length() - 1).split("\\.")[1]);
		final String attributeValue = germplasmDataManager.getAttributeValue(gpid2, variableId);

		for (final StringBuilder value : values) {
			this.replaceAttributeExpressionWithValue(value, ATTRIBUTE_KEY, variableId, attributeValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return DeprecatedAttributeMaleParentExpression.PATTERN_KEY;
	}

	protected Integer getSourceParentGID(final Integer gid) {
		final Germplasm groupSource = this.germplasmDataManager.getGermplasmByGID(gid);
		if (groupSource != null) {
			return groupSource.getGpid2();
		} else {
			return null;
		}
	}
}
