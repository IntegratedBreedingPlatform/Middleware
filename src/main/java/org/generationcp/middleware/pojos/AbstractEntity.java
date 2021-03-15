package org.generationcp.middleware.pojos;

import org.generationcp.middleware.ContextHolder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = -3183448477255317893L;

	@Column(name = "created_date", nullable = false, updatable = false)
	private Date createdDate = new Date();

	@Column(name = "created_by", nullable = false, updatable = false)
	private Integer createdBy;

	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "modified_by")
	private Integer modifiedBy;

	protected AbstractEntity() {
		this.createdBy = ContextHolder.getLoggedInUserId();
	}

	public Date getCreatedDate() {
		return createdDate;
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

	//TODO: this method must be not public
	public void update() {
		this.modifiedBy = ContextHolder.getLoggedInUserId();
		this.modifiedDate = new Date();
	}

}
