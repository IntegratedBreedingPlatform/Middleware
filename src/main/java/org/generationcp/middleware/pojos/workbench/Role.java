
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
	
	@Column(name = "name", nullable = false)
	private String name;

	public Role() {
	}
	
	public Role(final Integer id) {
		this.id = id;
	}
	
	public Role(final String name) {
		this.name = name;
	}

	
	public Integer getId() {
		return id;
	}

	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Role [id=" + this.id + ", name=" + this.name + "]";
	}

	public String getCapitalizedName() {
		return WordUtils.capitalize(this.getName().toUpperCase());
	}

}
