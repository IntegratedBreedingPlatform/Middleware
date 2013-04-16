package org.generationcp.middleware.v2.pojos;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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

	@Id
	@Basic(optional = false)
	@GeneratedValue
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
	 * @return
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ProjectProperty> properties;
	
	
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DmsProject other = (DmsProject) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DmsProject [projectId=" + projectId + ", name=" + name
				+ ", description=" + description 
				//+ ", parent=" + (parent != null ? parent.getDmsProjectId() : "null")
				;
	}

}
