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

package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO class for searching studies stored in {@link DmsProject}.
 *
 * @author Donald Barre
 *
 */
public class StudySearchDao extends GenericDAO<DmsProject, Integer> {

	private static final String IN_STUDY_SEARCH_DAO = " in StudySearchDao: ";
	private static final String PROGRAM_UUID = "programUUID";
	private static final String UNION_DISTINCT = "  UNION DISTINCT";
	private static final Logger LOG = LoggerFactory.getLogger(StudySearchDao.class);
	private static final String NOT_IN_DELETED_STUDIES_QUERY = " AND p.deleted = 0 ";
	private static final String HAS_STUDY_TYPE = " and p.study_type_id is not null ";

	public StudySearchDao(final Session session) {
		super(session);
	}

	public List<StudyReference> searchStudies(final BrowseStudyQueryFilter filter, final List<Integer> locationIds) {

		final String studyName = filter.getName();
		final List<StudyReference> studiesMatchedByName =
			this.getStudiesByName(studyName, filter.getStudySearchMatchingOption(), filter.getProgramUUID());
		final Integer startDate = filter.getStartDate();
		final List<StudyReference> studiesMatchedByDate = this.getStudiesByStartDate(startDate, filter.getProgramUUID());
		final List<StudyReference> studiesMatchedByLocation = this.getStudiesByLocationIds(locationIds, filter.getProgramUUID());
		final Season season = filter.getSeason();
		final List<StudyReference> studiesMatchedBySeason = this.getStudiesBySeason(season, filter.getProgramUUID());

		final Set<StudyReference> finalStudies = new HashSet<>();
		finalStudies.addAll(studiesMatchedByName);
		finalStudies.addAll(studiesMatchedByDate);
		finalStudies.addAll(studiesMatchedByLocation);
		finalStudies.addAll(studiesMatchedBySeason);

		// Get intersection of all search results
		if (studyName != null && !studyName.isEmpty()) {
			finalStudies.retainAll(studiesMatchedByName);
		}
		if (startDate != null) {
			finalStudies.retainAll(studiesMatchedByDate);
		}
		if (locationIds != null && !locationIds.isEmpty()) {
			finalStudies.retainAll(studiesMatchedByLocation);
		}
		if (season != null) {
			finalStudies.retainAll(studiesMatchedBySeason);
		}
		return new ArrayList<>(finalStudies);

	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByName(final String name, final StudySearchMatchingOption studySearchMatchingOption,
		final String programUUID) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		if (name != null && !name.isEmpty()) {
			try {
				final SQLQuery query = this.getSession().createSQLQuery("select distinct p.project_id, p.name, p.description "
					+ this.getSearchByNameMainQuery(studySearchMatchingOption, name));

				query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
				this.assignNameParameter(studySearchMatchingOption, query, name);

				final List<Object[]> results = query.list();
				for (final Object[] row : results) {
					studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
				}

			} catch (final HibernateException e) {
				final String message = "Error in getStudiesByName=" + name + StudySearchDao.IN_STUDY_SEARCH_DAO + e.getMessage();
				StudySearchDao.LOG.error(message, e);
				throw new MiddlewareQueryException(message, e);
			}
		}
		return studyReferences;
	}

	private String getSearchByNameMainQuery(final StudySearchMatchingOption studySearchMatchingOption, final String name) {
		return "from project p "
			+ "where p.program_uuid = :programUUID AND p.name " + this.buildMatchCondition(studySearchMatchingOption, name)
			+ StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY + StudySearchDao.HAS_STUDY_TYPE;
	}

	private String buildMatchCondition(final StudySearchMatchingOption studySearchMatchingOption, final String name) {

		String condition = "";

		if (studySearchMatchingOption == StudySearchMatchingOption.EXACT_MATCHES || name == null || name.isEmpty()) {
			condition = "= :name";
		} else if (studySearchMatchingOption == StudySearchMatchingOption.MATCHES_CONTAINING
			|| studySearchMatchingOption == StudySearchMatchingOption.MATCHES_STARTING_WITH) {
			condition = "LIKE :name";
		}
		return condition;

	}

	private String assignNameParameter(final StudySearchMatchingOption studySearchMatchingOption, final SQLQuery query, final String name) {

		final String condition = "";

		if (studySearchMatchingOption == StudySearchMatchingOption.EXACT_MATCHES) {
			query.setParameter("name", name);
		} else if (studySearchMatchingOption == StudySearchMatchingOption.MATCHES_CONTAINING) {
			query.setParameter("name", "%" + name + "%");
		} else if (studySearchMatchingOption == StudySearchMatchingOption.MATCHES_STARTING_WITH) {
			query.setParameter("name", name + "%");
		}
		return condition;

	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByStartDate(final Integer startDate, final String programUUID) {

		final List<StudyReference> studyReferences = new ArrayList<>();
		if (startDate != null) {
			try {
				final String dateString = String.valueOf(startDate) + "%";

				final SQLQuery query = this.getSession()
					.createSQLQuery("select distinct p.project_id, p.name, p.description " + this.getSearchByStartDateMainQuery());

				query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
				query.setParameter("compareDate", dateString);

				final List<Object[]> results = query.list();
				for (final Object[] row : results) {
					final StudyReference sr = new StudyReference((Integer) row[0], (String) row[1], (String) row[2]);
					studyReferences.add(sr);
				}

			} catch (final HibernateException e) {
				final String message = "Error in getStudiesByStartDate=" + startDate + StudySearchDao.IN_STUDY_SEARCH_DAO + e.getMessage();
				StudySearchDao.LOG.error(message, e);
				throw new MiddlewareQueryException(message, e);
			}
		}
		return studyReferences;
	}

	private String getSearchByStartDateMainQuery() {
		return "from project p "
			+ "	WHERE p.start_date LIKE :compareDate AND p.program_uuid = :programUUID " + StudySearchDao
			.NOT_IN_DELETED_STUDIES_QUERY;
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesBySeason(final Season season, final String programUUID) {

		final List<StudyReference> studyReferences = new ArrayList<>();
		if (season != null) {
			try {
				int valueId = 0;
				if (season == Season.DRY) {
					valueId = 10290;
				} else if (season == Season.WET) {
					valueId = 10300;
				}

				if (valueId != 0) {
					final SQLQuery query = this.getSession().createSQLQuery("SELECT DISTINCT p.project_id, p.name, p.description"
						+ this.getSearchBySeasonAtEnvironmentLevelMainQuery() + StudySearchDao.UNION_DISTINCT
						+ "  SELECT DISTINCT p.project_id, p.name, p.description " + this.getSearchBySeasonAtStudyLevelMainQuery());

					query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
					query.setParameter("seasonId", valueId);

					final List<Object[]> results = query.list();
					for (final Object[] row : results) {
						final StudyReference sr = new StudyReference((Integer) row[0], (String) row[1], (String) row[2]);
						studyReferences.add(sr);
					}
				}

			} catch (final HibernateException e) {
				final String message = "Error in getStudiesBySeason=" + season + StudySearchDao.IN_STUDY_SEARCH_DAO + e.getMessage();
				StudySearchDao.LOG.error(message, e);
				throw new MiddlewareQueryException(message, e);
			}
		}
		return studyReferences;
	}

	private String getSearchBySeasonAtEnvironmentLevelMainQuery() {
		return " FROM project p "
				+ " INNER JOIN project ds ON ds.study_id = p.project_id"
				+ " INNER JOIN nd_experiment e ON e.project_id = ds.project_id  and e.type_id = "
				+ TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
				+ " INNER JOIN nd_geolocationprop gp on gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
				+ TermId.SEASON_VAR.getId() + " WHERE  p.program_uuid = :programUUID AND gp.value = :seasonId "
				+ StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY + StudySearchDao.HAS_STUDY_TYPE;
	}

	private String getSearchBySeasonAtStudyLevelMainQuery() {
		return "FROM project p  "
			+ "INNER JOIN projectprop pp ON p.project_id = pp.project_id AND pp.variable_id = "
			+ TermId.SEASON_VAR.getId() + "  " + "WHERE  p.program_uuid = :programUUID AND pp.value = :seasonId "
			+ StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY + StudySearchDao.HAS_STUDY_TYPE;
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByLocationIds(final List<Integer> locationIds, final String programUUID) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		if (!locationIds.isEmpty()) {
			try {
				final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT DISTINCT p.project_id, p.name, p.description "
						+ this.getSearchByLocationAtEnvironmentLevelMainQuery(locationIds) + StudySearchDao.UNION_DISTINCT
						+ "  SELECT DISTINCT p.project_id, p.name, p.description "
						+ this.getSearchByLocationAtStudyLevelMainQuery(locationIds));

				query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);

				final List<Object[]> results = query.list();
				for (final Object[] row : results) {
					studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
				}

			} catch (final HibernateException e) {
				final String message = "Error in getStudiesByLocationIds=" + locationIds + " in StudyDao: " + e.getMessage();
				StudySearchDao.LOG.error(message, e);
				throw new MiddlewareQueryException(message, e);
			}
		}
		return studyReferences;
	}

	private String getSearchByLocationAtEnvironmentLevelMainQuery(final List<Integer> locationIds) {
		return " FROM project p "
			+ " INNER JOIN project ds ON ds.study_id = p.project_id"
			+ " INNER JOIN nd_experiment e ON e.project_id = ds.project_id  and e.type_id = "
			+ TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
			+ " INNER JOIN nd_geolocationprop gp on gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
				+ TermId.LOCATION_ID.getId() + " WHERE  p.program_uuid = :programUUID AND  gp.value IN (" + this.stringify(locationIds)
				+ ") " + StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY + StudySearchDao.HAS_STUDY_TYPE;
	}

	private String getSearchByLocationAtStudyLevelMainQuery(final List<Integer> locationIds) {
		return "FROM project p"
			+ "  INNER JOIN projectprop pp ON p.project_id = pp.project_id AND pp.variable_id = "
			+ TermId.LOCATION_ID.getId() + "  WHERE  p.program_uuid = :programUUID  AND pp.value IN (" + this.stringify(locationIds)
			+ ")" + StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY + StudySearchDao.HAS_STUDY_TYPE;
	}

	private String stringify(final List<Integer> locationIds) {
		final StringBuilder ids = new StringBuilder();
		boolean first = true;
		for (final Integer locId : locationIds) {
			if (!first) {
				ids.append(",");
			}
			ids.append("'").append(locId).append("'");
			first = false;
		}
		return ids.toString();

	}
}
