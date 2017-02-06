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

package org.generationcp.middleware.domain.search;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.operation.searcher.Searcher;
import org.generationcp.middleware.pojos.Country;

public class StudyResultSetByNameStartDateSeasonCountry extends Searcher implements StudyResultSet {

	private final String name;
	private final Integer startDate;
	private final Season season;
	private final String country;
	private final int numOfRows;
	private final String programUUID;
	private final StudySearchMatchingOption studySearchMatchingOption;

	private final List<Integer> locationIds;

	private final long countOfStudiesByName;
	private final long countOfStudiesByStartDate;
	private final long countOfStudiesBySeason;
	private final long countOfStudiesByCountry;

	private int currentRow;

	private List<StudyReference> buffer;
	private int bufIndex;

	public StudyResultSetByNameStartDateSeasonCountry(BrowseStudyQueryFilter filter, int numOfRows,
			HibernateSessionProvider sessionProvider) {

		super(sessionProvider);

		this.name = filter.getName();
		this.startDate = filter.getStartDate();
		this.season = filter.getSeason();
		this.country = filter.getCountry();
		this.programUUID = filter.getProgramUUID();
		this.studySearchMatchingOption = filter.getStudySearchMatchingOption();

		this.numOfRows = numOfRows;

		this.locationIds = this.getLocationIds(this.country);

		this.countOfStudiesByName = this.countStudiesByName(this.name);
		this.countOfStudiesByStartDate = this.countStudiesByStartDate(this.startDate);
		this.countOfStudiesBySeason = this.countStudiesBySeason(this.season);
		this.countOfStudiesByCountry = this.countStudiesByCountry();

		this.currentRow = 0;
		this.bufIndex = 0;
	}

	private List<Integer> getLocationIds(String countryName) {
		List<Integer> locationIds = new ArrayList<Integer>();
		if (countryName != null) {
			List<Country> countries = this.getCountryDao().getByIsoFull(countryName);
			locationIds = this.getLocationSearchDao().getLocationIds(countries);
		}
		return locationIds;
	}

	private long countStudiesByName(String name) {
		return this.getStudySearchDao().countStudiesByName(name, studySearchMatchingOption, programUUID);
	}

	private long countStudiesByStartDate(Integer startDate) {
		if (startDate != null) {
			return this.getStudySearchDao().countStudiesByStartDate(startDate, programUUID);
		}
		return 0;
	}

	private long countStudiesBySeason(Season season) {
		if (season != null) {
			return this.getStudySearchDao().countStudiesBySeason(season, programUUID);
		}
		return 0;
	}

	private long countStudiesByCountry() {
		if (this.locationIds != null && !this.locationIds.isEmpty()) {
			return this.getStudySearchDao().countStudiesByLocationIds(this.locationIds, programUUID);
		}
		return 0;
	}

	@Override
	public boolean hasMore() {
		return this.currentRow < this.size();
	}

	@Override
	public StudyReference next() {
		if (this.isEmptyBuffer()) {
			this.fillBuffer();
		}
		this.currentRow++;
		return this.buffer.get(this.bufIndex++);
	}

	private boolean isEmptyBuffer() {
		return this.buffer == null || this.bufIndex >= this.buffer.size();
	}

	private void fillBuffer() {
		if (this.currentRow < this.countOfStudiesByName) {
			this.fillBufferByName(this.currentRow);
		} else if (this.currentRow < this.countOfStudiesByName) {
			int start = this.currentRow - (int) this.countOfStudiesByName;
			this.fillBufferByName(start);
		} else if (this.currentRow < this.countOfStudiesByName
				+ this.countOfStudiesByStartDate) {
			int start = this.currentRow - (int) this.countOfStudiesByName;
			this.fillBufferByStartDate(start);
		} else if (this.currentRow < this.countOfStudiesByName
				+ this.countOfStudiesByStartDate + this.countOfStudiesBySeason) {
			int start =
					this.currentRow - (int) this.countOfStudiesByName
							- (int) this.countOfStudiesByStartDate;
			this.fillBufferBySeason(start);
		} else if (this.currentRow < this.countOfStudiesByName
				+ this.countOfStudiesByStartDate + this.countOfStudiesBySeason
				+ this.countOfStudiesByCountry) {
			int start = this.currentRow - (int) this.countOfStudiesByName - (int) this.countOfStudiesByStartDate - (int) this.countOfStudiesBySeason;
			this.fillBufferByCountry(start);
		}
	}

	private void fillBufferByName(int start) {
		this.buffer = this.getStudySearchDao().getStudiesByName(this.name, start, this.numOfRows, studySearchMatchingOption, programUUID);
		this.bufIndex = 0;
	}

	private void fillBufferByStartDate(int start) {
		this.buffer = this.getStudySearchDao().getStudiesByStartDate(this.startDate, start, this.numOfRows, programUUID);
		this.bufIndex = 0;
	}

	private void fillBufferBySeason(int start) {
		this.buffer = this.getStudySearchDao().getStudiesBySeason(this.season, start, this.numOfRows, programUUID);
		this.bufIndex = 0;
	}

	private void fillBufferByCountry(int start) {
		this.buffer = this.getStudySearchDao().getStudiesByLocationIds(this.locationIds, start, this.numOfRows, programUUID);
		this.bufIndex = 0;
	}

	@Override
	public long size() {
		return this.countOfStudiesByName + this.countOfStudiesByStartDate + this.countOfStudiesBySeason
				+ this.countOfStudiesByCountry;
	}
}
