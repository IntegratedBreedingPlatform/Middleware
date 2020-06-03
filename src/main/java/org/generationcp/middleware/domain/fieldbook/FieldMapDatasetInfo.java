/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.fieldbook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.util.Debug;

/**
 * The Class FieldMapDatasetInfo.
 */
public class FieldMapDatasetInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The dataset id. */
	private Integer datasetId;

	/** The dataset name. */
	private String datasetName;

	/** The trial instances. */
	private List<FieldMapTrialInstanceInfo> trialInstances;

	/**
	 * Instantiates a new field map dataset info.
	 */
	public FieldMapDatasetInfo() {
	}

	/**
	 * Instantiates a new field map dataset info.
	 *
	 * @param datasetId the dataset id
	 * @param datasetName the dataset name
	 * @param trialInstances the trial instances
	 */
	public FieldMapDatasetInfo(Integer datasetId, String datasetName, List<FieldMapTrialInstanceInfo> trialInstances) {
		this.datasetId = datasetId;
		this.datasetName = datasetName;
		this.trialInstances = trialInstances;
	}

	public List<FieldMapTrialInstanceInfo> getTrialInstancesWithFieldMap() {
		List<FieldMapTrialInstanceInfo> info = new ArrayList<FieldMapTrialInstanceInfo>();
		if (this.trialInstances != null && !this.trialInstances.isEmpty()) {
			for (FieldMapTrialInstanceInfo geoloc : this.trialInstances) {
				if (geoloc.isFieldMapGenerated()) {
					info.add(geoloc);
				}
			}
		}
		return info;
	}

	/**
	 * Gets the dataset id.
	 *
	 * @return the dataset id
	 */
	public Integer getDatasetId() {
		return this.datasetId;
	}

	/**
	 * Sets the dataset id.
	 *
	 * @param datasetId the new dataset id
	 */
	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	/**
	 * Gets the dataset name.
	 *
	 * @return the dataset name
	 */
	public String getDatasetName() {
		return this.datasetName;
	}

	/**
	 * Sets the dataset name.
	 *
	 * @param datasetName the new dataset name
	 */
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	/**
	 * Gets the trial instances.
	 *
	 * @return the trial instances
	 */
	public List<FieldMapTrialInstanceInfo> getTrialInstances() {
		return this.trialInstances;
	}

	/**
	 * Sets the trial instances.
	 *
	 * @param trialInstances the new trial instances
	 */
	public void setTrialInstances(List<FieldMapTrialInstanceInfo> trialInstances) {
		this.trialInstances = trialInstances;
	}

	/**
	 * Gets the trial instance.
	 *
	 * @param geolocationId the geolocation id
	 * @return the trial instance
	 */
	public FieldMapTrialInstanceInfo getTrialInstance(Integer geolocationId) {
		for (FieldMapTrialInstanceInfo trialInstance : this.trialInstances) {
			if (geolocationId.equals(trialInstance.getInstanceId())) {
				return trialInstance;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FieldMapDatasetInfo [datasetId=");
		builder.append(this.datasetId);
		builder.append(", datasetName=");
		builder.append(this.datasetName);
		builder.append(", trialInstances=");
		builder.append(this.trialInstances.toString());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Prints the.
	 *
	 * @param indent the indent
	 */
	public void print(int indent) {
		Debug.println(indent, "FieldMapDatasetInfo: ");
		indent = indent + 3;
		Debug.println(indent, "Dataset Id = " + this.datasetId);
		Debug.println(indent, "Dataset Name = " + this.datasetName);
		Debug.println(indent, "Trial Instances = ");
		for (FieldMapTrialInstanceInfo trialInstance : this.trialInstances) {
			trialInstance.print(indent + 3);
		}
	}
}
