package org.generationcp.middleware.ruleengine.naming.newexpression;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttributeFemaleParentExpression extends AttributeExpression {

	// Example: ATTRFP.2000
	public static final String ATTRIBUTE_KEY = "ATTRFP";
	public static final String PATTERN_KEY = "\\[" + ATTRIBUTE_KEY + "\\.([^\\.]*)\\]";

	// TODO: refactor. Try to avoid hitting the DB for each line
	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Override
	public <T extends AbstractAdvancingSource> void apply(final List<StringBuilder> values, final T source, final String capturedText) {

		final Method breedingMethod = source.getBreedingMethod();
		Integer gpid1 = null;
		if (breedingMethod.isGenerative()) {
			// If the method is Generative, GPID1 refers to the GID of the female parent
			gpid1 = source.getFemaleGid();
		} else if (breedingMethod.isDerivativeOrMaintenance()) {

			// if the method is Derivative or Maintenance, GPID1 refers to the female parent of the group source
			final Integer groupSourceGID = getGroupSourceGID(source);
			gpid1 = getSourceParentGID(groupSourceGID);

		}

		final Integer variableId = Integer.valueOf(capturedText.substring(1, capturedText.length() - 1).split("\\.")[1]);
		final String attributeValue = germplasmDataManager.getAttributeValue(gpid1, variableId);

		for (final StringBuilder value : values) {
			replaceAttributeExpressionWithValue(value, ATTRIBUTE_KEY, variableId, attributeValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return AttributeFemaleParentExpression.PATTERN_KEY;
	}

	protected Integer getSourceParentGID(final Integer gid) {
		final Germplasm groupSource = this.germplasmDataManager.getGermplasmByGID(gid);
		if (groupSource != null) {
			return groupSource.getGpid1();
		} else {
			return null;
		}
	}
}
