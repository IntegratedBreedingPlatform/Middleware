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
import org.generationcp.middleware.util.Debug;

public class StudyDetails {
	
	private String studyName;
	
	private String title;
	
	private String pmKey;
	
	private String objective;
	
	private String startDate;
	
	private String endDate;
	
	private StudyType studyType;
	
	private long parentFolderId;

	public StudyDetails(){
		
	}
	
	public StudyDetails(String studyName, String title, String pmKey,
			String objective, String startDate, String endDate, StudyType studyType, long parentFolderId) {
		this.studyName = studyName;
		this.title = title;
		this.pmKey = pmKey;
		this.objective = objective;
		this.startDate = startDate;
		this.endDate = endDate;
		this.studyType = studyType;
		this.parentFolderId = parentFolderId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudyDetails [studyName=");
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
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "StudyDetails: ");
		Debug.println(indent + 3, "Name: " + studyName);
	    Debug.println(indent + 3, "Title: " + title);
	    Debug.println(indent + 3, "PM Key: " + pmKey);
	    Debug.println(indent + 3, "Objective: " + objective);
	    Debug.println(indent + 3, "Start Date: " + startDate);
	    Debug.println(indent + 3, "End Date: " + endDate);
		Debug.println(indent + 3, "Study Type: " + studyType);
		Debug.println(indent + 3, "Parent Folder Id: " + parentFolderId);
	}

}
