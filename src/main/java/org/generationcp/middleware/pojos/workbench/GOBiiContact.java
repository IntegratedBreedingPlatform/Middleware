package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "gobii_contact", schema = "workbench")
public class GOBiiContact {

	@Id
	@Column(name = "gobii_contact_id", nullable = false)
	private Integer id;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "GOBiiContact [Id=" + this.id + ", firstName=" + this.firstName + ", lastName=" + this.lastName + "]";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final GOBiiContact goBiiContact = (GOBiiContact) o;
		return Objects.equals(id, goBiiContact.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
