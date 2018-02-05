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
import java.util.Date;

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

/**
 * POJO for workbench_project_user_info table.
 * 
 * @author Aldrin Batac
 * 
 */
@Entity
@Table(name = "workbench_project_user_info")
public class ProjectUserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "user_info_id")
	private Integer userInfoId;

    @OneToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

	@Column(name = "user_id")
	private Integer userId;

	@Basic(optional = true)
	@Column(name = "last_open_date")
	private Date lastOpenDate;

	public ProjectUserInfo() {
	}

	public ProjectUserInfo(final Project project, Integer userId) {
		this.setProject(project);
		this.setUserId(userId);
	}

	public ProjectUserInfo(final Project project, Integer userId, Date lastOpenDate) {
		this.setProject(project);
		this.setUserId(userId);
		this.setLastOpenDate(lastOpenDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getUserInfoId()).hashCode();
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
		if (!ProjectUserInfo.class.isInstance(obj)) {
			return false;
		}

		ProjectUserInfo otherObj = (ProjectUserInfo) obj;

		return new EqualsBuilder().append(this.getUserInfoId(), otherObj.getUserInfoId()).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectUserInfo [projectUserId=");
		builder.append(this.getUserInfoId());
		builder.append(", project=");
		builder.append(this.getProject());
		builder.append(", userId=");
		builder.append(this.getUserId());
		builder.append(", lastOpenDate=");
		builder.append(this.getLastOpenDate());
		builder.append("]");
		return builder.toString();
	}

	public Integer getUserInfoId() {
		return this.userInfoId;
	}

	public void setUserInfoId(Integer userInfoId) {
		this.userInfoId = userInfoId;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(final Project project) {
		this.project = project;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getLastOpenDate() {
		return this.lastOpenDate;
	}

	public void setLastOpenDate(Date lastOpenDate) {
		this.lastOpenDate = lastOpenDate;
	}

}
