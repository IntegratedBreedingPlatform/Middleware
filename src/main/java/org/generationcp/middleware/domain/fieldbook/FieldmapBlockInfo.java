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
import java.util.List;

/**
 * The Class FieldmapBlockInfo.
 */
public class FieldmapBlockInfo implements Serializable{
	
    private static final long serialVersionUID = 1L;

    /** The block id. */
	private int blockId;
	
	/** The rows in block. */
	private int rowsInBlock;
	
	/** The ranges in block. */
	private int rangesInBlock;
	
	/** The number of rows in plot. */
	private int numberOfRowsInPlot;
	
	/** planting order */
	private int plantingOrder;
	
	/** machine row capacity */
	private int machineRowCapacity;
	
	private boolean isNewBlock;
	
	private List<String> deletedPlots;
	
	private Integer fieldId;
	
	/**
	 * Instantiates a new fieldmap block info.
	 *
	 * @param blockId the block id
	 * @param rowsInBlock the rows in block
	 * @param rangesInBlock the ranges in block
	 * @param numberOfRowsInPlot the number of rows in plot
	 * @param isNew the is new
	 */
    public FieldmapBlockInfo(int blockId, int rowsInBlock, int rangesInBlock,
            int numberOfRowsInPlot, boolean isNew) {
        this.blockId = blockId;
        this.rowsInBlock = rowsInBlock;
        this.rangesInBlock = rangesInBlock;
        this.numberOfRowsInPlot = numberOfRowsInPlot;
        this.isNewBlock = isNew;
    }
	
    public FieldmapBlockInfo(int blockId, int rowsInBlock, int rangesInBlock,
            int numberOfRowsInPlot, int plantingOrder, int machineRowCapacity, boolean isNew, List<String> deletedPlots, Integer fieldId) {
        this.blockId = blockId;
        this.rowsInBlock = rowsInBlock;
        this.rangesInBlock = rangesInBlock;
        this.numberOfRowsInPlot = numberOfRowsInPlot;
        this.plantingOrder = plantingOrder;
        this.machineRowCapacity = machineRowCapacity;
        this.isNewBlock = isNew;
        this.deletedPlots = deletedPlots;
        this.fieldId = fieldId;
    }
	/**
	 * Gets the block id.
	 *
	 * @return the block id
	 */
	public int getBlockId() {
		return blockId;
	}
	
	/**
	 * Sets the block id.
	 *
	 * @param blockId the new block id
	 */
	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}
	
	/**
	 * Gets the rows in block.
	 *
	 * @return the rows in block
	 */
	public int getRowsInBlock() {
		return rowsInBlock;
	}
	
	/**
	 * Sets the rows in block.
	 *
	 * @param rowsInBlock the new rows in block
	 */
	public void setRowsInBlock(int rowsInBlock) {
		this.rowsInBlock = rowsInBlock;
	}
	
	/**
	 * Gets the ranges in block.
	 *
	 * @return the ranges in block
	 */
	public int getRangesInBlock() {
		return rangesInBlock;
	}
	
	/**
	 * Sets the ranges in block.
	 *
	 * @param rangesInBlock the new ranges in block
	 */
	public void setRangesInBlock(int rangesInBlock) {
		this.rangesInBlock = rangesInBlock;
	}
	
	/**
	 * Gets the number of rows in plot.
	 *
	 * @return the number of rows in plot
	 */
	public int getNumberOfRowsInPlot() {
		return numberOfRowsInPlot;
	}
	
	/**
	 * Sets the number of rows in plot.
	 *
	 * @param numberOfRowsInPlot the new number of rows in plot
	 */
	public void setNumberOfRowsInPlot(int numberOfRowsInPlot) {
		this.numberOfRowsInPlot = numberOfRowsInPlot;
	}

	public boolean isNewBlock() {
		return isNewBlock;
	}

	public void setNewBlock(boolean isNewBlock) {
		this.isNewBlock = isNewBlock;
	}

	/**
	 * @return the plantingOrder
	 */
	public int getPlantingOrder() {
		return plantingOrder;
	}

	/**
	 * @param plantingOrder the plantingOrder to set
	 */
	public void setPlantingOrder(int plantingOrder) {
		this.plantingOrder = plantingOrder;
	}

	/**
	 * @return the machineRowCapacity
	 */
	public int getMachineRowCapacity() {
		return machineRowCapacity;
	}

	/**
	 * @param machineRowCapacity the machineRowCapacity to set
	 */
	public void setMachineRowCapacity(int machineRowCapacity) {
		this.machineRowCapacity = machineRowCapacity;
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

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + blockId;
        result = prime * result + (isNewBlock ? 1231 : 1237);
        result = prime * result + machineRowCapacity;
        result = prime * result + numberOfRowsInPlot;
        result = prime * result + plantingOrder;
        result = prime * result + rangesInBlock;
        result = prime * result + rowsInBlock;
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
        FieldmapBlockInfo other = (FieldmapBlockInfo) obj;
        if (blockId != other.blockId)
            return false;
        if (isNewBlock != other.isNewBlock)
            return false;
        if (machineRowCapacity != other.machineRowCapacity)
            return false;
        if (numberOfRowsInPlot != other.numberOfRowsInPlot)
            return false;
        if (plantingOrder != other.plantingOrder)
            return false;
        if (rangesInBlock != other.rangesInBlock)
            return false;
        if (rowsInBlock != other.rowsInBlock)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FieldmapBlockInfo [blockId=");
        builder.append(blockId);
        builder.append(", rowsInBlock=");
        builder.append(rowsInBlock);
        builder.append(", rangesInBlock=");
        builder.append(rangesInBlock);
        builder.append(", numberOfRowsInPlot=");
        builder.append(numberOfRowsInPlot);
        builder.append(", plantingOrder=");
        builder.append(plantingOrder);
        builder.append(", machineRowCapacity=");
        builder.append(machineRowCapacity);
        builder.append(", isNew=");
        builder.append(isNewBlock);
        builder.append("]");
        return builder.toString();
    }
	
	
}
