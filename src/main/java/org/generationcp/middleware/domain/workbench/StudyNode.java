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
package org.generationcp.middleware.domain.workbench;

import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;

public class StudyNode implements Comparable<StudyNode> {
    
    private Integer id;
	
    private String name;
    
    private String description;
    
	private String startDate;
	
	private StudyType studyType;
	
    private Season season;

	public StudyNode(){
		
	}
	
    public StudyNode(String name, String description, String startDate, StudyType studyType, Season season) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.studyType = studyType;
        this.season = season;
    }


    public StudyNode(Integer id, String name, String description, String startDate, StudyType studyType, Season season) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.studyType = studyType;
        this.season = season;
    }
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
		return name;
	}

	public void setName(String studyName) {
		this.name = studyName;
	}

	/**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getStartYear(){
	    if (startDate != null){
	        return startDate.substring(0,4);
	    }
	    return null;
	}

	public StudyType getStudyType() {
		return studyType;
	}

	public void setStudyType(StudyType studyType) {
		this.studyType = studyType;
	}

	public boolean isNursery() {
		if (this.studyType != null && this.studyType==StudyType.N) {
			return true;
		}
		return false;
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
        builder.append("StudyNode [");
        builder.append("id=");
        builder.append(id);
        builder.append(", studyName=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", startDate=");
        builder.append(startDate);
        builder.append(", studyType=");
        builder.append(studyType);
        builder.append(", season=");
        builder.append(season);
        builder.append(", startYear=");
        builder.append(getStartYear());
        builder.append("]");
        return builder.toString();
    }

	public void print(int indent) {
		Debug.println(indent, "StudyDetails: ");
        Debug.println(indent + 3, "Id: " + id);
		Debug.println(indent + 3, "Name: " + name);
	    Debug.println(indent + 3, "Start Date: " + startDate);
		Debug.println(indent + 3, "Study Type: " + studyType);
        Debug.println(indent + 3, "Season: " + season);
        Debug.println(indent + 3, "Start Year: " + getStartYear());
	}
	
    @Override
    public int compareTo(StudyNode node2) {
        int c = node2.getStartYear().compareTo(this.getStartYear()); // descending by year
        if (c == 0){
            c = this.getSeason().getSortOrder().compareTo(node2.getSeason().getSortOrder()); // season in this order: dry, wet, general
        }
        if (c == 0){
            c = this.getStudyType().compareTo(node2.getStudyType()); // nursery then trial
        }
        if (c == 0){
            c = this.getName().compareTo(node2.getName()); // sort by study name
        }
        return c;
    }

}
