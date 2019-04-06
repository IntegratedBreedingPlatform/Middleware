package org.generationcp.middleware.util.projection;

public abstract class CustomProjections {

	public static ConcatProperties concatProperties(final String separator, final String... properties) {
		return new ConcatProperties(separator, properties);
	}

	public static CoalesceProjection coalesce(final String... properties) {
        return new CoalesceProjection(properties);
	}
}
