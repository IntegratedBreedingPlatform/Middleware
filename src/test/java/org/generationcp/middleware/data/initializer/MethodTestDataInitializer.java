
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Method;

public class MethodTestDataInitializer {

	public Method createMethod() {
		final Method method = new Method();
		method.setMcode("PSP");
		return method;
	}

	public List<Method> createMethodList() {
		final List<Method> methodList = new ArrayList<>();
		methodList.add(this.createMethod());
		return methodList;
	}
}
