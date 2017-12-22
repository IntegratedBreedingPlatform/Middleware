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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.User;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * POJO for workbench_project_user_role table.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "workbench_project_user_role")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="workbench_project_user_role")
public class ProjectUserRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "project_user_id")
	private Integer projectUserId;

	/** The project. */
	@OneToOne(optional = false)
	@JoinColumn(name = "project_id")
	private Project project;

	/** The user. */
	@Column(name = "user_id")
	private Integer userId;

	public ProjectUserRole() {
	}

	public ProjectUserRole(Integer projectUserId, Project project, Integer userId) {
		this.projectUserId = projectUserId;
		this.project = project;
		this.userId = userId;
	}

	public ProjectUserRole(Project project, User user) {
		this.project = project;
		this.userId = user.getUserid();
	}

	public Integer getProjectUserId() {
		return this.projectUserId;
	}

	public void setProjectUserId(Integer projectUserId) {
		this.projectUserId = projectUserId;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.projectUserId).hashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!ProjectUserRole.class.isInstance(obj)) {
			return false;
		}

		ProjectUserRole otherObj = (ProjectUserRole) obj;

		return new EqualsBuilder().append(this.projectUserId, otherObj.projectUserId).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectUserRole [projectUserId=");
		builder.append(this.projectUserId);
		builder.append(", project=");
		builder.append(this.project);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append("]");
		return builder.toString();
	}

}
