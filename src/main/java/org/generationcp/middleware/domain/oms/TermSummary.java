
package org.generationcp.middleware.domain.oms;

public class TermSummary {

	private final Integer id;

	private final String name;

	private final String definition;

	public TermSummary(Integer id, String name, String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
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
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof TermSummary)) {
			return false;
		}

		TermSummary other = (TermSummary) obj;
		return this.getId().equals(other.getId());
	}

	@Override
	public String toString() {
		return "TermSummary [termId=" + this.id + ", name=" + this.name + ", definition=" + this.definition + "]";
	}

}
