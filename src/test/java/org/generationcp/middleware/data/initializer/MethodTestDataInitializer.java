
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Method;

public class MethodTestDataInitializer {

	private static final int METHOD_ID = 1;
	private static final String METHOD_TYPE = "GEN";
	private static final String MCODE = "PSP";

	public MethodTestDataInitializer() {
		// do nothing
	}

	public static Method createMethod() {
		return MethodTestDataInitializer.createMethod(MethodTestDataInitializer.METHOD_ID,
				MethodTestDataInitializer.METHOD_TYPE);
	}

	public static Method createMethod(final int methodId, final String methodType) {
		final Method method = new Method();
		method.setMid(methodId);
		method.setMtype(methodType);
		method.setMcode(MethodTestDataInitializer.MCODE);
		method.setMname("Method Name " + (methodId + 1));
		method.setMdesc("Method Description " + (methodId + 1));
		return method;
	}

	public static List<Method> createMethodList() {
		final List<Method> methodList = new ArrayList<>();
		methodList.add(MethodTestDataInitializer.createMethod());
		return methodList;
	}

	public static List<Method> createMethodList(final int noOfEntries) {
		final List<Method> methodList = new ArrayList<>();
		for (int i = 0; i < noOfEntries; i++) {
			methodList.add(MethodTestDataInitializer.createMethod(i, MethodTestDataInitializer.METHOD_TYPE));
		}
		return methodList;
	}
}
