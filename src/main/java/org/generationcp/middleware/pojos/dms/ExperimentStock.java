package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experiment_stock
 * 
 * Part of a stock or a clone of a stock that is used in an experiment.
 * 
 * @author tippsgo
 *
 */
@Entity
@Table(name = "nd_experiment_stock")
public class ExperimentStock implements Serializable {

	private static final long serialVersionUID = -3361812145612014083L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "nd_experiment_stock_id")
	private Long experimentStockId;
	
	/**
	 * The Experiment object.
	 */
	@ManyToOne(targetEntity = Experiment.class)
	@JoinColumn(name = "nd_experiment_id", nullable = false)
	private Experiment experiment;
	
	/**
	 * Stock used in the extraction or the corresponding stock for the clone.
	 */
	@ManyToOne(targetEntity = Stock.class)
	@JoinColumn(name = "stock_id", nullable = false)
	private Stock stock;
	
	/**
	 * The Type object.
	 */
	@ManyToOne(targetEntity = CVTerm.class)
	@JoinColumn(name = "type_id", nullable = false)
	private CVTerm type;

	public Long getExperimentStockId() {
		return experimentStockId;
	}

	public void setExperimentStockId(Long experimentStockId) {
		this.experimentStockId = experimentStockId;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public CVTerm getType() {
		return type;
	}

	public void setType(CVTerm type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((experimentStockId == null) ? 0 : experimentStockId
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExperimentStock other = (ExperimentStock) obj;
		if (experimentStockId == null) {
			if (other.experimentStockId != null)
				return false;
		} else if (!experimentStockId.equals(other.experimentStockId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExperimentStock [experimentStockId=" + experimentStockId
				+ ", experiment=" + experiment + ", stock=" + stock + ", type="
				+ type + "]";
	}	

}
