package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnvironmentDao extends GenericDAO<ExperimentModel, Integer> {
	private static final Logger LOG = LoggerFactory.getLogger(EnvironmentDao.class);
	private static final String ENVT_ID = "envtId";
	private static final String LOCATION_ID = "locationId";
	private static final String PROJECT_ID = "project_id";
	private static final String ISOABBR = "isoabbr";
	private static final String PROVINCE_NAME = "provinceName";
	private static final String LOCATION_NAME = "locationName";
	private static final String DESCRIPTION = "description";


	private static final String GET_ALL_ENVIRONMENTS_QUERY =
		"SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname AS locationName, prov.lname AS provinceName, "
			+ "       c.isoabbr, p.project_id, p.name, gp.value AS locationId, p.description AS description "
			+ "  FROM nd_experimentprop xp "
			+ " INNER JOIN nd_experiment e on e.nd_experiment_id = xp.nd_experiment_id AND e.type_id = 1020 "
			+ " INNER JOIN project ds ON ds.project_id = e.project_id "
			+ " INNER JOIN project p ON p.project_id = ds.study_id "
			+ "  LEFT JOIN location l ON l.locid = gp.value " + "  LEFT JOIN location prov ON prov.locid = l.snl1id "
			+ "  LEFT JOIN cntry c ON c.cntryid = l.cntryid " + " WHERE xp.type_id = " + TermId.LOCATION_ID.getId();



	public List<ExperimentModel> getEnvironmentsByDataset(final Integer datasetId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("project.projectId", datasetId));
		criteria.add(Restrictions.eq("typeId", ExperimentType.TRIAL_ENVIRONMENT.getTermId()));
		return criteria.list();
	}


	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getAllTrialEnvironments() {
		final List<TrialEnvironment> environments = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(EnvironmentDao.GET_ALL_ENVIRONMENTS_QUERY);
			query.addScalar(EnvironmentDao.ENVT_ID);
			query.addScalar(EnvironmentDao.LOCATION_NAME);
			query.addScalar(EnvironmentDao.PROVINCE_NAME);
			query.addScalar(EnvironmentDao.ISOABBR);
			query.addScalar(EnvironmentDao.PROJECT_ID);
			query.addScalar("name");
			query.addScalar(EnvironmentDao.LOCATION_ID);
			query.addScalar(EnvironmentDao.DESCRIPTION);
			final List<Object[]> list = query.list();
			for (final Object[] row : list) {
				// otherwise it's invalid data and should not be included
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment(
						(Integer) row[0],
						new LocationDto(Integer.valueOf(row[6].toString()), (String) row[1], (String) row[2],
							(String) row[3]),
						new StudyReference((Integer) row[4], (String) row[5], (String) row[7])));
				}
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getAllTrialEnvironments at EnvironmentDao: " + e.getMessage();
			EnvironmentDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return environments;
	}

	public long countAllTrialEnvironments() {
		try {
			final String sql = "SELECT COUNT(DISTINCT nd_experiment_id) " + " FROM nd_experimentprop WHERE type_id = "
				+ TermId.LOCATION_ID.getId();
			final Query query = this.getSession().createSQLQuery(sql);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countAllTrialEnvironments at EnvironmentDao: " + e.getMessage();
			EnvironmentDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(final List<Integer> environmentIds) {
		final List<TrialEnvironmentProperty> properties = new ArrayList<>();
		try {
			// if categorical value, get related cvterm.definition as property
			// value.
			// Else, get the value as it's stored in nd_experimentprop
			final String sql = "SELECT DISTINCT xp.type_id, cvt.name, cvt.definition, nd_experiment_id, "
				+ "CASE WHEN (v.name IS NOT NULL AND cvr.cvterm_relationship_id IS NOT NULL) THEN v.definition "
				+ " ELSE gp.value END AS propvalue " + " FROM nd_experimentprop xp"
				+ " LEFT JOIN cvterm cvt ON xp.type_id = cvt.cvterm_id"
				+ " LEFT JOIN cvterm v ON v.cvterm_id = gp.value"
				+ " LEFT JOIN cvterm_relationship cvr ON cvr.subject_id = gp.type_id AND cvr.type_id = " + TermId.HAS_SCALE.getId()
				+ " WHERE nd_experiment_id IN (:environmentIds)" + " ORDER BY gp.type_id, nd_experiment_id";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("environmentIds", environmentIds);

			int lastId = 0;
			String lastName = "";
			String lastDescription = "";
			Map<Integer, String> environmentValuesMap = new HashMap<>();

			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				final Integer id = (Integer) row[0];

				if (lastId != id.intValue()) {
					final String name = (String) row[1];
					final String description = (String) row[2];

					if (lastId != 0) {
						properties.add(
							new TrialEnvironmentProperty(lastId, lastName, lastDescription, environmentValuesMap));
					}

					lastId = id;
					lastName = name;
					lastDescription = description;
					environmentValuesMap = new HashMap<>();
				}

				environmentValuesMap.put((Integer) row[3], (String) row[4]);
			}

			if (lastId != 0) {
				properties.add(new TrialEnvironmentProperty(lastId, lastName, lastDescription, environmentValuesMap));
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getPropertiesForTrialEnvironments=" + environmentIds
				+ " at EnvironmentDao: " + e.getMessage();
			EnvironmentDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return properties;
	}


	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getTrialEnvironmentDetails(final Set<Integer> environmentIds) {
		final List<TrialEnvironment> environmentDetails = new ArrayList<>();

		if (environmentIds.isEmpty()) {
			return environmentDetails;
		}

		try {

			// Get location name, study id and study name
			final String sql =
				"SELECT DISTINCT e.nd_geolocation_id, l.lname, gp.value, p.project_id, p.name, p.description, prov.lname as provinceName, c.isoabbr "
					+ "FROM nd_experiment e "
					+ "	LEFT JOIN nd_experimentprop xp ON e.nd_experiment_id = xp.nd_experiment_id"
					+ "	AND xp.type_id =  " + TermId.LOCATION_ID.getId()
					+ "	LEFT JOIN location l ON l.locid = xp.value "
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id "
					+ "	LEFT JOIN cntry c ON l.cntryid = c.cntryid "
					+ " INNER JOIN project ds ON ds.project_id = e.project_id "
					+ "	INNER JOIN project p ON p.project_id = ds.study_id "
					+ " WHERE e.nd_experiment_id IN (:locationIds) ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("locationIds", environmentIds);
			query.addScalar("nd_geolocation_id", Hibernate.INTEGER);
			query.addScalar("lname", Hibernate.STRING);
			query.addScalar("value", Hibernate.INTEGER);
			query.addScalar(EnvironmentDao.PROJECT_ID, Hibernate.INTEGER);
			query.addScalar("name", Hibernate.STRING);
			query.addScalar(EnvironmentDao.DESCRIPTION, Hibernate.STRING);
			query.addScalar(EnvironmentDao.PROVINCE_NAME, Hibernate.STRING);
			query.addScalar(EnvironmentDao.ISOABBR, Hibernate.STRING);
			final List<Integer> locIds = new ArrayList<>();

			final List<Object[]> result = query.list();

			for (final Object[] row : result) {
				final Integer environmentId = (Integer) row[0];
				final String locationName = (String) row[1];
				final Integer locId = (Integer) row[2];
				final Integer studyId = (Integer) row[3];
				final String studyName = (String) row[4];
				final String studyDescription = (String) row[5];
				final String provinceName = (String) row[6];
				final String countryName = (String) row[7];

				environmentDetails.add(new TrialEnvironment(
					environmentId,
					new LocationDto(locId, locationName, provinceName, countryName),
					new StudyReference(studyId, studyName, studyDescription)));
				locIds.add(locId);
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getTrialEnvironmentDetails=" + environmentIds
				+ " at EnvironmentDao: " + e.getMessage();
			EnvironmentDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return environmentDetails;
	}

	@SuppressWarnings("unchecked")
	public TrialEnvironments getEnvironmentsForTraits(final List<Integer> traitIds, final String programUUID) {
		final TrialEnvironments environments = new TrialEnvironments();
		try {
			final String sql =
				"SELECT DISTINCT xp.nd_experiment_id as envtId, l.lname as locationName, prov.lname as provinceName, c.isoabbr, p.project_id, p.name, gp.value as locationId"
					+ " FROM nd_experiment e "
					+ " INNER JOIN project ds ON ds.project_id = e.project_id "
					+ " INNER JOIN project p ON p.project_id = ds.study_id "
					+ " INNER JOIN phenotype ph ON ph.nd_experiment_id = e.nd_experiment_id"
					+ " INNER JOIN nd_experimentprop xp ON xp.nd_experiment_id = e.nd_experiment_id AND xp.type_id = "
					+ TermId.LOCATION_ID.getId()
					+ " LEFT JOIN location l ON l.locid = gp.value"
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id"
					+ " LEFT JOIN cntry c ON c.cntryid = l.cntryid"
					+ " WHERE ph.observable_id IN (:traitIds) AND p.program_uuid = :programUUID ;";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addScalar(EnvironmentDao.ENVT_ID);
			query.addScalar(EnvironmentDao.LOCATION_NAME);
			query.addScalar(EnvironmentDao.PROVINCE_NAME);
			query.addScalar(EnvironmentDao.ISOABBR);
			query.addScalar(EnvironmentDao.PROJECT_ID);
			query.addScalar("name");
			query.addScalar(EnvironmentDao.LOCATION_ID);
			query.setParameterList("traitIds", traitIds);
			query.setParameter("programUUID", programUUID);
			final List<Object[]> list = query.list();
			for (final Object[] row : list) {
				// otherwise it's invalid data and should not be included
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment(
						(Integer) row[0], new LocationDto(Integer.valueOf(row[6].toString()), (String) row[1],
						(String) row[2], (String) row[3]),
						new StudyReference((Integer) row[4], (String) row[5])));
				}
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getEnvironmentForTraits at GeolocationDao: " + e.getMessage();
			EnvironmentDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return environments;
	}


}
