package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Person;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@IdClass(CropPersonId.class)
@Table(name = "crop_persons")
public class CropPerson implements Serializable {

	public CropPerson() {

	}

	public CropPerson(final CropType cropType, final Person person) {
		this.cropType = cropType;
		this.person = person;
	}

	@Id
	@ManyToOne
	@JoinColumn(name = "crop_name")
	private CropType cropType;

	@Id
	@ManyToOne
	@JoinColumn(name = "personid")
	private Person person;

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	public CropType getCropType() {
		return this.cropType;
	}

	public void setCropType(final CropType cropType) {
		this.cropType = cropType;
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



