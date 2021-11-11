package org.generationcp.middleware.pojos.workbench.feedback;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

public class FeedbackUserId implements Serializable {

	private Feedback feedback;
	private WorkbenchUser user;

	public Feedback getFeedback() {
		return this.feedback;
	}

	public void setFeedback(final Feedback feedback) {
		this.feedback = feedback;
	}

	public WorkbenchUser getUser() {
		return this.user;
	}

	public void setUser(final WorkbenchUser user) {
		this.user = user;
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
		if (!(obj instanceof CropPerson)) {
			return false;
		}

		final FeedbackUser otherObj = (FeedbackUser) obj;

		return new EqualsBuilder().append(this.feedback.getId(), otherObj.getFeedback().getId())
				.append(this.user.getUserid(), otherObj.getUser().getUserid()).isEquals();
	}

}
