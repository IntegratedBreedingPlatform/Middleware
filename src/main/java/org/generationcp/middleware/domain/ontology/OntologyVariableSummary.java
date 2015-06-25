
package org.generationcp.middleware.domain.ontology;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.oms.VariableType;

public class OntologyVariableSummary {

	private final TermSummary term;

	private TermSummary propertySummary;

	private TermSummary methodSummary;

	private Scale scaleSummary;

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
		return this.term.getId();
	}

	public String getName() {
		return this.term.getName();
	}

	public String getDescription() {
		return this.term.getDefinition();
	}

	public TermSummary getTerm() {
		return this.term;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public TermSummary getPropertySummary() {
		return this.propertySummary;
	}

	public void setPropertySummary(TermSummary property) {
		this.propertySummary = property;
	}

	public TermSummary getMethodSummary() {
		return this.methodSummary;
	}

	public void setMethodSummary(TermSummary methodSummary) {
		this.methodSummary = methodSummary;
	}

	public Scale getScaleSummary() {
		return this.scaleSummary;
	}

	public void setScaleSummary(Scale scaleSummary) {
		this.scaleSummary = scaleSummary;
	}

	public Set<VariableType> getVariableTypes() {
		return this.variableTypes;
	}

	public void addVariableType(VariableType variableType) {
		this.variableTypes.add(variableType);
	}

	public Boolean getIsFavorite() {
		return this.isFavorite;
	}

	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateLastModified() {
		return this.dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	public String getMinValue() {
		return this.minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
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
		if (!(obj instanceof OntologyVariableSummary)) {
			return false;
		}
		OntologyVariableSummary other = (OntologyVariableSummary) obj;
		return this.getId().equals(other.getId());
	}

	@Override
	public String toString() {
		return "OntologyVariableSummary{" + "term=" + this.term
				+ ", propertySummary="
				+ this.propertySummary
				+ ", methodSummary="
				+ this.methodSummary
				+
				// ", scaleSummary=" + scaleSummary +
				// ", dataType=" + dataType +
				", variableTypes=" + this.variableTypes + ", alias='" + this.alias + '\'' + ", isFavorite=" + this.isFavorite
				+ ", dateCreated=" + this.dateCreated + ", dateLastModified=" + this.dateLastModified + ", minValue='" + this.minValue
				+ '\'' + ", maxValue='" + this.maxValue + '\'' + '}';
	}
}
