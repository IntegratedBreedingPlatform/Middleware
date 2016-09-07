
package org.generationcp.middleware.pojos;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.generationcp.middleware.pojos.dms.ExperimentModel;

@Entity
@Table(name = "plant_samples")
public class Sample {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "plot_id", referencedColumnName = "nd_experiment_id")
	private ExperimentModel plot;

	@Column(name = "plant_id")
	private String plantId;

	@Column(name = "sample_id")
	private String sampleId;

	@Column(name = "taken_by")
	private String takenBy;

	@Column(name = "sample_date")
	private String sampleDate;

	@Column(name = "notes")
	private String notes;

	public Sample() {

	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public ExperimentModel getPlot() {
		return this.plot;
	}

	public void setPlot(final ExperimentModel plot) {
		this.plot = plot;
	}

	public String getPlantId() {
		return this.plantId;
	}

	public void setPlantId(final String plantId) {
		this.plantId = plantId;
	}

	public String getSampleId() {
		return this.sampleId;
	}

	public void setSampleId(final String sampleId) {
		this.sampleId = sampleId;
	}

	public String getTakenBy() {
		return this.takenBy;
	}

	public void setTakenBy(final String takenBy) {
		this.takenBy = takenBy;
	}

	public String getSampleDate() {
		return this.sampleDate;
	}

	public void setSampleDate(final String sampleDate) {
		this.sampleDate = sampleDate;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

}
