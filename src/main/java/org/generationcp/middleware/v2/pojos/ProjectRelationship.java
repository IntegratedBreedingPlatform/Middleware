package org.generationcp.middleware.v2.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	@ManyToOne(targetEntity = DmsProject.class)
	@JoinColumn(name = "subject_project_id", nullable = false, referencedColumnName = "project_id")
	private DmsProject subjectProject;
	
	/**
	 * The Object of the Relationship.
	 */
	@ManyToOne(targetEntity = DmsProject.class)
	@JoinColumn(name = "object_project_id", nullable = false, referencedColumnName = "project_id")
	private DmsProject objectProject;
	
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

	public DmsProject getSubjectProject() {
		return subjectProject;
	}

	public void setSubjectProject(DmsProject subjectProject) {
		this.subjectProject = subjectProject;
	}

	public DmsProject getObjectProject() {
		return objectProject;
	}

	public void setObjectProject(DmsProject objectProject) {
		this.objectProject = objectProject;
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
				+ projectRelationshipId + ", subjectProject=" + subjectProject
				+ ", objectProject=" + objectProject + ", typeId=" + typeId
				+ "]";
	}

}
