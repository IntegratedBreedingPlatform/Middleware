package org.generationcp.middleware.pojos.workbench.feedback;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "feedback")
public class Feedback implements Serializable {

	private static final long serialVersionUID = 9081485395114816328L;

	@Id
	@ManyToOne
	@JoinColumn(name = "user_id", updatable = false, nullable = false)
	private WorkbenchUser user;

	@Id
	@Enumerated(value = EnumType.STRING)
	@Column(name = "feature", updatable = false, nullable = false)
	private FeedbackFeature feature;

	@Column(name = "show_again", nullable = false)
	private Boolean showAgain = true;

	private Feedback() {
	}

	public Feedback(final WorkbenchUser user, final FeedbackFeature feature) {
		this.user = user;
		this.feature = feature;
		this.showAgain = Boolean.FALSE;
	}

	public WorkbenchUser getUser() {
		return this.user;
	}

	public FeedbackFeature getFeature() {
		return this.feature;
	}

	public Boolean getShowAgain() {
		return this.showAgain;
	}

	public void dontShowAgain() {
		this.showAgain = Boolean.FALSE;
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
		if (!(obj instanceof Feedback)) {
			return false;
		}

		final Feedback otherObj = (Feedback) obj;

		return new EqualsBuilder().append(this.user.getUserid(), otherObj.getUser().getUserid())
				.append(this.feature.name(), otherObj.getFeature().name()).isEquals();
	}

}
