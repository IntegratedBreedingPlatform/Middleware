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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for workbench_dataset table.
 * 
 */
@Entity
@Table(name = "workbench_dataset")
public class WorkbenchDataset implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "dataset_id")
	private Long datasetId;

	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "creation_date")
	private Date creationDate;

	@ManyToOne(targetEntity = Project.class)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Column(name = "type")
	private DatasetType datasetType;

	public WorkbenchDataset() {
	}

	public WorkbenchDataset(Long datasetId, String datasetName, String description, Date creationDate, Project project,
			DatasetType datasetType) {
		this.datasetId = datasetId;
		this.name = datasetName;
		this.description = description;
		this.creationDate = creationDate;
		this.project = project;
		this.datasetType = datasetType;
	}

	public WorkbenchDataset(String datasetName, String description, Date creationDate, Project project, DatasetType datasetType) {
		this.name = datasetName;
		this.description = description;
		this.creationDate = creationDate;
		this.project = project;
		this.datasetType = datasetType;
	}

	public Long getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Long datasetId) {
		this.datasetId = datasetId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String datasetName) {
		this.name = datasetName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public DatasetType getType() {
		return this.datasetType;
	}

	public void setType(DatasetType datasetType) {
		this.datasetType = datasetType;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.datasetId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!WorkbenchDataset.class.isInstance(obj)) {
			return false;
		}

		WorkbenchDataset otherObj = (WorkbenchDataset) obj;

		return new EqualsBuilder().append(this.datasetId, otherObj.datasetId).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkbenchDataset [datasetId=");
		builder.append(this.datasetId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", creationDate=");
		builder.append(this.creationDate);
		builder.append(", project=");
		builder.append(this.project);
		builder.append(", datasetType=");
		builder.append(this.datasetType);
		builder.append("]");
		return builder.toString();
	}
}
