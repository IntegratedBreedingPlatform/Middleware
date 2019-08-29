package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

	public RoleType() {
	}

	public RoleType(final String name) {
		this.name = name;
	}

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


	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!RoleType.class.isInstance(obj)) {
			return false;
		}

		final RoleType otherObj = (RoleType) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public String toString() {
		return "RoleType{" + "id=" + this.id + ", name='" + this.name + '}';
	}

}
