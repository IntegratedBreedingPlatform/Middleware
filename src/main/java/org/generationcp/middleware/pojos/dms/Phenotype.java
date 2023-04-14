/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.dms;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.generationcp.middleware.pojos.PhenotypeExternalReference;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_phenotype
 * <p>
 * A phenotypic statement, or a single atomic phenotypic observation, is a controlled sentence describing observable effects of non-wild
 * type function. E.g. Obs=eye, attribute=color, cvalue=red.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "phenotype")
public class Phenotype implements Serializable {

	public static final String MISSING = "missing";
	private static final long serialVersionUID = 1L;
	public static final String MISSING_VALUE = "missing";


	public enum ValueStatus {

		OUT_OF_SYNC("OUT_OF_SYNC"),
		MANUALLY_EDITED("MANUALLY_EDITED");

		ValueStatus(final String name) {
			this.name = name;
		}

		private final String name;

		public String getName() {
			return this.name;
		}

	}


	@Id
	@TableGenerator(name = "phenotypeIdGenerator", table = "sequence", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "phenotype", allocationSize = 500)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "phenotypeIdGenerator")
	@Basic(optional = false)
	@Column(name = "phenotype_id")
	private Integer phenotypeId;

	@Column(name = "uniquename")
	private String uniqueName;

	@Column(name = "name")
	private String name;

	// References cvterm
	@Column(name = "observable_id")
	private Integer observableId;

	// References cvterm
	@Column(name = "attr_id")
	private Integer attributeId;

	@Column(name = "value")
	private String value;

	// References cvterm
	@Column(name = "cvalue_id")
	private Integer cValueId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private ValueStatus valueStatus;

	// References cvterm
	@Column(name = "assay_id")
	private Integer assayId;

	@ManyToOne
	@JoinColumn(name = "nd_experiment_id", nullable = false)
	private ExperimentModel experiment;

	@Column(name = "created_date", updatable = false, insertable = false)
	private Date createdDate;

	@Column(name = "updated_date", updatable = false, insertable = false)
	private Date updatedDate;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "draft_value")
	private String draftValue;

	// References cvterm
	@Column(name = "draft_cvalue_id")
	private Integer draftCValueId;

	@Column(name = "json_props")
	private String jsonProps;

	@OneToMany(mappedBy = "phenotype", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PhenotypeExternalReference> externalReferences = new ArrayList<>();

	@Transient
	private boolean changed = false;

	@Transient
	private Boolean isDerivedTrait;

	public Phenotype() {
	}

	public Phenotype(final Integer phenotypeId, final String uniqueName, final String name, final Integer observableId,
		final Integer attributeId, final String value, final Integer cValueId, final Integer assayId, final String draftValue,
		final Integer draftCValueId) {
		this.phenotypeId = phenotypeId;
		this.uniqueName = uniqueName;
		this.name = name;
		this.observableId = observableId;
		this.attributeId = attributeId;
		this.value = value;
		this.cValueId = cValueId;
		this.assayId = assayId;
		this.draftCValueId = draftCValueId;
		this.draftValue = draftValue;
	}

	public Phenotype(final Integer observableId, final String value, final ExperimentModel experiment) {
		this.observableId = observableId;
		this.value = value;
		this.experiment = experiment;
	}

	public Integer getPhenotypeId() {
		return this.phenotypeId;
	}

	public void setPhenotypeId(final Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	public String getUniqueName() {
		return this.uniqueName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getObservableId() {
		return this.observableId;
	}

	public void setObservableId(final Integer observableId) {
		this.observableId = observableId;
	}

	public Integer getAttributeId() {
		return this.attributeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getcValueId() {
		return this.cValueId;
	}

	public void setcValue(final Integer cValueId) {
		this.cValueId = cValueId;
	}

	public Integer getAssayId() {
		return this.assayId;
	}

	public ExperimentModel getExperiment() {
		return this.experiment;
	}

	public void setExperiment(final ExperimentModel experiment) {
		this.experiment = experiment;
	}

	public ValueStatus getValueStatus() {
		return this.valueStatus;
	}

	public void setValueStatus(final ValueStatus valueStatus) {
		this.valueStatus = valueStatus;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(final Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public boolean isChanged() {
		return this.changed;
	}

	public void setChanged(final boolean changed) {
		this.changed = changed;
	}

	public String getDraftValue() {
		return this.draftValue;
	}

	public void setDraftValue(final String draftValue) {
		this.draftValue = draftValue;
	}

	public Integer getDraftCValueId() {
		return this.draftCValueId;
	}

	public void setDraftCValueId(final Integer draftCValueId) {
		this.draftCValueId = draftCValueId;
	}

	public Boolean isDerivedTrait() {
		return this.isDerivedTrait;
	}

	public void setDerivedTrait(final Boolean derivedTrait) {
		this.isDerivedTrait = derivedTrait;
	}

	public List<PhenotypeExternalReference> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<PhenotypeExternalReference> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(final Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getJsonProps() {
		return this.jsonProps;
	}

	public void setJsonProps(final String jsonProps) {
		this.jsonProps = jsonProps;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Phenotype))
			return false;
		final Phenotype phenotype = (Phenotype) o;
		return Objects.equals(this.getPhenotypeId(), phenotype.getPhenotypeId())
			&& Objects.equals(this.getUniqueName(), phenotype.getUniqueName())
			&& Objects.equals(this.getName(), phenotype.getName())
			&& Objects.equals(this.getObservableId(), phenotype.getObservableId())
			&& Objects.equals(this.getAttributeId(), phenotype.getAttributeId())
			&& Objects.equals(this.getValue(), phenotype.getValue())
			&& Objects.equals(this.getcValueId(), phenotype.getcValueId())
			&& Objects.equals(this.getAssayId(), phenotype.getAssayId())
			&& Objects.equals(this.getExperiment(), phenotype.getExperiment())
			&& Objects.equals(this.getDraftValue(), phenotype.getDraftValue())
			&& Objects.equals(this.getDraftCValueId(), phenotype.getDraftCValueId());
	}

	@Override
	public int hashCode() {

		return Objects.hash(
			this.getPhenotypeId(),
			this.getUniqueName(),
			this.getName(),
			this.getObservableId(),
			this.getAttributeId(),
			this.getValue(),
			this.getcValueId(),
			this.getValueStatus(),
			this.getAssayId(),
			this.getExperiment(),
			this.getDraftValue(),
			this.getDraftCValueId());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("phenotypeId", this.phenotypeId)
			.append("uniqueName", this.uniqueName)
			.append("name", this.name)
			.append("observableId", this.observableId)
			.append("attributeId", this.attributeId)
			.append("value", this.value)
			.append("cValueId", this.cValueId)
			.append("valueStatus", this.valueStatus)
			.append("assayId", this.assayId)
			.append("experiment", this.experiment)
			.append("createdDate", this.createdDate)
			.append("updatedDate", this.updatedDate)
			.append("draftValue", this.draftValue)
			.append("draftCValueId", this.draftCValueId)
			.append("changed", this.changed)
			.append("isDerivedTrait", this.isDerivedTrait)
			.append("createdBy", this.createdBy)
			.append("updatedBy", this.updatedBy)
			.toString();
	}
}
