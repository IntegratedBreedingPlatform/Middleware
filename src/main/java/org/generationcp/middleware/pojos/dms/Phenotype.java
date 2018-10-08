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
import javax.persistence.TableGenerator;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_phenotype
 *
 * A phenotypic statement, or a single atomic phenotypic observation, is a controlled sentence describing observable effects of non-wild
 * type function. E.g. Obs=eye, attribute=color, cvalue=red.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "phenotype")
public class Phenotype implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ValueStatus {

		OUT_OF_SYNC ("OUT_OF_SYNC"),
		MANUALLY_EDITED ("MANUALLY_EDITED");

		ValueStatus(final String name) {
			this.name = name;
		}

		private String name;

		public String getName() {
			return this.name;
		}

		public void setName(final String description) {
			this.name = this.name;
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

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_date")
	private Date updatedDate;

	public Phenotype() {
	}

	public Phenotype(final Integer phenotypeId, final String uniqueName, final String name, final Integer observableId,
		final Integer attributeId, final String value, final Integer cValueId, final Integer assayId) {
		this.phenotypeId = phenotypeId;
		this.uniqueName = uniqueName;
		this.name = name;
		this.observableId = observableId;
		this.attributeId = attributeId;
		this.value = value;
		this.cValueId = cValueId;
		this.assayId = assayId;
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

	public void setUniqueName(final String uniqueName) {
		this.uniqueName = uniqueName;
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

	public void setAttributeId(final Integer attributeId) {
		this.attributeId = attributeId;
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

	public void setAssayId(final Integer assayId) {
		this.assayId = assayId;
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

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Phenotype))
			return false;
		final Phenotype phenotype = (Phenotype) o;
		return Objects.equals(this.getPhenotypeId(), phenotype.getPhenotypeId()) && Objects.equals(this.getUniqueName(), phenotype.getUniqueName())
			&& Objects.equals(this.getName(), phenotype.getName()) && Objects.equals(this.getObservableId(), phenotype.getObservableId()) && Objects
			.equals(this.getAttributeId(), phenotype.getAttributeId()) && Objects.equals(this.getValue(), phenotype.getValue()) && Objects
			.equals(this.getcValueId(), phenotype.getcValueId()) && Objects.equals(this.getAssayId(), phenotype.getAssayId()) && Objects
			.equals(this.getExperiment(), phenotype.getExperiment());
	}

	@Override
	public int hashCode() {

		return Objects.hash(
			this.getPhenotypeId(), this.getUniqueName(), this.getName(), this.getObservableId(), this.getAttributeId(), this.getValue(), this.getcValueId(),
			this.getValueStatus(), this.getAssayId(), this.getExperiment());
	}

	@Override
	public String toString() {
		return "Phenotype{" +
			"phenotypeId=" + phenotypeId +
			", uniqueName='" + uniqueName + '\'' +
			", name='" + name + '\'' +
			", observableId=" + observableId +
			", attributeId=" + attributeId +
			", value='" + value + '\'' +
			", cValueId=" + cValueId +
			", valueStatus=" + valueStatus +
			", assayId=" + assayId +
			", experiment=" + experiment +
			", createdDate=" + createdDate +
			", updatedDate=" + updatedDate +
			'}';
	}
}
