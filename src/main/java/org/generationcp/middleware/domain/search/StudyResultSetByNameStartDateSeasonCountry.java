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

public class StudyResultSetByNameStartDateSeasonCountry extends Searcher implements StudyResultSet {

	private String name;
	private Integer startDate;
	private Season season;
	private String country;
	private int numOfRows;
	
	private List<Integer> locationIds;
	
	private long countOfLocalStudiesByName;
	private long countOfLocalStudiesByStartDate;
	private long countOfLocalStudiesBySeason;
	private long countOfLocalStudiesByCountry;
	private long countOfCentralStudiesByName;
	private long countOfCentralStudiesByStartDate;
	private long countOfCentralStudiesBySeason;
	private long countOfCentralStudiesByCountry;
	
	private int currentRow;
	
	private List<StudyReference> buffer;
	private int bufIndex;
	

	public StudyResultSetByNameStartDateSeasonCountry(BrowseStudyQueryFilter filter, int numOfRows,
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral)
			throws MiddlewareQueryException {

		super(sessionProviderForLocal, sessionProviderForCentral);
		
		this.name = filter.getName();
		this.startDate = filter.getStartDate();
		this.season = filter.getSeason();
		this.country = filter.getCountry();
		
		this.numOfRows = numOfRows;
		
		this.locationIds = getLocationIds(country);
		
		this.countOfLocalStudiesByName = countStudiesByName(Database.LOCAL, name);
		this.countOfLocalStudiesByStartDate = countStudiesByStartDate(Database.LOCAL, startDate);
		this.countOfLocalStudiesBySeason = countStudiesBySeason(Database.LOCAL, season);
		this.countOfLocalStudiesByCountry = countStudiesByCountry(Database.LOCAL);
		this.countOfCentralStudiesByName = countStudiesByName(Database.CENTRAL, name);
		this.countOfCentralStudiesByStartDate = countStudiesByStartDate(Database.CENTRAL, startDate);
		this.countOfCentralStudiesBySeason = countStudiesBySeason(Database.CENTRAL, season);
		this.countOfCentralStudiesByCountry = countStudiesByCountry(Database.CENTRAL);
		
		this.currentRow = 0;
		this.bufIndex = 0;
	}

	private List<Integer> getLocationIds(String countryName) throws MiddlewareQueryException {
		List<Integer> locationIds = new ArrayList<Integer>();
		if (countryName != null) {
			if (this.setWorkingDatabase(Database.CENTRAL)) {
				List<Country> countries = getCountryDao().getByIsoFull(countryName);
				locationIds = getLocationSearchDao().getLocationIds(countries);
			}
		}
		return locationIds;
	}

	private long countStudiesByName(Database database, String name) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database) && name != null) {
			return this.getStudySearchDao().countStudiesByName(name);
		}
		return 0;
	}

	private long countStudiesByStartDate(Database database, Integer startDate) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database) && startDate != null) {
			return this.getStudySearchDao().countStudiesByStartDate(startDate);
		}
		return 0;
	}
	
	private long countStudiesBySeason(Database database, Season season) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database) && season != null) {
			return this.getStudySearchDao().countStudiesBySeason(season);
		}
		return 0;
	}
	
	private long countStudiesByCountry(Database database) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database) && locationIds != null && locationIds.size() > 0) {
			return this.getStudySearchDao().countStudiesByLocationIds(locationIds);
		}
		return 0;
	}
	
	@Override
	public boolean hasMore() {
		return currentRow < size();
	}

	@Override
	public StudyReference next() throws MiddlewareQueryException {
		if (isEmptyBuffer()) {
			fillBuffer();
		}
		currentRow++;
		return buffer.get(bufIndex++);
	}

	private boolean isEmptyBuffer() {
		return buffer == null || bufIndex >= buffer.size();
	}

	private void fillBuffer() throws MiddlewareQueryException {
		if (currentRow < this.countOfLocalStudiesByName) {
			fillBufferByName(Database.LOCAL, currentRow);
		}
		else if (currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName) {
			int start = currentRow - (int) this.countOfLocalStudiesByName;
			fillBufferByName(Database.CENTRAL, start);
		}
		else if (currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName + 
				              this.countOfLocalStudiesByStartDate) {
			int start = currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName;
			fillBufferByStartDate(Database.LOCAL, start);
		}
		else if (currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName + 
				              this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate) {
			int start = currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName -
		                             (int) this.countOfLocalStudiesByStartDate;
			fillBufferByStartDate(Database.CENTRAL, start);
		}
		else if (currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName + 
	              this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate +
	              this.countOfLocalStudiesBySeason) {
			int start = currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName -
		                             (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate;
            fillBufferBySeason(Database.LOCAL, start);
        }
		else if (currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName + 
	              this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate +
	              this.countOfLocalStudiesBySeason + this.countOfCentralStudiesBySeason) {
			int start = currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName -
		                             (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate -
		                             (int) this.countOfLocalStudiesBySeason;
            fillBufferBySeason(Database.CENTRAL, start);
        }
		else if (currentRow < this.countOfLocalStudiesByName + this.countOfCentralStudiesByName + 
	              this.countOfLocalStudiesByStartDate + this.countOfCentralStudiesByStartDate +
	              this.countOfLocalStudiesBySeason + this.countOfCentralStudiesBySeason +
	              this.countOfLocalStudiesByCountry) {
			int start = currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName -
		                             (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate -
		                             (int) this.countOfLocalStudiesBySeason - (int) this.countOfCentralStudiesBySeason;
            fillBufferByCountry(Database.LOCAL, start);
        }
		else {
			int start = currentRow - (int) this.countOfLocalStudiesByName - (int) this.countOfCentralStudiesByName -
                                     (int) this.countOfLocalStudiesByStartDate - (int) this.countOfCentralStudiesByStartDate -
                                     (int) this.countOfLocalStudiesBySeason - (int) this.countOfCentralStudiesBySeason -
                                     (int) this.countOfLocalStudiesByCountry;
            fillBufferByCountry(Database.CENTRAL, start);
        }
	}
	
	private void fillBufferByName(Database database, int start) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database)) {
			buffer = this.getStudySearchDao().getStudiesByName(name, start, numOfRows);
			bufIndex = 0;
		}
	}
	
	private void fillBufferByStartDate(Database database, int start) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database)) {
			buffer = this.getStudySearchDao().getStudiesByStartDate(startDate, start, numOfRows);
			bufIndex = 0;
		}
	}
	
	private void fillBufferBySeason(Database database, int start) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database)) {
			buffer = this.getStudySearchDao().getStudiesBySeason(season, start, numOfRows);
			bufIndex = 0;
		}
	}
	
	private void fillBufferByCountry(Database database, int start) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(database)) {
			buffer = this.getStudySearchDao().getStudiesByLocationIds(locationIds, start, numOfRows);
			bufIndex = 0;
		}
	}

	@Override
	public long size() {
		return countOfLocalStudiesByName +
		       countOfLocalStudiesByStartDate +
		       countOfLocalStudiesBySeason +
		       countOfLocalStudiesByCountry +
		       countOfCentralStudiesByName +
		       countOfCentralStudiesByStartDate +
		       countOfCentralStudiesBySeason +
		       countOfCentralStudiesByCountry;
	}
}
