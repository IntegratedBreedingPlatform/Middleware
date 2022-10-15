
package org.generationcp.middleware.pojos.dms;

import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * http://wiki.cimmyt.org/confluence/display/MBP/Business+Rules+for+Mapping+to+Chado
 * <p>
 * The project_properties table captures links to the ontology. Properties with the same rank represent a single compound property. The type
 * column distinguishes the components of the compound property. Note that these entries are just the names of the properties with their
 * ontology mappings. Values for properties are stored in the appropriate sub-module, usually in Experiment.
 *
 * @author tippsgo
 */
@Entity
@Table(name = "projectprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "type_id", "rank"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "projectprop")
public class ProjectProperty implements Serializable {

	private static final long serialVersionUID = 7517773605676616639L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@OneToOne
	@JoinColumn(name = "variable_id", updatable = false, insertable = false)
	private CVTerm variable;

	@Column(name = "alias")
	private String alias;

	@Formula("(select c.definition from cvterm c where c.cvterm_id = variable_id) ")
	private String description;

	@Column(name = "name_fldno")
	private Integer nameFldno;

	public ProjectProperty() {
	}

	public ProjectProperty(final DmsProject project, final Integer typeId, final String value, final Integer rank, final Integer variableId,
		final String alias) {
		this.project = project;
		this.typeId = typeId;
		this.value = value;
		this.rank = rank;
		this.variableId = variableId;
		this.alias = alias;
	}

	public ProjectProperty(final DmsProject project, final Integer rank, final Integer nameFldno, final String alias) {
		this.project = project;
		this.rank = rank;
		this.nameFldno = nameFldno;
		this.alias = alias;
	}

	public Integer getNameFldno() {
		return this.nameFldno;
	}

	public void setNameFldno(final Integer nameFldno) {
		this.nameFldno = nameFldno;
	}

	public Integer getProjectPropertyId() {
		return this.projectPropertyId;
	}

	public void setProjectPropertyId(final Integer id) {
		this.projectPropertyId = id;
	}

	public DmsProject getProject() {
		return this.project;
	}

	public void setProject(final DmsProject project) {
		this.project = project;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Integer type) {
		this.typeId = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(final Integer rank) {
		this.rank = rank;
	}

	public Integer getVariableId() {
		return this.variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public CVTerm getVariable() {
		return this.variable;
	}

	public void setVariable(final CVTerm variable) {
		this.variable = variable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.projectPropertyId == null ? 0 : this.projectPropertyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final ProjectProperty other = (ProjectProperty) obj;
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
		final StringBuilder builder = new StringBuilder();
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
		builder.append(", nameType=");
		builder.append(this.nameFldno);
		builder.append("]");
		return builder.toString();
	}

}
