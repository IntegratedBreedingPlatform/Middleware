
package org.generationcp.middleware.utils.test;

import java.util.Random;

public class OntologyDataCreationUtil {

	private OntologyDataCreationUtil() {
	}

	public static String getNewRandomName() {
		return "Name_" + new Random().nextInt(10000);
	}
}
