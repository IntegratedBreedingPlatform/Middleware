package org.generationcp.middleware.pojos.workbench.feedback;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

@Entity
@IdClass(FeedbackUserId.class)
@Table(name = "feedback_user")
public class FeedbackUser implements Serializable {

	private static final long serialVersionUID = 5495378050351424107L;

	@Id
	@ManyToOne
	@JoinColumn(name = "feedback_id", updatable = false, nullable = false)
	private Feedback feedback;

	@Id
	@ManyToOne
	@JoinColumn(name = "user_id", updatable = false, nullable = false)
	private WorkbenchUser user;

	@Column(name = "views", nullable = false)
	private Integer views;

	@Column(name = "show_again", nullable = false)
	private Boolean showAgain;

	private FeedbackUser() {
	}

	public FeedbackUser(final Feedback feedback, final WorkbenchUser user) {
		this.feedback = feedback;
		this.user = user;
		this.showAgain = Boolean.TRUE;
		this.views = 1;
	}

	public WorkbenchUser getUser() {
		return this.user;
	}

	public Feedback getFeedback() {
		return this.feedback;
	}

	public Boolean getShowAgain() {
		return this.showAgain;
	}

	public void dontShowAgain() {
		this.showAgain = Boolean.FALSE;
	}

	public Integer getViews() {
		return views;
	}

	public void hasSeen() {
		this.views += 1;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.feedback.getId()).append(this.user.getUserid()).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof FeedbackUser)) {
			return false;
		}

		final FeedbackUser otherObj = (FeedbackUser) obj;

		return new EqualsBuilder().append(this.feedback.getId(), otherObj.getFeedback().getId())
			.append(this.user.getUserid(), otherObj.getUser().getUserid()).isEquals();
	}

}
