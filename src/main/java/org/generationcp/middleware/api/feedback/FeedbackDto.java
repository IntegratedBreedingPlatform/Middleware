package org.generationcp.middleware.api.feedback;

import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class FeedbackDto {

	private Integer id;

	private FeedbackFeature feature;

	private String collectorId;

	private Integer attempts;

	private boolean enabled;

	public FeedbackDto(final Feedback feedback) {
		this.id = feedback.getId();
		this.feature = feedback.getFeature();
		this.collectorId = feedback.getCollectorId();
		this.attempts = feedback.getAttempts();
		this.enabled = feedback.isEnabled();
	}

	public Integer getId() {
		return id;
	}

	public FeedbackFeature getFeature() {
		return feature;
	}

	public String getCollectorId() {
		return this.collectorId;
	}

	public void setCollectorId(final String collectorId) {
		this.collectorId = collectorId;
	}

	public Integer getAttempts() {
		return this.attempts;
	}

	public void setAttempts(final Integer attempts) {
		this.attempts = attempts;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
