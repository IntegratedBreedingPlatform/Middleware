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

import com.google.common.base.Optional;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link Geolocation}.
 */
public class GeolocationDao extends GenericDAO<Geolocation, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GeolocationDao.class);
	private static final String LOCATION_ID = "locationId";
	private static final String PROJECT_ID = "project_id";
	private static final String ISOABBR = "isoabbr";
	private static final String PROVINCE_NAME = "provinceName";
	private static final String LOCATION_NAME = "locationName";
	private static final String ENVT_ID = "envtId";
	private static final String DESCRIPTION = "description";
	private static final String AT_GEOLOCATION_DAO = " at GeolocationDao: ";
	private static final String GET_ALL_ENVIRONMENTS_QUERY =
		"SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname AS locationName, prov.lname AS provinceName, "
			+ "       c.isoabbr, p.project_id, p.name, gp.value AS locationId, p.description AS description "
			+ "  FROM nd_geolocationprop gp "
			+ " INNER JOIN nd_experiment e on e.nd_geolocation_id = gp.nd_geolocation_id "
			+ "       AND e.nd_experiment_id = " + "		( " + "			SELECT MIN(nd_experiment_id) "
			+ "			  FROM nd_experiment min" + "			 WHERE min.nd_geolocation_id = gp.nd_geolocation_id"
			+ "		) "
			+ " INNER JOIN project_relationship pr ON (pr.object_project_id = e.project_id OR pr.subject_project_id = e.project_id) "
			+ "		AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
			+ " INNER JOIN project p ON p.project_id = pr.object_project_id "
			+ "  LEFT JOIN location l ON l.locid = gp.value " + "  LEFT JOIN location prov ON prov.locid = l.snl1id "
			+ "  LEFT JOIN cntry c ON c.cntryid = l.cntryid " + " WHERE gp.type_id = " + TermId.LOCATION_ID.getId();

	public Geolocation getParentGeolocation(final int projectId) {
		try {
			final String sql = "SELECT DISTINCT g.*" + " FROM nd_geolocation g"
				+ " INNER JOIN nd_experiment se ON se.nd_geolocation_id = g.nd_geolocation_id"
				+ " INNER JOIN project_relationship pr ON pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
				+ " AND pr.object_project_id = se.project_id AND pr.subject_project_id = :projectId";
			final Query query = this.getSession().createSQLQuery(sql).addEntity(this.getPersistentClass())
				.setParameter("projectId", projectId);
			return (Geolocation) query.uniqueResult();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getParentGeolocation=" + projectId + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Set<Geolocation> findInDataSet(final int datasetId) {
		final Set<Geolocation> locations = new LinkedHashSet<>();
		try {

			final String sql = "SELECT DISTINCT e.nd_geolocation_id" + " FROM nd_experiment e"
				+ " WHERE e.project_id = :projectId ORDER BY e.nd_geolocation_id";
			final Query query = this.getSession().createSQLQuery(sql).setParameter("projectId", datasetId);
			final List<Integer> ids = query.list();
			for (final Integer id : ids) {
				locations.add(this.getById(id));
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at findInDataSet=" + datasetId + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return locations;
	}

	@SuppressWarnings("unchecked")
	public Geolocation findByDescription(final String description) {
		try {

			final String sql = "SELECT DISTINCT loc.nd_geolocation_id" + " FROM nd_geolocation loc"
				+ " WHERE loc.description = :description";
			final Query query = this.getSession().createSQLQuery(sql).setParameter(
				GeolocationDao.DESCRIPTION,
				description);
			final List<Integer> ids = query.list();
			if (!ids.isEmpty()) {
				return this.getById(ids.get(0));
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at findByDescription=" + description + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> getLocationIds(final Integer projectId) {
		final Set<Integer> locationIds = new HashSet<>();
		try {
			final String sql = "SELECT DISTINCT e.nd_geolocation_id"
				+ " FROM nd_experiment e "
				+ " WHERE e.project_id = " + projectId;
			final Query query = this.getSession().createSQLQuery(sql);
			locationIds.addAll(query.list());

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getLocationIds=" + projectId + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return locationIds;
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getAllTrialEnvironments() {
		final List<TrialEnvironment> environments = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(GeolocationDao.GET_ALL_ENVIRONMENTS_QUERY);
			query.addScalar(GeolocationDao.ENVT_ID);
			query.addScalar(GeolocationDao.LOCATION_NAME);
			query.addScalar(GeolocationDao.PROVINCE_NAME);
			query.addScalar(GeolocationDao.ISOABBR);
			query.addScalar(GeolocationDao.PROJECT_ID);
			query.addScalar("name");
			query.addScalar(GeolocationDao.LOCATION_ID);
			query.addScalar(GeolocationDao.DESCRIPTION);
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
			final String errorMessage = "Error at getAllTrialEnvironments at GeolocationDao: " + e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return environments;
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getTrialEnvironments(final int start, final int numOfRows) {
		final List<TrialEnvironment> environments = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(GeolocationDao.GET_ALL_ENVIRONMENTS_QUERY);
			query.addScalar(GeolocationDao.ENVT_ID);
			query.addScalar(GeolocationDao.LOCATION_NAME);
			query.addScalar(GeolocationDao.PROVINCE_NAME);
			query.addScalar(GeolocationDao.ISOABBR);
			query.addScalar(GeolocationDao.PROJECT_ID);
			query.addScalar("name");
			query.addScalar(GeolocationDao.LOCATION_ID);
			query.addScalar(GeolocationDao.DESCRIPTION);
			this.setStartAndNumOfRows(query, start, numOfRows);
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
			final String errorMessage = "Error at getTrialEnvironments at GeolocationDao: " + e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return environments;
	}

	public long countAllTrialEnvironments() {
		try {
			final String sql = "SELECT COUNT(DISTINCT nd_geolocation_id) " + " FROM nd_geolocationprop WHERE type_id = "
				+ TermId.LOCATION_ID.getId();
			final Query query = this.getSession().createSQLQuery(sql);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countAllTrialEnvironments at GeolocationDao: " + e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(final List<Integer> environmentIds) {
		final List<TrialEnvironmentProperty> properties = new ArrayList<>();
		try {
			// if categorical value, get related cvterm.definition as property
			// value.
			// Else, get the value as it's stored in nd_geolocationprop
			final String sql = "SELECT DISTINCT gp.type_id, cvt.name, cvt.definition, gp.nd_geolocation_id, "
				+ "CASE WHEN (v.name IS NOT NULL AND cvr.cvterm_relationship_id IS NOT NULL) THEN v.definition "
				+ " ELSE gp.value END AS propvalue " + " FROM nd_geolocationprop gp"
				+ " LEFT JOIN cvterm cvt ON gp.type_id = cvt.cvterm_id"
				+ " LEFT JOIN cvterm v ON v.cvterm_id = gp.value"
				+ " LEFT JOIN cvterm_relationship cvr ON cvr.subject_id = gp.type_id AND cvr.type_id = " + TermId.HAS_SCALE.getId()
				+ " WHERE gp.nd_geolocation_id IN (:environmentIds)" + " ORDER BY gp.type_id, gp.nd_geolocation_id";
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
				+ GeolocationDao.AT_GEOLOCATION_DAO + e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
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
					+ "	LEFT JOIN nd_geolocationprop gp ON e.nd_geolocation_id = gp.nd_geolocation_id "
					+ "	AND gp.type_id =  " + TermId.LOCATION_ID.getId() + " AND e.nd_geolocation_id IN (:locationIds) "
					+ "	LEFT JOIN location l ON l.locid = gp.value "
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id "
					+ "	LEFT JOIN cntry c ON l.cntryid = c.cntryid "
					+ "	INNER JOIN project_relationship pr ON pr.subject_project_id = e.project_id AND pr.type_id = "
					+ TermId.BELONGS_TO_STUDY.getId() + " "
					+ "	INNER JOIN project p ON p.project_id = pr.object_project_id ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("locationIds", environmentIds);
			query.addScalar("nd_geolocation_id", Hibernate.INTEGER);
			query.addScalar("lname", Hibernate.STRING);
			query.addScalar("value", Hibernate.INTEGER);
			query.addScalar(GeolocationDao.PROJECT_ID, Hibernate.INTEGER);
			query.addScalar("name", Hibernate.STRING);
			query.addScalar(GeolocationDao.DESCRIPTION, Hibernate.STRING);
			query.addScalar(GeolocationDao.PROVINCE_NAME, Hibernate.STRING);
			query.addScalar(GeolocationDao.ISOABBR, Hibernate.STRING);
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
				+ GeolocationDao.AT_GEOLOCATION_DAO + e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return environmentDetails;
	}

	@SuppressWarnings("unchecked")
	public void setLocationNameProvinceAndCountryForLocationsIds(
		final List<TrialEnvironment> environments,
		final List<Integer> locationIds) {

		// Get location name, province and country
		final String sql = "SELECT DISTINCT l.locid, l.lname, prov.lname as provinceName, c.isoabbr "
			+ "	FROM location l " + "	LEFT JOIN location prov ON prov.locid = l.snl1id "
			+ "	LEFT JOIN cntry c ON l.cntryid = c.cntryid " + " WHERE l.locid in (:locationIds)";
		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameterList("locationIds", locationIds);
		query.addScalar("locid", Hibernate.INTEGER);
		query.addScalar("lname", Hibernate.STRING);
		query.addScalar(GeolocationDao.PROVINCE_NAME, Hibernate.STRING);
		query.addScalar(GeolocationDao.ISOABBR, Hibernate.STRING);

		final List<Object[]> result = query.list();

		for (final Object[] row : result) {
			final Integer locationId = (Integer) row[0];
			final String locationName = (String) row[1];
			final String provinceName = (String) row[2];
			final String countryName = (String) row[3];

			for (int i = 0, size = environments.size(); i < size; i++) {
				final TrialEnvironment env = environments.get(i);
				final LocationDto loc = env.getLocation();

				if (loc.getId().intValue() == locationId.intValue()) {
					loc.setLocationName(locationName);
					loc.setProvinceName(provinceName);
					loc.setCountryName(countryName);
					env.setLocation(loc);
				}
			}

		}
	}

	@SuppressWarnings("unchecked")
	public TrialEnvironments getEnvironmentsForTraits(final List<Integer> traitIds) {
		final TrialEnvironments environments = new TrialEnvironments();
		try {
			final String sql =
				"SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname as locationName, prov.lname as provinceName, c.isoabbr, p.project_id, p.name, gp.value as locationId"
					+ " FROM project p"
					+ " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id = "
					+ TermId.BELONGS_TO_STUDY.getId()
					+ " INNER JOIN nd_experiment e ON (e.project_id = p.project_id OR e.project_id = pr.subject_project_id)"
					+ " INNER JOIN phenotype ph ON ph.nd_experiment_id = e.nd_experiment_id"
					+ " INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
					+ TermId.LOCATION_ID.getId() + " LEFT JOIN location l ON l.locid = gp.value"
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id"
					+ " LEFT JOIN cntry c ON c.cntryid = l.cntryid" + " WHERE ph.observable_id IN (:traitIds);";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addScalar(GeolocationDao.ENVT_ID);
			query.addScalar(GeolocationDao.LOCATION_NAME);
			query.addScalar(GeolocationDao.PROVINCE_NAME);
			query.addScalar(GeolocationDao.ISOABBR);
			query.addScalar(GeolocationDao.PROJECT_ID);
			query.addScalar("name");
			query.addScalar(GeolocationDao.LOCATION_ID);
			query.setParameterList("traitIds", traitIds);
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
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return environments;
	}

	@SuppressWarnings("unchecked")
	public Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(
		final String projectName,
		final String locationDescription, final String programUUID) {
		try {
			final String sql = "SELECT DISTINCT e.nd_geolocation_id"
				+ " FROM nd_experiment e, project p, nd_geolocation g, project_relationship pr "
				+ " WHERE e.project_id = pr.subject_project_id " + "   and pr.type_id = "
				+ TermId.BELONGS_TO_STUDY.getId()
				// link to the dataset instead
				+ "   and pr.object_project_id = p.project_id "
				+ "   and e.nd_geolocation_id = g.nd_geolocation_id " + "   and p.name = :projectName"
				+ "   and p.program_uuid = :programUUID" + "   and g.description = :locationDescription";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectName", projectName);
			query.setParameter("locationDescription", locationDescription);
			query.setParameter("programUUID", programUUID);
			final List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getLocationIdByProjectNameAndDescription with project name ="
				+ projectName + " and location description = " + locationDescription
				+ GeolocationDao.AT_GEOLOCATION_DAO + e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctTrialInstances() {
		final List<ValueReference> results = new ArrayList<>();

		try {
			final String sql = "SELECT DISTINCT description FROM nd_geolocation";
			final Query query = this.getSession().createSQLQuery(sql);
			final List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (final String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getDistinctTrialInstances " + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctLatitudes() {
		final List<ValueReference> results = new ArrayList<>();

		try {
			final String sql = "SELECT DISTINCT latitude FROM nd_geolocation";
			final Query query = this.getSession().createSQLQuery(sql);
			final List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (final String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getDistinctLatitudes " + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctLongitudes() {
		final List<ValueReference> results = new ArrayList<>();

		try {
			final String sql = "SELECT DISTINCT longitude FROM nd_geolocation";
			final Query query = this.getSession().createSQLQuery(sql);
			final List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (final String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getDistinctLongitudes " + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctAltitudes() {
		final List<ValueReference> results = new ArrayList<>();

		try {
			final String sql = "SELECT DISTINCT altitude FROM nd_geolocation";
			final Query query = this.getSession().createSQLQuery(sql);
			final List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (final String row : list) {
					results.add(new ValueReference(row, row));
				}
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getDistinctAltitudes " + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctDatums() {
		final List<ValueReference> results = new ArrayList<>();

		try {
			final String sql = "SELECT DISTINCT geodetic_datum FROM nd_geolocation";
			final Query query = this.getSession().createSQLQuery(sql);
			final List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (final String row : list) {
					results.add(new ValueReference(row, row));
				}
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error at getDistinctDatums " + GeolocationDao.AT_GEOLOCATION_DAO
				+ e.getMessage();
			GeolocationDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return results;
	}

	public List<InstanceMetadata> getInstanceMetadata(final int studyId) {

		final String queryString = "select \n" + "    geoloc.nd_geolocation_id as instanceDBId, \n"
			+ "    geoloc.description as instanceNumber, \n" + "    pmain.project_id trialDbId, \n"
			+ "    pmain.name as trialName, \n" + "    proj.name as instanceDatasetName, \n"
			+ "    pmain.program_uuid as programDbId, \n"
			+ "    max(if(geoprop.type_id = 8190, loc.lname, null)) as LOCATION_NAME, \n"
			+ "    max(if(geoprop.type_id = 8190, geoprop.value, null)) as LOCATION_ID, \n"
			+ "    max(if(geoprop.type_id = 8189, geoprop.value, null)) as LOCATION_ABBR, \n"
			+ "    max(if(geoprop.type_id = 8370, geoprop.value, null)) as CROP_SEASON \n" + " from  \n"
			+ " nd_geolocation geoloc \n"
			+ "    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n"
			+ "    inner join project proj on proj.project_id = nde.project_id \n"
			+ "    inner join project_relationship pr on proj.project_id = pr.subject_project_id \n"
			+ "    inner join project pmain on pmain.project_id = pr.object_project_id and pr.type_id = 1150 \n"
			+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
			+ "	   left outer join location loc on geoprop.value = loc.locid and geoprop.type_id = 8190 \n"
			+ " where nde.type_id = 1020 and pmain.project_id = :studyId \n"
			+ "    group by geoloc.nd_geolocation_id " + "    order by geoloc.nd_geolocation_id asc \n";

		final SQLQuery query = this.getSession().createSQLQuery(queryString);

		query.setParameter("studyId", studyId);

		query.addScalar("instanceDBId", new IntegerType());
		query.addScalar("instanceNumber");
		query.addScalar("trialDbId", new IntegerType());
		query.addScalar("trialName");
		query.addScalar("instanceDatasetName");
		query.addScalar("programDbId");
		query.addScalar("LOCATION_NAME");
		query.addScalar("LOCATION_ID", new IntegerType());
		query.addScalar("LOCATION_ABBR");
		query.addScalar("CROP_SEASON");

		@SuppressWarnings("rawtypes") final List results = query.list();

		final List<InstanceMetadata> tiMetadata = new ArrayList<>();
		for (final Object result : results) {
			final Object[] row = (Object[]) result;

			final InstanceMetadata metadata = new InstanceMetadata();
			metadata.setInstanceDbId((Integer) row[0]);
			metadata.setInstanceNumber(String.valueOf(row[1]));
			metadata.setTrialDbId((Integer) row[2]);
			metadata.setTrialName(String.valueOf(row[3]));
			metadata.setInstanceDatasetName(String.valueOf(row[4]));
			metadata.setProgramDbId(String.valueOf(row[5]));
			metadata.setLocationName(String.valueOf(row[6]));
			metadata.setLocationDbId((Integer) row[7]);
			metadata.setLocationAbbreviation(String.valueOf(row[8]));
			metadata.setSeason(String.valueOf(row[9]));
			tiMetadata.add(metadata);
		}
		return tiMetadata;
	}

	public List<Geolocation> getEnvironmentGeolocations(final Integer studyId) {
		List<Geolocation> returnList = new ArrayList<>();
		if (studyId != null) {
			try {
				final String sql = "SELECT g.* " + //
					" FROM nd_geolocation g " + //
					" INNER JOIN nd_experiment exp ON (exp.nd_geolocation_id = g.nd_geolocation_id) " + //
					" INNER JOIN project_relationship pr ON (pr.subject_project_id = exp.project_id) " + //
					" INNER JOIN project envdataset on (envdataset.project_id = pr.subject_project_id) " + //
					" WHERE pr.object_project_id = :studyId and envdataset.name like '%-ENVIRONMENT' ";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("g", Geolocation.class);
				query.setParameter("studyId", studyId);
				returnList = query.list();
			} catch (final HibernateException e) {
				throw new MiddlewareQueryException("Error with getEnvironmentGeolocations(studyId=" + studyId + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}

	public Optional<InstanceMetadata> getInstanceMetadataByInstanceId(final int studyId, final int instanceId) {

		if (studyId != 0 && instanceId != 0) {
			final String queryString = "select \n" + "    geoloc.nd_geolocation_id as instanceDbId, \n"
				+ "    geoloc.description as instanceNumber, \n" + "    pmain.project_id trialDbId, \n"
				+ "    pmain.name as trialName, \n" + "    proj.name as instanceDatasetName, \n"
				+ "    pmain.program_uuid as programDbId, \n"
				+ "    max(if(geoprop.type_id = 8190, loc.lname, null)) as locationName, \n"
				+ "    max(if(geoprop.type_id = 8190, geoprop.value, null)) as locationDbId, \n"
				+ "    max(if(geoprop.type_id = 8189, geoprop.value, null)) as locationAbbreviation, \n"
				+ "    max(if(geoprop.type_id = 8370, geoprop.value, null)) as season \n" + " from  \n"
				+ " nd_geolocation geoloc \n"
				+ "    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "    inner join project proj on proj.project_id = nde.project_id \n"
				+ "    inner join project_relationship pr on proj.project_id = pr.subject_project_id \n"
				+ "    inner join project pmain on pmain.project_id = pr.object_project_id and pr.type_id = 1150 \n"
				+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "	   left outer join location loc on geoprop.value = loc.locid and geoprop.type_id = 8190 \n"
				+ " where nde.type_id = 1020 and pmain.project_id = :studyId and geoloc.nd_geolocation_id = :instanceId \n"
				+ "    group by geoloc.nd_geolocation_id";

			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.addScalar("instanceDbId", new IntegerType());
			query.addScalar("instanceNumber");
			query.addScalar("trialDbId", new IntegerType());
			query.addScalar("trialName");
			query.addScalar("instanceDatasetName");
			query.addScalar("programDbId");
			query.addScalar("locationName");
			query.addScalar("locationDbId", new IntegerType());
			query.addScalar("locationAbbreviation");
			query.addScalar("season");
			query.setParameter("studyId", studyId);
			query.setParameter("instanceId", instanceId);
			query.setResultTransformer(Transformers.aliasToBean(InstanceMetadata.class));
			return Optional.fromNullable((InstanceMetadata) query.uniqueResult());
		}

		return Optional.absent();

	}

	public Boolean existInstances(final Set<Integer> instanceIds) {
		for (Integer instanceId : instanceIds) {
			if (this.getById(instanceId) == null) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
}
