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

import org.generationcp.middleware.util.Debug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Class FieldMapTrialInstanceInfo.
 */
public class FieldMapTrialInstanceInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The instance id. */
	private Integer instanceId;

	/** The site name. trial location id */
	private String siteName;

	/** The trial instance no. */
	private String trialInstanceNo;

	/** The labels. */
	private List<FieldMapLabel> labels;

	private Map<Integer, String> labelHeaders;

	/** The block name. */
	private String blockName;

	/** The field name. */
	private String fieldName;

	/** site name or location name. */
	private String locationName;

	/** The fieldmap uuid. */
	private String fieldmapUUID;

	/** The columns in block. */
	private Integer rowsInBlock;

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

	/** The has geo json. */
	private boolean hasGeoJSON;

	/** The has means data. */
	private boolean hasMeansData;

	/** The rows per plot. */
	private Integer rowsPerPlot;

	/** The machine row capacity. */
	private Integer machineRowCapacity;

	/** The order. */
	private Integer order;

	private Integer locationId;

	private Integer fieldId;

	private Integer blockId;

	/* The total labels needed for each instance*/
	private Integer labelsNeeded;

	/** The deleted plot coordintes in (row, range) format */
	private List<String> deletedPlots;

	private boolean hasInValidValue;

	private boolean hasOverlappingCoordinates;

	/**
	 * Instantiates a new field map trial instance info.
	 */
	public FieldMapTrialInstanceInfo() {
	}

	/**
	 * Instantiates a new field map trial instance info.
	 *
	 * @param instanceId the instance id
	 * @param siteName the site name
	 * @param labels the labels
	 */
	public FieldMapTrialInstanceInfo(final Integer instanceId, final String siteName, final List<FieldMapLabel> labels) {
		this.instanceId = instanceId;
		this.siteName = siteName;
		this.labels = labels;
	}

	/**
	 * Checks if is field map generated.
	 *
	 * @return true, if is field map generated
	 */
	public boolean isFieldMapGenerated() {
		if (this.getFieldMapLabels() != null) {
			for (final FieldMapLabel label : this.getFieldMapLabels()) {
				if (label.getColumn() != null && label.getColumn() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the instance id.
	 *
	 * @return the instance id
	 */
	public Integer getInstanceId() {
		return this.instanceId;
	}

	/**
	 * Sets the instance id.
	 *
	 * @param instanceId the new instance id
	 */
	public void setInstanceId(final Integer instanceId) {
		this.instanceId = instanceId;
	}

	/**
	 * Gets the site name.
	 *
	 * @return the site name
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * Sets the site name.
	 *
	 * @param siteName the new site name
	 */
	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}

	/**
	 * Gets the trial instance no.
	 *
	 * @return the trial instance no
	 */
	public String getTrialInstanceNo() {
		return this.trialInstanceNo;
	}

	/**
	 * Sets the trial instance no.
	 *
	 * @param trialInstanceNo the new trial instance no
	 */
	public void setTrialInstanceNo(final String trialInstanceNo) {
		this.trialInstanceNo = trialInstanceNo;
	}

	/**
	 * Gets the field map labels.
	 *
	 * @return the field map labels
	 */
	public List<FieldMapLabel> getFieldMapLabels() {
		return this.labels;
	}

	/**
	 * Sets the field map labels.
	 *
	 * @param labels the new field map labels
	 */
	public void setFieldMapLabels(final List<FieldMapLabel> labels) {
		this.labels = labels;
	}

	/**
	 * Gets the block name.
	 *
	 * @return the blockName
	 */
	public String getBlockName() {
		return this.blockName;
	}

	/**
	 * Sets the block name.
	 *
	 * @param blockName the blockName to set
	 */
	public void setBlockName(final String blockName) {
		this.blockName = blockName;
	}

	/**
	 * Gets the columns in block.
	 *
	 * @return the columnsInBlock
	 */
	public Integer getRowsInBlock() {
		return this.rowsInBlock;
	}

	/**
	 * Sets the columns in block.
	 *
	 * @param rowsInBlock the rowsInBlock to set
	 */
	public void setRowsInBlock(final Integer rowsInBlock) {
		this.rowsInBlock = rowsInBlock;
	}

	/**
	 * Gets the ranges in block.
	 *
	 * @return the rangesInBlock
	 */
	public Integer getRangesInBlock() {
		return this.rangesInBlock;
	}

	/**
	 * Sets the ranges in block.
	 *
	 * @param rangesInBlock the rangesInBlock to set
	 */
	public void setRangesInBlock(final Integer rangesInBlock) {
		this.rangesInBlock = rangesInBlock;
	}

	/**
	 * Gets the planting order.
	 *
	 * @return the plantingOrder
	 */
	public Integer getPlantingOrder() {
		return this.plantingOrder;
	}

	/**
	 * Sets the planting order.
	 *
	 * @param plantingOrder the plantingOrder to set
	 */
	public void setPlantingOrder(final Integer plantingOrder) {
		this.plantingOrder = plantingOrder;
	}

	/**
	 * Gets the entry count.
	 *
	 * @return the entry count
	 */
	public long getEntryCount() {
		final Set<Integer> entries = new HashSet<Integer>();
		for (final FieldMapLabel label : this.labels) {
			entries.add(label.getEntryNumber());
		}
		return entries.size();
	}

	/**
	 * Sets the entry count.
	 *
	 * @param entryCount the new entry count
	 */
	public void setEntryCount(final long entryCount) {
		this.entryCount = entryCount;
	}

	/**
	 * Gets the rep count.
	 *
	 * @return the rep count
	 */
	public long getRepCount() {
		final List<Integer> reps = new ArrayList<Integer>();
		for (final FieldMapLabel label : this.labels) {
			reps.add(label.getRep());
		}
		if (reps.isEmpty()) {
			return 1;
		}
		return Collections.max(reps);
	}

	/**
	 * Sets the rep count.
	 *
	 * @param repCount the new rep count
	 */
	public void setRepCount(final long repCount) {
		this.repCount = repCount;
	}

	/**
	 * Gets the plot count.
	 *
	 * @return the plot count
	 */
	public long getPlotCount() {
		return this.labels.size();
	}

	/**
	 * Sets the plot count.
	 *
	 * @param plotCount the new plot count
	 */
	public void setPlotCount(final long plotCount) {
		this.plotCount = plotCount;
	}

	/**
	 * Gets the field map label.
	 *
	 * @param experimentId the experiment id
	 * @return the field map label
	 */
	public FieldMapLabel getFieldMapLabel(final Integer experimentId) {
		for (final FieldMapLabel label : this.labels) {
			if (experimentId.equals(label.getExperimentId())) {
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
		return this.startColumn;
	}

	/**
	 * Sets the start column.
	 *
	 * @param startColumn the startColumn to set
	 */
	public void setStartColumn(final Integer startColumn) {
		this.startColumn = startColumn;
	}

	/**
	 * Gets the start range.
	 *
	 * @return the startRange
	 */
	public Integer getStartRange() {
		return this.startRange;
	}

	/**
	 * Sets the start range.
	 *
	 * @param startRange the startRange to set
	 */
	public void setStartRange(final Integer startRange) {
		this.startRange = startRange;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FieldMapTrialInstanceInfo [instanceId=");
		builder.append(this.instanceId);
		builder.append(", siteName=");
		builder.append(this.siteName);
		builder.append(", labels=");
		builder.append(this.labels.toString());
		builder.append(", numberOfEntries=");
		builder.append(this.getEntryCount());
		builder.append(", numberOfReps=");
		builder.append(this.getRepCount());
		builder.append(", numberOfPlots=");
		builder.append(this.getPlotCount());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Prints the.
	 *
	 * @param indent the indent
	 */
	public void print(int indent) {
		Debug.println(indent, "FieldMapTrialInstanceInfo: ");
		indent = indent + 3;
		Debug.println(indent, "instance Id = " + this.instanceId);
		Debug.println(indent, "Site Name = " + this.siteName);
		Debug.println(indent, "Labels = ");
		for (final FieldMapLabel label : this.labels) {
			label.print(indent + 3);
		}
		Debug.println(indent, "Number of Entries: " + this.getEntryCount());
		Debug.println(indent, "Number of Reps: " + this.getRepCount());
		Debug.println(indent, "Number of Plots: " + this.getPlotCount());
	}

	/**
	 * Gets the checks for field map.
	 *
	 * @return the hasFieldMap
	 */
	public boolean getHasFieldMap() {
		return this.isFieldMapGenerated();
	}

	/**
	 * Sets the checks for field map.
	 *
	 * @param hasFieldMap the hasFieldMap to set
	 */
	public void setHasFieldMap(final boolean hasFieldMap) {
		this.hasFieldMap = hasFieldMap;
	}

	/**
	 * Gets the checks for geo json.
	 *
	 * @return the hasGeoJSON
	 */
	public boolean getHasGeoJSON() {
		return this.hasGeoJSON;
	}

	/**
	 * Sets the checks for geo json.
	 *
	 * @param hasGeoJSON the hasGeoJSON to set
	 */
	public void setHasGeoJSON(final boolean hasGeoJSON) {
		this.hasGeoJSON = hasGeoJSON;
	}

	/**
	 * Gets the checks for means data.
	 *
	 * @return the hasMeansData
	 */
	public boolean getHasMeansData() {
		return this.hasMeansData;
	}

	/**
	 * Sets the checks for means data.
	 *
	 * @param hasMeansData the hasMeansData to set
	 */
	public void setHasMeansData(final boolean hasMeansData) {
		this.hasMeansData = hasMeansData;
	}

	/**
	 * Gets the rows per plot.
	 *
	 * @return the rowsPerPlot
	 */
	public Integer getRowsPerPlot() {
		return this.rowsPerPlot;
	}

	/**
	 * Sets the rows per plot.
	 *
	 * @param rowsPerPlot the rowsPerPlot to set
	 */
	public void setRowsPerPlot(final Integer rowsPerPlot) {
		this.rowsPerPlot = rowsPerPlot;
	}

	/**
	 * Gets the field name.
	 *
	 * @return the fieldName
	 */
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * Sets the field name.
	 *
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Gets the fieldmap uuid.
	 *
	 * @return the fieldmapUUID
	 */
	public String getFieldmapUUID() {
		return this.fieldmapUUID;
	}

	/**
	 * Sets the fieldmap uuid.
	 *
	 * @param fieldmapUUID the fieldmapUUID to set
	 */
	public void setFieldmapUUID(final String fieldmapUUID) {
		this.fieldmapUUID = fieldmapUUID;
	}

	/**
	 * Gets the machine row capacity.
	 *
	 * @return the machineRowCapacity
	 */
	public Integer getMachineRowCapacity() {
		return this.machineRowCapacity;
	}

	/**
	 * Sets the machine row capacity.
	 *
	 * @param machineRowCapacity the machineRowCapacity to set
	 */
	public void setMachineRowCapacity(final Integer machineRowCapacity) {
		this.machineRowCapacity = machineRowCapacity;
	}

	/**
	 * Gets the location name.
	 *
	 * @return the locationName
	 */
	public String getLocationName() {
		return this.locationName;
	}

	/**
	 * Sets the location name.
	 *
	 * @param locationName the locationName to set
	 */
	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public Integer getOrder() {
		return this.order;
	}

	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	public void setOrder(final Integer order) {
		this.order = order;
	}

	/**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return this.locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the fieldId
	 */
	public Integer getFieldId() {
		return this.fieldId;
	}

	/**
	 * @param fieldId the fieldId to set
	 */
	public void setFieldId(final Integer fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * @return the blockId
	 */
	public Integer getBlockId() {
		return this.blockId;
	}

	/**
	 * @param blockId the blockId to set
	 */
	public void setBlockId(final Integer blockId) {
		this.blockId = blockId;
	}

	/**
	 * @return the deletedPlots
	 */
	public List<String> getDeletedPlots() {
		return this.deletedPlots;
	}

	/**
	 * @param deletedPlots the deletedPlots to set
	 */
	public void setDeletedPlots(final List<String> deletedPlots) {
		this.deletedPlots = deletedPlots;
	}

	/**
	 * @param blockInfo the blockInfo to set
	 */
	public void updateBlockInformation(final FieldmapBlockInfo blockInfo) {
		if (blockInfo != null) {
			this.rowsInBlock = blockInfo.getRowsInBlock();
			this.rangesInBlock = blockInfo.getRangesInBlock();
			this.rowsPerPlot = blockInfo.getNumberOfRowsInPlot();
			this.plantingOrder = blockInfo.getPlantingOrder();
			this.machineRowCapacity = blockInfo.getMachineRowCapacity();
			this.deletedPlots = blockInfo.getDeletedPlots();
			this.fieldId = blockInfo.getFieldId();
		}
	}

	public void clearColumnRangeIfExists() {
		if (this.getFieldMapLabels() != null) {
			for (final FieldMapLabel label : this.getFieldMapLabels()) {
				label.setColumn(null);
				label.setRange(null);
			}
		}
	}

	public Map<Integer, String> getLabelHeaders() {
		return this.labelHeaders;
	}

	public void setLabelHeaders(final Map<Integer, String> labelHeaders) {
		this.labelHeaders = labelHeaders;
	}

	public Integer getLabelsNeeded() {
		return this.labelsNeeded;
	}

	public void setLabelsNeeded(final Integer labelsNeeded) {
		this.labelsNeeded = labelsNeeded;
	}


	public boolean isHasInValidValue() {
		return this.hasInValidValue;
	}

	public void setHasInValidValue(final boolean hasInValidValue) {
		this.hasInValidValue = hasInValidValue;
	}

	public boolean isHasOverlappingCoordinates() {
		return this.hasOverlappingCoordinates;
	}

	public void setHasOverlappingCoordinates(final boolean hasOverlappingCoordinates) {
		this.hasOverlappingCoordinates = hasOverlappingCoordinates;
	}
}
