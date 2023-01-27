package org.generationcp.middleware.ruleengine.naming.expression;

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AttributeFemaleParentExpression extends AttributeExpression {

	// Example: ATTRFP.2000
	public static final String ATTRIBUTE_KEY = "ATTRFP";
	public static final String PATTERN_KEY = "\\[" + ATTRIBUTE_KEY + "\\.([^\\.]*)\\]";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmService germplasmService;

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {

		final Method breedingMethod = advancingSource.getBreedingMethod();
		Integer gpid1 = null;
		if (breedingMethod.isGenerative()) {
			// If the method is Generative, GPID1 refers to the GID of the female parent
			gpid1 = advancingSource.getFemaleGid();
		} else if (breedingMethod.isDerivativeOrMaintenance()) {

			// if the method is Derivative or Maintenance, GPID1 refers to the female parent of the group source
			final Integer groupSourceGID = getGroupSourceGID(advancingSource);
			gpid1 = this.getSourceParentGID(groupSourceGID);
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
		final List<BasicGermplasmDTO> groupSource = this.germplasmService.getBasicGermplasmByGids(Collections.singleton(gid));
		if (!CollectionUtils.isEmpty(groupSource)) {
			return groupSource.get(0).getGpid1();
		} else {
			return null;
		}
	}

}
