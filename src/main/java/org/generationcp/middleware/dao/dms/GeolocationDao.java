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
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
	private static final String DESCRIPTION = "description";
	private static final String AT_GEOLOCATION_DAO = " at GeolocationDao: ";


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

	public List<InstanceMetadata> getInstanceMetadata(final int studyId, final List<Integer> locationIds) {

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
			+ " where nde.type_id = 1020 and pmain.project_id = :studyId \n";

		final StringBuilder strBuilder = new StringBuilder(queryString);
		final boolean locationFilterSpecified = !CollectionUtils.isEmpty(locationIds);
		if (locationFilterSpecified) {
			strBuilder.append("    and geoprop.value in (:locationIds) ");
		}
		strBuilder.append("    group by geoloc.nd_geolocation_id ");
		strBuilder.append("    order by geoloc.nd_geolocation_id asc \n");
		final SQLQuery query = this.getSession().createSQLQuery(strBuilder.toString());

		query.setParameter("studyId", studyId);
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

	public Boolean existInstances(final Set<Integer> instanceIds) {
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
}
