package org.generationcp.middleware.domain.oms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class OntologyVariableSummary {

	private final TermSummary term;

	private TermSummary property;

	private TermSummary method;

	private TermSummary scale;

    private final Set<VariableType> variableTypes = new HashSet<>();

    private Boolean isFavorite;

    private Date dateCreated;

    private Date dateLastModified;

    private Integer observations;

    private String minValue;

    private String maxValue;

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

    public Set<VariableType> getVariableTypes() {
        return variableTypes;
    }

    public void addVariableType(VariableType variableType) {
        this.variableTypes.add(variableType);
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Date dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public Integer getObservations() {
        return observations;
    }

    public void setObservations(Integer observations) {
        this.observations = observations;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
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
