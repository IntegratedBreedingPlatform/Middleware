package org.generationcp.middleware.pojos;

import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceInput;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "germplasm_study_source")
@AutoProperty
public class GermplasmStudySource {

	public GermplasmStudySource() {
		// Default constructor
	}

	public GermplasmStudySource(final Germplasm germplasm, final DmsProject study, final ExperimentModel experimentModel,
		final GermplasmStudySourceType germplasmStudySourceType) {
		this.germplasm = germplasm;
		this.study = study;
		this.experimentModel = experimentModel;
		this.germplasmStudySourceType = germplasmStudySourceType;
	}

	public GermplasmStudySource(final GermplasmStudySourceInput input) {
		this.germplasm = new Germplasm(input.getGid());
		this.study = new DmsProject(input.getStudyId());
		if (input.getObservationUnitId() != null) {
			this.experimentModel = new ExperimentModel(input.getObservationUnitId());
		}
		this.germplasmStudySourceType = input.getType();
	}

	@Id
	@TableGenerator(name = "germplasmStudySourceIdGenerator", table = "sequence", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
		pkColumnValue = "germplasm_study_source", allocationSize = 500)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "germplasmStudySourceIdGenerator")
	@Basic(optional = false)
	@Column(name = "germplasm_study_source_id")
	private Integer germplasmStudySourceId;

	@OneToOne
	@JoinColumn(name = "gid", nullable = false)
	private Germplasm germplasm;

	@OneToOne
	@JoinColumn(name = "project_id", nullable = false)
	private DmsProject study;

	@OneToOne
	@JoinColumn(name = "nd_experiment_id", nullable = true)
	private ExperimentModel experimentModel;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private GermplasmStudySourceType germplasmStudySourceType;

	public Integer getGermplasmStudySourceId() {
		return this.germplasmStudySourceId;
	}

	public void setGermplasmStudySourceId(final Integer sourceId) {
		this.germplasmStudySourceId = sourceId;
	}

	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	public void setGermplasm(final Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	public DmsProject getStudy() {
		return this.study;
	}

	public void setStudy(final DmsProject study) {
		this.study = study;
	}

	public ExperimentModel getExperimentModel() {
		return this.experimentModel;
	}

	public void setExperimentModel(final ExperimentModel experimentModel) {
		this.experimentModel = experimentModel;
	}

	public GermplasmStudySourceType getGermplasmStudySourceType() {
		return this.germplasmStudySourceType;
	}

	public void setGermplasmStudySourceType(final GermplasmStudySourceType germplasmStudySourceType) {
		this.germplasmStudySourceType = germplasmStudySourceType;
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

