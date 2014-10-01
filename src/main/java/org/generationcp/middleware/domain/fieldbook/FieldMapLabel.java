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

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;

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
    private Integer blockNo;

    /** The plot no. */
    private Integer plotNo;
    
    /** The plot coordinate. */
    private String plotCoordinate;
    
    /** The column. */
    private Integer column;
    
    /** The range. */
    private Integer range;
    
    /** The study name. */
    private String studyName;
    
    /** The dataset id. */
    private Integer datasetId;
    
    /** The geolocation id. */
    private Integer geolocationId;
    
    /** The site name. */
    private String siteName;
    
    /** The gid. */
    private Integer gid;

    /** The season. */
    private Season season;
    
    /** The start year. */
    private String startYear;

    /* The direct ancestor (parent) of the gid */
    private String pedigree;

    
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
     * Gets the study name.
     *
     * @return the studyName
     */
    public String getStudyName() {
        return studyName;
    }

    
    /**
     * Sets the study name.
     *
     * @param studyName the studyName to set
     */
    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    
    /**
     * Gets the dataset id.
     *
     * @return the datasetId
     */
    public Integer getDatasetId() {
        return datasetId;
    }

    
    /**
     * Sets the dataset id.
     *
     * @param datasetId the datasetId to set
     */
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    
    /**
     * Gets the geolocation id.
     *
     * @return the geolocationId
     */
    public Integer getGeolocationId() {
        return geolocationId;
    }

    
    /**
     * Sets the geolocation id.
     *
     * @param geolocationId the geolocationId to set
     */
    public void setGeolocationId(Integer geolocationId) {
        this.geolocationId = geolocationId;
    }

    
    /**
     * Gets the site name.
     *
     * @return the siteName
     */
    public String getSiteName() {
        return siteName;
    }

    
    
    /**
     * Gets the gid.
     *
     * @return the gid
     */
    public Integer getGid() {
        return gid;
    }

    
    /**
     * Sets the gid.
     *
     * @param gid the new gid
     */
    public void setGid(Integer gid) {
        this.gid = gid;
    }

    
    /**
     * Gets the season.
     *
     * @return the season
     */
    public Season getSeason() {
        return season;
    }

    
    /**
     * Sets the season.
     *
     * @param season the new season
     */
    public void setSeason(Season season) {
        this.season = season;
    }

    public void setSeason(String seasonStr) {
        if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_DRY.getId()){
            this.season = Season.DRY;
        } else if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_WET.getId()){
            this.season = Season.WET;
        } else {
            this.season = Season.GENERAL;
        }
    }

    
    /**
     * Gets the start year.
     *
     * @return the start year
     */
    public String getStartYear() {
        return startYear;
    }

    
    /**
     * Sets the start year.
     *
     * @param startYear the new start year
     */
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }
    
    /**
     * Sets the site name.
     *
     * @param siteName the siteName to set
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getPedigree() {
        return pedigree;
    }

    public void setPedigree(String pedigree) {
        this.pedigree = pedigree;
    }
    
    

    public String getPlotCoordinate() {
    	if (getColumn() != null && getRange() != null) {
    		return "Col " + getColumn() + " Range " + getRange();
    	} 
    	return "";
	}

	public void setPlotCoordinate(String plotCoordinate) {
		this.plotCoordinate = plotCoordinate;
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
        builder.append(", column=");
        builder.append(column);
        builder.append(", range=");
        builder.append(range);
        builder.append(", studyName=");
        builder.append(studyName);
        builder.append(", datasetId=");
        builder.append(datasetId);
        builder.append(", geolocationId=");
        builder.append(geolocationId);
        builder.append(", siteName=");
        builder.append(siteName);
        builder.append(", gid=");
        builder.append(gid);
        builder.append(", season=");
        builder.append(season);
        builder.append(", startYear=");
        builder.append(startYear);
        builder.append(", pedigree=");
        builder.append(pedigree);
        builder.append("]");
        return builder.toString();
    }

	/**
	 * Prints the information on FieldMapLabel.
	 *
	 * @param indent the indent
	 */
	public void print(int indent) {
        Debug.println(indent, "FieldMapLabel: " );
        indent = indent + 3;
        Debug.println(indent, "Experiment Id = " + experimentId);
        Debug.println(indent, "Entry Number = " + entryNumber);
        Debug.println(indent, "Germplasm Name = " + germplasmName);
        Debug.println(indent, "Rep = " + rep);
        Debug.println(indent, "Plot Number = " + plotNo);
        Debug.println(indent, "Column = " + column);
        Debug.println(indent, "Range = " + range);
        Debug.println(indent, "Study Name = " + studyName);
        Debug.println(indent, "Dataset ID = " + datasetId);
        Debug.println(indent, "Geolocation ID = " + geolocationId);
        Debug.println(indent, "Site Name = " + siteName);
        Debug.println(indent, "GID = " + gid);
        Debug.println(indent, "Season = " + season);
        Debug.println(indent, "Start Year = " + startYear);
        Debug.println(indent, "Pedigree = " + pedigree);
    }

	public Integer getBlockNo() {
		return blockNo;
	}

	public void setBlockNo(Integer blockNo) {
		this.blockNo = blockNo;
	}

}
