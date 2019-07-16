
package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.enumeration.SampleListType;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sample_list")
public class SampleList implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6160350425863896876L;
	private static final String FOLDER_TYPE = "FOLDER";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "list_id")
	private Integer id;

	@Column(name = "list_name")
	@Basic(optional = false)
	private String listName;

	@Column(name = "description")
	private String description;

	@ManyToOne(targetEntity = SampleList.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "hierarchy")
	@NotFound(action = NotFoundAction.IGNORE)
	private SampleList hierarchy;

	@Basic(optional = false)
	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "notes")
	private String notes;

	@Basic(optional = false)
	@Column(name = "created_by")
	private Integer createdBy;

	@OneToMany(mappedBy = "sampleList", cascade = CascadeType.ALL)
	private List<Sample> samples;

	@OneToMany(mappedBy = "hierarchy", fetch = FetchType.LAZY)
	private List<SampleList> children;

	@Column(name="type")
	@Enumerated(EnumType.STRING)
	private SampleListType type;

	@Column(name = "program_uuid")
	private String programUUID;

	public SampleListType getType() {
		return type;
	}

	public void setType(SampleListType type) {
		this.type = type;
	}

	public List<Sample> getSamples() {
		return this.samples;
	}

	public void setSamples(final List<Sample> samples) {
		this.samples = samples;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getListName() {
		return this.listName;
	}

	public void setListName(final String listName) {
		this.listName = listName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public SampleList getHierarchy() {
		return this.hierarchy;
	}

	public void setHierarchy(final SampleList hierarchy) {
		this.hierarchy = hierarchy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final Integer createdBy) {
		this.createdBy = createdBy;
	}

	public List<SampleList> getChildren() {
		return children;
	}

	public void setChildren(final List<SampleList> children) {
		this.children = children;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof SampleList)) {
			return false;
		}
		final SampleList castOther = (SampleList) other;
		return new EqualsBuilder().append(this.id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public boolean isFolder() {
		return this.getType() != null && this.getType().name().equalsIgnoreCase(SampleList.FOLDER_TYPE) ? true : false;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}
}
