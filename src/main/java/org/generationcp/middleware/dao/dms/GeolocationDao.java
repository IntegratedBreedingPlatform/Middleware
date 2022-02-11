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

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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
			+ " INNER JOIN project ds ON ds.project_id = e.project_id "
			+ " INNER JOIN project p ON p.project_id = ds.study_id "
			+ "  LEFT JOIN location l ON l.locid = gp.value " + "  LEFT JOIN location prov ON prov.locid = l.snl1id "
			+ "  LEFT JOIN cntry c ON c.cntryid = l.cntryid "
			+ " WHERE gp.type_id = " + TermId.LOCATION_ID.getId() +  " AND p.deleted = 0 ";

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
					+ "	AND gp.type_id =  " + TermId.LOCATION_ID.getId()
					+ "	LEFT JOIN location l ON l.locid = gp.value "
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id "
					+ "	LEFT JOIN cntry c ON l.cntryid = c.cntryid "
					+ " INNER JOIN project ds ON ds.project_id = e.project_id "
					+ "	INNER JOIN project p ON p.project_id = ds.study_id "
					+ " WHERE e.nd_geolocation_id IN (:locationIds) ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameterList("locationIds", environmentIds);
			query.addScalar("nd_geolocation_id", IntegerType.INSTANCE);
			query.addScalar("lname", StringType.INSTANCE);
			query.addScalar("value", IntegerType.INSTANCE);
			query.addScalar(GeolocationDao.PROJECT_ID, IntegerType.INSTANCE);
			query.addScalar("name", StringType.INSTANCE);
			query.addScalar(GeolocationDao.DESCRIPTION, StringType.INSTANCE);
			query.addScalar(GeolocationDao.PROVINCE_NAME, StringType.INSTANCE);
			query.addScalar(GeolocationDao.ISOABBR, StringType.INSTANCE);
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
	public TrialEnvironments getEnvironmentsForTraits(final List<Integer> traitIds, final String programUUID) {
		final TrialEnvironments environments = new TrialEnvironments();
		try {
			final String sql =
				"SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname as locationName, prov.lname as provinceName, c.isoabbr, p.project_id, p.name, gp.value as locationId"
					+ " FROM nd_experiment e "
					+ " INNER JOIN project ds ON ds.project_id = e.project_id "
					+ " INNER JOIN project p ON p.project_id = ds.study_id "
					+ " INNER JOIN phenotype ph ON ph.nd_experiment_id = e.nd_experiment_id"
					+ " INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
					+ TermId.LOCATION_ID.getId()
					+ " LEFT JOIN location l ON l.locid = gp.value"
					+ " LEFT JOIN location prov ON prov.locid = l.snl1id"
					+ " LEFT JOIN cntry c ON c.cntryid = l.cntryid"
					+ " WHERE ph.observable_id IN (:traitIds) AND p.program_uuid = :programUUID AND p.deleted = 0"
					+ " AND ph.value IS NOT NULL;";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addScalar(GeolocationDao.ENVT_ID);
			query.addScalar(GeolocationDao.LOCATION_NAME);
			query.addScalar(GeolocationDao.PROVINCE_NAME);
			query.addScalar(GeolocationDao.ISOABBR);
			query.addScalar(GeolocationDao.PROJECT_ID);
			query.addScalar("name");
			query.addScalar(GeolocationDao.LOCATION_ID);
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
				+ " FROM nd_experiment e"
				+ " INNER JOIN nd_geolocation g ON g.nd_geolocation_id = e.nd_geolocation_id"
				+ " INNER JOIN project p ON e.project_id = p.project_id "
				+ " INNER JOIN project st ON st.project_id = p.study_id "
				+ " WHERE st.name = :projectName"
				+ "   and st.program_uuid = :programUUID" + "   and g.description = :locationDescription";
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

	public List<InstanceMetadata> getInstanceMetadata(final List<Integer> studyIds, final List<Integer> locationIds) {

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
			+ "    inner join project pmain on pmain.project_id = proj.study_id \n"
			+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
			+ "	   left outer join location loc on geoprop.value = loc.locid and geoprop.type_id = 8190 \n"
			+ " where nde.type_id = 1020 and pmain.project_id IN (:studyIds) \n";

		final StringBuilder strBuilder = new StringBuilder(queryString);
		final boolean locationFilterSpecified = !CollectionUtils.isEmpty(locationIds);
		if (locationFilterSpecified) {
			strBuilder.append("    and geoprop.value in (:locationIds) ");
		}
		strBuilder.append("    group by geoloc.nd_geolocation_id ");
		strBuilder.append("    order by geoloc.nd_geolocation_id asc \n");
		final SQLQuery query = this.getSession().createSQLQuery(strBuilder.toString());

		query.setParameterList("studyIds", studyIds);
		if (locationFilterSpecified) {
			query.setParameterList("locationIds", locationIds);
		}
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

	public List<Geolocation> getEnvironmentGeolocationsForInstances(final Integer studyId, final List<Integer> instanceNumbers) {
		List<Geolocation> returnList = new ArrayList<>();
		if (studyId != null) {
			final String sql = "SELECT DISTINCT g.* " + //
				" FROM nd_geolocation g " + //
				" INNER JOIN nd_experiment exp ON (exp.nd_geolocation_id = g.nd_geolocation_id) " + //
				" INNER JOIN project envdataset on (envdataset.project_id = exp.project_ID) " + //
				" WHERE envdataset.study_id = :studyId and envdataset.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId();
			final StringBuilder sb = new StringBuilder(sql);
			if (!CollectionUtils.isEmpty(instanceNumbers)) {
				sb.append(" AND g.description IN (:instanceNumbers)");
			}
			final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.addEntity("g", Geolocation.class);
			query.setParameter("studyId", studyId);
			if (!CollectionUtils.isEmpty(instanceNumbers)) {
				query.setParameterList("instanceNumbers", instanceNumbers);
			}
			returnList = query.list();

		}
		return returnList;
	}


	public List<Geolocation> getEnvironmentGeolocations(final Integer studyId) {
		return this.getEnvironmentGeolocationsForInstances(studyId, Collections.<Integer>emptyList());
	}

	public boolean isInstancesExist(final Set<Integer> instanceIds) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.in("locationId", instanceIds));
		return instanceIds.size() == criteria.list().size();
	}

	/**
	 * FIXME IBP-3472: make a single query
	 *
	 * @return TRUE if all of the instanceIds exists. Otherwise FALSE
	 */
	public Boolean instanceExists(final Set<Integer> instanceIds) {
		for (final Integer instanceId : instanceIds) {
			if (this.getById(instanceId) == null) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	public Integer getNextInstanceNumber(final Integer datasetId) {
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT max(cast(g.description as unsigned)) ");
		sb.append(" FROM nd_geolocation g ");
		sb.append(" INNER JOIN nd_experiment e ON e.nd_geolocation_id = g.nd_geolocation_id ");
		sb.append(" WHERE e.project_id = :datasetId" );
		final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
		query.setParameter("datasetId", datasetId);
		final BigInteger maxInstanceNumber = (BigInteger) query.uniqueResult();
		if (maxInstanceNumber != null) {
			return maxInstanceNumber.intValue() + 1;
		}
		return 1;
	}

	public void deleteGeolocations(final List<Integer> locationIds) {
		final List<Geolocation> geolocations = this.getByCriteria(Collections.singletonList(Restrictions.in("locationId", locationIds)));
		for (final Geolocation geolocation : geolocations) {
			this.makeTransient(geolocation);
		}
	}


}
