package org.generationcp.middleware.pojos.workbench.releasenote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.io.Serializable;

public class ReleaseNotePersonId implements Serializable {

	private ReleaseNote releaseNote;
	private Person person;

	public ReleaseNote getReleaseNote() {
		return this.releaseNote;
	}

	public void setReleaseNote(final ReleaseNote releaseNote) {
		this.releaseNote = releaseNote;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.releaseNote.getId()).append(this.person.getId()).hashCode();
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

		final ReleaseNotePerson otherObj = (ReleaseNotePerson) obj;

		return new EqualsBuilder().append(this.releaseNote.getId(), otherObj.getReleaseNote().getId())
			.append(this.person.getId(), otherObj.getPerson().getId()).isEquals();
	}

}
