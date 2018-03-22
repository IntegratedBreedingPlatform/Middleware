/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.workbench;

import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.util.Debug;

public class StudyNode implements Comparable<StudyNode> {

	private Integer id;

	private String name;

	private String description;

	private String startDate;

	private StudyTypeDto studyType;

	private Season season;

	public StudyNode() {

	}

	public StudyNode(final String name, final String description, final String startDate, final StudyTypeDto studyType, final Season season) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.studyType = studyType;
		this.season = season;
	}

	public StudyNode(final Integer id, final String name, final String description, final String startDate, final StudyTypeDto studyType, final Season season) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.studyType = studyType;
		this.season = season;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String studyName) {
		this.name = studyName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	public String getStartDate() {
		 return this.startDate;
	 }

	 public void setStartDate(final String startDate) {
		 this.startDate = startDate;
	 }

	public String getStartYear() {
		if (this.startDate != null) {
			return this.startDate.substring(0, 4);
		}
		return null;
	 }

	 public StudyTypeDto getStudyType() {
		 return this.studyType;
	 }

	 public void setStudyType(final StudyTypeDto studyType) {
		 this.studyType = studyType;
	 }

	 public Season getSeason() {
		 return this.season;
	 }

	 public void setSeason(final Season season) {
		 this.season = season;
	 }

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("StudyNode [");
		builder.append("id=");
		builder.append(this.id);
		builder.append(", studyName=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", startDate=");
		builder.append(this.startDate);
		builder.append(", studyType=");
		builder.append(this.studyType);
		builder.append(", season=");
		builder.append(this.season);
		builder.append(", startYear=");
		builder.append(this.getStartYear());
		builder.append("]");
		return builder.toString();
	}

	 public void print(final int indent) {
		 Debug.println(indent, "StudyDetails: ");
		Debug.println(indent + 3, "Id: " + this.id);
		 Debug.println(indent + 3, "Name: " + this.name);
		Debug.println(indent + 3, "Start Date: " + this.startDate);
		 Debug.println(indent + 3, "Study Type: " + this.studyType);
		Debug.println(indent + 3, "Season: " + this.season);
		Debug.println(indent + 3, "Start Year: " + this.getStartYear());
	 }

	@Override
	public int compareTo(final StudyNode node2) {
		int c = node2.getStartYear().compareTo(this.getStartYear()); // descending by year
		if (c == 0) {
			c = this.getSeason().getSortOrder().compareTo(node2.getSeason().getSortOrder()); // season in this order: dry, wet, general
		}
		if (c == 0) {
			c = this.getStudyType().compareTo(node2.getStudyType()); // nursery then trial
		}
		if (c == 0) {
			c = this.getName().compareTo(node2.getName()); // sort by study name
		}
		return c;
	}

}
