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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * POJO for workbench_project_activity table.
 * 
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "workbench_project_activity")
public class ProjectActivity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Used by ProjectActivityDAO.countByProjectId() */
	public static final String COUNT_ACTIVITIES_BY_PROJECT_ID = "SELECT COUNT(*) " + "FROM workbench_project_activity "
			+ "WHERE project_id = :projectId";

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "project_activity_id")
	private Integer projectActivityId;

	@OneToOne(optional = false)
	@JoinColumn(name = "project_id")
	private Project project;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@OneToOne(optional = false)
	@JoinColumn(name = "user_id")
	private WorkbenchUser user;

	@Column(name = "date")
	private Date createdAt;

	public ProjectActivity() {
	}

	public ProjectActivity(final Integer projectActivityId, final Project project, final String name, final String description,
		final WorkbenchUser user, final Date createdAt) {
		super();
		this.projectActivityId = projectActivityId;
		this.project = project;
		this.name = name;
		this.description = description;
		this.user = user;
		this.createdAt = createdAt;
	}

	public Integer getProjectActivityId() {
		return this.projectActivityId;
	}

	public void setProjectActivityId(final Integer projectActivityId) {
		this.projectActivityId = projectActivityId;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(final Project project) {
		this.project = project;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public WorkbenchUser getUser() {
		return this.user;
	}

	public void setUser(final WorkbenchUser user) {
		this.user = user;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(final Date date) {
		this.createdAt = date;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.projectActivityId).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ProjectActivity)) {
			return false;
		}

		final ProjectActivity otherObj = (ProjectActivity) obj;

		return new EqualsBuilder().append(this.projectActivityId, otherObj.projectActivityId).isEquals();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ProjectActivity [projectActivityId=");
		builder.append(this.projectActivityId);
		builder.append(", project=");
		builder.append(this.project);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", user=");
		builder.append(this.user);
		builder.append(", date=");
		builder.append(this.createdAt);
		builder.append("]");
		return builder.toString();
	}

}
