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

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class StudyDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StudyDetails.class);

	private Integer id;

	private String programUUID;

	private String studyName;

	private String objective;

	private String startDate;

	private String endDate;

	private StudyType studyType;

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

	public StudyDetails() {

	}

	public StudyDetails(String studyName, String description, String objective, String startDate, String endDate,
			StudyType studyType, long parentFolderId, String trialDatasetName, String measurementDatasetName) {
		this.studyName = studyName;
		this.description = description;
		this.objective = objective;
		this.startDate = startDate;
		this.endDate = endDate;
		this.studyType = studyType;
		this.parentFolderId = parentFolderId;
		this.trialDatasetName = trialDatasetName;
		this.measurementDatasetName = measurementDatasetName;
		this.label = studyName;
	}

	// Used by getTrialObservationTable
	public StudyDetails(Integer id, String studyName, String description, String objective, String startDate, String endDate,
			StudyType studyType, String piName, String siteName, String piId, String siteId) {

		this(studyName, description, objective, startDate, endDate, studyType, piName, siteName);
		this.id = id;
		if (piId != null && NumberUtils.isNumber(piId)) {
			this.piId = Double.valueOf(piId).intValue();
		}
		if (siteId != null && NumberUtils.isNumber(siteId)) {
			this.siteId = Double.valueOf(siteId).intValue();
		}
	}

	public StudyDetails(Integer id, String studyName, String description, String objective, String startDate, String endDate,
			StudyType studyType, String piName, String siteName) {

		this(studyName, description, objective, startDate, endDate, studyType, piName, siteName);
		this.id = id;
	}

	public StudyDetails(String studyName, String description, String objective, String startDate, String endDate, StudyType studyType,
			String piName, String siteName) {
		this(studyName, description, objective, startDate, endDate, studyType, 0, null, null);
		this.siteName = siteName;
		this.setPiName(piName);
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStudyName() {
		return this.studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getObjective() {
		return this.objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartYear() {
		if (this.startDate != null) {
			try {
				return this.startDate.substring(0, 4);
			} catch (Exception e) {
				StudyDetails.LOG.error(e.getMessage(), e);
				return null;
			}
		}
		return null;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public StudyType getStudyType() {
		return this.studyType;
	}

	public void setStudyType(StudyType studyType) {
		this.studyType = studyType;
	}

	public long getParentFolderId() {
		return this.parentFolderId;
	}

	public void setParentFolderId(long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public boolean isNursery() {
		return this.studyType != null && this.studyType == StudyType.N;
	}

	public String getTrialDatasetName() {
		return this.trialDatasetName;
	}

	public void setTrialDatasetName(String trialDatasetName) {
		this.trialDatasetName = trialDatasetName;
	}

	public String getMeasurementDatasetName() {
		return this.measurementDatasetName;
	}

	public void setMeasurementDatasetName(String measurementDatasetName) {
		this.measurementDatasetName = measurementDatasetName;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @return the piName
	 */
	public String getPiName() {
		return this.piName;
	}

	/**
	 * @param piName the piName to set
	 */
	public void setPiName(String piName) {
		this.piName = piName;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	/**
	 * @return the piId
	 */
	public Integer getPiId() {
		return this.piId;
	}

	/**
	 * @param piId the piId to set
	 */
	public void setPiId(Integer piId) {
		this.piId = piId;
	}

	/**
	 * @return the siteId
	 */
	public Integer getSiteId() {
		return this.siteId;
	}

	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.endDate == null ? 0 : this.endDate.hashCode());
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		result = prime * result + (this.measurementDatasetName == null ? 0 : this.measurementDatasetName.hashCode());
		result = prime * result + (this.objective == null ? 0 : this.objective.hashCode());
		result = prime * result + (int) (this.parentFolderId ^ this.parentFolderId >>> 32);
		result = prime * result + (this.piName == null ? 0 : this.piName.hashCode());
		result = prime * result + (this.season == null ? 0 : this.season.hashCode());
		result = prime * result + (this.siteName == null ? 0 : this.siteName.hashCode());
		result = prime * result + (this.startDate == null ? 0 : this.startDate.hashCode());
		result = prime * result + (this.studyName == null ? 0 : this.studyName.hashCode());
		result = prime * result + (this.studyType == null ? 0 : this.studyType.hashCode());
		result = prime * result + (this.description == null ? 0 : this.description.hashCode());
		result = prime * result + (this.trialDatasetName == null ? 0 : this.trialDatasetName.hashCode());
		result = prime * result + (this.description == null ? 0 : this.description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		StudyDetails other = (StudyDetails) obj;
		if (this.endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!this.endDate.equals(other.endDate)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.measurementDatasetName == null) {
			if (other.measurementDatasetName != null) {
				return false;
			}
		} else if (!this.measurementDatasetName.equals(other.measurementDatasetName)) {
			return false;
		}
		if (this.objective == null) {
			if (other.objective != null) {
				return false;
			}
		} else if (!this.objective.equals(other.objective)) {
			return false;
		}
		if (this.parentFolderId != other.parentFolderId) {
			return false;
		}
		if (this.piName == null) {
			if (other.piName != null) {
				return false;
			}
		} else if (!this.piName.equals(other.piName)) {
			return false;
		}
		if (this.season != other.season) {
			return false;
		}
		if (this.siteName == null) {
			if (other.siteName != null) {
				return false;
			}
		} else if (!this.siteName.equals(other.siteName)) {
			return false;
		}
		if (this.startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!this.startDate.equals(other.startDate)) {
			return false;
		}
		if (this.studyName == null) {
			if (other.studyName != null) {
				return false;
			}
		} else if (!this.studyName.equals(other.studyName)) {
			return false;
		}
		if (this.studyType != other.studyType) {
			return false;
		}
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.trialDatasetName == null) {
			if (other.trialDatasetName != null) {
				return false;
			}
		} else if (!this.trialDatasetName.equals(other.trialDatasetName)) {
			return false;
		}
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudyDetails [");
		builder.append("id=");
		builder.append(this.id);
		builder.append(", studyName=");
		builder.append(this.studyName);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", objective=");
		builder.append(this.objective);
		builder.append(", startDate=");
		builder.append(this.startDate);
		builder.append(", endDate=");
		builder.append(this.endDate);
		builder.append(", studyType=");
		builder.append(this.studyType);
		builder.append(", parentFolderId=");
		builder.append(this.parentFolderId);
		builder.append(", trialDatasetName=");
		builder.append(this.trialDatasetName);
		builder.append(", measurementDatasetName=");
		builder.append(this.measurementDatasetName);
		builder.append(", siteName=");
		builder.append(this.siteName);
		builder.append(", piName=");
		builder.append(this.piName);
		builder.append(", season=");
		builder.append(this.season);
		builder.append(", piId=");
		builder.append(this.piId);
		builder.append(", siteId=");
		builder.append(this.siteId);
		builder.append(", description=");
		builder.append(this.description);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "StudyDetails: ");
		Debug.println(indent + 3, "Id: " + this.id);
		Debug.println(indent + 3, "Name: " + this.studyName);
		Debug.println(indent + 3, "Title: " + this.description);
		Debug.println(indent + 3, "Objective: " + this.objective);
		Debug.println(indent + 3, "Start Date: " + this.startDate);
		Debug.println(indent + 3, "End Date: " + this.endDate);
		Debug.println(indent + 3, "Study Type: " + this.studyType);
		Debug.println(indent + 3, "Parent Folder Id: " + this.parentFolderId);
		Debug.println(indent + 3, "Trial Dataset Name: " + this.trialDatasetName);
		Debug.println(indent + 3, "Measurement Dataset Name: " + this.measurementDatasetName);
		Debug.println(indent + 3, "Site Name: " + this.siteName);
		Debug.println(indent + 3, "Season: " + this.season);
		Debug.println(indent + 3, "PI Name: " + this.piName);
		Debug.println(indent + 3, "PI Id: " + this.piId);
		Debug.println(indent + 3, "Site Id: " + this.siteId);
		Debug.println(indent + 3, "Description: " + this.description);
	}

	public String getStudyUpdate() {
		return studyUpdate;
	}

	public void setStudyUpdate(final String studyUpdate) {
		this.studyUpdate = studyUpdate;
	}
}
