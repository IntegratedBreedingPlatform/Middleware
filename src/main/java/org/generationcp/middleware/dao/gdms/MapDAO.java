/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao.gdms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link Map}.
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jul 9, 2012.
 */
public class MapDAO extends GenericDAO<Map, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(MapDAO.class);

	private static final String GET_MAP_DETAILS_SELECT =
			"SELECT COUNT(gdms_mapping_data.marker_id) AS marker_count " + "       , MAX(gdms_mapping_data.start_position) AS max "
					+ "       , gdms_mapping_data.linkage_group AS Linkage_group "
					+ "       , concat(gdms_mapping_data.map_name,'') AS map " + "       , concat(gdms_map.map_type,'') AS map_type "
					+ "       , gdms_map.map_desc AS map_desc " + "       , gdms_map.map_unit AS map_unit "
					+ "FROM gdms_mapping_data, gdms_map " + "WHERE gdms_mapping_data.map_id=gdms_map.map_id ";

	private static final String GET_MAP_DETAILS_WHERE = "       AND lower(gdms_mapping_data.map_name) LIKE (:nameLike) ";

	private static final String GET_MAP_DETAILS_GROUP_ORDER = "GROUP BY gdms_mapping_data.linkage_group, gdms_mapping_data.map_name "
			+ "ORDER BY gdms_mapping_data.map_name, gdms_mapping_data.linkage_group ";

	public static final String GET_MAP_DETAILS = MapDAO.GET_MAP_DETAILS_SELECT + MapDAO.GET_MAP_DETAILS_GROUP_ORDER;

	public static final String GET_MAP_DETAILS_BY_NAME =
			MapDAO.GET_MAP_DETAILS_SELECT + MapDAO.GET_MAP_DETAILS_WHERE + MapDAO.GET_MAP_DETAILS_GROUP_ORDER;

	public static final String COUNT_MAP_DETAILS = "SELECT COUNT(DISTINCT gdms_mapping_data.linkage_group, gdms_mapping_data.map_name) "
			+ "FROM `gdms_mapping_data` JOIN `gdms_map` ON gdms_mapping_data.map_id=gdms_map.map_id ";

	public static final String COUNT_MAP_DETAILS_BY_NAME =
			MapDAO.COUNT_MAP_DETAILS + "WHERE lower(gdms_mapping_data.map_name) LIKE (:nameLike) ";

	public static final String GET_MAP_ID_BY_NAME = "SELECT map_id FROM gdms_map WHERE map_name = :mapName LIMIT 0,1";

	public static final String GET_MAP_NAME_BY_ID = "SELECT map_name FROM gdms_map WHERE map_id = :mapId";

	public static final String GET_MAP_AND_MARKER_COUNT_BY_MARKERS =
			"SELECT CONCAT(m.map_name, ''), COUNT(k.marker_id) " + "FROM gdms_map m "
					+ "INNER JOIN gdms_markers_onmap k ON k.map_id = m.map_id " + "WHERE k.marker_id IN (:markerIds) "
					+ "GROUP BY m.map_name";

	public static final String GET_MAP_INFO_BY_MAP_AND_CHROMOSOME =
			"SELECT DISTINCT " + "  gdms_markers_onmap.marker_id" + " ,gdms_marker.marker_name" + " ,gdms_map.map_name"
					+ " ,gdms_map.map_type" + " ,gdms_markers_onmap.start_position" + " ,gdms_markers_onmap.linkage_group"
					+ " ,gdms_map.map_unit" + " FROM gdms_map" + "     INNER JOIN gdms_markers_onmap ON"
					+ "         gdms_map.map_id = gdms_markers_onmap.map_id" + "     LEFT JOIN gdms_marker ON"
					+ "         gdms_marker.marker_id = gdms_markers_onmap.marker_id" + " WHERE" + "     gdms_markers_onmap.map_id = :mapId"
					+ "     AND gdms_markers_onmap.linkage_group = :chromosome";

	public static final String GET_MAP_INFO_BY_MAP_CHROMOSOME_AND_POSITION =
			"SELECT DISTINCT " + "  gdms_markers_onmap.marker_id" + " ,gdms_marker.marker_name" + " ,gdms_map.map_name"
					+ " ,gdms_map.map_type" + " ,gdms_markers_onmap.linkage_group" + " ,gdms_map.map_unit" + " FROM gdms_map"
					+ "     INNER JOIN gdms_markers_onmap ON" + "         gdms_map.map_id = gdms_markers_onmap.map_id"
					+ "     LEFT JOIN gdms_marker ON" + "         gdms_marker.marker_id = gdms_markers_onmap.marker_id" + " WHERE"
					+ "     gdms_markers_onmap.map_id = :mapId" + "     AND gdms_markers_onmap.linkage_group = :chromosome"
					+ "     AND gdms_markers_onmap.start_position = :startPosition" + " ORDER BY" + "      gdms_map.map_name"
					+ "     ,gdms_markers_onmap.linkage_group" + "     ,gdms_markers_onmap.start_position ASC";

	public static final String GET_MAP_INFO_BY_MARKERS_AND_MAP =
			"SELECT DISTINCT " + "  gdms_markers_onmap.marker_id" + " ,gdms_marker.marker_name" + " ,gdms_map.map_name"
					+ " ,gdms_map.map_type" + " ,gdms_markers_onmap.start_position" + " ,gdms_markers_onmap.linkage_group"
					+ " ,gdms_map.map_unit" + " FROM gdms_map" + "     INNER JOIN gdms_markers_onmap ON"
					+ "         gdms_map.map_id = gdms_markers_onmap.map_id" + "     LEFT JOIN gdms_marker ON"
					+ "         gdms_marker.marker_id = gdms_markers_onmap.marker_id" + " WHERE"
					+ "     gdms_markers_onmap.marker_id IN (:markerIdList)" + "     AND gdms_markers_onmap.map_id = :mapId" + " ORDER BY"
					+ "     gdms_map.map_name" + "     ,gdms_markers_onmap.linkage_group" + "     ,gdms_markers_onmap.start_position ASC";

	public MapDAO(final Session session) {
		super(session);
	}

	@SuppressWarnings("rawtypes")
	public List<MapDetailElement> getMapDetailsByName(final String nameLike, final int start, final int numOfRows) {

		final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_DETAILS_BY_NAME);
		query.setString("nameLike", nameLike.toLowerCase());
		query.setFirstResult(start);
		query.setMaxResults(numOfRows);

		final List<MapDetailElement> maps = new ArrayList<MapDetailElement>();

		try {

			final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final int markerCount = ((BigInteger) result[0]).intValue();
					final Double maxStartPosition = (Double) result[1];
					final String linkageGroup = (String) result[2];
					final String mapName = (String) result[3];
					final String mapType = (String) result[4];
					final String mapDesc = (String) result[5];
					final String mapUnit = (String) result[6];

					final MapDetailElement map =
							new MapDetailElement(markerCount, maxStartPosition, linkageGroup, mapName, mapType, mapDesc, mapUnit);
					maps.add(map);
				}
			}

			return maps;

		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapDetailsByName() query from Map: " + e.getMessage(), e);
		}
	}

	public List<MapInfo> getMapInfoByMapAndChromosome(final Integer mapId, final String chromosome) {
		final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_INFO_BY_MAP_AND_CHROMOSOME);
		query.setInteger("mapId", mapId);
		query.setString("chromosome", chromosome);

		query.addScalar("marker_id", new IntegerType());
		query.addScalar("marker_name", new StringType());
		query.addScalar("map_name", new StringType());
		query.addScalar("map_type", new StringType());
		query.addScalar("start_position", new FloatType());
		query.addScalar("linkage_group", new StringType());
		query.addScalar("map_unit", new StringType());

		final List<MapInfo> mapInfoList = new ArrayList<MapInfo>();

		try {
			@SuppressWarnings("rawtypes") final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;

				if (result != null) {
					final Integer markerId = (Integer) result[0];
					final String markerName = (String) result[1];
					final String mapName = (String) result[2];
					final String mapType = (String) result[3];
					final Float startPosition = (Float) result[4];
					final String linkageGroup = (String) result[5];
					final String mapUnit = (String) result[6];

					final MapInfo mapInfo =
							new MapInfo(markerId, markerName, mapId, mapName, linkageGroup, startPosition, mapType, mapUnit);
					mapInfoList.add(mapInfo);
				}
			}
		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapInfoByMapAndChromosome() query: " + e.getMessage(), e);
		}

		return mapInfoList;
	}

	public List<MapInfo> getMapInfoByMapChromosomeAndPosition(final Integer mapId, final String chromosome, final Float startPosition) {
		final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_INFO_BY_MAP_CHROMOSOME_AND_POSITION);
		query.setInteger("mapId", mapId);
		query.setString("chromosome", chromosome);
		query.setFloat("startPosition", startPosition);

		query.addScalar("marker_id", new IntegerType());
		query.addScalar("marker_name", new StringType());
		query.addScalar("map_name", new StringType());
		query.addScalar("map_type", new StringType());
		query.addScalar("linkage_group", new StringType());
		query.addScalar("map_unit", new StringType());

		final List<MapInfo> mapInfoList = new ArrayList<MapInfo>();

		try {
			@SuppressWarnings("rawtypes") final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;

				if (result != null) {
					final Integer markerId = (Integer) result[0];
					final String markerName = (String) result[1];
					final String mapName = (String) result[2];
					final String mapType = (String) result[3];
					final String linkageGroup = (String) result[4];
					final String mapUnit = (String) result[5];

					final MapInfo mapInfo =
							new MapInfo(markerId, markerName, mapId, mapName, linkageGroup, startPosition, mapType, mapUnit);
					mapInfoList.add(mapInfo);
				}
			}
		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapInfoByMapChromosomeAndPosition() query: " + e.getMessage(), e);
		}

		return mapInfoList;
	}

	public List<MapInfo> getMapInfoByMarkersAndMap(final List<Integer> markers, final Integer mapId) {
		final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_INFO_BY_MARKERS_AND_MAP);
		query.setParameterList("markerIdList", markers);
		query.setInteger("mapId", mapId);

		query.addScalar("marker_id", new IntegerType());
		query.addScalar("marker_name", new StringType());
		query.addScalar("map_name", new StringType());
		query.addScalar("map_type", new StringType());
		query.addScalar("start_position", new FloatType());
		query.addScalar("linkage_group", new StringType());
		query.addScalar("map_unit", new StringType());

		final List<MapInfo> mapInfoList = new ArrayList<MapInfo>();

		try {
			@SuppressWarnings("rawtypes") final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;

				if (result != null) {
					final Integer markerId = (Integer) result[0];
					final String markerName = (String) result[1];
					final String mapName = (String) result[2];
					final String mapType = (String) result[3];
					final Float startPosition = (Float) result[4];
					final String linkageGroup = (String) result[5];
					final String mapUnit = (String) result[6];

					final MapInfo mapInfo =
							new MapInfo(markerId, markerName, mapId, mapName, linkageGroup, startPosition, mapType, mapUnit);
					mapInfoList.add(mapInfo);
				}
			}
		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapInfoByMarkersAndMap() query: " + e.getMessage(), e);
		}

		return mapInfoList;
	}

	public Map getByName(final String mapName) {
		Map map = null;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("mapName", mapName));
			map = (Map) criteria.uniqueResult();

		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getByName() query: " + e.getMessage(), e);
		}

		return map;
	}

	@Override
	public Map getById(final Integer mapId) {
		Map map = null;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("mapId", mapId));
			map = (Map) criteria.uniqueResult();

		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getById() query: " + e.getMessage(), e);
		}

		return map;
	}

	public Long countMapDetailsByName(final String nameLike) {

		final SQLQuery query = this.getSession().createSQLQuery(MapDAO.COUNT_MAP_DETAILS_BY_NAME);
		query.setString("nameLike", nameLike.toLowerCase());

		try {
			final BigInteger result = (BigInteger) query.uniqueResult();
			return result.longValue();
		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with countMapDetailsByName() query: " + e.getMessage(), e);
		}

	}

	@SuppressWarnings("rawtypes")
	public List<MapDetailElement> getAllMapDetails(final int start, final int numOfRows) {
		final List<MapDetailElement> values = new ArrayList<MapDetailElement>();

		try {
			final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_DETAILS);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					final BigInteger markerCount = (BigInteger) result[0];
					final Double max = (Double) result[1];
					final String linkageGroup = (String) result[2];
					final String mapName2 = (String) result[3];
					final String mapType = (String) result[4];
					final String mapDesc = (String) result[5];
					final String mapUnit = (String) result[6];

					final MapDetailElement element =
							new MapDetailElement(markerCount.intValue(), max, linkageGroup, mapName2, mapType, mapDesc, mapUnit);
					values.add(element);
				}
			}

			return values;
		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getAllMapDetails() query: " + e.getMessage(), e);
		}
	}

	public long countAllMapDetails() {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MapDAO.COUNT_MAP_DETAILS);
			final BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}
			return 0;
		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with countAllMapDetails() query: " + e.getMessage(), e);
		}
	}

	public Integer getMapIdByName(final String mapName) {
		try {

			final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_ID_BY_NAME);
			query.setParameter("mapName", mapName);
			return (Integer) query.uniqueResult();

		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapIdByName(" + mapName + ") in MapDAO: " + e.getMessage(), e);
		}
	}

	public String getMapNameById(final Integer mapId) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_NAME_BY_ID);
			query.setParameter("mapId", mapId);
			return (String) query.addScalar("map_name", StringType.class.newInstance()).uniqueResult();

		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapNameById(" + mapId + ") in MapDAO: " + e.getMessage(), e);
		} catch (final Exception e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapNameById(" + mapId + ") in MapDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<MapDetailElement> getMapAndMarkerCountByMarkers(final List<Integer> markerIds) {
		final List<MapDetailElement> details = new ArrayList<MapDetailElement>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MapDAO.GET_MAP_AND_MARKER_COUNT_BY_MARKERS);
			query.setParameterList("markerIds", markerIds);
			final List results = query.list();
			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				details.add(new MapDetailElement(((BigInteger) result[1]).intValue(), null, null, result[0].toString(), null, null, null));
			}

		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getMapAndMarkerCountByMarkers(" + markerIds + ") in MapDAO: " + e.getMessage(),
					e);
		}
		return details;
	}

	public void deleteByMapId(final int mapId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_map WHERE map_id = " + mapId);
			statement.executeUpdate();
		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error in deleteByMapId=" + mapId + " in MapDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map> getMapsByIds(final List<Integer> mapIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("mapId", mapIds));

			return criteria.list();

		} catch (final HibernateException e) {
			MapDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error in getMapsByIds=" + mapIds + " in MapDAO: " + e.getMessage(), e);
		}

	}

}
