package org.generationcp.middleware.pojos.derived_variables;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * To compute derived traits or variables
 */
@Entity
@Table(name = "formula")
public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "formula_id")
	private Integer formulaId;

	@ManyToOne(targetEntity = CVTerm.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "target_variable_id")
	private CVTerm targetCVTerm;

	@OneToMany
	@JoinTable(
		name = "formula_input",
		joinColumns = @JoinColumn(name = "formula_id"),
		inverseJoinColumns = @JoinColumn(name = "variable_id"))
	private List<CVTerm> inputs = new ArrayList<>();

	@Basic(optional = false)
	@Column(name = "definition")
	private String definition;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "active", columnDefinition = "TINYINT")
	private Boolean active;

	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Basic(optional = false)
	@Column(name = "description")
	private String description;

	public Integer getFormulaId() {
		return formulaId;
	}

	public void setFormulaId(final Integer formulaId) {
		this.formulaId = formulaId;
	}

	public CVTerm getTargetCVTerm() {
		return targetCVTerm;
	}

	public void setTargetCVTerm(final CVTerm targetCVTerm) {
		this.targetCVTerm = targetCVTerm;
	}

	public List<CVTerm> getInputs() {
		return this.inputs;
	}

	public void setInputs(final List<CVTerm> inputs) {
		this.inputs = inputs;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Formula formula = (Formula) o;

		return new EqualsBuilder()
			.append(formulaId, formula.formulaId)
			.append(targetCVTerm, formula.targetCVTerm)
			.append(definition, formula.definition)
			.append(active, formula.active)
			.append(name, formula.name)
			.append(description, formula.description)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(formulaId)
			.append(targetCVTerm)
			.append(definition)
			.append(active)
			.append(name)
			.append(description)
			.toHashCode();
	}
}
