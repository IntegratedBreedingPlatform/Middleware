package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity()
@Table(name="project", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class DmsProject implements Serializable {

	private static final long serialVersionUID = 464731947805951726L;

	@Id
	@Basic(optional = false)
	@Column(name = "project_id")
	private Integer id;
	
	@Basic(optional = false)
	@Column(name = "name")
	private String name;
	
	@Basic(optional = false)
	@Column(name = "description")
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
		DmsProject rhs = (DmsProject) obj;
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
