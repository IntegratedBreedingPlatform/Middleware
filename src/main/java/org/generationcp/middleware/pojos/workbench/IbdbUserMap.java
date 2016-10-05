/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for workbench_ibdb_user_map table.
 * 
 */
@Entity
@Table(name = "workbench_ibdb_user_map")
public class IbdbUserMap implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String GET_LOCAL_IBDB_USER_ID =
			"SELECT ibdb_user_id FROM workbench_ibdb_user_map WHERE workbench_user_id = :workbenchUserId AND project_id = :projectId";

	public static final String GET_WORKBENCH_USER_ID =
			"SELECT workbench_user_id FROM workbench_ibdb_user_map WHERE ibdb_user_id = :ibdbUserId AND project_id = :projectId";

	public static final String GET_LOCAL_IBDB_BY_PROJECT = "SELECT ibdb_user_id FROM workbench_ibdb_user_map WHERE project_id = :projectId";

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "ibdb_user_map_id")
	private Long ibdbUserMapId;

	@Basic(optional = false)
	@Column(name = "workbench_user_id")
	private Integer workbenchUserId;

	@Basic(optional = false)
	@Column(name = "project_id")
	private Long projectId;

	@Basic(optional = false)
	@Column(name = "ibdb_user_id")
	private Integer ibdbUserId;

	public Long getIbdbUserMapId() {
		return this.ibdbUserMapId;
	}

	public void setIbdbUserMapId(Long ibdbUserMapId) {
		this.ibdbUserMapId = ibdbUserMapId;
	}

	public Integer getWorkbenchUserId() {
		return this.workbenchUserId;
	}

	public void setWorkbenchUserId(Integer workbenchUserId) {
		this.workbenchUserId = workbenchUserId;
	}

	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getIbdbUserId() {
		return this.ibdbUserId;
	}

	public void setIbdbUserId(Integer ibdbUserId) {
		this.ibdbUserId = ibdbUserId;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.ibdbUserMapId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!IbdbUserMap.class.isInstance(obj)) {
			return false;
		}

		IbdbUserMap otherObj = (IbdbUserMap) obj;

		return new EqualsBuilder().append(this.ibdbUserMapId, otherObj.ibdbUserMapId).isEquals();
	}
}
