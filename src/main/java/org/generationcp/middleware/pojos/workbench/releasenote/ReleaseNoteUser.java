package org.generationcp.middleware.pojos.workbench.releasenote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

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
@IdClass(ReleaseNoteUserId.class)
@Table(name = "release_note_user")
public class ReleaseNoteUser implements Serializable {

	private static final long serialVersionUID = 9081485395114816328L;

	@Id
	@ManyToOne
	@JoinColumn(name = "release_note_id", updatable = false, nullable = false)
	private ReleaseNote releaseNote;

	@Id
	@ManyToOne
	@JoinColumn(name = "user_id", updatable = false, nullable = false)
	private WorkbenchUser user;

	@Column(name = "view_date", nullable = false, updatable = false)
	private Date viewDate = new Date();

	@Column(name = "show_again", nullable = false)
	private Boolean showAgain = true;

	private ReleaseNoteUser() {
	}

	public ReleaseNoteUser(final ReleaseNote releaseNote, final WorkbenchUser user) {
		this.releaseNote = releaseNote;
		this.user = user;
	}

	public ReleaseNote getReleaseNote() {
		return this.releaseNote;
	}

	public WorkbenchUser getUser() {
		return this.user;
	}

	public Date getViewDate() {
		return this.viewDate;
	}

	public Boolean getShowAgain() {
		return this.showAgain;
	}

	public void setShowAgain(final boolean showAgain) {
		this.showAgain = showAgain;
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
		if (!(obj instanceof ReleaseNoteUser)) {
			return false;
		}

		final ReleaseNoteUser otherObj = (ReleaseNoteUser) obj;

		return new EqualsBuilder().append(this.releaseNote.getId(), otherObj.getReleaseNote().getId())
			.append(this.user.getUserid(), otherObj.getUser().getUserid()).isEquals();
	}

}
