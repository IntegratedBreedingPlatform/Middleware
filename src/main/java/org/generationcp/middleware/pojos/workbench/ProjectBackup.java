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
import javax.persistence.Table;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for workbench_project_backup table.
 * 
 */
@Entity
@Table(name = "workbench_project_backup")
public class ProjectBackup implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String GET_ALL_DISTINCT_PROJECT_BACKUP =
			"select project_backup_id, project_id, backup_time, backup_path from workbench_project_backup group by project_id";

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "project_backup_id")
	private Long projectBackupId;

	@Basic(optional = false)
	@Column(name = "project_id")
	private Long projectId;

	@Basic(optional = false)
	@Column(name = "backup_time")
	private Date backupTime;

	@Basic(optional = false)
	@Column(name = "backup_path")
	private String backupPath;

	public ProjectBackup(Long projectBackupId2, Long projectId, Date backupTime, String backupPath) {
		this.projectBackupId = projectBackupId2;
		this.projectId = projectId;
		this.backupTime = backupTime;
		this.backupPath = backupPath;
	}

	public ProjectBackup() {
	}

	public Long getProjectBackupId() {
		return this.projectBackupId;
	}

	public void setProjectBackupId(Long projectBackupId) {
		this.projectBackupId = projectBackupId;
	}

	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Date getBackupTime() {
		return this.backupTime;
	}

	public void setBackupTime(Date backupTime) {
		this.backupTime = backupTime;
	}

	public String getBackupPath() {
		return this.backupPath;
	}

	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.projectBackupId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		ProjectBackup other = (ProjectBackup) obj;
		if (this.projectBackupId == null) {
			if (other.projectBackupId != null) {
				return false;
			}
		} else if (!this.projectBackupId.equals(other.projectBackupId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Workbench Project Backup [projectBackupId=");
		builder.append(this.projectBackupId);
		builder.append(", projectId=");
		builder.append(this.projectId);
		builder.append(", backupPath=");
		builder.append(this.backupPath);
		builder.append(", backupTime=");
		builder.append(this.backupTime);

		builder.append("]");
		return builder.toString();
	}
}
