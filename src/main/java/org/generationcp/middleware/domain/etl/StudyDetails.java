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

import java.io.Serializable;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;

public class StudyDetails implements Serializable{
    
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String studyName;
	
	private String title;
	
	/*private String pmKey;*/
	
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
    
    //row count in both trial dataset and measurement dataset
    private Integer rowCount;

	public StudyDetails(){
		
	}
	
    public StudyDetails(String studyName, String title, String pmKey,
            String objective, String startDate, String endDate, StudyType studyType, 
            long parentFolderId, String trialDatasetName, String measurementDatasetName) {
        this.studyName = studyName;
        this.title = title;
        /*this.pmKey = pmKey;*/
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
            String piName, String siteName, String piId, String siteId) {

        this(studyName, title, objective, startDate, endDate, studyType, piName, siteName);
        this.id = id;
        if (piId != null && NumberUtils.isNumber(piId)) {
        	this.piId = Double.valueOf(piId).intValue();
        }
        if (siteId != null && NumberUtils.isNumber(siteId)) {
        	this.siteId = Double.valueOf(siteId).intValue();
        }
    }

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

	/*public String getPmKey() {
		return pmKey;
	}

	public void setPmKey(String pmKey) {
		this.pmKey = pmKey;
	}*/

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
	
	public String getStartYear(){
	    if (startDate != null){
	    	try {
	    		return startDate.substring(0,4);
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
	    return null;
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

    /**
	 * @return the piId
	 */
	public Integer getPiId() {
		return piId;
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
		return siteId;
	}

	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	/**
	 * @return the rowCount
	 */
	public Integer getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
	
	public boolean hasRows() {
		return rowCount != null && rowCount > 0;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((measurementDatasetName == null) ? 0 : measurementDatasetName.hashCode());
        result = prime * result + ((objective == null) ? 0 : objective.hashCode());
        result = prime * result + (int) (parentFolderId ^ (parentFolderId >>> 32));
        result = prime * result + ((piName == null) ? 0 : piName.hashCode());
        /*result = prime * result + ((pmKey == null) ? 0 : pmKey.hashCode());*/
        result = prime * result + ((season == null) ? 0 : season.hashCode());
        result = prime * result + ((siteName == null) ? 0 : siteName.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((studyName == null) ? 0 : studyName.hashCode());
        result = prime * result + ((studyType == null) ? 0 : studyType.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((trialDatasetName == null) ? 0 : trialDatasetName.hashCode());
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
        StudyDetails other = (StudyDetails) obj;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (measurementDatasetName == null) {
            if (other.measurementDatasetName != null)
                return false;
        } else if (!measurementDatasetName.equals(other.measurementDatasetName))
            return false;
        if (objective == null) {
            if (other.objective != null)
                return false;
        } else if (!objective.equals(other.objective))
            return false;
        if (parentFolderId != other.parentFolderId)
            return false;
        if (piName == null) {
            if (other.piName != null)
                return false;
        } else if (!piName.equals(other.piName))
            return false;
        /*if (pmKey == null) {
            if (other.pmKey != null)
                return false;
        } else if (!pmKey.equals(other.pmKey))
            return false;*/
        if (season != other.season)
            return false;
        if (siteName == null) {
            if (other.siteName != null)
                return false;
        } else if (!siteName.equals(other.siteName))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (studyName == null) {
            if (other.studyName != null)
                return false;
        } else if (!studyName.equals(other.studyName))
            return false;
        if (studyType != other.studyType)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (trialDatasetName == null) {
            if (other.trialDatasetName != null)
                return false;
        } else if (!trialDatasetName.equals(other.trialDatasetName))
            return false;
        return true;
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
        /*builder.append(", pmKey=");
        builder.append(pmKey);*/
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
        builder.append(", rowCount=");
        builder.append(rowCount);
        builder.append("]");
        return builder.toString();
    }

	public void print(int indent) {
		Debug.println(indent, "StudyDetails: ");
        Debug.println(indent + 3, "Id: " + id);
		Debug.println(indent + 3, "Name: " + studyName);
	    Debug.println(indent + 3, "Title: " + title);
	    /*Debug.println(indent + 3, "PM Key: " + pmKey);*/
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
	}

}
