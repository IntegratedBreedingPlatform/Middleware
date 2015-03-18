package org.generationcp.middleware.domain.oms;

public class OntologyVariableSummary {

	private final TermSummary term;

	private TermSummary property;

	private TermSummary method;

	private TermSummary scale;

	public OntologyVariableSummary(Integer id, String name, String description) {
		this.term = new TermSummary(id, name, description);
	}
	
	public Integer getId() {
    	return term.getId();
    }

	public String getName() {
		return term.getName();
	}
	
	public String getDescription() {
		return term.getDefinition();
	}
	
	public TermSummary getTerm() {
		return term;
	}

	public TermSummary getProperty() {
		return property;
	}

	public void setProperty(TermSummary property) {
		this.property = property;
	}

	public TermSummary getMethod() {
		return method;
	}

	public void setMethod(TermSummary method) {
		this.method = method;
	}

	public TermSummary getScale() {
		return scale;
	}

	public void setScale(TermSummary scale) {
		this.scale = scale;
	}

	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
            return false;
        }
		if (!(obj instanceof OntologyVariableSummary)) {
            return false;
        }
		OntologyVariableSummary other = (OntologyVariableSummary) obj;
		return getId().equals(other.getId());
	}

    @Override
    public String toString() {
        return "OntologyVariableSummary{" +
                "term=" + term +
                ", property=" + property +
                ", method=" + method +
                ", scale=" + scale +
                '}';
    }
}
