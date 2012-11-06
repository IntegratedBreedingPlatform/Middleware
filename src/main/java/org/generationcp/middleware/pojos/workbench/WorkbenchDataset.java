/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
 * The Class WorkbenchDataset. Maps to the table workbench_dataset
 */
@Entity
@Table(name = "workbench_dataset")
public class WorkbenchDataset implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The dataset id. */
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "dataset_id")
    private Integer datasetId;

    /** The dataset name. */
    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    /** The description. */
    @Column(name = "description")
    private String description;

    /** The creation date. */
    @Column(name = "creation_date")
    private Date creationDate;

    /** The project. */
    @ManyToOne(targetEntity = Project.class)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    
    /** The dataset type. */
    @Column(name = "type")
    private DatasetType datasetType;
    
    
    /**
     * Instantiates a new dataset.
     */
    public WorkbenchDataset() {
    }

    /**
     * Instantiates a new dataset.
     *
     * @param datasetId the dataset id
     * @param datasetName the dataset name
     * @param description the description
     * @param creationDate the creation date
     * @param project the project
     * @param datasetType the dataset type
     */
    public WorkbenchDataset(Integer datasetId, String datasetName, String description, Date creationDate, Project project, DatasetType datasetType) {
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
    /**
     * Gets the dataset id.
     *
     * @return the dataset id
     */
    public Integer getDatasetId() {
        return datasetId;
    }
    
    /**
     * Sets the dataset id.
     *
     * @param datasetId the new dataset id
     */
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    /**
     * Gets the dataset name.
     *
     * @return the dataset name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the dataset name.
     *
     * @param datasetName the new dataset name
     */
    public void setName(String datasetName) {
        this.name = datasetName;
    }
    
    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the creation date.
     *
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }
    
    /**
     * Sets the creation date.
     *
     * @param creationDate the new creation date
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    /**
     * Gets the project.
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }
    
    /**
     * Sets the project.
     *
     * @param project the new project
     */
    public void setProject(Project project) {
        this.project = project;
    }
    
    /**
     * Gets the dataset type.
     *
     * @return the dataset type
     */
    public DatasetType getType() {
        return datasetType;
    }

    /**
     * Sets the dataset type.
     *
     * @param datasetType the new dataset type
     */
    public void setType(DatasetType datasetType) {
        this.datasetType = datasetType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(datasetId).hashCode();
    }

    /* (non-Javadoc)
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
        if (!WorkbenchDataset.class.isInstance(obj)) {
            return false;
        }

        WorkbenchDataset otherObj = (WorkbenchDataset) obj;

        return new EqualsBuilder().append(datasetId, otherObj.datasetId).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkbenchDataset [datasetId=");
        builder.append(datasetId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", creationDate=");
        builder.append(creationDate);
        builder.append(", project=");
        builder.append(project);
        builder.append(", datasetType=");
        builder.append(datasetType);
        builder.append("]");
        return builder.toString();
    }
}