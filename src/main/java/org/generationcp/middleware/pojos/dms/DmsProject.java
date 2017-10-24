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

package org.generationcp.middleware.pojos.dms;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_project
 *
 * A Study is captured using the Project table. Information stored at this level describes properties relevant for all field trials in a
 * Project (Study). Since it is important both that local breeders are free to use their nomenclature and that these local terms are mapped
 * to a central ontology, the properties table maps all terms to the Ontology at the project level.
 *
 * @author tippsgo
 *
 */
@Entity()
@Table(name = "project", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="project")
public class DmsProject implements Serializable {

	private static final long serialVersionUID = 464731947805951726L;

	/** The project id of the SYSTEM root folder */
	public static final Integer SYSTEM_FOLDER_ID = 1;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
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

	@Column(name = "program_uuid")
	private String programUUID;

	/**
	 * List of Project Properties
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@BatchSize(size = 1000)
	private List<ProjectProperty> properties;

	@OneToMany(mappedBy = "subjectProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ProjectRelationship> relatedTos;

	@OneToMany(mappedBy = "objectProject", fetch = FetchType.LAZY)
	private List<ProjectRelationship> relatedBys;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "deleted", columnDefinition = "TINYINT")
	private boolean deleted;

	public DmsProject() {
	}

	public DmsProject(Integer projectId, String name, String description, List<ProjectProperty> properties,
			List<ProjectRelationship> relatedTos, List<ProjectRelationship> relatedBys) {
		super();
		this.projectId = projectId;
		this.name = name;
		this.description = description;
		this.properties = properties;
		this.relatedTos = relatedTos;
		this.relatedBys = relatedBys;
	}

	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}

	public List<ProjectProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(List<ProjectProperty> properties) {
		this.properties = properties;
	}

	public List<ProjectRelationship> getRelatedTos() {
		return this.relatedTos;
	}

	public void setRelatedTos(List<ProjectRelationship> relatedTos) {
		this.relatedTos = relatedTos;
	}

	public List<ProjectRelationship> getRelatedBys() {
		return this.relatedBys;
	}

	public void setRelatedBys(List<ProjectRelationship> relatedBys) {
		this.relatedBys = relatedBys;
	}

	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(final Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.projectId == null ? 0 : this.projectId.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		DmsProject other = (DmsProject) obj;
		if (this.projectId == null) {
			if (other.projectId != null) {
				return false;
			}
		} else if (!this.projectId.equals(other.projectId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getEntityName() + " [projectId=");
		builder.append(this.projectId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", deleted=");
		builder.append(this.deleted);
		builder.append("]");
		return builder.toString();
	}

	public String getEntityName() {
		return "DmsProject";
	}

	public void addProperty(ProjectProperty property) {
		if (this.properties == null) {
			this.properties = new ArrayList<>();
		}
		this.properties.add(property);
	}
}
