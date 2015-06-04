
package org.generationcp.middleware.domain.oms;

import java.util.HashSet;
import java.util.Set;

/**
 * This class consist of variable association ids and basic values.
 *
 */
public class OntologyVariableInfo {

	private Term term;
	private String programUuid;
	private String alias;
	private final Set<VariableType> variableTypes = new HashSet<>();
	private Integer methodId;
	private Integer propertyId;
	private Integer scaleId;
	private String minValue;
	private String maxValue;
	private boolean isFavorite;

	public OntologyVariableInfo() {
		this.term = new Term();
	}

	public Term getTerm() {
		return this.term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public String getProgramUuid() {
		return this.programUuid;
	}

	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
	}

	public int getId() {
		return this.term.getId();
	}

	public void setId(int id) {
		this.term.setId(id);
	}

	public String getName() {
		return this.term.getName();
	}

	public void setName(String name) {
		this.term.setName(name);
	}

	public String getDescription() {
		return this.term.getDefinition();
	}

	public void setDescription(String description) {
		this.term.setDefinition(description);
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Set<VariableType> getVariableTypes() {
		return this.variableTypes;
	}

	public void addVariableType(VariableType variableType) {
		this.variableTypes.add(variableType);
	}

	public Integer getMethodId() {
		return this.methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

	public Integer getPropertyId() {
		return this.propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
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

	public boolean isFavorite() {
		return this.isFavorite;
	}

	public void setIsFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
}
