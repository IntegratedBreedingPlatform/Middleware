package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;

public abstract class AttributeExpression implements Expression {

	protected Integer getGroupSourceGID(final AdvancingSource advancingSource) {

		final Integer sourceGpid1 = advancingSource.getOriginGermplasm().getGpid1();
		final Integer sourceGpid2 = advancingSource.getOriginGermplasm().getGpid2();
		final String sourceMethodType = (advancingSource.getSourceMethod() == null) ? null : advancingSource.getSourceMethod().getMtype();

		if (MethodType.isGenerative(sourceMethodType) || advancingSource.getOriginGermplasm().getGnpgs() < 0 && (sourceGpid1 != null && sourceGpid1
			.equals(0))
			&& (sourceGpid2 != null && sourceGpid2.equals(0))) {
			// If the source germplasm is a new CROSS, then the group source is the cross itself
			return advancingSource.getOriginGermplasm().getGid();
		} else {
			// Else group source gid is always the female parent of the source germplasm.
			return advancingSource.getOriginGermplasm().getGpid1();
		}

	}

	protected void replaceAttributeExpressionWithValue(final StringBuilder container, final String attributeKey, final Integer variableId,
		final String value) {
		final String key = "[" + attributeKey + "." + variableId + "]";
		int start = container.indexOf(key, 0);
		while (start > -1) {
			final int end = start + key.length();
			final int nextSearchStart = start + value.length();
			container.replace(start, end, value);
			start = container.indexOf(key, nextSearchStart);
		}
	}

}
