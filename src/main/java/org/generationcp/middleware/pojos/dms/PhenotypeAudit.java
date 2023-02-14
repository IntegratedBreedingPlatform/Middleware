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

import org.generationcp.middleware.service.impl.audit.RevisionType;
import org.pojomatic.Pojomatic;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "phenotype_aud")
public class PhenotypeAudit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "aud_id")
	private Integer audId;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "rev_type")
	private RevisionType revisionType;

	@ManyToOne
	@JoinColumn(name = "phenotype_id", nullable = false)
	private Phenotype phenotype;

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
	private Phenotype.ValueStatus valueStatus;

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

	@Transient
	private boolean changed = false;

	@Transient
	private Boolean isDerivedTrait;

	public PhenotypeAudit() {
	}

	public PhenotypeAudit(final Phenotype phenotype, final String uniqueName, final String name, final Integer observableId,
		final Integer attributeId, final String value, final Integer cValueId, final Integer assayId, final String draftValue,
		final Integer draftCValueId) {
		this.phenotype = phenotype;
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

	public PhenotypeAudit(final Integer observableId, final String value, final ExperimentModel experiment) {
		this.observableId = observableId;
		this.value = value;
		this.experiment = experiment;
	}

	public Phenotype getPhenotype() {
		return this.phenotype;
	}

	public void setPhenotype(final Phenotype phenotype) {
		this.phenotype = phenotype;
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

	public Phenotype.ValueStatus getValueStatus() {
		return this.valueStatus;
	}

	public void setValueStatus(final Phenotype.ValueStatus valueStatus) {
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

	public RevisionType getRevisionType() {
		return this.revisionType;
	}

	public void setRevisionType(final RevisionType revisionType) {
		this.revisionType = revisionType;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
