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

import org.generationcp.middleware.util.Debug;

/**
 * 
 * Contains the field map label needed by the Field Map tool: 
 * Entry Number, Germplasm Name, Rep, ExperimentId
 * 
 * @author Joyce Avestro
 *
 */
public class FieldMapLabel implements Serializable{
    
	private static final long serialVersionUID = 1L;

	private Integer experimentId;
    
    private Integer entryNumber;

    private String germplasmName;
    
    private Integer rep; // null if isTrial = false

    private Integer plotNo;
    
    private Integer column;
    
    private Integer range;
    
    public FieldMapLabel() {
    }

    public FieldMapLabel(Integer experimentId, Integer entryNumber, 
    		String germplasmName, Integer rep, Integer plotNo) {
		this.experimentId = experimentId;
		this.entryNumber = entryNumber;
		this.germplasmName = germplasmName;
		this.rep = rep;
		this.plotNo = plotNo;
	}

	public Integer getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(Integer experimentId) {
		this.experimentId = experimentId;
	}

	public Integer getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getGermplasmName() {
		return germplasmName;
	}

	public void setGermplasmName(String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public Integer getRep() {
		if (rep == null || rep == 0){
			rep = 1;
		}
		return rep;
	}

	public void setRep(Integer rep) {
		this.rep = rep;
	}

	public Integer getPlotNo() {
		return plotNo;
	}

	public void setPlotNo(Integer plotNo) {
		this.plotNo = plotNo;
	}

    /**
     * @return the column
     */
    public Integer getColumn() {
        return column;
    }
    
    /**
     * @param column the column to set
     */
    public void setColumn(Integer column) {
        this.column = column;
    }
    
    /**
     * @return the range
     */
    public Integer getRange() {
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(Integer range) {
        this.range = range;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FieldMapLabel [experimentId=");
		builder.append(experimentId);
		builder.append(", entryNumber=");
		builder.append(entryNumber);
		builder.append(", germplasmName=");
		builder.append(germplasmName);
		builder.append(", rep=");
		builder.append(rep);
		builder.append(", plotNo=");
		builder.append(plotNo);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
        Debug.println(indent, "FieldMapLabel: " );
        indent = indent + 3;
        Debug.println(indent, "Experiment Id = " + experimentId);
        Debug.println(indent, "Entry Number = " + entryNumber);
        Debug.println(indent, "Germplasm Name Id = " + germplasmName);
        Debug.println(indent, "Rep = " + rep);
        Debug.println(indent, "Plot Number = " + plotNo);
    }
}
