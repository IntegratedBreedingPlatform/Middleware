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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.operation.searcher.Searcher;
import org.generationcp.middleware.pojos.Country;

public class StudyResultSetByNameStartDateSeasonCountry extends Searcher implements StudyResultSet {

	private final String name;
	private final Integer startDate;
	private final Season season;
	private final String country;
	private final int numOfRows;
	private final StudySearchMatchingOption studySearchMatchingOption;

	private final List<Integer> locationIds;

	private final long countOfLocalStudiesByName;
	private final long countOfLocalStudiesByStartDate;
	private final long countOfLocalStudiesBySeason;
	private final long countOfLocalStudiesByCountry;

	private int currentRow;

	private List<StudyReference> buffer;
	private int bufIndex;

	public StudyResultSetByNameStartDateSeasonCountry(BrowseStudyQueryFilter filter, int numOfRows,
			HibernateSessionProvider sessionProviderForLocal) {

		super(sessionProviderForLocal);

		this.name = filter.getName();
		this.startDate = filter.getStartDate();
		this.season = filter.getSeason();
		this.country = filter.getCountry();
		this.studySearchMatchingOption = filter.getStudySearchMatchingOption();

		this.numOfRows = numOfRows;

		this.locationIds = this.getLocationIds(this.country);

		this.countOfLocalStudiesByName = this.countStudiesByName(this.name);
		this.countOfLocalStudiesByStartDate = this.countStudiesByStartDate(this.startDate);
		this.countOfLocalStudiesBySeason = this.countStudiesBySeason(this.season);
		this.countOfLocalStudiesByCountry = this.countStudiesByCountry();

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
		return this.getStudySearchDao().countStudiesByName(name, studySearchMatchingOption);
	}

	private long countStudiesByStartDate(Integer startDate) {
		if (startDate != null) {
			return this.getStudySearchDao().countStudiesByStartDate(startDate);
		}
		return 0;
	}

	private long countStudiesBySeason(Season season) {
		if (season != null) {
			return this.getStudySearchDao().countStudiesBySeason(season);
		}
		return 0;
	}

	private long countStudiesByCountry() {
		if (this.locationIds != null && !this.locationIds.isEmpty()) {
			return this.getStudySearchDao().countStudiesByLocationIds(this.locationIds);
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
		if (this.currentRow < this.countOfLocalStudiesByName) {
			this.fillBufferByName(this.currentRow);
		} else if (this.currentRow < this.countOfLocalStudiesByName) {
			int start = this.currentRow - (int) this.countOfLocalStudiesByName;
			this.fillBufferByName(start);
		} else if (this.currentRow < this.countOfLocalStudiesByName
				+ this.countOfLocalStudiesByStartDate) {
			int start = this.currentRow - (int) this.countOfLocalStudiesByName;
			this.fillBufferByStartDate(start);
		} else if (this.currentRow < this.countOfLocalStudiesByName
				+ this.countOfLocalStudiesByStartDate + this.countOfLocalStudiesBySeason) {
			int start =
					this.currentRow - (int) this.countOfLocalStudiesByName
							- (int) this.countOfLocalStudiesByStartDate;
			this.fillBufferBySeason(start);
		} else if (this.currentRow < this.countOfLocalStudiesByName
				+ this.countOfLocalStudiesByStartDate + this.countOfLocalStudiesBySeason
				+ this.countOfLocalStudiesByCountry) {
			int start = this.currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfLocalStudiesByStartDate - (int) this.countOfLocalStudiesBySeason;
			this.fillBufferByCountry(start);
		}
	}

	private void fillBufferByName(int start) {
		this.buffer = this.getStudySearchDao().getStudiesByName(this.name, start, this.numOfRows, studySearchMatchingOption);
		this.bufIndex = 0;
	}

	private void fillBufferByStartDate(int start) {
		this.buffer = this.getStudySearchDao().getStudiesByStartDate(this.startDate, start, this.numOfRows);
		this.bufIndex = 0;
	}

	private void fillBufferBySeason(int start) {
		this.buffer = this.getStudySearchDao().getStudiesBySeason(this.season, start, this.numOfRows);
		this.bufIndex = 0;
	}

	private void fillBufferByCountry(int start) {
		this.buffer = this.getStudySearchDao().getStudiesByLocationIds(this.locationIds, start, this.numOfRows);
		this.bufIndex = 0;
	}

	@Override
	public long size() {
		return this.countOfLocalStudiesByName + this.countOfLocalStudiesByStartDate + this.countOfLocalStudiesBySeason
				+ this.countOfLocalStudiesByCountry;
	}
}
