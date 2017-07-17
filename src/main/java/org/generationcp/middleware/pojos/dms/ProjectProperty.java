
package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * http://wiki.cimmyt.org/confluence/display/MBP/Business+Rules+for+Mapping+to+Chado
 *
 * The project_properties table captures links to the ontology. Properties with the same rank represent a single compound property. The type
 * column distinguishes the components of the compound property. Note that these entries are just the names of the properties with their
 * ontology mappings. Values for properties are stored in the appropriate sub-module, usually in Experiment.
 *
 * @author tippsgo
 *
 */
@Entity
@Table(name = "projectprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "type_id", "rank"})})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="projectprop")
public class ProjectProperty implements Serializable {

	private static final long serialVersionUID = 7517773605676616639L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "projectprop_id")
	private Integer projectPropertyId;

	/**
	 * The DMS Project associated with this property.
	 */
	@ManyToOne(targetEntity = DmsProject.class)
	@JoinColumn(name = "project_id", nullable = false)
	private DmsProject project;

	/**
	 * The type defined in CV term.
	 */
	@Column(name = "type_id")
	private Integer typeId;

	/**
	 * The value of the property.
	 */
	@Column(name = "value")
	private String value;

	/**
	 * Used for grouping compound properties.
	 */
	@Column(name = "rank")
	private Integer rank;

	@Column(name = "variable_id")
	private Integer variableId;

	@Column(name = "alias")
	private String alias;

	public ProjectProperty() {
	}

	public ProjectProperty(DmsProject project, Integer typeId, String value, Integer rank, Integer variableId, String alias) {
		this.project = project;
		this.typeId = typeId;
		this.value = value;
		this.rank = rank;
		this.variableId = variableId;
		this.alias = alias;
	}

	public Integer getProjectPropertyId() {
		return this.projectPropertyId;
	}

	public void setProjectPropertyId(Integer id) {
		this.projectPropertyId = id;
	}

	public DmsProject getProject() {
		return this.project;
	}

	public void setProject(DmsProject project) {
		this.project = project;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer type) {
		this.typeId = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(Integer variableId) {
		this.variableId = variableId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.projectPropertyId == null ? 0 : this.projectPropertyId.hashCode());
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
		ProjectProperty other = (ProjectProperty) obj;
		if (this.projectPropertyId == null) {
			if (other.projectPropertyId != null) {
				return false;
			}
		} else if (!this.projectPropertyId.equals(other.projectPropertyId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectProperty [projectPropertyId=");
		builder.append(this.projectPropertyId);
		builder.append(", project=");
		builder.append(this.project.getName());
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", rank=");
		builder.append(this.rank);
		builder.append(", variable_id=");
		builder.append(this.variableId);
		builder.append(", alias=");
		builder.append(this.alias);
		builder.append("]");
		return builder.toString();
	}

}
