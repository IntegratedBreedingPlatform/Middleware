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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the field map label needed by the Field Map tool: Entry Number, Germplasm Name, Rep, ExperimentId.
 *
 * @author Joyce Avestro
 */
public class FieldMapLabel implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The experiment id. */
	private Integer experimentId;

	/** The plot id. */
	private String obsUnitId;

	/** The entry number. */
	private Integer entryNumber;

	/** The germplasm name. */
	private String germplasmName;

	/** The rep. */
	private Integer rep;
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

	private Map<Integer, String> userFields;

	/* Inventory related columns */
	private Double inventoryAmount;
	private Integer lotId;
	private String scaleName;

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
	public FieldMapLabel(final Integer experimentId, final Integer entryNumber, final String germplasmName, final Integer rep, final Integer plotNo) {
		this(experimentId, entryNumber, germplasmName, rep, plotNo, null);
		this.userFields = new HashMap<>();
	}

	public FieldMapLabel(final Integer experimentId, final Integer entryNumber, final String germplasmName, final Integer rep, final Integer plotNo,
			final Map<Integer, String> userFields) {
		this.experimentId = experimentId;
		this.entryNumber = entryNumber;
		this.germplasmName = germplasmName;
		this.rep = rep;
		this.plotNo = plotNo;
		this.userFields = userFields;
	}

	/**
	 * Gets the experiment id.
	 *
	 * @return the experiment id
	 */
	public Integer getExperimentId() {
		return this.experimentId;
	}

	/**
	 * Sets the experiment id.
	 *
	 * @param experimentId the new experiment id
	 */
	public void setExperimentId(final Integer experimentId) {
		this.experimentId = experimentId;
	}

	/**
	 * Gets the entry number.
	 *
	 * @return the entry number
	 */
	public Integer getEntryNumber() {
		return this.entryNumber;
	}

	/**
	 * Sets the entry number.
	 *
	 * @param entryNumber the new entry number
	 */
	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	/**
	 * Gets the germplasm name.
	 *
	 * @return the germplasm name
	 */
	public String getGermplasmName() {
		return this.germplasmName;
	}

	/**
	 * Sets the germplasm name.
	 *
	 * @param germplasmName the new germplasm name
	 */
	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	/**
	 * Gets the rep.
	 *
	 * @return the rep
	 */
	public Integer getRep() {
		if (this.rep == null || this.rep == 0) {
			this.rep = 1;
		}
		return this.rep;
	}

	/**
	 * Sets the rep.
	 *
	 * @param rep the new rep
	 */
	public void setRep(final Integer rep) {
		this.rep = rep;
	}

	/**
	 * Gets the plot no.
	 *
	 * @return the plot no
	 */
	public Integer getPlotNo() {
		return this.plotNo;
	}

	/**
	 * Sets the plot no.
	 *
	 * @param plotNo the new plot no
	 */
	public void setPlotNo(final Integer plotNo) {
		this.plotNo = plotNo;
	}

	/**
	 * Gets the column.
	 *
	 * @return the column
	 */
	public Integer getColumn() {
		return this.column;
	}

	/**
	 * Sets the column.
	 *
	 * @param column the column to set
	 */
	public void setColumn(final Integer column) {
		this.column = column;
	}

	/**
	 * Gets the range.
	 *
	 * @return the range
	 */
	public Integer getRange() {
		return this.range;
	}

	/**
	 * Sets the range.
	 *
	 * @param range the range to set
	 */
	public void setRange(final Integer range) {
		this.range = range;
	}

	/**
	 * Gets the study name.
	 *
	 * @return the studyName
	 */
	public String getStudyName() {
		return this.studyName;
	}

	/**
	 * Sets the study name.
	 *
	 * @param studyName the studyName to set
	 */
	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	/**
	 * Gets the dataset id.
	 *
	 * @return the datasetId
	 */
	public Integer getDatasetId() {
		return this.datasetId;
	}

	/**
	 * Sets the dataset id.
	 *
	 * @param datasetId the datasetId to set
	 */
	public void setDatasetId(final Integer datasetId) {
		this.datasetId = datasetId;
	}

	/**
	 * Gets the geolocation id.
	 *
	 * @return the geolocationId
	 */
	public Integer getGeolocationId() {
		return this.geolocationId;
	}

	/**
	 * Sets the geolocation id.
	 *
	 * @param geolocationId the geolocationId to set
	 */
	public void setGeolocationId(final Integer geolocationId) {
		this.geolocationId = geolocationId;
	}

	/**
	 * Gets the site name.
	 *
	 * @return the siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * Gets the gid.
	 *
	 * @return the gid
	 */
	public Integer getGid() {
		return this.gid;
	}

	/**
	 * Sets the gid.
	 *
	 * @param gid the new gid
	 */
	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	/**
	 * Gets the season.
	 *
	 * @return the season
	 */
	public Season getSeason() {
		return this.season;
	}

	/**
	 * Sets the season.
	 *
	 * @param season the new season
	 */
	public void setSeason(final Season season) {
		this.season = season;
	}

	public void setSeason(final String seasonStr) {
		if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_DRY.getId()) {
			this.season = Season.DRY;
		} else if (seasonStr != null && Integer.parseInt(seasonStr.trim()) == TermId.SEASON_WET.getId()) {
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
		return this.startYear;
	}

	/**
	 * Sets the start year.
	 *
	 * @param startYear the new start year
	 */
	public void setStartYear(final String startYear) {
		this.startYear = startYear;
	}

	/**
	 * Sets the site name.
	 *
	 * @param siteName the siteName to set
	 */
	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}

	public String getPedigree() {
		return this.pedigree;
	}

	public void setPedigree(final String pedigree) {
		this.pedigree = pedigree;
	}

	public String getPlotCoordinate() {
		if (this.getColumn() != null && this.getRange() != null) {
			return "Col " + this.getColumn() + " Range " + this.getRange();
		}
		return "";
	}

	public void setPlotCoordinate(final String plotCoordinate) {
		this.plotCoordinate = plotCoordinate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FieldMapLabel [experimentId=");
		builder.append(this.experimentId);
		builder.append(", entryNumber=");
		builder.append(this.entryNumber);
		builder.append(", germplasmName=");
		builder.append(this.germplasmName);
		builder.append(", rep=");
		builder.append(this.rep);
		builder.append(", plotNo=");
		builder.append(this.plotNo);
		builder.append(", column=");
		builder.append(this.column);
		builder.append(", range=");
		builder.append(this.range);
		builder.append(", studyName=");
		builder.append(this.studyName);
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", geolocationId=");
		builder.append(this.geolocationId);
		builder.append(", siteName=");
		builder.append(this.siteName);
		builder.append(", gid=");
		builder.append(this.gid);
		builder.append(", season=");
		builder.append(this.season);
		builder.append(", startYear=");
		builder.append(this.startYear);
		builder.append(", pedigree=");
		builder.append(this.pedigree);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Prints the information on FieldMapLabel.
	 *
	 * @param indent the indent
	 */
	public void print(int indent) {
		Debug.println(indent, "FieldMapLabel: ");
		indent = indent + 3;
		Debug.println(indent, "Experiment Id = " + this.experimentId);
		Debug.println(indent, "Entry Number = " + this.entryNumber);
		Debug.println(indent, "Germplasm Name = " + this.germplasmName);
		Debug.println(indent, "Rep = " + this.rep);
		Debug.println(indent, "Plot Number = " + this.plotNo);
		Debug.println(indent, "Column = " + this.column);
		Debug.println(indent, "Range = " + this.range);
		Debug.println(indent, "Study Name = " + this.studyName);
		Debug.println(indent, "Dataset ID = " + this.datasetId);
		Debug.println(indent, "Geolocation ID = " + this.geolocationId);
		Debug.println(indent, "Site Name = " + this.siteName);
		Debug.println(indent, "GID = " + this.gid);
		Debug.println(indent, "Season = " + this.season);
		Debug.println(indent, "Start Year = " + this.startYear);
		Debug.println(indent, "Pedigree = " + this.pedigree);
	}

	public Integer getBlockNo() {
		return this.blockNo;
	}

	public void setBlockNo(final Integer blockNo) {
		this.blockNo = blockNo;
	}

	public Map<Integer, String> getUserFields() {
		return this.userFields;
	}

	public void setUserFields(final Map<Integer, String> userFields) {
		this.userFields = userFields;
	}

	public Double getInventoryAmount() {
		return this.inventoryAmount;
	}

	public void setInventoryAmount(final Double inventoryAmount) {
		this.inventoryAmount = inventoryAmount;
	}

	public Integer getLotId() {
		return this.lotId;
	}

	public void setLotId(final Integer lotId) {
		this.lotId = lotId;
	}

	public String getScaleName() {
		return this.scaleName;
	}

	public void setScaleName(final String scaleName) {
		this.scaleName = scaleName;
	}

	public String getObsUnitId() {
		return this.obsUnitId;
	}

	public FieldMapLabel setObsUnitId(final String obsUnitId) {
		this.obsUnitId = obsUnitId;
		return this;
	}
}
