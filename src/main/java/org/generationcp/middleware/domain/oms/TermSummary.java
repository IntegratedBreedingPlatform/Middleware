package org.generationcp.middleware.domain.oms;

import org.apache.commons.lang3.StringUtils;

public class TermSummary {

	private final Integer id;
	
	private final String name;

	private final String definition;

	public TermSummary(Integer id, String name, String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
	}

    public static TermSummary createNonEmpty(Integer id, String name, String definition){
        if(id == null && StringUtils.isBlank(name) && StringUtils.isBlank(definition)) {
            // Avoid creating an empty TermSummary
            return null;
        }
        return new TermSummary(id, name, definition);
    }

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDefinition() {
		return definition;
	}

	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof TermSummary)) {
			return false;
		}
		
		TermSummary other = (TermSummary) obj;
		return getId().equals(other.getId());
	}
	
	@Override
	public String toString() {
		return "TermSummary [termId=" + id + ", name=" + name
				+ ", definition=" + definition + "]";
	}
	
}

