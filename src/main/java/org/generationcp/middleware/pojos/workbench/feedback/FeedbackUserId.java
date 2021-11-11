package org.generationcp.middleware.pojos.workbench.feedback;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

public class FeedbackUserId implements Serializable {

	private WorkbenchUser user;
	private FeedbackFeature feature;

	public WorkbenchUser getUser() {
		return this.user;
	}

	public void setUser(final WorkbenchUser user) {
		this.user = user;
	}

	public FeedbackFeature getFeature() {
		return this.feature;
	}

	public void setFeature(final FeedbackFeature feature) {
		this.feature = feature;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.user.getUserid()).append(this.feature.name()).hashCode();
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

		final Feedback otherObj = (Feedback) obj;

		return new EqualsBuilder().append(this.user.getUserid(), otherObj.getUser().getUserid())
				.append(this.feature.name(), otherObj.getFeature().name()).isEquals();
	}

}
