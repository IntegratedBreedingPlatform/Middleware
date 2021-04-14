package org.generationcp.middleware.pojos.workbench.releasenote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import java.io.Serializable;

public class ReleaseNoteUserId implements Serializable {

	private ReleaseNote releaseNote;
	private WorkbenchUser user;

	public ReleaseNote getReleaseNote() {
		return this.releaseNote;
	}

	public void setReleaseNote(final ReleaseNote releaseNote) {
		this.releaseNote = releaseNote;
	}

	public WorkbenchUser getUser() {
		return this.user;
	}

	public void setUser(final WorkbenchUser user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.releaseNote.getId()).append(this.user.getUserid()).hashCode();
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

		final ReleaseNoteUser otherObj = (ReleaseNoteUser) obj;

		return new EqualsBuilder().append(this.releaseNote.getId(), otherObj.getReleaseNote().getId())
			.append(this.user.getUserid(), otherObj.getUser().getUserid()).isEquals();
	}

}
