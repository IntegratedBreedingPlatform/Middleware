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

package org.generationcp.middleware.domain.search.filter;

import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.manager.Season;

public class BrowseStudyQueryFilter implements StudyQueryFilter {

	private Integer startDate;

	private String name;

	private String country;

	private Season season;

	private StudySearchMatchingOption studySearchMatchingOption;

	public Integer getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public StudySearchMatchingOption getStudySearchMatchingOption() {
		return studySearchMatchingOption;
	}

	public void setStudySearchMatchingOption(final StudySearchMatchingOption studySearchMatchingOption) {
		this.studySearchMatchingOption = studySearchMatchingOption;
	}

	@Override
	public String toString() {
		return "StudyQueryFilter [startDate=" + this.startDate + ", name=" + this.name + ", country=" + this.country + ", season="
				+ this.season + "]";
	}

}
