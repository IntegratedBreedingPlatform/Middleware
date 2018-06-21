
package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.WordUtils;

@Entity
@Table(name = "role")
public class Role implements Serializable {
	public static final String SUPERADMIN = "SUPERADMIN";

	private static final long serialVersionUID = 7981410876951478010L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "description", nullable = false)
	private String description;

	public Role() {
	}
	
	public Role(final Integer id) {
		this.id = id;
	}
	
	public Role(final String description) {
		this.description = description;
	}
	
	public Role(final Integer id, final String description) {
		this.id = id;
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Role [id=" + this.id + ", description=" + this.description + "]";
	}

	public String getCapitalizedRole() {
		return WordUtils.capitalize(this.getDescription().toUpperCase());
	}
	
	public boolean isSuperAdminUser(){
		return SUPERADMIN.equalsIgnoreCase(this.description);
	}

}
