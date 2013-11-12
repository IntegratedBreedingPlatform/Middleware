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
    
    private List<String> entryNumbers;

    private List<String> germplasmNames;

    private List<Integer> reps;

    public long plotCount;
   
    public FieldMapInfo() {
    }

    public FieldMapInfo(Integer fieldbookId, String fieldbookName, int entryCount, int repCount, int plotCount) {
        this.fieldbookId = fieldbookId;
        this.fieldbookName = fieldbookName;
        this.plotCount = plotCount;
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
    
    public List<String> getGermplasmNames() {
        return germplasmNames;
    }

    public void setGermplasmNames(List<String> germplasmNames) {
        this.germplasmNames = germplasmNames;
    }
    
    public List<Integer> getReps() {
        return reps;
    }
    
    public void setReps(List<Integer> reps) {
        this.reps = reps;
    }
    
    public void setEntryNumbers(List<String> entryNumbers) {
        this.entryNumbers = entryNumbers;
    }
    
    public List<String> getEntryNumbers() {
        return entryNumbers;
    }
    
    public long getEntryCount() {
        return entryNumbers.size();
    }
    
    public long getRepCount() {
        return reps.size();
    }

    public long getPlotCount() {
        return plotCount;
    }

    public void setPlotCount(long plotCount) {
        this.plotCount = plotCount;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FieldMapInfo [fieldbookName=");
        builder.append(fieldbookName);
        builder.append(", entryNumbers=");
        builder.append(entryNumbers.toString());
        builder.append(", entryCount=");
        builder.append(entryNumbers.size());
        builder.append(", reps=");
        builder.append(reps.toString());
        builder.append(", repCount=");
        builder.append(reps.size());
        builder.append(", plotCount=");
        builder.append(plotCount);
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent) {
        Debug.println(indent, "FieldMapInfo: " );
        indent = indent + 3;
        Debug.println(indent, "Fieldbook Name: " + fieldbookName);
        Debug.println(indent, "Entry Numbers: " + entryNumbers.toString());
        Debug.println(indent, "Number of Entries: " + entryNumbers.size());
        Debug.println(indent, "Reps: " + reps.toString());
        Debug.println(indent, "Number of Reps: " + reps.size());
        Debug.println(indent, "Number of Plots: " + plotCount);
    }
}
