package org.generationcp.middleware.pojos.dms;

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
 * http://wiki.cimmyt.org/confluence/display/MBP/Business+Rules+for+Mapping+to+Chado
 * 
 * The project_properties table captures links to the ontology. 
 * Properties with the same rank represent a single compound property. 
 * The type column distinguishes the components of the compound property. 
 * Note that these entries are just the names of the properties with their ontology mappings. 
 * Values for properties are stored in the appropriate sub-module, usually in Experiment. 
 * 
 * @author tippsgo
 *
 */
@Entity
@Table(name = "projectprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "type_id", "rank"})})
public class ProjectProperty implements Serializable {

	private static final long serialVersionUID = 7517773605676616639L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "projectprop_id")
	private Long projectPropertyId;
	
	/**
	 * The DMS Project associated with this property.
	 */
	@ManyToOne(targetEntity = DmsProject.class)
	@JoinColumn(name = "project_id", nullable = false)
	private DmsProject project;
	
	/**
	 * The type defined in CV term.
	 */
	@ManyToOne(targetEntity = CVTerm.class)
	@JoinColumn(name = "type_id", nullable = false)
	private CVTerm type;
	
	/**
	 * The value of the property.
	 */
	@Column(name = "`value`")
	private String value;
	
	/**
	 * Used for grouping compound properties.
	 */
	@Column(name = "rank")
	private Integer rank;

	public Long getProjectPropertyId() {
		return projectPropertyId;
	}

	public void setProjectPropertyId(Long id) {
		this.projectPropertyId = id;
	}

	public DmsProject getProject() {
		return project;
	}

	public void setProject(DmsProject project) {
		this.project = project;
	}

	public CVTerm getType() {
		return type;
	}

	public void setType(CVTerm type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((projectPropertyId == null) ? 0 : projectPropertyId
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
		ProjectProperty other = (ProjectProperty) obj;
		if (projectPropertyId == null) {
			if (other.projectPropertyId != null)
				return false;
		} else if (!projectPropertyId.equals(other.projectPropertyId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProjectProperty [projectPropertyId=" + projectPropertyId
				+ ", project=" + project + ", type=" + type + ", value="
				+ value + ", rank=" + rank + "]";
	}
	
}
