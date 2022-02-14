package org.generationcp.middleware.pojos;

public class MethodHelper {

	public static Boolean isBulkingMethod(final Integer geneq) {
		if (geneq != null) {
			if (Method.BULKED_CLASSES.contains(geneq)) {
				return true;
			} else if (Method.NON_BULKED_CLASSES.contains(geneq)) {
				return false;
			}
		}
		return null;
	}

}
