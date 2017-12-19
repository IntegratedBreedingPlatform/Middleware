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
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.searcher.Searcher;
import org.generationcp.middleware.pojos.Country;

public class StudyResultSetByNameStartDateSeasonCountry extends Searcher implements StudyResultSet {

	private final List<Integer> locationIds;
	private Integer size;
	private List<StudyReference> studyReferences = new ArrayList<>();
	private int index;

	public StudyResultSetByNameStartDateSeasonCountry(final BrowseStudyQueryFilter filter, final HibernateSessionProvider sessionProvider) {

		super(sessionProvider);

		this.locationIds = this.getLocationIds(filter.getCountry());
		this.index = 0;

		this.searchStudiesMatchingFilter(filter);
	}

	private List<Integer> getLocationIds(final String countryName) {
		List<Integer> locationIds = new ArrayList<>();
		if (countryName != null) {
			final List<Country> countries = this.getCountryDao().getByIsoFull(countryName);
			locationIds = this.getLocationSearchDao().getLocationIds(countries);
		}
		return locationIds;
	}

	private void searchStudiesMatchingFilter(final BrowseStudyQueryFilter filter) {
		this.studyReferences = this.getStudySearchDao().searchStudies(filter, this.locationIds);
		this.size = this.studyReferences.size();
	}

	@Override
	public boolean hasMore() {
		return this.index < this.size;
	}

	@Override
	public StudyReference next() {
		return this.studyReferences.get(this.index++);
	}

	@Override
	public long size() {
		return this.size;
	}
}
