package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "role_type")
public class RoleType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "role_type_id", nullable = false)
	private Integer id;

	@Column(name = "name", nullable = false)
	private String name;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer roleTypeId) {
		this.id = roleTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
