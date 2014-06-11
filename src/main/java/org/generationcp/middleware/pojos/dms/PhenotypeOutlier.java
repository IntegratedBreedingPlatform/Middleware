/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
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
@Table(	name = "phenotype_outlier")
public class PhenotypeOutlier implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "phenotype_outlier_id")
	private Integer phenotypeOutlierId;

	// References phenotype
	@Basic(optional = false)
    @Column(name="phenotype_id")
	private Integer phenotypeId;
    
	// References cvterm
	@Basic(optional = false)
    @Column(name="observable_id")
	private Integer observableId;
    
	@Basic(optional = false)
    @Column(name="plot_no")
	private Integer plotNo;
    
    @Basic(optional = false)
    @Column(name="project_id")
	private Integer projectId;

	@Column(name = "value")
	private String value;
	

	public PhenotypeOutlier() {
	}


	public PhenotypeOutlier(
			Integer phenotypeOutlierId,
			Integer phenotypeId, 
			Integer observableId,
			Integer plotNo,
			Integer projectId,
			String value) {
		this.setPhenotypeOutlierId(phenotypeOutlierId);
		this.setPhenotypeId(phenotypeId);
		this.setObservableId(observableId);
		this.setPlotNo(plotNo);
		this.setProjectId(projectId);
		this.setValue(value);
	}
	
	public Integer getPhenotypeOutlierId() {
		return phenotypeOutlierId;
	}


	public void setPhenotypeOutlierId(Integer phenotypeOutlierId) {
		this.phenotypeOutlierId = phenotypeOutlierId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phenotypeOutlierId == null) ? 0 : phenotypeOutlierId.hashCode());
		result = prime * result + ((phenotypeId == null) ? 0 : phenotypeId.hashCode());
		result = prime * result + ((observableId == null) ? 0 : observableId.hashCode());
		result = prime * result + ((plotNo == null) ? 0 : plotNo.hashCode());
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		PhenotypeOutlier other = (PhenotypeOutlier) obj;
		
		if (phenotypeOutlierId == null) {
			if (other.phenotypeOutlierId != null)
				return false;
		} else if (!phenotypeOutlierId.equals(other.phenotypeOutlierId))
			return false;
		
		if (phenotypeId == null) {
			if (other.phenotypeId != null)
				return false;
		} else if (!phenotypeId.equals(other.phenotypeId))
			return false;
		
		if (observableId == null) {
			if (other.observableId != null)
				return false;
		} else if (!observableId.equals(other.observableId))
			return false;
		
		
		if (plotNo == null) {
			if (other.plotNo != null)
				return false;
		} else if (!plotNo.equals(other.plotNo))
			return false;
		
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhenotypeOutlier [phenotypeOutlierId=");
		builder.append(getPhenotypeOutlierId());
		builder.append(", phenotypeId=");
		builder.append(getPhenotypeId());
		builder.append(", observableId=");
		builder.append(getObservableId());
		builder.append(", plotNo=");
		builder.append(getPlotNo());
		builder.append(", projectId=");
		builder.append(getProjectId());
		builder.append(", value=");
		builder.append(getValue());
		builder.append("]");
		return builder.toString();
	}


	public Integer getPhenotypeId() {
		return phenotypeId;
	}


	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}


	public Integer getObservableId() {
		return observableId;
	}


	public void setObservableId(Integer observableId) {
		this.observableId = observableId;
	}


	public Integer getPlotNo() {
		return plotNo;
	}


	public void setPlotNo(Integer plotNo) {
		this.plotNo = plotNo;
	}


	public Integer getProjectId() {
		return projectId;
	}


	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}