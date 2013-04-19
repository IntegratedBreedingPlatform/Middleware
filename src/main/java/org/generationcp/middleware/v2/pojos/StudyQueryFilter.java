package org.generationcp.middleware.v2.pojos;

import org.generationcp.middleware.manager.Season;

public class StudyQueryFilter {

	private Integer startDate;
	
	private String name;
	
	private String country;
	
	private Season season;
	
	private int start;
	
	private int numOfRows;
	
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

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getNumOfRows() {
		return numOfRows;
	}

	public void setNumOfRows(int numOfRows) {
		this.numOfRows = numOfRows;
	}

	@Override
	public String toString() {
		return "StudyQueryFilter [startDate=" + startDate + ", name=" + name
				+ ", country=" + country + ", season=" + season + ", start="
				+ start + ", numOfRows=" + numOfRows
				+ "]";
	}

}
