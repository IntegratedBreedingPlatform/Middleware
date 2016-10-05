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
 * POJO for workbench_runtime_data table.
 * 
 */
@Entity
@Table(name = "workbench_runtime_data")
public class WorkbenchRuntimeData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "id")
	private Integer id;

	@Basic(optional = true)
	@Column(name = "user_id")
	private Integer userId;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer runtimeDataId) {
		this.id = runtimeDataId;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer user) {
		this.userId = user;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!WorkbenchRuntimeData.class.isInstance(obj)) {
			return false;
		}

		WorkbenchRuntimeData otherObj = (WorkbenchRuntimeData) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkbenchRuntimeData [id=");
		builder.append(this.id);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append("]");
		return builder.toString();
	}

}
