package org.generationcp.middleware.pojos;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = -3183448477255317893L;

	@Column(name = "created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	//TODO: this field must be non updatable
	@Column(name = "created_by", nullable = false, updatable = true)
	private Integer createdBy;

	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	protected AbstractEntity(final Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	//TODO: createdBy must be immutable. We need to clean up subclasses of AbstractEntity in order to assign the value
	// only in the constructor
	protected void setCreatedBy(final Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}
}
