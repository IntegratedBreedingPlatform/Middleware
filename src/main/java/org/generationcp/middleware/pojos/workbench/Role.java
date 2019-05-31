
package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang.WordUtils;
import org.pojomatic.Pojomatic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "role")
public class Role implements Serializable {

	public static final String ADMIN = "ADMIN";
	public static final String SUPERADMIN = "SUPERADMIN";

	private static final long serialVersionUID = 7981410876951478010L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Integer id;

//	@Column(name = "description", nullable = false)
//	private String description;

	@Column(name = "name", nullable = false)
	private String name;

	public Role() {
	}

	public Role(final String description, final String name) {
//		this.description = description;
		this.name = name;
	}

	public Role(final Integer id) {
		this.id = id;
	}

	public Role(final String name) {
		this.name = name;
	}

	public Role(final Integer id, final String name) {
		this.id = id;
		this.name = name;
	}

	public static String getADMIN() {
		return ADMIN;
	}

	public static String getSUPERADMIN() {
		return SUPERADMIN;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

//	public String getDescription() {
//		return this.description;
//	}
//
//	public void setDescription(final String description) {
//		this.description = description;
//	}

	public String getCapitalizedRole() {
		return WordUtils.capitalize(this.getName().toUpperCase());
	}

	public boolean isSuperAdminUser() {
		return SUPERADMIN.equalsIgnoreCase(this.name);
	}

}
