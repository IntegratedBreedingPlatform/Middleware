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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.QtlDataElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link QtlDetails}.
 *
 * @author Joyce Avestro
 *
 */
@SuppressWarnings("unchecked")
public class QtlDetailsDAO extends GenericDAO<QtlDetails, Integer> {

	public static final String GET_MARKER_IDS_BY_QTL = "SELECT marker_id " + "FROM gdms_markers_onmap, gdms_qtl_details, gdms_qtl "
			+ "WHERE gdms_markers_onmap.map_id = gdms_qtl_details.map_id " + "        AND gdms_qtl_details.qtl_id = gdms_qtl.qtl_id "
			+ "        AND gdms_qtl.qtl_name = :qtlName " + "        AND gdms_markers_onmap.linkage_group = :chromosome "
			+ "        AND gdms_markers_onmap.start_position between :min AND :max ";

	public static final String COUNT_MARKER_IDS_BY_QTL = "SELECT COUNT(marker_id)  "
			+ "FROM gdms_markers_onmap, gdms_qtl_details, gdms_qtl " + "WHERE gdms_markers_onmap.map_id = gdms_qtl_details.map_id "
			+ "        AND gdms_qtl_details.qtl_id = gdms_qtl.qtl_id " + "        AND gdms_qtl.qtl_name = :qtlName "
			+ "        AND gdms_markers_onmap.linkage_group = :chromosome "
			+ "        AND gdms_markers_onmap.start_position between :min AND :max ";

	public static final String GET_MAP_IDS_BY_QTL = "SELECT DISTINCT map_id " + "FROM gdms_qtl_details "
			+ "WHERE qtl_id = (SELECT qtl_id FROM gdms_qtl WHERE qtl_name = :qtlName) ";

	public static final String COUNT_MAP_IDS_BY_QTL = "SELECT COUNT(DISTINCT map_id) " + "FROM gdms_qtl_details "
			+ "WHERE qtl_id = (SELECT qtl_id FROM gdms_qtl WHERE qtl_name = :qtlName) ";

	public static final String GET_MARKER_IDS_BY_QTL_AND_MAP_ID = "SELECT DISTINCT gm.marker_id " + "FROM gdms_marker gm "
			+ "    INNER JOIN gdms_markers_onmap gmo ON gm.marker_id = gmo.marker_id "
			+ "    INNER JOIN gdms_qtl_details gqd ON gqd.map_id = gmo.map_id " + "    INNER JOIN gdms_qtl gq ON gq.qtl_id = gqd.qtl_id "
			+ "    WHERE gmo.linkage_group = :chromosome " + "         AND gmo.start_position BETWEEN :min AND :max "
			+ "         AND gmo.map_id = :mapId " + "         AND gq.qtl_name like :qtlName ";

	public static final String COUNT_MARKER_IDS_BY_QTL_AND_MAP_ID = "SELECT COUNT(DISTINCT gm.marker_id) " + "FROM gdms_marker gm "
			+ "    INNER JOIN gdms_markers_onmap gmo ON gm.marker_id = gmo.marker_id "
			+ "    INNER JOIN gdms_qtl_details gqd ON gqd.map_id = gmo.map_id " + "    INNER JOIN gdms_qtl gq ON gq.qtl_id = gqd.qtl_id "
			+ "    WHERE gmo.linkage_group = :chromosome " + "         AND gmo.start_position BETWEEN :min AND :max "
			+ "         AND gmo.map_id = :mapId " + "         AND gq.qtl_name like :qtlName ";

	public static final String GET_MAP_ID_BY_QTL = "SELECT DISTINCT map_id " + "FROM gdms_qtl_details "
			+ "WHERE qtl_id = (SELECT qtl_id FROM gdms_qtl WHERE qtl_name = :qtlName) ";;

	public static final String GET_QTL_TRAITS_BY_DATASET_ID = "SELECT DISTINCT gqd.tid " + "FROM gdms_qtl gq  "
			+ "INNER JOIN gdms_qtl_details gqd  " + "ON gq.qtl_id = gqd.qtl_id  " + "WHERE gq.dataset_id = :datasetId  ";

	public static final String COUNT_QTL_TRAITS_BY_DATASET_ID = "SELECT COUNT(DISTINCT gqd.tid) " + "FROM gdms_qtl gq  "
			+ "INNER JOIN gdms_qtl_details gqd  " + "ON gq.qtl_id = gqd.qtl_id  " + "WHERE gq.dataset_id = :datasetId  ";

	public static final String GET_QTL_DATA_BY_QTL_TRAITS = "SELECT CONCAT(gq.qtl_name,'') " + "		, gqd.linkage_group "
			+ "		, gqd.position " + "		, gqd.min_position " + "		, gqd.max_position " + "		, gqd.tid " + "		, CONCAT(gqd.experiment,'') "
			+ "		, gqd.left_flanking_marker " + "		, gqd.right_flanking_marker " + "		, gqd.effect " + "		, gqd.score_value "
			+ "		, gqd.r_square " + "FROM gdms_qtl_details gqd " + "INNER JOIN gdms_qtl gq " + "ON gqd.qtl_id = gq.qtl_id "
			+ "AND gqd.tid " + "IN (:qtlTraits) ";

	public static final String COUNT_QTL_DATA_BY_QTL_TRAITS = "SELECT COUNT(*) " + "FROM gdms_qtl_details gqd " + "INNER JOIN gdms_qtl gq "
			+ "ON gqd.qtl_id = gq.qtl_id " + "AND gqd.tid " + "IN (:qtlTraits) ";

	public QtlDetailsDAO(final Session session) {
		super(session);
	}

	public List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, float min, float max, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.GET_MARKER_IDS_BY_QTL);
			query.setParameter("qtlName", qtlName);
			query.setParameter("chromosome", chromosome);
			query.setParameter("min", min);
			query.setParameter("max", max);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkerIDsByQtl() query from QtlDetails: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countMarkerIdsByQtl(String qtlName, String chromosome, float min, float max) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.COUNT_MARKER_IDS_BY_QTL);
			query.setParameter("qtlName", qtlName);
			query.setParameter("chromosome", chromosome);
			query.setParameter("min", min);
			query.setParameter("max", max);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with countMarkerIdsByQtl() query from QtlDetails: " + e.getMessage(), e);
		}
		return 0;

	}

	public Integer getMapIdByQtlName(String qtlName) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.GET_MAP_ID_BY_QTL);
			query.setParameter("qtlName", qtlName);
			return (Integer) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMapIdByQtlName() query from QtlDetails: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<Integer> getMapIdsByQtlName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.GET_MAP_IDS_BY_QTL);
			query.setParameter("qtlName", qtlName);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMapIdsByQtlName() query from QtlDetails: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countMapIdsByQtlName(String qtlName) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.COUNT_MAP_IDS_BY_QTL);
			query.setParameter("qtlName", qtlName);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countMapIdsByQtlName() query from QtlDetails: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<Integer> getMarkerIdsByQtl(Integer mapId, String qtlName, String chromosome, int min, int max, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.GET_MARKER_IDS_BY_QTL_AND_MAP_ID);
			query.setParameter("qtlName", qtlName);
			query.setParameter("chromosome", chromosome);
			query.setParameter("min", min);
			query.setParameter("max", max);
			query.setParameter("mapId", mapId);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkerIDsByQtl() query from QtlDetails: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countMarkerIdsByQtl(Integer mapId, String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.COUNT_MARKER_IDS_BY_QTL_AND_MAP_ID);
			query.setParameter("qtlName", qtlName);
			query.setParameter("chromosome", chromosome);
			query.setParameter("min", min);
			query.setParameter("max", max);
			query.setParameter("mapId", mapId);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with countMarkerIdsByQtl() query from QtlDetails: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<Integer> getQtlTraitsByDatasetId(Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.GET_QTL_TRAITS_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getQtlTraitsByDatasetId() query from QtlDetails: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countQtlTraitsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.COUNT_QTL_TRAITS_BY_DATASET_ID);
				query.setParameter("datasetId", datasetId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countQtlTraitsByDatasetId() query from QtlDetails: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<QtlDataElement> getQtlDataByQtlTraits(List<Integer> qtlTraits, int start, int numOfRows) throws MiddlewareQueryException {
		List<QtlDataElement> toReturn = new ArrayList<QtlDataElement>();

		try {
			if (qtlTraits != null && !qtlTraits.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.GET_QTL_DATA_BY_QTL_TRAITS);
				query.setParameterList("qtlTraits", qtlTraits);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);

				@SuppressWarnings("rawtypes")
				List results = query.list();

				for (Object o : results) {
					Object[] result = (Object[]) o;
					if (result != null) {
						// Get the fields for QtlDataElement
						String qtlName = (String) result[0];
						String linkageGroup = (String) result[1];
						Float position = (Float) result[2];
						Float minPosition = (Float) result[3];
						Float maxPosition = (Float) result[4];
						Integer traitId = (Integer) result[5];
						String experiment = (String) result[6];
						String leftFlankingMarker = (String) result[7];
						String rightFlankingMarker = (String) result[8];
						Float effect = (Float) result[9];
						Float scoreValue = (Float) result[10];
						Float rSquare = (Float) result[11];

						QtlDataElement qtlData =
								new QtlDataElement(qtlName, linkageGroup, position, minPosition, maxPosition, traitId, experiment,
										leftFlankingMarker, rightFlankingMarker, effect, scoreValue, rSquare);
						toReturn.add(qtlData);
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getQtlDataByQtlTraits() query from QtlDetails: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public long countQtlDataByQtlTraits(List<Integer> qtlTraits) throws MiddlewareQueryException {
		try {
			if (qtlTraits != null && !qtlTraits.isEmpty()) {
				SQLQuery query = this.getSession().createSQLQuery(QtlDetailsDAO.COUNT_QTL_DATA_BY_QTL_TRAITS);
				query.setParameterList("qtlTraits", qtlTraits);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countQtlDataByQtlTraits() query from QtlDetails: " + e.getMessage(), e);
		}
		return 0;
	}

	public void deleteByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement =
					this.getSession()
							.createSQLQuery("DELETE FROM gdms_qtl_details WHERE qtl_id IN (" + StringUtils.join(qtlIds, ",") + ")");
			statement.executeUpdate();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByQtlIds=" + qtlIds + " in QtlDetailsDAO: " + e.getMessage(), e);
		}
	}

	public List<QtlDetails> getQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("mapId", mapId));

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getQtlDetailsByMapId=" + mapId + " in QtlDetailsDAO: " + e.getMessage(), e);
		}
		return new ArrayList<QtlDetails>();
	}

	public List<QtlDetails> getQtlDetailsByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("qtlId", qtlIds));

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getQtlDetailsByQtlId=" + qtlIds + " in QtlDetailsDAO: " + e.getMessage(), e);
		}
		return new ArrayList<QtlDetails>();
	}

	public long countQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("mapId", mapId));
			criteria.setProjection(Projections.rowCount());

			return (Long) criteria.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in countQtlDetailsByMapId=" + mapId + " in QtlDetailsDAO: " + e.getMessage(), e);
		}
		return 0;
	}
}
