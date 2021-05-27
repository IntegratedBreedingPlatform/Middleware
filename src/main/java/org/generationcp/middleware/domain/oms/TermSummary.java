
package org.generationcp.middleware.domain.oms;

import org.apache.commons.lang3.StringUtils;

public class TermSummary {

	private final Integer id;

	private final String name;

	private final String definition;

	public TermSummary(final Integer id, final String name, final String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
	}

	public static TermSummary createNonEmpty(final Integer id, final String name, final String definition) {
		if (id == null && StringUtils.isBlank(name) && StringUtils.isBlank(definition)) {
			// Avoid creating an empty TermSummary
			return null;
		}
		return new TermSummary(id, name, definition);
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDefinition() {
		return this.definition;
	}

	@Override
	public int hashCode() {
		return this.getId();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof TermSummary)) {
			return false;
		}

		final TermSummary other = (TermSummary) obj;
		return this.getId().equals(other.getId());
	}

	@Override
	public String toString() {
		return "TermSummary [termId=" + this.id + ", name=" + this.name + ", definition=" + this.definition + "]";
	}

}
