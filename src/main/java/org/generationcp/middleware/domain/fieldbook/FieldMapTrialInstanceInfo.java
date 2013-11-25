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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.util.Debug;

// TODO: Auto-generated Javadoc
/**
 * The Class FieldMapTrialInstanceInfo.
 */
public class FieldMapTrialInstanceInfo implements Serializable{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The geolocation id. */
    private Integer geolocationId;
    
    /** The site name. */
    private String siteName;
    
    /** The labels. */
    private List<FieldMapLabel> labels;
    
    /** The block name. */
    private String blockName;
    
    /** The columns in block. */
    private Integer columnsInBlock;
    
    /** The ranges in block. */
    private Integer rangesInBlock;
    
    /** The planting order. */
    private Integer plantingOrder;
    
    /** The start column index */
    private Integer startColumn;
    
    /** The start range index */
    private Integer startRange;
    
    private long entryCount;
    
    private long repCount;
    
    private long plotCount;
    
    private boolean hasFieldMap;
        
    /**
     * Instantiates a new field map trial instance info.
     */
    public FieldMapTrialInstanceInfo() {
    }

    /**
     * Instantiates a new field map trial instance info.
     *
     * @param geolocationId the geolocation id
     * @param siteName the site name
     * @param labels the labels
     */
    public FieldMapTrialInstanceInfo(Integer geolocationId, String siteName, 
    	List<FieldMapLabel> labels) {
	this.geolocationId = geolocationId;
	this.siteName = siteName;
	this.labels = labels;
    }

    public boolean isFieldMapGenerated() {
        if (getFieldMapLabels() != null) {
            for (FieldMapLabel label : getFieldMapLabels()) {
                if (label.getColumn() != null && label.getColumn() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Gets the geolocation id.
     *
     * @return the geolocation id
     */
    public Integer getGeolocationId() {
        return geolocationId;
    }

    /**
     * Sets the geolocation id.
     *
     * @param geolocationId the new geolocation id
     */
    public void setGeolocationId(Integer geolocationId) {
        this.geolocationId = geolocationId;
    }

    /**
     * Gets the site name.
     *
     * @return the site name
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * Sets the site name.
     *
     * @param siteName the new site name
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
    
    /**
     * Gets the field map labels.
     *
     * @return the field map labels
     */
    public List<FieldMapLabel> getFieldMapLabels() {
        return labels;
    }

    /**
     * Sets the field map labels.
     *
     * @param labels the new field map labels
     */
    public void setFieldMapLabels(List<FieldMapLabel> labels) {
        this.labels = labels;
    }
    
    /**
     * Gets the block name.
     *
     * @return the blockName
     */
    public String getBlockName() {
        return blockName;
    }

    
    /**
     * Sets the block name.
     *
     * @param blockName the blockName to set
     */
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    
    /**
     * Gets the columns in block.
     *
     * @return the columnsInBlock
     */
    public Integer getColumnsInBlock() {
        return columnsInBlock;
    }

    
    /**
     * Sets the columns in block.
     *
     * @param columnsInBlock the columnsInBlock to set
     */
    public void setColumnsInBlock(Integer columnsInBlock) {
        this.columnsInBlock = columnsInBlock;
    }

    
    /**
     * Gets the ranges in block.
     *
     * @return the rangesInBlock
     */
    public Integer getRangesInBlock() {
        return rangesInBlock;
    }

    
    /**
     * Sets the ranges in block.
     *
     * @param rangesInBlock the rangesInBlock to set
     */
    public void setRangesInBlock(Integer rangesInBlock) {
        this.rangesInBlock = rangesInBlock;
    }
    
    /**
     * Gets the planting order.
     *
     * @return the plantingOrder
     */
    public Integer getPlantingOrder() {
        return plantingOrder;
    }
    
    /**
     * Sets the planting order.
     *
     * @param plantingOrder the plantingOrder to set
     */
    public void setPlantingOrder(Integer plantingOrder) {
        this.plantingOrder = plantingOrder;
    }
    
    /**
     * Gets the entry count.
     *
     * @return the entry count
     */
    public long getEntryCount() {
        Set<Integer> entries = new HashSet<Integer>();
        for (FieldMapLabel label : labels){
                entries.add(label.getEntryNumber());
        }
        return entries.size();
    }
    
    public void setEntryCount(long entryCount) {
        this.entryCount = entryCount;
    }
    
    /**
     * Gets the rep count.
     *
     * @return the rep count
     */
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
    
    public void setRepCount(long repCount) {
        this.repCount = repCount;
    }

    /**
     * Gets the plot count.
     *
     * @return the plot count
     */
    public long getPlotCount() {
        return labels.size();
    }
    
    public void setPlotCount(long plotCount) {
        this.plotCount = plotCount;
    }
    
    /**
     * Gets the field map label.
     *
     * @param experimentId the experiment id
     * @return the field map label
     */
    public FieldMapLabel getFieldMapLabel(Integer experimentId) {
        for (FieldMapLabel label: labels) {
            if (experimentId == label.getExperimentId()) {
                return label;
            } 
        }
        return null;
    }

    
    /**
     * @return the startColumn
     */
    public Integer getStartColumn() {
        return startColumn;
    }

    
    /**
     * @param startColumn the startColumn to set
     */
    public void setStartColumn(Integer startColumn) {
        this.startColumn = startColumn;
    }

    
    /**
     * @return the startRange
     */
    public Integer getStartRange() {
        return startRange;
    }

    
    /**
     * @param startRange the startRange to set
     */
    public void setStartRange(Integer startRange) {
        this.startRange = startRange;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	    builder.append("FieldMapTrialInstanceInfo [geolocationId=");
	    builder.append(geolocationId);
	    builder.append(", siteName=");
	    builder.append(siteName);
	    builder.append(", labels=");
	    builder.append(labels.toString());
	    builder.append(", numberOfEntries=");
	    builder.append(getEntryCount());
	    builder.append(", numberOfReps=");
	    builder.append(getRepCount());
	    builder.append(", numberOfPlots=");
	    builder.append(getPlotCount());
	    builder.append("]");
	    return builder.toString();
    }

    /**
     * Prints the.
     *
     * @param indent the indent
     */
    public void print(int indent) {
        Debug.println(indent, "FieldMapTrialInstanceInfo: " );
        indent = indent + 3;
        Debug.println(indent, "Geolocation Id = " + geolocationId);
        Debug.println(indent, "Site Name = " + siteName);
        Debug.println(indent, "Labels = " );
        for (FieldMapLabel label : labels){
            label.print(indent + 3);
        }
        Debug.println(indent, "Number of Entries: " + getEntryCount());
        Debug.println(indent, "Number of Reps: " + getRepCount());
        Debug.println(indent, "Number of Plots: " + getPlotCount());
    }

    
    /**
     * @return the hasFieldMap
     */
    public boolean isHasFieldMap() {
        return hasFieldMap;
    }

    
    /**
     * @param hasFieldMap the hasFieldMap to set
     */
    public void setHasFieldMap(boolean hasFieldMap) {
        this.hasFieldMap = hasFieldMap;
    }
}
