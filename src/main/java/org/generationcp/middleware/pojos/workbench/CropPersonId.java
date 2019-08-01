package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Person;

import java.io.Serializable;

public class CropPersonId implements Serializable {

	private CropType cropType;
	private Person person;

	public CropType getCropType() {
		return this.cropType;
	}

	public void setCropType(final CropType cropType) {
		this.cropType = cropType;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.cropType.getCropName()).append(this.person.getId()).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CropPerson)) {
			return false;
		}

		final CropPerson otherObj = (CropPerson) obj;

		return new EqualsBuilder().append(this.cropType.getCropName(), otherObj.getCropType().getCropName())
			.append(this.person.getId(), otherObj.getPerson().getId()).isEquals();
	}

}
