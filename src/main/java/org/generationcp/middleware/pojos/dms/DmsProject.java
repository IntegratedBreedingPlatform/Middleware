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
package org.generationcp.middleware.pojos.dms;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_project
 * 
 * A Study is captured using the Project table. 
 * Information stored at this level describes properties relevant for all field trials in a Project (Study). 
 * Since it is important both that local breeders are free to use their nomenclature and that these local terms are mapped to a central ontology, 
 * the properties table maps all terms to the Ontology at the project level.
 * 
 * @author tippsgo
 *
 */
@Entity()
@Table(name="project", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class DmsProject implements Serializable {

	private static final long serialVersionUID = 464731947805951726L;
	
	/** The project id of the SYSTEM root folder */
	public static final Integer SYSTEM_FOLDER_ID = 1;
	
	@Id
	@Basic(optional = false)
	@Column(name = "project_id")
	private Integer projectId;
	
	/**
	 * The name of the project.
	 */
	@Basic(optional = false)
	@Column(name = "name")
	private String name;
	
	/**
	 * The description of the project.
	 */
	@Basic(optional = false)
	@Column(name = "description")
	private String description;
	
	/**
	 * List of Project Properties
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<ProjectProperty> properties;
	
	@OneToMany(mappedBy = "subjectProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ProjectRelationship> relatedTos;
	
	@OneToMany(mappedBy = "objectProject", fetch = FetchType.LAZY)
	private List<ProjectRelationship> relatedBys;
	
	public DmsProject() {
	}

	public DmsProject(Integer projectId, String name, String description,
			List<ProjectProperty> properties,
			List<ProjectRelationship> relatedTos,
			List<ProjectRelationship> relatedBys) {
		super();
		this.projectId = projectId;
		this.name = name;
		this.description = description;
		this.properties = properties;
		this.relatedTos = relatedTos;
		this.relatedBys = relatedBys;
	}
	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ProjectProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ProjectProperty> properties) {
		this.properties = properties;
	}

	public List<ProjectRelationship> getRelatedTos() {
		return relatedTos;
	}

	public void setRelatedTos(List<ProjectRelationship> relatedTos) {
		this.relatedTos = relatedTos;
	}

	public List<ProjectRelationship> getRelatedBys() {
		return relatedBys;
	}

	public void setRelatedBys(List<ProjectRelationship> relatedBys) {
		this.relatedBys = relatedBys;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (getClass() != obj.getClass()) {
            return false;
        }
		DmsProject other = (DmsProject) obj;
		if (projectId == null) {
			if (other.projectId != null) {
                return false;
            }
		} else if (!projectId.equals(other.projectId)) {
            return false;
        }
		return true;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getEntityName() + " [projectId=");
		builder.append(projectId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

	public String getEntityName(){
		return "DmsProject";
	}

	public void addProperty(ProjectProperty property) {
		if (this.properties == null) {
			this.properties = new ArrayList<ProjectProperty>();
		}
		this.properties.add(property);
	}
}
