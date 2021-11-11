package org.generationcp.middleware.pojos.workbench.feedback;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

@Entity
@Table(name = "feedback")
public class Feedback implements Serializable {

	private static final long serialVersionUID = -1280259123688554742L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "feedback_id")
	private Integer id;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "feature", updatable = false, nullable = false)
	private FeedbackFeature feature;

	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	private Feedback() {
	}

	public Feedback(final FeedbackFeature feature) {
		this.feature = feature;
	}

	public Integer getId() {
		return id;
	}

	public FeedbackFeature getFeature() {
		return feature;
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
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WorkbenchUser)) {
			return false;
		}

		final Feedback otherObj = (Feedback) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Feedback [id=");
		builder.append(this.id);
		builder.append(", feature=");
		builder.append(this.feature.name());
		builder.append(", enabled=");
		builder.append(this.enabled);

		builder.append("]");
		return builder.toString();
	}

}
