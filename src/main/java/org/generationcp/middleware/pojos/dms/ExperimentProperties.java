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
public class ExperimentProperties implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Basic(optional = false)
	@Column(name = "nd_experimentprop_id")	
	private Integer nd_experimentprop_id;
	
	
	@Basic(optional = false)
	@Column(name = "nd_experiment_id")
	private Integer nd_experiment_id;
	
	@Basic(optional = false)
	@Column(name = "type_id")
	private Integer type_id;
	
	@Column(name = "value", columnDefinition = "default NULL")
	private String value;
	
	@Basic(optional = false)
	@Column(name = "rank", columnDefinition = "default 0")
	private Integer rank;
	
	public ExperimentProperties(){
		
	}
	
	
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("ExperimentProperties [nd_experimentprop_id=" + nd_experimentprop_id);
    	sb.append(", nd_experiment_id=" + nd_experiment_id);
    	sb.append(", type_id=" + type_id);
    	sb.append(", value=" + value);
    	sb.append(", rank=" + rank);
    	sb.append("]");
    	
    	return sb.toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nd_experimentprop_id == null) ? 0 : nd_experimentprop_id.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExperimentProperties))
			return false;
		
		ExperimentProperties other = (ExperimentProperties) obj;
		if (nd_experimentprop_id == null) {
			if (other.nd_experimentprop_id != null)
				return false;
		} else if (!nd_experimentprop_id.equals(other.nd_experimentprop_id))
			return false;
		
		return true;
	}


	public Integer getNd_experimentprop_id() {
		return nd_experimentprop_id;
	}


	public void setNd_experimentprop_id(Integer nd_experimentprop_id) {
		this.nd_experimentprop_id = nd_experimentprop_id;
	}


	public Integer getNd_experiment_id() {
		return nd_experiment_id;
	}


	public void setNd_experiment_id(Integer nd_experiment_id) {
		this.nd_experiment_id = nd_experiment_id;
	}


	public Integer getType_id() {
		return type_id;
	}


	public void setType_id(Integer type_id) {
		this.type_id = type_id;
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
