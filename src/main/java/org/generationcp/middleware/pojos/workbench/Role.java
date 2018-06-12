
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

}
