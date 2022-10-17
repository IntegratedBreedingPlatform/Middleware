
package org.generationcp.middleware.manager.ontology.daoElements;

import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.VariableType;

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
	private String expectedMin;
	private String expectedMax;
	private Boolean isFavorite;

	public OntologyVariableInfo() {
		this.term = new Term();
	}

	public OntologyVariableInfo(String programUuid, String alias, Integer methodId, Integer propertyId, Integer scaleId, String expectedMin,
			String expectedMax, Boolean isFavorite, Set<VariableType> variableTypes) {
		this();

		this.programUuid = programUuid;
		this.alias = alias;
		this.methodId = methodId;
		this.propertyId = propertyId;
		this.scaleId = scaleId;
		this.expectedMin = expectedMin;
		this.expectedMax = expectedMax;
		this.isFavorite = isFavorite;
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

	public String getExpectedMin() {
		return this.expectedMin;
	}

	public void setExpectedMin(String expectedMin) {
		this.expectedMin = expectedMin;
	}

	public String getExpectedMax() {
		return this.expectedMax;
	}

	public void setExpectedMax(String expectedMax) {
		this.expectedMax = expectedMax;
	}

	public Boolean isFavorite() {
		return this.isFavorite;
	}

	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public Boolean isObsolete() {
		return this.term.isObsolete();
	}

	public void setObsolete(Boolean obsolete) {
		this.term.setObsolete(obsolete);
	}
}
