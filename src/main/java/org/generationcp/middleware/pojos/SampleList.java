package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sample_list")
public class SampleList implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "list_id")
	private Integer listId;

	@Column(name = "list_name")
	@Basic(optional = false)
	private String listName;

	@Column(name = "description")
	private String description;

	@ManyToOne(targetEntity = SampleList.class)
	@JoinColumn(name = "hierarchy")
	@NotFound(action = NotFoundAction.IGNORE)
	private SampleList hierarchy;

	@Column(name = "list_type")
	@Basic(optional = false)
	private String type;

	@Basic(optional = false)
	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "notes")
	private String notes;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "taken_by")
	@NotFound(action = NotFoundAction.IGNORE)
	private User createdBy;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Sample> samples;

	public List<Sample> getSamples() {
	  return samples;
	}

	public void setSamples(List<Sample> samples) {
	  this.samples = samples;
	}

  public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SampleList getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(SampleList hierarchy) {
		this.hierarchy = hierarchy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof SampleList)) {
			return false;
		}
		final SampleList castOther = (SampleList) other;
		return new EqualsBuilder().append(this.listId, castOther.listId).isEquals();
	}

	@Override public int hashCode() {

		return new HashCodeBuilder().append(this.listId).hashCode();
	}

	@Override public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
