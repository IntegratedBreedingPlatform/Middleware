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

// TODO: Auto-generated Javadoc
/**
 * The Class FieldmapBlockInfo.
 */
public class FieldmapBlockInfo implements Serializable{
	
	/** The block id. */
	private int blockId;
	
	/** The rows in block. */
	private int rowsInBlock;
	
	/** The ranges in block. */
	private int rangesInBlock;
	
	/** The number of rows in plot. */
	private int numberOfRowsInPlot;
	
	/** planting order */
	private String plantingOrder;
	
	/** machine row capacity */
	private int machineRowCapacity;
	
	/** The is new. */
	private boolean isNew;
	
	
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
		super();
		this.blockId = blockId;
		this.rowsInBlock = rowsInBlock;
		this.rangesInBlock = rangesInBlock;
		this.numberOfRowsInPlot = numberOfRowsInPlot;
		this.isNew = isNew;
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

	/**
	 * Checks if is new.
	 *
	 * @return true, if is new
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Sets the new.
	 *
	 * @param isNew the new new
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * @return the plantingOrder
	 */
	public String getPlantingOrder() {
		return plantingOrder;
	}

	/**
	 * @param plantingOrder the plantingOrder to set
	 */
	public void setPlantingOrder(String plantingOrder) {
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
	
	
}
