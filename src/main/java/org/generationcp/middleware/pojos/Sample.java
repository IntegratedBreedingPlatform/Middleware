
package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sample_list")
	private SampleList sampleList;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	@NotFound(action = NotFoundAction.IGNORE)
	private User createdBy;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "sample_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private List<AccMetadataSet> accMetadataSets;

	public Sample() {

	}

	public Sample(final Integer sampleId, final String sampleName, final User takenBy, final Date samplingDate, final Date createdDate,
			final String sampleBusinessKey, final Plant plant) {
		this.sampleId = sampleId;
		this.sampleName = sampleName;
		this.takenBy = takenBy;
		this.samplingDate = samplingDate;
		this.createdDate = createdDate;
		this.sampleBusinessKey = sampleBusinessKey;
		this.plant = plant;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final User createdBy) {
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

	public String getSampleName() {
		return this.sampleName;
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
	}

	public User getTakenBy() {
		return this.takenBy;
	}

	public void setTakenBy(final User takenBy) {
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

	public Plant getPlant() {
		return this.plant;
	}

	public void setPlant(final Plant plant) {
		this.plant = plant;
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
		return accMetadataSets;
	}

	public void setAccMetadataSets(final List<AccMetadataSet> accMetadataSets) {
		this.accMetadataSets = accMetadataSets;
	}
}
