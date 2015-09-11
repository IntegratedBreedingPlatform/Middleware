
package org.ibp.test.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ReflectionPojoUtilities {

	private ReflectionPojoUtilities() {
	}

	/**
	 * @param allClassesInThePackage a list of class from which we want to extract pojos
	 * @return {@link List} of pojo's
	 */
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

	/**
	 * @param constructors list of all constructors in a class
	 * @return true if the class has a default constructor.
	 */
	private static boolean doesKlassHaveDefaultConstructor(final Constructor<?>[] constructors) {
		for (final Constructor<?> constructor : constructors) {
			if (constructor.getParameterTypes().length == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param klass Determine is the provided {@link Class} is a pojo
	 * @return true if the class is actually a pojo
	 */
	private static boolean isPojo(final Class<? extends Object> klass) {
		final List<Method> methods = new ArrayList<Method>();
		ReflectionPojoUtilities.getApplicableMethods(methods, klass);

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

	/**
	 * Get all methods in a class
	 *
	 * @param methods this method is recursive. Thus passes the lists of methods to the super class so that it can append to an exiting list
	 * @param klass the class from which we want to retrieve a set of methods.
	 * @return {@link List} all methods in a class including the super class. Note the object class is ignored.
	 */
	private static List<Method> getApplicableMethods(List<Method> methods, final Class<?> klass) {
		methods.addAll(Arrays.asList(klass.getDeclaredMethods()));

		if (klass.getSuperclass() != null && !klass.getSuperclass().equals(Object.class)) {
			methods = ReflectionPojoUtilities.getApplicableMethods(methods, klass.getSuperclass());
		}

		return methods;
	}

	/**
	 * Is it a getter method
	 *
	 * @param method method name
	 * @return true if the method is a getter method
	 */
	private static boolean isGetter(final Method method) {
		// check return type is not void
		method.getReturnType();

		if (method.getName().startsWith("get") && method.getParameterTypes().length == 0 && !method.getReturnType().equals(Void.TYPE)) {
			return true;
		}
		return false;
	}

	/**
	 * Is it a setter method
	 *
	 * @param method method name
	 * @return true if the method is a setter method
	 */
	private static boolean isSetter(final Method method) {
		// check to make sure its not void
		if ((method.getName().startsWith("set") || method.getName().startsWith("add")) && method.getParameterTypes().length == 1
				&& method.getReturnType().equals(Void.TYPE)) {
			return true;
		}
		return false;
	}

	/**
	 * Is it the equals method
	 *
	 * @param method method name
	 * @return true if the method is equals method
	 */
	private static boolean isEqualsHashCodeOrToStringMethod(final Method method) {
		if (method.getName().equals("equals") || method.getName().equals("hashCode") || method.getName().equals("toString")) {
			return true;
		}
		return false;
	}
}
