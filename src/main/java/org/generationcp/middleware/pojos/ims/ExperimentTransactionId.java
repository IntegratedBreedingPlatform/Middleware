package org.generationcp.middleware.pojos.ims;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExperimentTransactionId implements Serializable {

	@Column(name = "experiment_id")
	private Integer experimentId;

	@Column(name = "trnid")
	private Integer transactionId;

	public ExperimentTransactionId() {
	}

	public ExperimentTransactionId(final Integer experimentId, final Integer transactionId) {
		this.experimentId = experimentId;
		this.transactionId = transactionId;
	}

	public Integer getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(final Integer experimentId) {
		this.experimentId = experimentId;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final Integer transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		ExperimentTransactionId that = (ExperimentTransactionId) o;
		return Objects.equals(experimentId, that.experimentId) &&
			Objects.equals(transactionId, that.transactionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(experimentId, transactionId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ExperimentTransactionId [experimentId=");
		builder.append(this.experimentId);
		builder.append(", transactionId=");
		builder.append(this.transactionId);
		builder.append("]");
		return builder.toString();
	}
}

