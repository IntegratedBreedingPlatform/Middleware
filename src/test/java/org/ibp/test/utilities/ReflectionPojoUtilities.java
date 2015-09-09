
package org.ibp.test.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReflectionPojoUtilities {

	private ReflectionPojoUtilities() {
	}

	static List<Class<? extends Object>> getAllPojos(final Set<Class<? extends Object>> allClassesInThePackage) {
		final List<Class<? extends Object>> allPojos = new ArrayList<>();
		for (final Class<? extends Object> klass : allClassesInThePackage) {
			if (klass.isInterface()) {
				continue;
			}

			if (ReflectionPojoUtilities.isPojo(klass)) {

				if (!ReflectionPojoUtilities.doesKlassHaveDefaultConstructor(klass.getConstructors())) {
					System.out.println(String.format("Warning no defualt constructor for pojo '%s'", klass.getName()));
					continue;
				}
				allPojos.add(klass);
			}
		}
		return allPojos;
	}

	private static boolean doesKlassHaveDefaultConstructor(final Constructor<?>[] constructors) {
		for (final Constructor<?> constructor : constructors) {
			if (constructor.getParameterTypes().length == 0) {
				return true;
			}
		}
		return false;
	}

	private static boolean isPojo(final Class<? extends Object> klass) {
		final Method[] methods = klass.getDeclaredMethods();

		for (final Method method : methods) {

			if (method.getName().equals("$jacocoInit")) {
				continue;
			}

			if (!(ReflectionPojoUtilities.isGetter(method) || ReflectionPojoUtilities.isSetter(method) || ReflectionPojoUtilities
					.isEqualsHashCodeOrToStringMethod(method))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isGetter(final Method method) {
		// check return type is not void
		method.getReturnType();

		if (method.getName().startsWith("get") && method.getParameterTypes().length == 0 && !method.getReturnType().equals(Void.TYPE)) {
			return true;
		}
		return false;
	}

	private static boolean isSetter(final Method method) {
		// check to make sure its not void
		if ((method.getName().startsWith("set") || method.getName().startsWith("add")) && method.getParameterTypes().length == 1
				&& method.getReturnType().equals(Void.TYPE)) {
			return true;
		}
		return false;
	}

	private static boolean isEqualsHashCodeOrToStringMethod(final Method method) {

		if (method.getName().equals("equals") || method.getName().equals("hashCode") || method.getName().equals("toString")) {
			return true;
		}
		return false;
	}
}
