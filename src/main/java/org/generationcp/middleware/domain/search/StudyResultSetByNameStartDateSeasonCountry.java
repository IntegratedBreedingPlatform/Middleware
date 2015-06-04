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
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.operation.searcher.Searcher;
import org.generationcp.middleware.pojos.Country;

// TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
public class StudyResultSetByNameStartDateSeasonCountry extends Searcher implements StudyResultSet {

	private final String name;
	private final Integer startDate;
	private final Season season;
	private final String country;
	private final int numOfRows;

	private final List<Integer> locationIds;

	private final long countOfLocalStudiesByName;
	private final long countOfLocalStudiesByStartDate;
	private final long countOfLocalStudiesBySeason;
	private final long countOfLocalStudiesByCountry;
	private final long countOfCentralStudiesByName;
	private final long countOfCentralStudiesByStartDate;
	private final long countOfCentralStudiesBySeason;
	private final long countOfCentralStudiesByCountry;

	private int currentRow;

	private List<StudyReference> buffer;
	private int bufIndex;

	public StudyResultSetByNameStartDateSeasonCountry(BrowseStudyQueryFilter filter, int numOfRows,
			HibernateSessionProvider sessionProviderForLocal) throws MiddlewareQueryException {

		super(sessionProviderForLocal);

		this.name = filter.getName();
		this.startDate = filter.getStartDate();
		this.season = filter.getSeason();
		this.country = filter.getCountry();

		this.numOfRows = numOfRows;

		this.locationIds = this.getLocationIds(this.country);

		this.countOfLocalStudiesByName = this.countStudiesByName(Database.LOCAL, this.name);
		this.countOfLocalStudiesByStartDate = this.countStudiesByStartDate(Database.LOCAL, this.startDate);
		this.countOfLocalStudiesBySeason = this.countStudiesBySeason(Database.LOCAL, this.season);
		this.countOfLocalStudiesByCountry = this.countStudiesByCountry(Database.LOCAL);
		this.countOfCentralStudiesByName = this.countStudiesByName(Database.CENTRAL, this.name);
		this.countOfCentralStudiesByStartDate = this.countStudiesByStartDate(Database.CENTRAL, this.startDate);
		this.countOfCentralStudiesBySeason = this.countStudiesBySeason(Database.CENTRAL, this.season);
		this.countOfCentralStudiesByCountry = this.countStudiesByCountry(Database.CENTRAL);

		this.currentRow = 0;
		this.bufIndex = 0;
	}

	private List<Integer> getLocationIds(String countryName) throws MiddlewareQueryException {
		List<Integer> locationIds = new ArrayList<Integer>();
		if (countryName != null) {
			List<Country> countries = this.getCountryDao().getByIsoFull(countryName);
			locationIds = this.getLocationSearchDao().getLocationIds(countries);
		}
		return locationIds;
	}

	private long countStudiesByName(Database database, String name) throws MiddlewareQueryException {
		return this.getStudySearchDao().countStudiesByName(name);
	}

	private long countStudiesByStartDate(Database database, Integer startDate) throws MiddlewareQueryException {
		if (startDate != null) {
			return this.getStudySearchDao().countStudiesByStartDate(startDate);
		}
		return 0;
	}

	private long countStudiesBySeason(Database database, Season season) throws MiddlewareQueryException {
		if (season != null) {
			return this.getStudySearchDao().countStudiesBySeason(season);
		}
		return 0;
	}

	private long countStudiesByCountry(Database database) throws MiddlewareQueryException {
		if (this.locationIds != null && this.locationIds.size() > 0) {
			return this.getStudySearchDao().countStudiesByLocationIds(this.locationIds);
		}
		return 0;
	}

	@Override
	public boolean hasMore() {
		return this.currentRow < this.size();
	}

	@Override
	public StudyReference next() throws MiddlewareQueryException {
		if (this.isEmptyBuffer()) {
			this.fillBuffer();
		}
		this.currentRow++;
		return this.buffer.get(this.bufIndex++);
	}

	private boolean isEmptyBuffer() {
		return this.buffer == null || this.bufIndex >= this.buffer.size();
	}

	private void fillBuffer() throws MiddlewareQueryException {
		if (this.currentRow < this.countOfLocalStudiesByName) {
			this.fillBufferByName(Database.LOCAL, this.currentRow);
		} else if (this.currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName) {
			int start = this.currentRow - (int) this.countOfLocalStudiesByName;
			this.fillBufferByName(Database.CENTRAL, start);
		} else if (this.currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName
				+ this.countOfLocalStudiesByStartDate) {
			int start = this.currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName;
			this.fillBufferByStartDate(Database.LOCAL, start);
		} else if (this.currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName
				+ this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate) {
			int start =
					this.currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName
							- (int) this.countOfLocalStudiesByStartDate;
			this.fillBufferByStartDate(Database.CENTRAL, start);
		} else if (this.currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName
				+ this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate + this.countOfLocalStudiesBySeason) {
			int start =
					this.currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName
							- (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate;
			this.fillBufferBySeason(Database.LOCAL, start);
		} else if (this.currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName
				+ this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate + this.countOfLocalStudiesBySeason
				+ this.countOfCentralStudiesBySeason) {
			int start =
					this.currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName
							- (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate
							- (int) this.countOfLocalStudiesBySeason;
			this.fillBufferBySeason(Database.CENTRAL, start);
		} else if (this.currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName
				+ this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate + this.countOfLocalStudiesBySeason
				+ this.countOfCentralStudiesBySeason + this.countOfLocalStudiesByCountry) {
			int start =
					this.currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName
							- (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate
							- (int) this.countOfLocalStudiesBySeason - (int) this.countOfCentralStudiesBySeason;
			this.fillBufferByCountry(Database.LOCAL, start);
		} else {
			int start =
					this.currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName
							- (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate
							- (int) this.countOfLocalStudiesBySeason - (int) this.countOfCentralStudiesBySeason
							- (int) this.countOfLocalStudiesByCountry;
			this.fillBufferByCountry(Database.CENTRAL, start);
		}
	}

	private void fillBufferByName(Database database, int start) throws MiddlewareQueryException {
		this.buffer = this.getStudySearchDao().getStudiesByName(this.name, start, this.numOfRows);
		this.bufIndex = 0;
	}

	private void fillBufferByStartDate(Database database, int start) throws MiddlewareQueryException {
		this.buffer = this.getStudySearchDao().getStudiesByStartDate(this.startDate, start, this.numOfRows);
		this.bufIndex = 0;
	}

	private void fillBufferBySeason(Database database, int start) throws MiddlewareQueryException {
		this.buffer = this.getStudySearchDao().getStudiesBySeason(this.season, start, this.numOfRows);
		this.bufIndex = 0;
	}

	private void fillBufferByCountry(Database database, int start) throws MiddlewareQueryException {
		this.buffer = this.getStudySearchDao().getStudiesByLocationIds(this.locationIds, start, this.numOfRows);
		this.bufIndex = 0;
	}

	@Override
	public long size() {
		return this.countOfLocalStudiesByName + this.countOfLocalStudiesByStartDate + this.countOfLocalStudiesBySeason
				+ this.countOfLocalStudiesByCountry + this.countOfCentralStudiesByName + this.countOfCentralStudiesByStartDate
				+ this.countOfCentralStudiesBySeason + this.countOfCentralStudiesByCountry;
	}
}
