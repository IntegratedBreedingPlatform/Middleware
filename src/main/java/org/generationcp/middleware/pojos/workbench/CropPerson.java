package org.generationcp.middleware.pojos.workbench;

import org.generationcp.middleware.pojos.Person;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity @IdClass(CropPersonId.class)
@Table(name = "crop_persons")
public class CropPerson {

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

}



