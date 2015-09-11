
package org.ibp.test.utilities;

import junit.framework.Test;

public class MiddlewareGetterAndSetterTestSuite {

	public static Test suite() {
		final TestGetterAndSetter testGetterAndSetter = new TestGetterAndSetter();
		return testGetterAndSetter.getTestSuite("MiddlewareGetterAndSetterTest",
				"org.generationcp.middleware");
	}
}
