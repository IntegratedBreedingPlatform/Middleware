
package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sample")
public class Sample implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5075457623110650250L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "sample_id")
	private Integer sampleId;

	@Basic(optional = false)
	@Column(name = "entry_no")
	private Integer entryNumber;

	@Basic(optional = false)
	@Column(name = "sample_name")
	private String sampleName;

	@Basic(optional = false)
	@Column(name = "taken_by")
	private Integer takenBy;

	@Column(name = "sampling_date")
	private Date samplingDate;

	@Basic(optional = false)
	@Column(name = "created_date")
	private Date createdDate;

	@Basic(optional = false)
	@Column(name = "sample_bk")
	private String sampleBusinessKey;

	@ManyToOne(targetEntity = ExperimentModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "nd_experiment_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@Basic(optional = false)
	private ExperimentModel experiment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sample_list")
	private SampleList sampleList;

	@Basic(optional = false)
	@Column(name = "created_by")
	private Integer createdBy;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "sample_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private List<AccMetadataSet> accMetadataSets;

	@Basic(optional = false)
	@Column(name = "plate_Id")
	private String plateId;

	@Basic(optional = false)
	@Column(name = "well")
	private String well;

	@Basic(optional = false)
	@Column(name = "sample_no")
	private Integer sampleNumber;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "sample_id")
	private List<SampleExternalReference> externalReferences = new ArrayList<>();

	public Sample() {

	}

	public Sample(final Integer sampleId) {
		this.sampleId = sampleId;
	}

	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final Integer createdBy) {
		this.createdBy = createdBy;
	}

	public SampleList getSampleList() {
		return this.sampleList;
	}

	public void setSampleList(final SampleList sampleList) {
		this.sampleList = sampleList;
	}

	public Integer getSampleId() {
		return this.sampleId;
	}

	public void setSampleId(final Integer sampleId) {
		this.sampleId = sampleId;
	}

	public Integer getEntryNumber() {
		return this.entryNumber;
	}

	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getSampleName() {
		return this.sampleName;
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
	}

	public Integer getTakenBy() {
		return this.takenBy;
	}

	public void setTakenBy(final Integer takenBy) {
		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return this.samplingDate;
	}

	public void setSamplingDate(final Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getSampleBusinessKey() {
		return this.sampleBusinessKey;
	}

	public void setSampleBusinessKey(final String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public String getPlateId() {
		return this.plateId;
	}

	public void setPlateId(final String plateId) {
		this.plateId = plateId;
	}

	public String getWell() {
		return this.well;
	}

	public void setWell(final String well) {
		this.well = well;
	}

	public ExperimentModel getExperiment() {
		return this.experiment;
	}

	public void setExperiment(final ExperimentModel experiment) {
		this.experiment = experiment;
	}

	public Integer getSampleNumber() {
		return this.sampleNumber;
	}

	public void setSampleNumber(final Integer sampleNumber) {
		this.sampleNumber = sampleNumber;
	}

	public List<SampleExternalReference> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<SampleExternalReference> externalReferences) {
		this.externalReferences = externalReferences;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Sample)) {
			return false;
		}
		final Sample castOther = (Sample) other;
		return new EqualsBuilder().append(this.sampleId, castOther.sampleId).isEquals();
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(this.sampleId).hashCode();
	}

	@Override
	public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public List<AccMetadataSet> getAccMetadataSets() {
		return this.accMetadataSets;
	}

	public void setAccMetadataSets(final List<AccMetadataSet> accMetadataSets) {
		this.accMetadataSets = accMetadataSets;
	}
}
