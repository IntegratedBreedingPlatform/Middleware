package org.generationcp.middleware.util.projection;

public class CustomProjections {

	private CustomProjections() {
	}

	public static ConcatProperties concatProperties(final String separator, final String... property) {
		return new ConcatProperties(separator, property);
	}

}
