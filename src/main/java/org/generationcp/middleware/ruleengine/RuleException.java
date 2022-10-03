
package org.generationcp.middleware.ruleengine;

import java.util.Locale;

public class RuleException extends Exception {

	private static final long serialVersionUID = 5937934311090989339L;

	private Object[] objects;

	private Locale locale;

	public RuleException(final String message, final Object[] objects, final Locale locale) {
		super(message);
		this.objects = objects;
		this.locale = locale;
	}

	public RuleException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public RuleException(final String message) {
		super(message);
	}

	public Object[] getObjects() {
		return this.objects;
	}

	public Locale getLocale() {
		return this.locale;
	}

}
