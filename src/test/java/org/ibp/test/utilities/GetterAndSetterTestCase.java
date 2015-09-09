
package org.ibp.test.utilities;

import java.lang.reflect.Method;

import org.junit.Assert;

import junit.framework.TestCase;

public class GetterAndSetterTestCase extends TestCase {

	private final Object expected;
	private final Object actual;

	public GetterAndSetterTestCase(final Object expected, final Object actual) {
		this.expected = expected;
		this.actual = actual;
		this.setName(actual.getClass().getName());
	}

	@Override
	protected void runTest() throws Throwable {
		@SuppressWarnings("rawtypes")
		final Class klass = this.expected.getClass();

		// Just to help with code coverage. Not a meaningful test
		final Method toStringMethod = this.getMethodIfItExists(klass, "toString");
		if (toStringMethod != null) {
			toStringMethod.invoke(this.actual);
		}

		final Method equalsMethod = this.getMethodIfItExists(klass, "equals", Object.class);

		if (equalsMethod != null) {
			Assert.assertTrue(String.format("The testing of class %s resulted in unequal values. Please make sure your equals method is corret.", klass.getName()),
					(Boolean) equalsMethod.invoke(this.expected, this.actual));
			Assert.assertEquals(String.format("The testing of class %s resulted in unequal hash values. Please  make sure hash method is corret.", klass.getName()),
					this.expected.hashCode(), this.actual.hashCode());
		}
	}

	private Method getMethodIfItExists(final Class<?> klass, final String name, final Class<?>... parameterTypes) {
		try {
			return klass.getDeclaredMethod(name, parameterTypes);
		} catch (final Exception e) {
			// This is only there because if the method does not exist we want to carry on.
			// Do not need to log anything as this is not an erroneous case
		}
		// return null if the method does not exist
		return null;
	}

}
