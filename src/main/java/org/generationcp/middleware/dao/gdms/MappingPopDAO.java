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

package org.generationcp.middleware.dao.gdms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link MappingPop}.
 *
 * @author Joyce Avestro
 *
 */
public class MappingPopDAO extends GenericDAO<MappingPop, Integer> {

	public static final String GET_PARENTS_BY_DATASET_ID = "SELECT parent_a_nid, parent_b_nid, mapping_type " + "FROM gdms_mapping_pop "
			+ "WHERE dataset_id = :datasetId";

	public static final String GET_MAPPING_VALUES_BY_GIDS_AND_MARKER_IDS = "SELECT DISTINCT" + " gdms_mapping_pop_values.dataset_id"
			+ ", gdms_mapping_pop.mapping_type" + ", gdms_mapping_pop.parent_a_nid" + ", gdms_mapping_pop.parent_b_nid"
			+ ", gdms_mapping_pop_values.gid " + ", gdms_mapping_pop_values.marker_id " + ", CONCAT(gdms_marker.marker_type, '')"
			+ ", gdms_mapping_pop_values.marker_sample_id " + ", gdms_mapping_pop_values.acc_sample_id " + " FROM gdms_mapping_pop_values "
			+ "JOIN gdms_mapping_pop ON gdms_mapping_pop_values.dataset_id = gdms_mapping_pop.dataset_id "
			+ "LEFT JOIN gdms_marker ON gdms_mapping_pop_values.marker_id = gdms_marker.marker_id "
			+ " WHERE gdms_mapping_pop_values.marker_id IN (:markerIdList)" + " AND gdms_mapping_pop_values.gid IN (:gidList)"
			+ " ORDER BY" + " gdms_mapping_pop_values.gid DESC" + ", gdms_marker.marker_name";

	public static final String GET_ALL_PARENTS_FROM_MAPPING_POPULATION = "SELECT parent_a_nid, parent_b_nid " + "FROM gdms_mapping_pop";

	public static final String COUNT_ALL_PARENTS_FROM_MAPPING_POPULATION = "SELECT count(parent_a_nid) " + "FROM gdms_mapping_pop";

	public MappingPopDAO(final Session session) {
		super(session);
	}

	@SuppressWarnings("rawtypes")
	public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		SQLQuery query = this.getSession().createSQLQuery(MappingPopDAO.GET_PARENTS_BY_DATASET_ID);
		query.setParameter("datasetId", datasetId);

		List<ParentElement> dataValues = new ArrayList<ParentElement>();
		try {
			List results = query.list();

			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer parentAGId = (Integer) result[0];
					Integer parentBGId = (Integer) result[1];
					String mappingPopType = (String) result[2];
					ParentElement parentElement = new ParentElement(parentAGId, parentBGId, mappingPopType);
					dataValues.add(parentElement);
				}
			}
			return dataValues;
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getParentsByDatasetId(datasetId=" + datasetId + ") query from MappingPop: " + e.getMessage(), e);
		}
		return dataValues;
	}

	@SuppressWarnings("rawtypes")
	public List<MappingValueElement> getMappingValuesByGidAndMarkerIds(List<Integer> gids, List<Integer> markerIds)
			throws MiddlewareQueryException {
		List<MappingValueElement> mappingValues = new ArrayList<MappingValueElement>();

		if (gids == null || gids.isEmpty() || markerIds == null || markerIds.isEmpty()) {
			return mappingValues;
		}

		SQLQuery query = this.getSession().createSQLQuery(MappingPopDAO.GET_MAPPING_VALUES_BY_GIDS_AND_MARKER_IDS);
		query.setParameterList("markerIdList", markerIds);
		query.setParameterList("gidList", gids);

		try {
			List results = query.list();

			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer datasetId = (Integer) result[0];
					String mappingPopType = (String) result[1];
					Integer parentAGid = (Integer) result[2];
					Integer parentBGid = (Integer) result[3];
					Integer gid = (Integer) result[4];
					Integer markerId = (Integer) result[5];
					String markerType = (String) result[6];
					Integer markerSampleId = (Integer) result[7];
					Integer accSampleId = (Integer) result[8];
					MappingValueElement mappingValueElement =
							new MappingValueElement(datasetId, mappingPopType, parentAGid, parentBGid, gid, markerId, markerType,
									markerSampleId, accSampleId);
					mappingValues.add(mappingValueElement);
				}
			}
			return mappingValues;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMappingValuesByGidAndMarkerIds(gids=" + gids + ", markerIds=" + markerIds
					+ ") query from MappingPop: " + e.getMessage(), e);
		}
		return mappingValues;
	}

	@SuppressWarnings("rawtypes")
	public List<ParentElement> getAllParentsFromMappingPopulation(int start, int numOfRows) throws MiddlewareQueryException {

		SQLQuery query = this.getSession().createSQLQuery(MappingPopDAO.GET_ALL_PARENTS_FROM_MAPPING_POPULATION);
		query.setFirstResult(start);
		query.setMaxResults(numOfRows);

		List<ParentElement> dataValues = new ArrayList<ParentElement>();
		try {
			List results = query.list();

			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					int parentAGId = (Integer) result[0];
					int parentBGId = (Integer) result[1];
					String mappingPopType = null;
					ParentElement parentElement = new ParentElement(parentAGId, parentBGId, mappingPopType);
					dataValues.add(parentElement);
				}
			}
			return dataValues;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllParentsFromMappingPopulation() query from MappingPop: " + e.getMessage(), e);
		}
		return dataValues;
	}

	public Long countAllParentsFromMappingPopulation() throws MiddlewareQueryException {

		SQLQuery query = this.getSession().createSQLQuery(MappingPopDAO.COUNT_ALL_PARENTS_FROM_MAPPING_POPULATION);

		try {
			BigInteger result = (BigInteger) query.uniqueResult();
			return result.longValue();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAllParentsFromMappingPopulation() query from MappingPop: " + e.getMessage(), e);
		}
		return 0L;
	}

	public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_mapping_pop WHERE dataset_id = " + datasetId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MappingPopDAO: " + e.getMessage(), e);
		}
	}

	public MappingPop getMappingPopByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.eq("datasetId", datasetId));
				return (MappingPop) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getMappingPopByDatasetId(datasetId=" + datasetId + ") query from MappingPop " + e.getMessage(), e);
		}
		return null;

	}

}
