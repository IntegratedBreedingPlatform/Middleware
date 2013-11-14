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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.util.Debug;


/**
 * 
 * Contains the field map information needed by the Field Map tool: 
 * Selected Trial (Fieldbook Name), Entry Numbers, Number of Entries, Reps, Number of Reps, Number of Plots.  
 * 
 * @author Joyce Avestro
 *
 */
public class FieldMapInfo{
    
    private Integer fieldbookId;
    
    private String fieldbookName;
    
    private List<FieldMapLabel> labels;
    
    private boolean isTrial; // false if this is for nursery
	

    public FieldMapInfo() {
    	isTrial = false; // not a trial by default
    }

    public FieldMapInfo(Integer fieldbookId, String fieldbookName, List<FieldMapLabel> labels) {
    	this.isTrial = false;
        this.fieldbookId = fieldbookId;
        this.fieldbookName = fieldbookName;
        this.labels = labels;
    }

    public Integer getFieldbookId() {
        return fieldbookId;
    }
    
    public void setFieldbookId(Integer fieldbookId) {
        this.fieldbookId = fieldbookId;
    }
    
    public String getFieldbookName() {
        return fieldbookName;
    }
    
    public void setFieldbookName(String fieldbookName) {
        this.fieldbookName = fieldbookName;
    }
    
    public List<FieldMapLabel> getFieldMapLabels() {
    	return labels;
    }

    public void setFieldMapLabels(List<FieldMapLabel> labels) {
    	this.labels = labels;
    }
    
	public boolean isTrial() {
		return isTrial;
	}

	public void setTrial(boolean isTrial) {
		this.isTrial = isTrial;
	}

    public long getEntryCount() {
    	return labels.size() / getRepCount();
    }
    
    public long getRepCount() {
    	List<Integer> reps = new ArrayList<Integer>();
    	for (FieldMapLabel label : labels){
    		reps.add(label.getRep());
    	}
    	if (reps.size() == 0){
    		return 1;
    	}
    	return Collections.max(reps);
    }

    public long getPlotCount() {
    	List<Integer> plotNumbers = new ArrayList<Integer>();
    	for (FieldMapLabel label : labels){
    		plotNumbers.add(label.getPlotNo());
    	}
    	
    	if (plotNumbers.size() == 0){
    		return 0;
    	}
    	
    	int minPlot = Collections.min(plotNumbers);
    	int maxPlot = Collections.max(plotNumbers);
    	return maxPlot - minPlot + 1;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FieldMapInfo [fieldbookName=");
        builder.append(fieldbookName);
        builder.append(", labels=");
        builder.append(labels.toString());
        builder.append(", numberOfEntries=");
        builder.append(getEntryCount());
        builder.append(", numberOfReps=");
        builder.append(getRepCount());
        builder.append(", numberOfPlots=");
        builder.append(getPlotCount());
        builder.append(", isTrial=");
		builder.append(isTrial);
		builder.append("]");
        return builder.toString();
    }

    public void print(int indent) {
        Debug.println(indent, "FieldMapInfo: " );
        indent = indent + 3;
        Debug.println(indent, "Fieldbook Name: " + fieldbookName);
        Debug.println(indent, "Is Trial = " + isTrial);
        Debug.println(indent, "Labels: " );
        for (FieldMapLabel label : labels){
        	label.print(indent + 3);
        }
        Debug.println(indent, "Number of Entries: " + getEntryCount());
        Debug.println(indent, "Number of Reps: " + getRepCount());
        Debug.println(indent, "Number of Plots: " + getPlotCount());
    }
}
