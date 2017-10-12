
package org.generationcp.middleware.pojos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.enumeration.SampleListType;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	@NotFound(action = NotFoundAction.IGNORE)
	private User createdBy;

	@OneToMany(mappedBy = "sampleList", cascade = CascadeType.ALL)
	private List<Sample> samples;

	@OneToMany(mappedBy = "hierarchy", fetch = FetchType.LAZY)
	private List<SampleList> children;

	@Column(name="type")
	@Enumerated(EnumType.STRING)
	private SampleListType type;

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

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final User createdBy) {
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
		return this.getType() != null && this.getType().equals(SampleList.FOLDER_TYPE) ? true : false;
	}
}
