package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author Aldrin Batac
 *
 */
@Entity
@Table(name = "nd_experimentprop", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "nd_experiment_id","type_id","rank" }) })
public class ExperimentProperty implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Basic(optional = false)
	@Column(name = "nd_experimentprop_id")	
	private Integer ndExperimentpropId;
	
	
	@Basic(optional = false)
	@Column(name = "nd_experiment_id")
	private Integer ndExperimentId;
	
	@Basic(optional = false)
	@Column(name = "type_id")
	private Integer typeId;
	
	@Column(name = "value", columnDefinition = "default NULL")
	private String value;
	
	@Basic(optional = false)
	@Column(name = "rank", columnDefinition = "default 0")
	private Integer rank;
	
	public ExperimentProperty(){
		
	}
	
	
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("ExperimentProperties [nd_experimentprop_id=" + ndExperimentpropId);
    	sb.append(", nd_experiment_id=" + ndExperimentId);
    	sb.append(", type_id=" + typeId);
    	sb.append(", value=" + value);
    	sb.append(", rank=" + rank);
    	sb.append("]");
    	
    	return sb.toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ndExperimentpropId == null) ? 0 : ndExperimentpropId.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExperimentProperty))
			return false;
		
		ExperimentProperty other = (ExperimentProperty) obj;
		if (ndExperimentpropId == null) {
			if (other.ndExperimentpropId != null)
				return false;
		} else if (!ndExperimentpropId.equals(other.ndExperimentpropId))
			return false;
		
		return true;
	}


	public Integer getNd_experimentprop_id() {
		return ndExperimentpropId;
	}


	public void setNd_experimentprop_id(Integer nd_experimentprop_id) {
		this.ndExperimentpropId = nd_experimentprop_id;
	}


	public Integer getNd_experiment_id() {
		return ndExperimentId;
	}


	public void setNd_experiment_id(Integer nd_experiment_id) {
		this.ndExperimentId = nd_experiment_id;
	}


	public Integer getType_id() {
		return typeId;
	}


	public void setType_id(Integer type_id) {
		this.typeId = type_id;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public Integer getRank() {
		return rank;
	}


	public void setRank(Integer rank) {
		this.rank = rank;
	}

	
}
