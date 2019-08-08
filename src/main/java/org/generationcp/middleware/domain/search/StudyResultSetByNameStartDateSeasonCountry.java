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

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Country;

import java.util.ArrayList;
import java.util.List;

public class StudyResultSetByNameStartDateSeasonCountry {

	private DaoFactory daoFactory;
	private BrowseStudyQueryFilter filter;

	public StudyResultSetByNameStartDateSeasonCountry(final BrowseStudyQueryFilter filter, final HibernateSessionProvider sessionProvider) {
		this.filter  = filter;
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	private List<Integer> getLocationIds(final String countryName) {
		List<Integer> locationIds = new ArrayList<>();
		if (countryName != null) {
			final List<Country> countries = this.daoFactory.getCountryDao().getByIsoFull(countryName);
			locationIds = this.daoFactory.getLocationSearchDao().getLocationIds(countries);
		}
		return locationIds;
	}

	public List<StudyReference> getMatchingStudies() {
		final List<Integer> locationIds = this.getLocationIds(this.filter.getCountry());
		return this.daoFactory.getStudySearchDao().searchStudies(this.filter, locationIds);
	}

}
