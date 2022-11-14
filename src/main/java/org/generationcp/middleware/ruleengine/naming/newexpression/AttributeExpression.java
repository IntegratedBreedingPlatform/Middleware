package org.generationcp.middleware.ruleengine.naming.newexpression;

import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;

public abstract class AttributeExpression implements Expression {

	protected <T extends AbstractAdvancingSource> Integer getGroupSourceGID(final T source) {

		final Integer sourceGpid1 = source.getOriginGermplasmGpid1();
		final Integer sourceGpid2 = source.getOriginGermplasmGpid2();
		final String sourceMethodType = (source.getSourceMethod() == null) ? null : source.getSourceMethod().getMtype();

		if (MethodType.isGenerative(sourceMethodType) || source.getOriginGermplasmGnpgs() < 0 && (sourceGpid1 != null && sourceGpid1
			.equals(0))
			&& (sourceGpid2 != null && sourceGpid2.equals(0))) {
			// If the source germplasm is a new CROSS, then the group source is the cross itself
			return source.getOriginGermplasmGid();
		} else {
			// Else group source gid is always the female parent of the source germplasm.
			return source.getOriginGermplasmGpid1();
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
