package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;

@Deprecated
public abstract class DeprecatedAttributeExpression implements DeprecatedExpression {

	protected static String METHOD_TYPE_GEN = "GEN";
	protected static String	METHOD_TYPE_DER = "DER";
	protected static String METHOD_TYPE_MAN= "MAN";

	protected Integer getGroupSourceGID(final DeprecatedAdvancingSource source) {

		final Integer sourceGpid1 = source.getGermplasm().getGpid1();
		final Integer sourceGpid2 = source.getGermplasm().getGpid2();
		final Method sourceMethod = source.getSourceMethod();

		if (sourceMethod != null && sourceMethod.getMtype() != null && METHOD_TYPE_GEN
				.equals(sourceMethod.getMtype()) || source.getGermplasm().getGnpgs() < 0 && (sourceGpid1 != null && sourceGpid1.equals(0))
				&& (sourceGpid2 != null && sourceGpid2.equals(0))) {
			// If the source germplasm is a new CROSS, then the group source is the cross itself
			return Integer.valueOf(source.getGermplasm().getGid());
		} else {
			// Else group source gid is always the female parent of the source germplasm.
			return source.getGermplasm().getGpid1();
		}

	}

	protected void replaceAttributeExpressionWithValue(final StringBuilder container, final String attributeKey, final Integer variableId,
			final String value) {
		final String key = "[" + attributeKey + "." + variableId + "]";
		int start = container.indexOf(key, 0);
		while (start > -1) {
			int end = start + key.length();
			int nextSearchStart = start + value.length();
			container.replace(start, end, value);
			start = container.indexOf(key, nextSearchStart);
		}
	}

}
