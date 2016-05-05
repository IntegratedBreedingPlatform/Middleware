
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

	public Method createMethod() {
		return this.createMethod(METHOD_ID, METHOD_TYPE);
	}

	public Method createMethod(final int methodId, final String methodType) {
		final Method method = new Method();
		method.setMid(methodId);
		method.setMtype(methodType);
		method.setMcode(MCODE);
		return method;
	}

	public List<Method> createMethodList() {
		final List<Method> methodList = new ArrayList<>();
		methodList.add(this.createMethod());
		return methodList;
	}
}
