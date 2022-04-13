/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.etl;

import java.io.Serializable;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StudyDetails.class);

	private Integer id;

	private String programUUID;

	private String studyName;

	private String objective;

	private String startDate;

	private String endDate;

	private StudyTypeDto studyType;

	private long parentFolderId;

	private String trialDatasetName;

	private String measurementDatasetName;

	private String siteName;

	private String piName;

	private Season season;

	private Integer piId;

	private Integer siteId;

	// used to display "Add to New Study"
	private String label;

	private String description;

	private String studyUpdate;

	private String createdBy;
	
	private Boolean isLocked;

	private Integer generationLevel;

	public StudyDetails() {

	}

	public StudyDetails(final String studyName, final String description, final String objective, final String startDate,
			final String endDate, final StudyTypeDto studyType, final long parentFolderId, final String trialDatasetName,
			final String measurementDatasetName, final String studyUpdate, final String createdBy, final Boolean isLocked) {
		this.studyName = studyName;
		this.description = description;
		this.objective = objective;
		this.startDate = startDate;
		this.endDate = endDate;
		this.studyType = studyType;
		this.parentFolderId = parentFolderId;
		this.trialDatasetName = trialDatasetName;
		this.measurementDatasetName = measurementDatasetName;
		label = studyName;
		this.studyUpdate = studyUpdate;
		this.createdBy = createdBy;
		this.isLocked = isLocked;
	}

	// Used by getTrialObservationTable
	public StudyDetails(final Integer id, final String studyName, final String description, final String objective, final String startDate,
			final String endDate, final StudyTypeDto studyType, final String piName, final String siteName, final String piId,
			final String siteId, final String studyUpdate, final String createdBy, final Boolean isLocked, final Integer generationLevel) {

		this(studyName, description, objective, startDate, endDate, studyType, piName, siteName, studyUpdate, createdBy, isLocked);
		this.id = id;
		if ((piId != null) && NumberUtils.isNumber(piId)) {
			this.piId = Double.valueOf(piId).intValue();
		}
		if (siteId != null && NumberUtils.isNumber(siteId)) {
			this.siteId = Double.valueOf(siteId).intValue();
		}
		this.generationLevel = generationLevel;
	}

	public StudyDetails(final Integer id, final String studyName, final String description, final String objective, final String startDate,
			final String endDate, final StudyTypeDto studyType, final String piName, final String siteName, final String studyUpdate,
			final String createdBy, final Boolean isLocked) {

		this(studyName, description, objective, startDate, endDate, studyType, piName, siteName, studyUpdate, createdBy, isLocked);
		this.id = id;
	}

	public StudyDetails(final String studyName, final String description, final String objective, final String startDate,
			final String endDate, final StudyTypeDto studyType, final String piName, final String siteName, final String studyUpdate,
			final String createdBy, final Boolean isLocked) {
		this(studyName, description, objective, startDate, endDate, studyType, 0, null, null, studyUpdate, createdBy, isLocked);
		this.siteName = siteName;
		setPiName(piName);
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(final String objective) {
		this.objective = objective;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getStartYear() {
		if (startDate != null) {
			try {
				return startDate.substring(0, 4);
			} catch (final Exception e) {
				StudyDetails.LOG.error(e.getMessage(), e);
				return null;
			}
		}
		return null;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public StudyTypeDto getStudyType() {
		return studyType;
	}

	public void setStudyType(final StudyTypeDto studyType) {
		this.studyType = studyType;
	}

	public long getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(final long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public String getTrialDatasetName() {
		return trialDatasetName;
	}

	public void setTrialDatasetName(final String trialDatasetName) {
		this.trialDatasetName = trialDatasetName;
	}

	public String getMeasurementDatasetName() {
		return measurementDatasetName;
	}

	public void setMeasurementDatasetName(final String measurementDatasetName) {
		this.measurementDatasetName = measurementDatasetName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @return the piName
	 */
	public String getPiName() {
		return piName;
	}

	/**
	 * @param piName the piName to set
	 */
	public void setPiName(final String piName) {
		this.piName = piName;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

	/**
	 * @return the piId
	 */
	public Integer getPiId() {
		return piId;
	}

	/**
	 * @param piId the piId to set
	 */
	public void setPiId(final Integer piId) {
		this.piId = piId;
	}

	/**
	 * @return the siteId
	 */
	public Integer getSiteId() {
		return siteId;
	}

	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(final Integer siteId) {
		this.siteId = siteId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Integer getGenerationLevel() {
		return generationLevel;
	}

	public void setGenerationLevel(final Integer generationLevel) {
		this.generationLevel = generationLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.endDate == null) ? 0 : endDate.hashCode());
		result = (prime * result) + ((this.id == null) ? 0 : id.hashCode());
		result = (prime * result) + ((this.measurementDatasetName == null) ? 0 : measurementDatasetName.hashCode());
		result = (prime * result) + ((this.objective == null) ? 0 : objective.hashCode());
		result = (prime * result) + (int) (parentFolderId ^ (this.parentFolderId >>> 32));
		result = (prime * result) + ((this.piName == null) ? 0 : piName.hashCode());
		result = (prime * result) + ((this.season == null) ? 0 : season.hashCode());
		result = (prime * result) + ((this.siteName == null) ? 0 : siteName.hashCode());
		result = (prime * result) + ((this.startDate == null) ? 0 : startDate.hashCode());
		result = (prime * result) + ((this.studyName == null) ? 0 : studyName.hashCode());
		result = (prime * result) + ((this.studyType == null) ? 0 : studyType.hashCode());
		result = (prime * result) + ((this.description == null) ? 0 : description.hashCode());
		result = (prime * result) + ((this.trialDatasetName == null) ? 0 : trialDatasetName.hashCode());
		result = (prime * result) + ((this.description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final StudyDetails other = (StudyDetails) obj;
		if (endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!endDate.equals(other.endDate)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (measurementDatasetName == null) {
			if (other.measurementDatasetName != null) {
				return false;
			}
		} else if (!measurementDatasetName.equals(other.measurementDatasetName)) {
			return false;
		}
		if (objective == null) {
			if (other.objective != null) {
				return false;
			}
		} else if (!objective.equals(other.objective)) {
			return false;
		}
		if (parentFolderId != other.parentFolderId) {
			return false;
		}
		if (piName == null) {
			if (other.piName != null) {
				return false;
			}
		} else if (!piName.equals(other.piName)) {
			return false;
		}
		if (season != other.season) {
			return false;
		}
		if (siteName == null) {
			if (other.siteName != null) {
				return false;
			}
		} else if (!siteName.equals(other.siteName)) {
			return false;
		}
		if (startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!startDate.equals(other.startDate)) {
			return false;
		}
		if (studyName == null) {
			if (other.studyName != null) {
				return false;
			}
		} else if (!studyName.equals(other.studyName)) {
			return false;
		}
		if (studyType != other.studyType) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (trialDatasetName == null) {
			if (other.trialDatasetName != null) {
				return false;
			}
		} else if (!trialDatasetName.equals(other.trialDatasetName)) {
			return false;
		}
		if (description == null) {
		} else if (!description.equals(other.description)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("StudyDetails [");
		builder.append("id=");
		builder.append(id);
		builder.append(", studyName=");
		builder.append(studyName);
		builder.append(", description=");
		builder.append(description);
		builder.append(", objective=");
		builder.append(objective);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", studyType=");
		builder.append(studyType);
		builder.append(", parentFolderId=");
		builder.append(parentFolderId);
		builder.append(", trialDatasetName=");
		builder.append(trialDatasetName);
		builder.append(", measurementDatasetName=");
		builder.append(measurementDatasetName);
		builder.append(", siteName=");
		builder.append(siteName);
		builder.append(", piName=");
		builder.append(piName);
		builder.append(", season=");
		builder.append(season);
		builder.append(", piId=");
		builder.append(piId);
		builder.append(", siteId=");
		builder.append(siteId);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

	public void print(final int indent) {
		Debug.println(indent, "StudyDetails: ");
		Debug.println(indent + 3, "Id: " + id);
		Debug.println(indent + 3, "Name: " + studyName);
		Debug.println(indent + 3, "Title: " + description);
		Debug.println(indent + 3, "Objective: " + objective);
		Debug.println(indent + 3, "Start Date: " + startDate);
		Debug.println(indent + 3, "End Date: " + endDate);
		Debug.println(indent + 3, "Study Type: " + studyType);
		Debug.println(indent + 3, "Parent Folder Id: " + parentFolderId);
		Debug.println(indent + 3, "Trial Dataset Name: " + trialDatasetName);
		Debug.println(indent + 3, "Measurement Dataset Name: " + measurementDatasetName);
		Debug.println(indent + 3, "Site Name: " + siteName);
		Debug.println(indent + 3, "Season: " + season);
		Debug.println(indent + 3, "PI Name: " + piName);
		Debug.println(indent + 3, "PI Id: " + piId);
		Debug.println(indent + 3, "Site Id: " + siteId);
		Debug.println(indent + 3, "Description: " + description);
	}

	public String getStudyUpdate() {
		return this.studyUpdate;
	}

	public void setStudyUpdate(final String studyUpdate) {
		this.studyUpdate = studyUpdate;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	
	public Boolean getIsLocked() {
		return isLocked;
	}

	
	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}
}
