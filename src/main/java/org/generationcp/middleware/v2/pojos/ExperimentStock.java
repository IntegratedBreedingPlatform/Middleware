package org.generationcp.middleware.v2.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	@Basic(optional = false)
	@Column(name = "nd_experiment_id")
	private Long experimentId;
	
	/**
	 * Stock used in the extraction or the corresponding stock for the clone.
	 */
	@Basic(optional = false)
	@Column(name = "stock_id")
	private Long stockId;
	
	/**
	 * The Type object.
	 */
	@Basic(optional = false)
	@Column(name = "type_id")
	private Long typeId;

	public Long getExperimentStockId() {
		return experimentStockId;
	}

	public void setExperimentStockId(Long experimentStockId) {
		this.experimentStockId = experimentStockId;
	}

	Long getExperimentId() {
		return experimentId;
	}

	void setExperimentId(Long experimentId) {
		this.experimentId = experimentId;
	}

	Long getStockId() {
		return stockId;
	}

	void setStockId(Long stockId) {
		this.stockId = stockId;
	}

	Long getTypeId() {
		return typeId;
	}

	void setTypeId(Long typeId) {
		this.typeId = typeId;
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
				+ ", experimentId=" + experimentId + ", stockId=" + stockId
				+ ", typeId=" + typeId + "]";
	}

}
