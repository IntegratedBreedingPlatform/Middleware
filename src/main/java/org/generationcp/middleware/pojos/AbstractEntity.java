package org.generationcp.middleware.pojos;

import org.generationcp.middleware.ContextHolder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is used to audit fields. Take a look at {@link org.generationcp.middleware.hibernate.listener.CustomPreUpdateEventListener}
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	public static final String MODIFIED_DATE_FIELD_NAME = "modifiedDate";
	public static final String MODIFIED_BY_FIELD_NAME = "modifiedBy";

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

}
