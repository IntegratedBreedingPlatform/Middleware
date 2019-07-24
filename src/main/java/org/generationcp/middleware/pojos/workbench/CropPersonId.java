package org.generationcp.middleware.pojos.workbench;

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

}
