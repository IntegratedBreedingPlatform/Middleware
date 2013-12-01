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

// TODO: Auto-generated Javadoc
/**
 * Contains the field map label needed by the Field Map tool:
 * Entry Number, Germplasm Name, Rep, ExperimentId.
 *
 * @author Joyce Avestro
 */
public class FieldMapLabel implements Serializable{
    
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The experiment id. */
	private Integer experimentId;
    
    /** The entry number. */
    private Integer entryNumber;

    /** The germplasm name. */
    private String germplasmName;
    
    /** The rep. */
    private Integer rep; // null if isTrial = false

    /** The plot no. */
    private Integer plotNo;
    
    /** The column. */
    private Integer column;
    
    /** The range. */
    private Integer range;
    
    private String studyName;
    
    /**
     * Instantiates a new field map label.
     */
    public FieldMapLabel() {
    }

    /**
     * Instantiates a new field map label.
     *
     * @param experimentId the experiment id
     * @param entryNumber the entry number
     * @param germplasmName the germplasm name
     * @param rep the rep
     * @param plotNo the plot no
     */
    public FieldMapLabel(Integer experimentId, Integer entryNumber, 
    		String germplasmName, Integer rep, Integer plotNo) {
		this.experimentId = experimentId;
		this.entryNumber = entryNumber;
		this.germplasmName = germplasmName;
		this.rep = rep;
		this.plotNo = plotNo;
	}

	/**
	 * Gets the experiment id.
	 *
	 * @return the experiment id
	 */
	public Integer getExperimentId() {
		return experimentId;
	}

	/**
	 * Sets the experiment id.
	 *
	 * @param experimentId the new experiment id
	 */
	public void setExperimentId(Integer experimentId) {
		this.experimentId = experimentId;
	}

	/**
	 * Gets the entry number.
	 *
	 * @return the entry number
	 */
	public Integer getEntryNumber() {
		return entryNumber;
	}

	/**
	 * Sets the entry number.
	 *
	 * @param entryNumber the new entry number
	 */
	public void setEntryNumber(Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	/**
	 * Gets the germplasm name.
	 *
	 * @return the germplasm name
	 */
	public String getGermplasmName() {
		return germplasmName;
	}

	/**
	 * Sets the germplasm name.
	 *
	 * @param germplasmName the new germplasm name
	 */
	public void setGermplasmName(String germplasmName) {
		this.germplasmName = germplasmName;
	}

	/**
	 * Gets the rep.
	 *
	 * @return the rep
	 */
	public Integer getRep() {
		if (rep == null || rep == 0){
			rep = 1;
		}
		return rep;
	}

	/**
	 * Sets the rep.
	 *
	 * @param rep the new rep
	 */
	public void setRep(Integer rep) {
		this.rep = rep;
	}

	/**
	 * Gets the plot no.
	 *
	 * @return the plot no
	 */
	public Integer getPlotNo() {
		return plotNo;
	}

	/**
	 * Sets the plot no.
	 *
	 * @param plotNo the new plot no
	 */
	public void setPlotNo(Integer plotNo) {
		this.plotNo = plotNo;
	}

    /**
     * Gets the column.
     *
     * @return the column
     */
    public Integer getColumn() {
        return column;
    }
    
    /**
     * Sets the column.
     *
     * @param column the column to set
     */
    public void setColumn(Integer column) {
        this.column = column;
    }
    
    /**
     * Gets the range.
     *
     * @return the range
     */
    public Integer getRange() {
        return range;
    }

    /**
     * Sets the range.
     *
     * @param range the range to set
     */
    public void setRange(Integer range) {
        this.range = range;
    }

    
    /**
     * @return the studyName
     */
    public String getStudyName() {
        return studyName;
    }

    
    /**
     * @param studyName the studyName to set
     */
    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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

	/**
	 * Prints the.
	 *
	 * @param indent the indent
	 */
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
