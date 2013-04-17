package org.generationcp.middleware.v2.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables
 * 
 * 
 * @author tippsgo
 *
 */
@Entity()
@Table(name="project_relationship", 
		uniqueConstraints = {@UniqueConstraint(columnNames = {"subject_project_id", "object_project_id", "type_id"})})
public class ProjectRelationship implements Serializable {

	private static final long serialVersionUID = -5199851718622429971L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "project_relationship_id")
	private Integer projectRelationshipId;
	
	/**
	 * The Subject of the Relationship.
	 */
	@Basic(optional = false)
	@Column(name = "subject_project_id")
	private Integer subjectProjectId;
	
	/**
	 * The Object of the Relationship.
	 */
	@Basic(optional = false)
	@Column(name = "object_project_id")
	private Integer objectProjectId;
	
	/**
	 * The Type of Relationship.
	 */
	@Basic(optional = false)
	@Column(name = "type_id")
	private Integer typeId;

	public Integer getProjectRelationshipId() {
		return projectRelationshipId;
	}

	public void setProjectRelationshipId(Integer projectRelationshipId) {
		this.projectRelationshipId = projectRelationshipId;
	}

	public Integer getSubjectProjectId() {
		return subjectProjectId;
	}

	public void setSubjectProjectId(Integer subjectProjectId) {
		this.subjectProjectId = subjectProjectId;
	}

	public Integer getObjectProjectId() {
		return objectProjectId;
	}

	public void setObjectProjectId(Integer objectProjectId) {
		this.objectProjectId = objectProjectId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((projectRelationshipId == null) ? 0 : projectRelationshipId
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectRelationship other = (ProjectRelationship) obj;
		if (projectRelationshipId == null) {
			if (other.projectRelationshipId != null)
				return false;
		} else if (!projectRelationshipId.equals(other.projectRelationshipId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProjectRelationship [projectRelationshipId="
				+ projectRelationshipId + ", subjectProjectId="
				+ subjectProjectId + ", objectProjectId=" + objectProjectId
				+ ", typeId=" + typeId + "]";
	}

}
