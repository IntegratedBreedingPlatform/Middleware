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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link MarkerOnMap}.
 *
 * <b>Authors</b>: Dennis Billano <br>
 * <b>File Created</b>: Mar 7, 2013
 */

public class MarkerOnMapDAO extends GenericDAO<MarkerOnMap, Integer> {

	private static final String FROM_GDMS_MARKERS_ONMAP = "FROM gdms_markers_onmap ";

	public MarkerOnMapDAO(final Session session) {
		super(session);
	}

	public void deleteByMapId(int mapId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			SQLQuery statement = this.getSession().createSQLQuery("DELETE FROM gdms_markers_onmap WHERE map_id = " + mapId);
			statement.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByMapId=" + mapId + " in MarkerOnMapDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public MarkerOnMap findByMarkerIdAndMapId(int markerId, int mapId) {
		try {

			Query query = this.getSession().createQuery("FROM MarkerOnMap WHERE markerId = :markerId AND mapId = :mapId");
			query.setParameter("markerId", markerId);
			query.setParameter("mapId", mapId);
			List<MarkerOnMap> result = query.list();
			if (result != null && !result.isEmpty()) {
				return result.get(0);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getByMarkerIdAndMapId(markerId=" + markerId + ", mapId=" + mapId + " in MarkerOnMapDAO: "
					+ e.getMessage(), e);
		}
		return null;
		
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<String>> getMapNameByMarkerIds(List<Integer> markerIds) {
		Map<Integer, List<String>> markerMaps = new HashMap<>();

		try {
			StringBuilder sqlString =
					new StringBuilder().append("SELECT DISTINCT marker_id, CONCAT(map_name,'') ")
							.append("FROM gdms_map map INNER JOIN gdms_markers_onmap markermap ON map.map_id = markermap.map_id ")
							.append(" WHERE markermap.marker_id IN (:markerIds) ");

			Query query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("markerIds", markerIds);

			List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					Integer markerId = (Integer) row[0];
					String mapName = (String) row[1];

					List<String> mapNames = new ArrayList<>();
					if (markerMaps.containsKey(markerId)) {
						mapNames = markerMaps.get(markerId);
					}
					mapNames.add(mapName);
					markerMaps.put(markerId, mapNames);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getMapNameByMarkerIds() query from MarkerOnMap: " + e.getMessage(), e);
		}
		return markerMaps;

	}

	@SuppressWarnings("unchecked")
	public List<MarkerOnMap> getMarkersOnMapByMapId(Integer mapId) {
		List<MarkerOnMap> markersOnMap = new ArrayList<>();

		try {
			StringBuilder sqlString =
					new StringBuilder().append("SELECT markeronmap_id, map_id, marker_id, start_position, end_position, linkage_group ")
							.append("FROM gdms_markers_onmap  ").append("WHERE map_id = :mapId  ")
							.append("ORDER BY linkage_group, start_position ");
			Query query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("mapId", mapId);

			List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					Integer markerOnMapId = (Integer) row[0];
					Integer mapId2 = (Integer) row[1];
					Integer markerId = (Integer) row[2];
					Double startPosition = (Double) row[3];
					Double endPosition = (Double) row[4];
					String linkageGroup = (String) row[5];

					final Float startPositionFloatValue = startPosition != null ? startPosition.floatValue() : null;
					final Float endPositionFloatValue = endPosition != null ? endPosition.floatValue() : null;
					markersOnMap.add(new MarkerOnMap(markerOnMapId, mapId2, markerId, startPositionFloatValue, endPositionFloatValue,
							linkageGroup));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByMapId(id=" + mapId + ")query from MarkerOnMap: " + e.getMessage(), e);
		}

		return markersOnMap;

	}
	
	@SuppressWarnings("unchecked")
	public List<MarkerOnMap> getMarkerOnMapByLinkageGroupAndMapIdAndNotInMarkerId(Integer mapId, Integer linkageGroupId, Integer markerId) {
		List<Object[]> list = new ArrayList<>();
		List<MarkerOnMap> markersOnMap = new ArrayList<>();
		String str2MarkerQuerry2 =
				"SELECT * FROM gdms_markers_onmap WHERE map_id=(:mapId) AND linkage_group=(:linkageGroupId)"
						+ " AND marker_id != (:markerId)";
		
		Query query = this.getSession().createSQLQuery(str2MarkerQuerry2);
		query.setParameter("mapId", mapId);
		query.setParameter("linkageGroupId", linkageGroupId);
		query.setParameter("markerId", markerId);
		list = query.list();
		
		if (list != null && !list.isEmpty()) {
			for (Object[] row : list) {
				Integer markerOnMapId = (Integer) row[0];
				Integer mapId2 = (Integer) row[1];
				Integer mId = (Integer) row[2];
				Double startPosition = (Double) row[3];
				Double endPosition = (Double) row[4];
				String linkageGroup = (String) row[5];

				markersOnMap.add(new MarkerOnMap(markerOnMapId, mapId2, mId, startPosition.floatValue(), endPosition.floatValue(),
						linkageGroup));
			}

		}
			
		return markersOnMap;
		
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getMarkerIdsByPositionAndLinkageGroup(double startPos, double endPos, String linkageGroup) {
		List<Integer> toReturn = new ArrayList<>();
		try {

			SQLQuery query;

			StringBuffer sql =
					new StringBuffer().append("SELECT marker_id ").append(FROM_GDMS_MARKERS_ONMAP)
							.append("WHERE linkage_group = :linkage_group ").append("AND start_position ")
							.append("BETWEEN :start_position ").append("AND :end_position ").append("ORDER BY marker_id ");

			query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("linkage_group", linkageGroup);
			query.setParameter("start_position", startPos);
			query.setParameter("end_position", endPos);

			toReturn = query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersByPositionAndLinkageGroup(linkageGroup=" + linkageGroup + ", startPos="
					+ startPos + ", endPos=" + endPos + ") query from gdms_markers_onmap: " + e.getMessage(), e);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public List<MarkerOnMap> getMarkersOnMap(List<Integer> mapIds, String linkageGroup, double startPos, double endPos) {
		List<MarkerOnMap> markersOnMap = new ArrayList<>();

		try {

			StringBuilder sqlString =
					new StringBuilder().append("SELECT gdms_markers_onmap.* ").append(FROM_GDMS_MARKERS_ONMAP)
							.append("WHERE map_id IN (:mapIds) AND linkage_group = :linkageGroup ")
							.append("		AND start_position BETWEEN :startPos AND :endPos ")
							.append("ORDER BY map_id, Linkage_group, start_position ");
			Query query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("mapIds", mapIds);
			query.setParameter("linkageGroup", linkageGroup);
			query.setParameter("startPos", startPos);
			query.setParameter("endPos", endPos);

			List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					Integer markerOnMapId = (Integer) row[0];
					Integer mapId2 = (Integer) row[1];
					Integer markerId = (Integer) row[2];
					Double startPosition = (Double) row[3];
					Double endPosition = (Double) row[4];
					String linkageGroup2 = (String) row[5];

					markersOnMap.add(new MarkerOnMap(markerOnMapId, mapId2, markerId, startPosition.floatValue(), endPosition.floatValue(),
							linkageGroup2));

				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersOnMap query from MarkerOnMap: " + e.getMessage(), e);
		}

		return markersOnMap;

	}

	@SuppressWarnings("unchecked")
	public List<MarkerOnMap> getMarkersOnMapByMarkerIds(List<Integer> markerIds) {
		List<MarkerOnMap> markersOnMap = new ArrayList<>();

		try {

			StringBuilder sqlString =
					new StringBuilder().append("SELECT markeronmap_id, map_id, marker_id, start_position, end_position, linkage_group ")
							.append(FROM_GDMS_MARKERS_ONMAP).append("WHERE marker_id IN (:markerIds) ")
							.append("ORDER BY map_id, Linkage_group, start_position ");
			Query query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("markerIds", markerIds);

			List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					Integer markerOnMapId = (Integer) row[0];
					Integer mapId2 = (Integer) row[1];
					Integer markerId = (Integer) row[2];
					Double startPosition = (Double) row[3];
					Double endPosition = (Double) row[4];
					String linkageGroup2 = (String) row[5];

					markersOnMap.add(new MarkerOnMap(markerOnMapId, mapId2, markerId, startPosition.floatValue(), endPosition.floatValue(),
							linkageGroup2));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMarkersOnMapByMarkerIds query from MarkerOnMap: " + e.getMessage(), e);
		}

		return markersOnMap;
	}
	
	// FIXME : not sure if this is the correct DAO - perhaps should be MapDAO
	public List<Object> getMarkersOnMapByMarkerIdsAndMapId(List<Integer> markerIds, Integer mapID) {
		
		List<Object> result = new ArrayList<>();
		
		String strQuerry =
			"SELECT distinct gdms_markers_onmap.marker_id, gdms_map.map_name, gdms_markers_onmap.start_position, gdms_markers_onmap.linkage_group, gdms_map.map_unit FROM "
					+ "gdms_map join gdms_markers_onmap on gdms_map.map_id=gdms_markers_onmap.map_id where gdms_markers_onmap.marker_id in (:markerIds) "
					+ "and gdms_map.map_id=(:mapID) "
					+ "order BY gdms_map.map_name, gdms_markers_onmap.linkage_group, gdms_markers_onmap.start_position asc";
		
		SQLQuery query = this.getSession().createSQLQuery(strQuerry);
		query.setParameterList("markerIds", markerIds);
		query.setParameter("mapID", mapID);

		query.addScalar("marker_id", IntegerType.INSTANCE);
		query.addScalar("map_name", StringType.INSTANCE);
		query.addScalar("start_position", DoubleType.INSTANCE);
		query.addScalar("linkage_group", StringType.INSTANCE);
		query.addScalar("map_unit", StringType.INSTANCE);
		result = query.list();
		
		return result;
	
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAllMarkerIds() {
		List<Integer> toReturn = new ArrayList<>();

		try {
			StringBuilder sqlString = new StringBuilder().append("SELECT marker_id ").append(FROM_GDMS_MARKERS_ONMAP);
			Query query = this.getSession().createSQLQuery(sqlString.toString());

			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllMarkerIds query from MarkerOnMap: " + e.getMessage(), e);
		}

		return toReturn;
	}

}
