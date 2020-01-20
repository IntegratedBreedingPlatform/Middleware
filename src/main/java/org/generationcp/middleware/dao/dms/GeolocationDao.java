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

}
