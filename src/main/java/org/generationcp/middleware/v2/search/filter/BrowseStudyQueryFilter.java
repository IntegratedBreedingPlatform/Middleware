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
package org.generationcp.middleware.v2.search.filter;

import org.generationcp.middleware.manager.Season;

public class BrowseStudyQueryFilter implements StudyQueryFilter {

	private Integer startDate;
	
	private String name;
	
	private String country;
	
	private Season season;
	
	public Integer getStartDate() {
		return startDate;
	}

	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	@Override
	public String toString() {
		return "StudyQueryFilter [startDate=" + startDate + ", name=" + name
				+ ", country=" + country + ", season=" + season
				+ "]";
	}

}
