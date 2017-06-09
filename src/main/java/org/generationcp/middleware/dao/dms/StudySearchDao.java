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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final String NOT_IN_DELETED_STUDIES_QUERY = " AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "
			+ TermId.STUDY_STATUS.getId() + "  AND pp.project_id = p.project_id AND pp.value = " + TermId.DELETED_STUDY.getId() + ") ";

	public long countStudiesByName(final String name, final StudySearchMatchingOption studySearchMatchingOption, final String programUUID) {
		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("select count(distinct p.project_id) " + this.getSearchByNameMainQuery(studySearchMatchingOption));

			query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
			this.assignNameParameter(studySearchMatchingOption, query, name);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String message = "Error in countStudiesByName=" + name + StudySearchDao.IN_STUDY_SEARCH_DAO + e.getMessage();
			StudySearchDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByName(final String name, final int start, final int numOfRows,
			final StudySearchMatchingOption studySearchMatchingOption, final String programUUID) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(
					"select distinct p.project_id, p.name, p.description " + this.getSearchByNameMainQuery(studySearchMatchingOption));

			query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
			this.assignNameParameter(studySearchMatchingOption, query, name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			final List<Object[]> results = query.list();
			for (final Object[] row : results) {
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
			}

		} catch (final HibernateException e) {
			final String message = "Error in getStudiesByName=" + name + StudySearchDao.IN_STUDY_SEARCH_DAO + e.getMessage();
			StudySearchDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return studyReferences;
	}

	private String getSearchByNameMainQuery(final StudySearchMatchingOption studySearchMatchingOption) {
		return "from project p " + " inner join project_relationship r on r.object_project_id = p.project_id and r.type_id" + " NOT IN ("
				+ TermId.HAS_PARENT_FOLDER.getId() + "," + TermId.STUDY_HAS_FOLDER.getId() + ") "
				+ "where p.program_uuid = :programUUID AND p.name " + this.buildMatchCondition(studySearchMatchingOption)
				+ StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY;

	}

	private String buildMatchCondition(final StudySearchMatchingOption studySearchMatchingOption) {

		String condition = "";

		if (studySearchMatchingOption == StudySearchMatchingOption.EXACT_MATCHES) {
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

	public long countStudiesByStartDate(final int startDate, final String programUUID) {
		try {
			String dateString = String.valueOf(startDate);
			// pad LIKE wildcard characters
			if (dateString.length() == 4) { // only year specified
				dateString += "____";
			} else if (dateString.length() == 6) { // only month and year
				dateString += "__";
			}
			final SQLQuery query =
					this.getSession().createSQLQuery("select count(distinct p.project_id) " + this.getSearchByStartDateMainQuery());

			query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
			query.setParameter("compareDate", dateString);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String message = "Error in countStudiesByStartDate=" + startDate + StudySearchDao.IN_STUDY_SEARCH_DAO + e.getMessage();
			StudySearchDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByStartDate(final int startDate, final int start, final int numOfRows, final String programUUID) {

		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			String dateString = String.valueOf(startDate);
			// pad LIKE wildcard characters
			if (dateString.length() == 4) { // only year specified
				dateString += "____";
			} else if (dateString.length() == 6) { // only month and year
				dateString += "__";
			}

			final SQLQuery query = this.getSession()
					.createSQLQuery("select distinct p.project_id, p.name, p.description " + this.getSearchByStartDateMainQuery());

			query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
			query.setParameter("compareDate", dateString);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

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
		return studyReferences;
	}

	private String getSearchByStartDateMainQuery() {
		return "from project p "
				+ " INNER JOIN projectprop projectPropStartDate ON p.project_id = projectPropStartDate.project_id AND projectPropStartDate.type_id = "
				+ TermId.START_DATE.getId() + " AND projectPropStartDate.value LIKE :compareDate "
				+ "	WHERE p.program_uuid = :programUUID " + StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY;
	}

	public long countStudiesBySeason(final Season season, final String programUUID) {
		try {
			int valueId = 0;
			if (season == Season.DRY) {
				valueId = TermId.SEASON_DRY.getId();
			} else if (season == Season.WET) {
				valueId = TermId.SEASON_WET.getId();
			}

			if (valueId != 0) {
				final SQLQuery query = this.getSession()
						.createSQLQuery("SELECT COUNT(*) FROM (SELECT DISTINCT p.project_id"
								+ this.getSearchBySeasonAtEnvironmentLevelMainQuery() + StudySearchDao.UNION_DISTINCT
								+ "  SELECT DISTINCT p.project_id " + this.getSearchBySeasonAtStudyLevelMainQuery() + ") projectlist");

				query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
				query.setParameter("seasonId", valueId);
				return ((BigInteger) query.uniqueResult()).longValue();
			}

		} catch (final HibernateException e) {
			final String message = "Error in countStudiesBySeason=" + season + StudySearchDao.IN_STUDY_SEARCH_DAO + e.getMessage();
			StudySearchDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesBySeason(final Season season, final int start, final int numOfRows, final String programUUID) {

		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			int valueId = 0;
			if (season == Season.DRY) {
				valueId = 10290;
			} else if (season == Season.WET) {
				valueId = 10300;
			}

			if (valueId != 0) {
				final SQLQuery query = this.getSession()
						.createSQLQuery("SELECT DISTINCT p.project_id, p.name, p.description"
								+ this.getSearchBySeasonAtEnvironmentLevelMainQuery() + StudySearchDao.UNION_DISTINCT
								+ "  SELECT DISTINCT p.project_id, p.name, p.description " + this.getSearchBySeasonAtStudyLevelMainQuery());

				query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
				query.setParameter("seasonId", valueId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);

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
		return studyReferences;
	}

	private String getSearchBySeasonAtEnvironmentLevelMainQuery() {
		return " FROM project p " + " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id =  "
				+ TermId.BELONGS_TO_STUDY.getId() + " INNER JOIN nd_experiment_project ep ON ep.project_id = pr.subject_project_id "
				+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = ep.nd_experiment_id and e.type_id = "
				+ TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
				+ " INNER JOIN nd_geolocationprop gp on gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
				+ TermId.SEASON_VAR.getId() + " WHERE  p.program_uuid = :programUUID AND gp.value = :seasonId "
				+ StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY;
	}

	private String getSearchBySeasonAtStudyLevelMainQuery() {
		return "FROM project p  " + "INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id =  "
				+ TermId.BELONGS_TO_STUDY.getId() + "  INNER JOIN projectprop pp ON p.project_id = pp.project_id AND pp.type_id = "
				+ TermId.SEASON_VAR.getId() + "  " + "WHERE  p.program_uuid = :programUUID AND pp.value = :seasonId "
				+ StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY;
	}

	public long countStudiesByLocationIds(final List<Integer> locationIds, final String programUUID) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery("SELECT COUNT(*) FROM (SELECT DISTINCT p.project_id "
					+ this.getSearchByLocationAtEnvironmentLevelMainQuery(locationIds) + StudySearchDao.UNION_DISTINCT
					+ "  SELECT DISTINCT p.project_id " + this.getSearchByLocationAtStudyLevelMainQuery(locationIds) + ") locationList;");

			query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String message = "Error in countStudiesByLocationIds=" + locationIds + " in StudyDao: " + e.getMessage();
			StudySearchDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByLocationIds(final List<Integer> locationIds, final int start, final int numOfRows,
			final String programUUID) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT DISTINCT p.project_id, p.name, p.description "
							+ this.getSearchByLocationAtEnvironmentLevelMainQuery(locationIds) + StudySearchDao.UNION_DISTINCT
							+ "  SELECT DISTINCT p.project_id, p.name, p.description "
							+ this.getSearchByLocationAtStudyLevelMainQuery(locationIds));

			query.setParameter(StudySearchDao.PROGRAM_UUID, programUUID);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			final List<Object[]> results = query.list();
			for (final Object[] row : results) {
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
			}

		} catch (final HibernateException e) {
			final String message = "Error in getStudiesByLocationIds=" + locationIds + " in StudyDao: " + e.getMessage();
			StudySearchDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return studyReferences;
	}

	private String getSearchByLocationAtEnvironmentLevelMainQuery(final List<Integer> locationIds) {
		return " FROM project p " + " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id =  "
				+ TermId.BELONGS_TO_STUDY.getId() + " INNER JOIN nd_experiment_project ep ON ep.project_id = pr.subject_project_id "
				+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = ep.nd_experiment_id and e.type_id = "
				+ TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
				+ " INNER JOIN nd_geolocationprop gp on gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
				+ TermId.LOCATION_ID.getId() + " WHERE  p.program_uuid = :programUUID AND  gp.value IN (" + this.stringify(locationIds)
				+ ") " + StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY;
	}

	private String getSearchByLocationAtStudyLevelMainQuery(final List<Integer> locationIds) {
		return "FROM project p" + "  INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id =  "
				+ TermId.BELONGS_TO_STUDY.getId() + "  INNER JOIN projectprop pp ON p.project_id = pp.project_id AND pp.type_id = "
				+ TermId.LOCATION_ID.getId() + "  WHERE  p.program_uuid = :programUUID  AND pp.value IN (" + this.stringify(locationIds)
				+ ")" + StudySearchDao.NOT_IN_DELETED_STUDIES_QUERY;
	}

	private String stringify(final List<Integer> locationIds) {
		final StringBuffer ids = new StringBuffer();
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
