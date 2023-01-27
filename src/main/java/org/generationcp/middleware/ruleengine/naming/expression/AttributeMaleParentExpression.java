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
public class AttributeMaleParentExpression extends AttributeExpression {

	// Example: ATTRMP.2000
	public static final String ATTRIBUTE_KEY = "ATTRMP";
	public static final String PATTERN_KEY = "\\[" + ATTRIBUTE_KEY + "\\.([^\\.]*)\\]";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmService germplasmService;

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {

		final Method breedingMethod = advancingSource.getBreedingMethod();
		Integer gpid2 = null;
		if (breedingMethod.isGenerative()) {
			// If the method is Generative, GPID2 refers to male parent of the cross
			gpid2 = advancingSource.getMaleGid();
		} else if (breedingMethod.isDerivativeOrMaintenance()) {

			// If the method is Derivative or Maintenance, GPID2 refers to the male parent of the group source
			final Integer groupSourceGid = this.getGroupSourceGID(advancingSource);
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
		return AttributeMaleParentExpression.PATTERN_KEY;
	}

	protected Integer getSourceParentGID(final Integer gid) {
		final List<BasicGermplasmDTO> groupSource = this.germplasmService.getBasicGermplasmByGids(Collections.singleton(gid));
		if (!CollectionUtils.isEmpty(groupSource)) {
			return groupSource.get(0).getGpid2();
		} else {
			return null;
		}
	}
}
