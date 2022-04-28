package org.generationcp.middleware.api.feedback;

import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class FeedbackDto {

	private Integer id;

	private FeedbackFeature feature;

	private String collectorId;

	private Integer attempts;

	private boolean enabled = true;

	public FeedbackDto(final Integer id, final FeedbackFeature feature, final String collectorId, final Integer attempts,
		final boolean enabled) {
		this.id = id;
		this.feature = feature;
		this.collectorId = collectorId;
		this.attempts = attempts;
		this.enabled = enabled;
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

	public void enable() {
		this.enabled = true;
	}

	public void disable() {
		this.enabled = false;
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
