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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link MappingData}.
 *
 * @author Joyce Avestro
 *
 */
public class MappingDataDAO extends GenericDAO<MappingData, Integer> {

	public static final String GET_MAP_INFO_BY_MAP_NAME = "SELECT marker_id, marker_name, map_name, linkage_group, start_position "
			+ "FROM gdms_mapping_data " + "WHERE map_name = :mapName " + "ORDER BY linkage_group, start_position, marker_name";

	public MappingDataDAO(final Session session) {
		super(session);
	}

	/**
	 * Gets the map info by map name.
	 *
	 * @param mapName the map name
	 * @return the map info by map name
	 */
	@SuppressWarnings("unchecked")
	public List<MapInfo> getMapInfoByMapName(String mapName) throws MiddlewareQueryException {
		List<MapInfo> mapInfoList = new ArrayList<MapInfo>();
		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("mapName", mapName));
			criteria.addOrder(Order.asc("linkageGroup"));
			criteria.addOrder(Order.asc("startPosition"));
			criteria.addOrder(Order.asc("markerName"));
			List<MappingData> list = criteria.list();
			for (MappingData mapData : list) {
				mapInfoList.add(MapInfo.build(mapData));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMapInfoByMapName(mapName=" + mapName + ") query from MappingData: " + e.getMessage(),
					e);
		}

		return mapInfoList;
	}

}
