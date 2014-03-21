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

/**
 * The Class FieldMapTrialInstanceInfo.
 */
public class FieldMapTrialInstanceInfo implements Serializable{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The geolocation id. */
    private Integer geolocationId;
    
    /** The site name. trial location id */
    private String siteName;
    
    /** The trial instance no. */
    private String trialInstanceNo;
    
    /** The labels. */
    private List<FieldMapLabel> labels;
    
    /** The block name. */
    private String blockName;
    
    /** The field name. */
    private String fieldName;
    
    /** site name or location name. */
    private String locationName;
    
    /** The fieldmap uuid. */
    private String fieldmapUUID;
    
    
    /** The columns in block. */
    private Integer columnsInBlock;
    
    /** The ranges in block. */
    private Integer rangesInBlock;
    
    /** The planting order. */
    private Integer plantingOrder;
    
    /** The start column index. */
    private Integer startColumn;
    
    /** The start range index. */
    private Integer startRange;
    
    /** The entry count. */
    private long entryCount;
    
    /** The rep count. */
    private long repCount;
    
    /** The plot count. */
    private long plotCount;
    
    /** The has field map. */
    private boolean hasFieldMap;
    
    /** The rows per plot. */
    private Integer rowsPerPlot;
    
    /** The machine row capacity. */
    private Integer machineRowCapacity;
    
    /** The order. */
    private Integer order;
    
    private Integer locationId;
    
    private Integer fieldId;
    
    private Integer blockId;
    
    /** The deleted plot coordintes in (row, range) format */
    private List<String> deletedPlots;
    
        
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

    /**
     * Checks if is field map generated.
     *
     * @return true, if is field map generated
     */
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
     * Gets the trial instance no.
     *
     * @return the trial instance no
     */
    public String getTrialInstanceNo() {
        return trialInstanceNo;
    }

    /**
     * Sets the trial instance no.
     *
     * @param trialInstanceNo the new trial instance no
     */
    public void setTrialInstanceNo(String trialInstanceNo) {
        this.trialInstanceNo = trialInstanceNo;
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
    
    /**
     * Sets the entry count.
     *
     * @param entryCount the new entry count
     */
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
    
    /**
     * Sets the rep count.
     *
     * @param repCount the new rep count
     */
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
    
    /**
     * Sets the plot count.
     *
     * @param plotCount the new plot count
     */
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
     * Gets the start column.
     *
     * @return the startColumn
     */
    public Integer getStartColumn() {
        return startColumn;
    }

    
    /**
     * Sets the start column.
     *
     * @param startColumn the startColumn to set
     */
    public void setStartColumn(Integer startColumn) {
        this.startColumn = startColumn;
    }

    
    /**
     * Gets the start range.
     *
     * @return the startRange
     */
    public Integer getStartRange() {
        return startRange;
    }

    
    /**
     * Sets the start range.
     *
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
     * Gets the checks for field map.
     *
     * @return the hasFieldMap
     */
    public boolean getHasFieldMap() {
        return isFieldMapGenerated();
    }

    
    /**
     * Sets the checks for field map.
     *
     * @param hasFieldMap the hasFieldMap to set
     */
    public void setHasFieldMap(boolean hasFieldMap) {
        this.hasFieldMap = hasFieldMap;
    }

    
    /**
     * Gets the rows per plot.
     *
     * @return the rowsPerPlot
     */
    public Integer getRowsPerPlot() {
        return rowsPerPlot;
    }

    
    /**
     * Sets the rows per plot.
     *
     * @param rowsPerPlot the rowsPerPlot to set
     */
    public void setRowsPerPlot(Integer rowsPerPlot) {
        this.rowsPerPlot = rowsPerPlot;
    }

    /**
     * Gets the field name.
     *
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    
    /**
     * Sets the field name.
     *
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    
    /**
     * Gets the fieldmap uuid.
     *
     * @return the fieldmapUUID
     */
    public String getFieldmapUUID() {
        return fieldmapUUID;
    }

    
    /**
     * Sets the fieldmap uuid.
     *
     * @param fieldmapUUID the fieldmapUUID to set
     */
    public void setFieldmapUUID(String fieldmapUUID) {
        this.fieldmapUUID = fieldmapUUID;
    }

    
    /**
     * Gets the machine row capacity.
     *
     * @return the machineRowCapacity
     */
    public Integer getMachineRowCapacity() {
        return machineRowCapacity;
    }

    
    /**
     * Sets the machine row capacity.
     *
     * @param machineRowCapacity the machineRowCapacity to set
     */
    public void setMachineRowCapacity(Integer machineRowCapacity) {
        this.machineRowCapacity = machineRowCapacity;
    }

    
    /**
     * Gets the location name.
     *
     * @return the locationName
     */
    public String getLocationName() {
        return locationName;
    }
    
    /**
     * Sets the location name.
     *
     * @param locationName the locationName to set
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order the new order
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

	/**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the fieldId
	 */
	public Integer getFieldId() {
		return fieldId;
	}

	/**
	 * @param fieldId the fieldId to set
	 */
	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * @return the blockId
	 */
	public Integer getBlockId() {
		return blockId;
	}

	/**
	 * @param blockId the blockId to set
	 */
	public void setBlockId(Integer blockId) {
		this.blockId = blockId;
	}

	/**
	 * @return the deletedPlots
	 */
	public List<String> getDeletedPlots() {
		return deletedPlots;
	}

	/**
	 * @param deletedPlots the deletedPlots to set
	 */
	public void setDeletedPlots(List<String> deletedPlots) {
		this.deletedPlots = deletedPlots;
	}

	/**
	 * @param blockInfo the blockInfo to set
	 */
	public void updateBlockInformation(FieldmapBlockInfo blockInfo) {
		this.columnsInBlock = blockInfo.getRowsInBlock();
		this.rangesInBlock = blockInfo.getRangesInBlock();
		this.rowsPerPlot = blockInfo.getNumberOfRowsInPlot();
		this.plantingOrder = blockInfo.getPlantingOrder();
		this.machineRowCapacity = blockInfo.getMachineRowCapacity();
		this.deletedPlots = blockInfo.getDeletedPlots();
		this.fieldId = blockInfo.getFieldId();
	}


}
