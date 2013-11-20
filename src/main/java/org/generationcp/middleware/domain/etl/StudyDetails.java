/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.etl;

import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;

public class StudyDetails {
    
    private Integer id;
	
	private String studyName;
	
	private String title;
	
	private String pmKey;
	
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

	public StudyDetails(){
		
	}
	
    public StudyDetails(String studyName, String title, String pmKey,
            String objective, String startDate, String endDate, StudyType studyType, 
            long parentFolderId, String trialDatasetName, String measurementDatasetName) {
        this.studyName = studyName;
        this.title = title;
        this.pmKey = pmKey;
        this.objective = objective;
        this.startDate = startDate;
        this.endDate = endDate;
        this.studyType = studyType;
        this.parentFolderId = parentFolderId;
        this.trialDatasetName = trialDatasetName;
        this.measurementDatasetName = measurementDatasetName;
    }

    // Used by getStudyDetails
    public StudyDetails(Integer id, String studyName, String title,
            String objective, String startDate, String endDate, StudyType studyType, 
            String piName, String siteName) {

        this(studyName, title, objective, startDate, endDate, studyType, piName, siteName);
        this.id = id;
    }

    public StudyDetails(String studyName, String title,
            String objective, String startDate, String endDate, StudyType studyType, 
            String piName, String siteName) {
        this(studyName, title, null, objective, startDate, endDate, studyType, 
                0, null, null);
        this.siteName = siteName;
        this.setPiName(piName);
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPmKey() {
		return pmKey;
	}

	public void setPmKey(String pmKey) {
		this.pmKey = pmKey;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public StudyType getStudyType() {
		return studyType;
	}

	public void setStudyType(StudyType studyType) {
		this.studyType = studyType;
	}

	public long getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}
	
	public boolean isNursery() {
		if (this.studyType != null && this.studyType==StudyType.N) {
			return true;
		}
		return false;
	}
	
	public String getTrialDatasetName() {
		return trialDatasetName;
	}

	public void setTrialDatasetName(String trialDatasetName) {
		this.trialDatasetName = trialDatasetName;
	}

	public String getMeasurementDatasetName() {
		return measurementDatasetName;
	}

	public void setMeasurementDatasetName(String measurementDatasetName) {
		this.measurementDatasetName = measurementDatasetName;
	}

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
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
    public void setPiName(String piName) {
        this.piName = piName;
    }

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StudyDetails [");
        builder.append("id=");
        builder.append(id);
        builder.append(", studyName=");
        builder.append(studyName);
        builder.append(", title=");
        builder.append(title);
        builder.append(", pmKey=");
        builder.append(pmKey);
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
        builder.append("]");
        return builder.toString();
    }

	public void print(int indent) {
		Debug.println(indent, "StudyDetails: ");
        Debug.println(indent + 3, "Id: " + id);
		Debug.println(indent + 3, "Name: " + studyName);
	    Debug.println(indent + 3, "Title: " + title);
	    Debug.println(indent + 3, "PM Key: " + pmKey);
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
	}

}
