package org.generationcp.middleware.pojos.workbench.releasenote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropPersonId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@IdClass(ReleaseNotePersonId.class)
@Table(name = "release_note_person")
public class ReleaseNotePerson implements Serializable {

	private static final long serialVersionUID = 9081485395114816328L;

	@Id
	@ManyToOne
	@JoinColumn(name = "release_note_id", updatable = false, nullable = false)
	private ReleaseNote releaseNote;

	@Id
	@ManyToOne
	@JoinColumn(name = "person_id", updatable = false, nullable = false)
	private Person person;

	@Column(name = "view_date", nullable = false, updatable = false)
	private Date viewDate = new Date();

	@Column(name = "show_again", nullable = false)
	private Boolean showAgain = true;

	private ReleaseNotePerson() {
	}

	public ReleaseNotePerson(final ReleaseNote releaseNote, final Person person) {
		this.releaseNote = releaseNote;
		this.person = person;
	}

	public ReleaseNote getReleaseNote() {
		return releaseNote;
	}

	public Person getPerson() {
		return person;
	}

	public Date getViewDate() {
		return viewDate;
	}

	public Boolean getShowAgain() {
		return showAgain;
	}

	public void dontShowAgain() {
		this.showAgain = Boolean.FALSE;
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
