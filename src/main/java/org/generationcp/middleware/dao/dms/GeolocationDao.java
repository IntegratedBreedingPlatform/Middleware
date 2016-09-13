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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.hibernate.type.IntegerType;

/**
 * DAO class for {@link Geolocation}.
 *
 */
public class GeolocationDao extends GenericDAO<Geolocation, Integer> {

	private static final String GET_ALL_ENVIRONMENTS_QUERY =
			"SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname AS locationName, prov.lname AS provinceName, "
					+ "       c.isoabbr, p.project_id, p.name, gp.value AS locationId, p.description AS description "
					+ "  FROM nd_geolocationprop gp "
					+ " INNER JOIN nd_experiment e on e.nd_geolocation_id = gp.nd_geolocation_id "
					+ "       AND e.nd_experiment_id = "
					+ "		( "
					+ "			SELECT MIN(nd_experiment_id) "
					+ "			  FROM nd_experiment min"
					+ "			 WHERE min.nd_geolocation_id = gp.nd_geolocation_id"
					+ "		) "
					+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id"
					+ " INNER JOIN project_relationship pr ON (pr.object_project_id = ep.project_id OR pr.subject_project_id = ep.project_id) "
					+ "		AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
					+ " INNER JOIN project p ON p.project_id = pr.object_project_id " + "  LEFT JOIN location l ON l.locid = gp.value "
					+ "  LEFT JOIN location prov ON prov.locid = l.snl1id " + "  LEFT JOIN cntry c ON c.cntryid = l.cntryid "
					+ " WHERE gp.type_id = " + TermId.LOCATION_ID.getId();

	public Geolocation getParentGeolocation(int projectId) throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT DISTINCT g.*" + " FROM nd_geolocation g"
							+ " INNER JOIN nd_experiment se ON se.nd_geolocation_id = g.nd_geolocation_id"
							+ " INNER JOIN nd_experiment_project sep ON sep.nd_experiment_id = se.nd_experiment_id"
							+ " INNER JOIN project_relationship pr ON pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
							+ " AND pr.object_project_id = sep.project_id AND pr.subject_project_id = :projectId";
			Query query = this.getSession().createSQLQuery(sql).addEntity(this.getPersistentClass()).setParameter("projectId", projectId);
			return (Geolocation) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getParentGeolocation=" + projectId + " at GeolocationDao: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Set<Geolocation> findInDataSet(int datasetId) throws MiddlewareQueryException {
		Set<Geolocation> locations = new LinkedHashSet<Geolocation>();
		try {

			String sql =
					"SELECT DISTINCT e.nd_geolocation_id" + " FROM nd_experiment e"
							+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id"
							+ " WHERE ep.project_id = :projectId ORDER BY e.nd_geolocation_id";
			Query query = this.getSession().createSQLQuery(sql).setParameter("projectId", datasetId);
			List<Integer> ids = query.list();
			for (Integer id : ids) {
				locations.add(this.getById(id));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at findInDataSet=" + datasetId + " at GeolocationDao: " + e.getMessage(), e);
		}
		return locations;
	}

	@SuppressWarnings("unchecked")
	public Geolocation findByDescription(String description) throws MiddlewareQueryException {
		try {

			String sql = "SELECT DISTINCT loc.nd_geolocation_id" + " FROM nd_geolocation loc" + " WHERE loc.description = :description";
			Query query = this.getSession().createSQLQuery(sql).setParameter("description", description);
			List<Integer> ids = query.list();
			if (!ids.isEmpty()) {
				return this.getById(ids.get(0));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at findByDescription=" + description + " at GeolocationDao: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> getLocationIds(Integer projectId) throws MiddlewareQueryException {
		Set<Integer> locationIds = new HashSet<Integer>();
		try {
			String sql =
					"SELECT DISTINCT e.nd_geolocation_id" + " FROM nd_experiment e, nd_experiment_project ep "
							+ " WHERE e.nd_experiment_id = ep.nd_experiment_id " + "   and ep.project_id = " + projectId;
			Query query = this.getSession().createSQLQuery(sql);
			locationIds.addAll(query.list());

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getLocationIds=" + projectId + " at GeolocationDao: " + e.getMessage(), e);
		}
		return locationIds;
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getAllTrialEnvironments() throws MiddlewareQueryException {
		List<TrialEnvironment> environments = new ArrayList<TrialEnvironment>();
		try {
			SQLQuery query = this.getSession().createSQLQuery(GeolocationDao.GET_ALL_ENVIRONMENTS_QUERY);
			query.addScalar("envtId");
			query.addScalar("locationName");
			query.addScalar("provinceName");
			query.addScalar("isoabbr");
			query.addScalar("project_id");
			query.addScalar("name");
			query.addScalar("locationId");
			query.addScalar("description");
			List<Object[]> list = query.list();
			for (Object[] row : list) {
				// otherwise it's invalid data and should not be included
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment((Integer) row[0], new LocationDto(Integer.valueOf(row[6].toString()),
							(String) row[1], (String) row[2], (String) row[3]), new StudyReference((Integer) row[4], (String) row[5],
							(String) row[7])));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getAllTrialEnvironments at GeolocationDao: " + e.getMessage(), e);
		}
		return environments;
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getTrialEnvironments(int start, int numOfRows) throws MiddlewareQueryException {
		List<TrialEnvironment> environments = new ArrayList<TrialEnvironment>();
		try {
			SQLQuery query = this.getSession().createSQLQuery(GeolocationDao.GET_ALL_ENVIRONMENTS_QUERY);
			query.addScalar("envtId");
			query.addScalar("locationName");
			query.addScalar("provinceName");
			query.addScalar("isoabbr");
			query.addScalar("project_id");
			query.addScalar("name");
			query.addScalar("locationId");
			query.addScalar("description");
			this.setStartAndNumOfRows(query, start, numOfRows);
			List<Object[]> list = query.list();
			for (Object[] row : list) {
				// otherwise it's invalid data and should not be included
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment((Integer) row[0], new LocationDto(Integer.valueOf(row[6].toString()),
							(String) row[1], (String) row[2], (String) row[3]), new StudyReference((Integer) row[4], (String) row[5],
							(String) row[7])));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getTrialEnvironments at GeolocationDao: " + e.getMessage(), e);
		}
		return environments;
	}

	public long countAllTrialEnvironments() throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT COUNT(DISTINCT nd_geolocation_id) " + " FROM nd_geolocationprop WHERE type_id = " + TermId.LOCATION_ID.getId();
			Query query = this.getSession().createSQLQuery(sql);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countAllTrialEnvironments at GeolocationDao: " + e.getMessage(), e);
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		List<TrialEnvironmentProperty> properties = new ArrayList<TrialEnvironmentProperty>();
		try {
			// if categorical value, get related cvterm.definition as property value.
			// Else, get the value as it's stored in nd_geolocationprop
			String sql =
					"SELECT DISTINCT gp.type_id, cvt.name, cvt.definition, gp.nd_geolocation_id, "
					+ "CASE WHEN (v.name IS NOT NULL AND cvr.cvterm_relationship_id IS NOT NULL) THEN v.definition "
					+ " ELSE gp.value END AS propvalue " + " FROM nd_geolocationprop gp"
					+ " LEFT JOIN cvterm cvt ON gp.type_id = cvt.cvterm_id" + " LEFT JOIN cvterm v ON v.cvterm_id = gp.value"
					+ " LEFT JOIN cvterm_relationship cvr ON cvr.subject_id = gp.type_id AND cvr.type_id = 1190"
					+ " WHERE gp.nd_geolocation_id IN (:environmentIds)" + " ORDER BY gp.type_id, gp.nd_geolocation_id";
			Query query = this.getSession().createSQLQuery(sql).setParameterList("environmentIds", environmentIds);

			int lastId = 0;
			String lastName = new String();
			String lastDescription = new String();
			Map<Integer, String> environmentValuesMap = new HashMap<Integer, String>();

			List<Object[]> result = query.list();
			for (Object[] row : result) {
				Integer id = (Integer) row[0];

				if (lastId != id.intValue()) {
					String name = (String) row[1];
					String description = (String) row[2];

					if (lastId != 0) {
						properties.add(new TrialEnvironmentProperty(lastId, lastName, lastDescription, environmentValuesMap));
					}

					lastId = id;
					lastName = name;
					lastDescription = description;
					environmentValuesMap = new HashMap<Integer, String>();
				}

				environmentValuesMap.put((Integer) row[3], (String) row[4]);
			}

			if (lastId != 0) {
				properties.add(new TrialEnvironmentProperty(lastId, lastName, lastDescription, environmentValuesMap));
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error at getPropertiesForTrialEnvironments=" + environmentIds + " at GeolocationDao: " + e.getMessage(), e);
		}
		return properties;
	}

	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getTrialEnvironmentDetails(Set<Integer> environmentIds) throws MiddlewareQueryException {
		List<TrialEnvironment> environmentDetails = new ArrayList<TrialEnvironment>();

		if (environmentIds.isEmpty()) {
			return environmentDetails;
		}

		try {

			// Get location name, study id and study name
			String sql =
					"SELECT DISTINCT e.nd_geolocation_id, l.lname, gp.value, p.project_id, p.name, p.description, prov.lname as provinceName, c.isoabbr "
							+ "FROM nd_experiment e " + "	INNER JOIN nd_geolocationprop gp ON e.nd_geolocation_id = gp.nd_geolocation_id "
							+ "						AND gp.type_id =  " + TermId.LOCATION_ID.getId() + " 					AND e.nd_geolocation_id IN (:locationIds) "
							+ "	LEFT JOIN location l ON l.locid = gp.value " + "	LEFT JOIN location prov ON prov.locid = l.snl1id "
							+ "	LEFT JOIN cntry c ON l.cntryid = c.cntryid "
							+ "	INNER JOIN nd_experiment_project ep ON e.nd_experiment_id = ep.nd_experiment_id "
							+ "	INNER JOIN project_relationship pr ON pr.subject_project_id = ep.project_id AND pr.type_id = "
							+ TermId.BELONGS_TO_STUDY.getId() + " " + "	INNER JOIN project p ON p.project_id = pr.object_project_id ";

			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("locationIds", environmentIds);
			query.addScalar("nd_geolocation_id", Hibernate.INTEGER);
			query.addScalar("lname", Hibernate.STRING);
			query.addScalar("value", Hibernate.INTEGER);
			query.addScalar("project_id", Hibernate.INTEGER);
			query.addScalar("name", Hibernate.STRING);
			query.addScalar("description", Hibernate.STRING);
			query.addScalar("provinceName", Hibernate.STRING);
			query.addScalar("isoabbr", Hibernate.STRING);
			List<Integer> locIds = new ArrayList<Integer>();

			List<Object[]> result = query.list();

			for (Object[] row : result) {
				Integer environmentId = (Integer) row[0];
				String locationName = (String) row[1];
				Integer locId = (Integer) row[2];
				Integer studyId = (Integer) row[3];
				String studyName = (String) row[4];
				String studyDescription = (String) row[5];
				String provinceName = (String) row[6];
				String countryName = (String) row[7];

				environmentDetails.add(new TrialEnvironment(environmentId, new LocationDto(locId, locationName, provinceName, countryName),
						new StudyReference(studyId, studyName, studyDescription)));
				locIds.add(locId);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getTrialEnvironmentDetails=" + environmentIds + " at GeolocationDao: " + e.getMessage(), e);
		}

		return environmentDetails;
	}

	@SuppressWarnings("unchecked")
	public void setLocationNameProvinceAndCountryForLocationsIds(List<TrialEnvironment> environments, List<Integer> locationIds)
			throws MiddlewareQueryException {

		// Get location name, province and country
		String sql =
				"SELECT DISTINCT l.locid, l.lname, prov.lname as provinceName, c.isoabbr " + "	FROM location l "
						+ "	LEFT JOIN location prov ON prov.locid = l.snl1id " + "	LEFT JOIN cntry c ON l.cntryid = c.cntryid "
						+ " WHERE l.locid in (:locationIds)";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameterList("locationIds", locationIds);
		query.addScalar("locid", Hibernate.INTEGER);
		query.addScalar("lname", Hibernate.STRING);
		query.addScalar("provinceName", Hibernate.STRING);
		query.addScalar("isoabbr", Hibernate.STRING);

		List<Object[]> result = query.list();

		for (Object[] row : result) {
			Integer locationId = (Integer) row[0];
			String locationName = (String) row[1];
			String provinceName = (String) row[2];
			String countryName = (String) row[3];

			for (int i = 0, size = environments.size(); i < size; i++) {
				TrialEnvironment env = environments.get(i);
				LocationDto loc = env.getLocation();

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
	public TrialEnvironments getEnvironmentsForTraits(List<Integer> traitIds) throws MiddlewareQueryException {
		TrialEnvironments environments = new TrialEnvironments();
		try {
			String sql =
					"SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname as locationName, prov.lname as provinceName, c.isoabbr, p.project_id, p.name, gp.value as locationId"
							+ " FROM project p"
							+ " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id = "
							+ TermId.BELONGS_TO_STUDY.getId()
							+ " INNER JOIN nd_experiment_project ep ON (ep.project_id = p.project_id OR ep.project_id = pr.subject_project_id)"
							+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = ep.nd_experiment_id"
							+ " INNER JOIN nd_experiment_phenotype eph ON eph.nd_experiment_id = e.nd_experiment_id"
							+ " INNER JOIN phenotype ph ON eph.phenotype_id = ph.phenotype_id"
							+ " INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
							+ TermId.LOCATION_ID.getId()
							+ " LEFT JOIN location l ON l.locid = gp.value"
							+ " LEFT JOIN location prov ON prov.locid = l.snl1id"
							+ " LEFT JOIN cntry c ON c.cntryid = l.cntryid"
							+ " WHERE ph.observable_id IN (:traitIds);";
			SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addScalar("envtId");
			query.addScalar("locationName");
			query.addScalar("provinceName");
			query.addScalar("isoabbr");
			query.addScalar("project_id");
			query.addScalar("name");
			query.addScalar("locationId");
			query.setParameterList("traitIds", traitIds);
			List<Object[]> list = query.list();
			for (Object[] row : list) {
				// otherwise it's invalid data and should not be included
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment((Integer) row[0], new LocationDto(Integer.valueOf(row[6].toString()),
							(String) row[1], (String) row[2], (String) row[3]), new StudyReference((Integer) row[4], (String) row[5])));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getEnvironmentForTraits at GeolocationDao: " + e.getMessage(), e);
		}
		return environments;
	}

	@SuppressWarnings("unchecked")
	public Integer getLocationIdByProjectNameAndDescriptionAndProgramUUID(String projectName, String locationDescription, String programUUID)
			throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT DISTINCT e.nd_geolocation_id"
							+ " FROM nd_experiment e, nd_experiment_project ep, project p, nd_geolocation g, project_relationship pr "
							+ " WHERE e.nd_experiment_id = ep.nd_experiment_id " + "   and ep.project_id = pr.subject_project_id "
							+ "   and pr.type_id = "
							+ TermId.BELONGS_TO_STUDY.getId()
							// link to the dataset instead
							+ "   and pr.object_project_id = p.project_id " + "   and e.nd_geolocation_id = g.nd_geolocation_id "
							+ "   and p.name = :projectName" + "   and p.program_uuid = :programUUID"
							+ "   and g.description = :locationDescription";
			Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectName", projectName);
			query.setParameter("locationDescription", locationDescription);
			query.setParameter("programUUID", programUUID);
			List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getLocationIdByProjectNameAndDescription with project name =" + projectName
					+ " and location description = " + locationDescription + " at GeolocationDao: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctTrialInstances() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();

		try {
			String sql = "SELECT DISTINCT description FROM nd_geolocation";
			Query query = this.getSession().createSQLQuery(sql);
			List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getDistinctTrialInstances " + " at GeolocationDao: " + e.getMessage(), e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctLatitudes() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();

		try {
			String sql = "SELECT DISTINCT latitude FROM nd_geolocation";
			Query query = this.getSession().createSQLQuery(sql);
			List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getDistinctLatitudes " + " at GeolocationDao: " + e.getMessage(), e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctLongitudes() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();

		try {
			String sql = "SELECT DISTINCT longitude FROM nd_geolocation";
			Query query = this.getSession().createSQLQuery(sql);
			List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getDistinctLongitudes " + " at GeolocationDao: " + e.getMessage(), e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctAltitudes() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();

		try {
			String sql = "SELECT DISTINCT altitude FROM nd_geolocation";
			Query query = this.getSession().createSQLQuery(sql);
			List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(row, row));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getDistinctAltitudes " + " at GeolocationDao: " + e.getMessage(), e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctDatums() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();

		try {
			String sql = "SELECT DISTINCT geodetic_datum FROM nd_geolocation";
			Query query = this.getSession().createSQLQuery(sql);
			List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(row, row));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getDistinctDatums " + " at GeolocationDao: " + e.getMessage(), e);
		}

		return results;
	}

	public List<InstanceMetadata> getInstanceMetadata(final int studyId) {

		String queryString = "select \n"
				+ "    geoloc.nd_geolocation_id as instanceDBId, \n"
				+ "    geoloc.description as instanceNumber, \n" 
				+ "    pmain.project_id trialDbId, \n" 
				+ "    pmain.name as trialName, \n"
				+ "    proj.name as instanceDatasetName, \n" 
				+ "    pmain.program_uuid as programDbId, \n"
				+ "    max(if(geoprop.type_id = 8180, geoprop.value, null)) as LOCATION_NAME, \n"
				+ "    max(if(geoprop.type_id = 8190, geoprop.value, null)) as LOCATION_ID, \n"
				+ "    max(if(geoprop.type_id = 8189, geoprop.value, null)) as LOCATION_ABBR, \n"
				+ "    max(if(geoprop.type_id = 8370, geoprop.value, null)) as CROP_SEASON \n"
				+ " from  \n"
				+ " nd_geolocation geoloc \n"
				+ "    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "    inner join nd_experiment_project ndep on ndep.nd_experiment_id = nde.nd_experiment_id \n"
				+ "    inner join project proj on proj.project_id = ndep.project_id \n"
				+ "    inner join project_relationship pr on proj.project_id = pr.subject_project_id \n"
				+ "    inner join project pmain on pmain.project_id = pr.object_project_id and pr.type_id = 1150 \n"
				+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ " where nde.type_id = 1020 and pmain.project_id = :studyId \n"
				+ "    group by geoloc.nd_geolocation_id "
				+ "    order by geoloc.nd_geolocation_id asc \n";
		
		
		SQLQuery query = getSession().createSQLQuery(queryString);

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

		@SuppressWarnings("rawtypes")
		final List results = query.list();

		List<InstanceMetadata> tiMetadata = new ArrayList<>();
		for (Object result : results) {
			final Object[] row = (Object[]) result;

			InstanceMetadata metadata = new InstanceMetadata();
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
}
