package org.generationcp.middleware.domain.oms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class OntologyVariableSummary {

	private final TermSummary term;

	private TermSummary propertySummary;

	private TermSummary methodSummary;

	private TermSummary scaleSummary;

    private final Set<VariableType> variableTypes = new HashSet<>();

    private String alias;

    private Boolean isFavorite;

    private Date dateCreated;

    private Date dateLastModified;

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

	public TermSummary getPropertySummary() {
		return propertySummary;
	}

	public void setPropertySummary(TermSummary property) {
		this.propertySummary = property;
	}

	public TermSummary getMethodSummary() {
		return methodSummary;
	}

	public void setMethodSummary(TermSummary methodSummary) {
		this.methodSummary = methodSummary;
	}

	public TermSummary getScaleSummary() {
		return scaleSummary;
	}

	public void setScaleSummary(TermSummary scaleSummary) {
		this.scaleSummary = scaleSummary;
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
}
