package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sample")
public class Sample implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "sample_id")
	private Integer sampleId;

	@Basic(optional = false)
	@Column(name = "sample_name")
	private String sampleName;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "taken_by")
	@NotFound(action = NotFoundAction.IGNORE)
	private User takenBy;

	@Column(name = "sampling_date")
	private Date samplingDate;

	@Basic(optional = false)
	@Column(name = "created_date")
	private Date createdDate;

	@Basic(optional = false)
	@Column(name = "sample_bk")
	private String sampleBusinessKey;

	@JoinColumn(name = "plant_id")
	@OneToOne(targetEntity = Plant.class, cascade = CascadeType.ALL)
	private Plant plant;

	@ManyToOne(targetEntity = SampleList.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "sample_list")
	@NotFound(action = NotFoundAction.IGNORE)
	private SampleList sampleList;

	public Sample() {

	}

	public Sample(Integer sampleId, String sampleName, User takenBy, Date samplingDate, Date createdDate, String sampleBusinessKey,
		Plant plant) {
		this.sampleId = sampleId;
		this.sampleName = sampleName;
		this.takenBy = takenBy;
		this.samplingDate = samplingDate;
		this.createdDate = createdDate;
		this.sampleBusinessKey = sampleBusinessKey;
		this.plant = plant;
	}

	public SampleList getSampleList() {
	  return sampleList;
	}

	public void setSampleList(SampleList sampleList) {
	  this.sampleList = sampleList;
	}

  	public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public User getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(User takenBy) {
		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return samplingDate;
	}

	public void setSamplingDate(Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getSampleBusinessKey() {
		return sampleBusinessKey;
	}

	public void setSampleBusinessKey(String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public Plant getPlant() {
		return plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof Sample)) {
			return false;
		}
		final Sample castOther = (Sample) other;
		return new EqualsBuilder().append(this.sampleId, castOther.sampleId).isEquals();
	}

	@Override public int hashCode() {

		return new HashCodeBuilder().append(this.sampleId).hashCode();
	}

	@Override public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
