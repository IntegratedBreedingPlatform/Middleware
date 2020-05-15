package org.generationcp.middleware.pojos.ims;

import org.generationcp.middleware.pojos.dms.ExperimentModel;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ims_experiment_transaction")
public class ExperimentTransaction implements Serializable {

	@EmbeddedId
	private ExperimentTransactionId id;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("experimentId")
	@JoinColumn(name = "nd_experiment_id")
	private ExperimentModel experiment;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("transactionId")
	@JoinColumn(name = "trnid")
	private Transaction transaction;

	@Column(name = "type")
	//Not mapped as Enum, custom converter not supported in Hibernate3
	private Integer type;

	public ExperimentTransaction() {
	}

	public ExperimentTransaction(ExperimentModel experiment, Transaction transaction) {
		this.experiment = experiment;
		this.transaction = transaction;
		this.id = new ExperimentTransactionId(experiment.getNdExperimentId(), transaction.getId());
	}

	public ExperimentTransactionId getId() {
		return id;
	}

	public void setId(final ExperimentTransactionId id) {
		this.id = id;
	}

	public ExperimentModel getExperiment() {
		return experiment;
	}

	public void setExperiment(final ExperimentModel experiment) {
		this.experiment = experiment;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(final Transaction transaction) {
		this.transaction = transaction;
	}

	public Integer getType() {
		return type;
	}

	public void setType(final Integer type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		ExperimentTransaction that = (ExperimentTransaction) o;
		return Objects.equals(experiment, that.experiment) &&
			Objects.equals(transaction, that.transaction);
	}

	@Override
	public int hashCode() {
		return Objects.hash(experiment, transaction);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ExperimentTransaction [id=");
		builder.append(this.id);
		builder.append(", experiment=");
		builder.append(this.experiment);
		builder.append(", transaction=");
		builder.append(this.transaction);
		builder.append(", type=");
		builder.append(this.type);
		builder.append("]");
		return builder.toString();
	}
}
