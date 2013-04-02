package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "projectprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "type_id", "rank"})})
public class ProjectProperty implements Serializable {

	private static final long serialVersionUID = 7517773605676616639L;

	@Id
	@Basic(optional = false)
	@Column(name = "projectprop_id")
	private Integer id;
	
	@ManyToOne(targetEntity = DmsProject.class)
	@JoinColumn(name = "project_id", nullable = false)
	private DmsProject project;
	
	@ManyToOne(targetEntity = CVTerm.class)
	@JoinColumn(name = "type_id", nullable = false)
	private CVTerm type;
	
	@Column(name = "`value`")
	private String value;
	
	@Column(name = "rank")
	private Integer rank;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	public String toString() {
		StringBuffer out = new StringBuffer();
		
		out.append("[" + this.getClass().getName() + " ");
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (field.getAnnotation(Column.class) != null) {
					out.append("[" + field.getName() + "=" + field.get(this) + "]");
				}
			}
		} catch (Exception e) {
			//do nothing
		}
		out.append("]");
		
		return out.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		ProjectProperty rhs = (ProjectProperty) obj;
		return new EqualsBuilder()
					.appendSuper(super.equals(obj))
					.append(this.id, rhs.id)
					.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}
	
}
