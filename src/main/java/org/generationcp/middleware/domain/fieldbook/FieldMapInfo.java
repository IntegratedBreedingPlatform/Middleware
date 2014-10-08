/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.domain.fieldbook;

import org.generationcp.middleware.util.Debug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Contains the field map information needed by the Field Map tool: 
 * Selected Trial (Fieldbook Name), Entry Numbers, Number of Entries, Reps, Number of Reps, Number of Plots.  
 * 
 * @author Joyce Avestro
 *
 */
public class FieldMapInfo implements Serializable{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The fieldbook id. */
    private Integer fieldbookId;
    
    /** The fieldbook name. */
    private String fieldbookName;
    
    /** The is trial. */
    private boolean isTrial; // false if this is for nursery
    
    /** The datasets. */
    private List<FieldMapDatasetInfo> datasets;
    

    /**
     * Instantiates a new field map info.
     */
    public FieldMapInfo() {
    	isTrial = false; // not a trial by default
    }

    /**
     * Instantiates a new field map info.
     *
     * @param fieldbookId the fieldbook id
     * @param fieldbookName the fieldbook name
     * @param labels the labels
     */
    public FieldMapInfo(Integer fieldbookId, String fieldbookName, List<FieldMapLabel> labels) {
    	this.isTrial = false;
        this.fieldbookId = fieldbookId;
        this.fieldbookName = fieldbookName;
    }
    
    public List<FieldMapDatasetInfo> getDatasetsWithFieldMap() {
        List<FieldMapDatasetInfo> info = new ArrayList<FieldMapDatasetInfo>();
        
        if (getDatasets() != null && !getDatasets().isEmpty()) {
            for (FieldMapDatasetInfo dataset : getDatasets()) {
                if (!dataset.getTrialInstancesWithFieldMap().isEmpty()) {
                    info.add(dataset);
                }
            }
        }
        
        return info;
    }

    /**
     * Gets the fieldbook id.
     *
     * @return the fieldbook id
     */
    public Integer getFieldbookId() {
        return fieldbookId;
    }
    
    /**
     * Sets the fieldbook id.
     *
     * @param fieldbookId the new fieldbook id
     */
    public void setFieldbookId(Integer fieldbookId) {
        this.fieldbookId = fieldbookId;
    }
    
    /**
     * Gets the fieldbook name.
     *
     * @return the fieldbook name
     */
    public String getFieldbookName() {
        return fieldbookName;
    }
    
    /**
     * Sets the fieldbook name.
     *
     * @param fieldbookName the new fieldbook name
     */
    public void setFieldbookName(String fieldbookName) {
        this.fieldbookName = fieldbookName;
    }
    
    /**
     * Checks if is trial.
     *
     * @return true, if is trial
     */
    public boolean isTrial() {
	return isTrial;
    }

    /**
     * Sets the trial.
     *
     * @param isTrial the new trial
     */
    public void setTrial(boolean isTrial) {
	this.isTrial = isTrial;
    }
        
    /**
     * Gets the datasets.
     *
     * @return the datasets
     */
    public List<FieldMapDatasetInfo> getDatasets() {
        return datasets;
    }
    
    /**
     * Sets the datasets.
     *
     * @param datasets the new datasets
     */
    public void setDatasets(List<FieldMapDatasetInfo> datasets) {
        this.datasets = datasets;
    }
    
    /**
     * Gets the data set.
     *
     * @param datasetId the dataset id
     * @return the data set
     */
    public FieldMapDatasetInfo getDataSet(Integer datasetId) {
        for (FieldMapDatasetInfo dataset: datasets) {
            if (datasetId.equals(dataset.getDatasetId())) {
                return dataset;
            } 
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FieldMapInfo [fieldbookName=");
        builder.append(fieldbookName);        
        builder.append(", isTrial=");
		builder.append(isTrial);
		builder.append("]");
        return builder.toString();
    }

    /**
     * Prints the.
     *
     * @param indent the indent
     */
    public void print(int indent) {
        Debug.println(indent, "FieldMapInfo: " );
        indent = indent + 3;
        Debug.println(indent, "Fieldbook Name: " + fieldbookName);
        Debug.println(indent, "Is Trial = " + isTrial);
        if (datasets != null){
            for (FieldMapDatasetInfo datasetInfo : datasets){
                datasetInfo.print(indent);
            }
        }
    }
    
}
