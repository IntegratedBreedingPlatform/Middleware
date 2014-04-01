/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * POJO for gdms_map table.
 * 
 * @author Michael Blancaflor
 */
@Entity
@Table(name = "gdms_map")
public class Map implements Serializable{

    private static final long serialVersionUID = 1803546446290398372L;

    @Id
    @Basic(optional = false)
    @Column(name = "map_id")
    private Integer mapId;
    
    @Basic(optional = false)
    @Column(name = "map_name")
    private String mapName;
    
    @Basic(optional = false)
    @Column(name = "map_type")
    private String mapType;
    
    @Column(name = "mp_id")
    private Integer mpId;
    
    @Column(name = "map_desc")
    private String mapDesc;
    
    @Column(name = "map_unit")
    private String mapUnit;
    
    private static final String GET_MAP_DETAILS_SELECT = 
            "SELECT COUNT(DISTINCT gdms_mapping_data.marker_id) AS marker_count " +
            "       , MAX(gdms_mapping_data.start_position) AS max " +
            "       , gdms_mapping_data.linkage_group AS Linkage_group " +
            "       , concat(gdms_mapping_data.map_name,'') AS map " +
            "       , concat(gdms_map.map_type,'') AS map_type " +
            "       , gdms_map.map_desc AS map_desc " +
            "       , gdms_map.map_unit AS map_unit " +
            "FROM gdms_mapping_data, gdms_map " +
            "WHERE gdms_mapping_data.map_id=gdms_map.map_id " 
            ;

    private static final String GET_MAP_DETAILS_WHERE = 
            "       AND lower(gdms_mapping_data.map_name) LIKE (:nameLike) " ;

    private static final String GET_MAP_DETAILS_GROUP_ORDER = 
            "GROUP BY gdms_mapping_data.linkage_group, gdms_mapping_data.map_name " +
                    "ORDER BY gdms_mapping_data.map_name, gdms_mapping_data.linkage_group "
                    ;

    public static final String GET_MAP_DETAILS = 
            GET_MAP_DETAILS_SELECT + GET_MAP_DETAILS_GROUP_ORDER;
    
    public static final String GET_MAP_DETAILS_BY_NAME = 
            GET_MAP_DETAILS_SELECT + GET_MAP_DETAILS_WHERE + GET_MAP_DETAILS_GROUP_ORDER;

    public static final String COUNT_MAP_DETAILS = 
            "SELECT COUNT(DISTINCT gdms_mapping_data.linkage_group, gdms_mapping_data.map_name) " +
            "FROM `gdms_mapping_data` JOIN `gdms_map` ON gdms_mapping_data.map_id=gdms_map.map_id "
            ;

    public static final String COUNT_MAP_DETAILS_BY_NAME = 
            COUNT_MAP_DETAILS + "WHERE lower(gdms_mapping_data.map_name) LIKE (:nameLike) ";
    
    public static final String GET_MAP_ID_BY_NAME = 
    		"SELECT map_id FROM gdms_map WHERE map_name = :mapName LIMIT 0,1";

    public static final String GET_MAP_AND_MARKER_COUNT_BY_MARKERS = 
    		"SELECT CONCAT(m.map_name, ''), COUNT(k.marker_id) " +
    		"FROM gdms_map m " +
    		"INNER JOIN gdms_markers_onmap k ON k.map_id = m.map_id " +
    		"WHERE k.marker_id IN (:markerIds) " +
    		"GROUP BY m.map_name";
    
    public static final String GET_MAP_INFO_BY_MAP_AND_CHROMOSOME =
            "SELECT DISTINCT "
            + "  gdms_markers_onmap.marker_id"
            + " ,gdms_marker.marker_name"
            + " ,gdms_map.map_name"
            + " ,gdms_map.map_type"
            + " ,gdms_markers_onmap.start_position"
            + " ,gdms_markers_onmap.linkage_group"
            + " ,gdms_map.map_unit"
            + " FROM gdms_map"
            + "     INNER JOIN gdms_markers_onmap ON"
            + "         gdms_map.map_id = gdms_markers_onmap.map_id"
            + "     INNER JOIN gdms_marker ON"
            + "         gdms_marker.marker_id = gdms_markers_onmap.marker_id"
            + " WHERE"
            + "     gdms_markers_onmap.map_id = :mapId"
            + "     AND gdms_markers_onmap.linkage_group = :chromosome"
            ;
    
    public static final String GET_MAP_INFO_BY_MAP_CHROMOSOME_AND_POSITION =
            "SELECT DISTINCT "
            + "  gdms_markers_onmap.marker_id"
            + " ,gdms_marker.marker_name"
            + " ,gdms_map.map_name"
            + " ,gdms_map.map_type"
            + " ,gdms_markers_onmap.linkage_group"
            + " ,gdms_map.map_unit"
            + " FROM gdms_map"
            + "     INNER JOIN gdms_markers_onmap ON"
            + "         gdms_map.map_id = gdms_markers_onmap.map_id"
            + "     INNER JOIN gdms_marker ON"
            + "         gdms_marker.marker_id = gdms_markers_onmap.marker_id"
            + " WHERE"
            + "     gdms_markers_onmap.map_id = :mapId"
            + "     AND gdms_markers_onmap.linkage_group = :chromosome"
            + "     AND gdms_markers_onmap.start_position = :startPosition"
            + " ORDER BY"
            + "      gdms_map.map_name"
            + "     ,gdms_markers_onmap.linkage_group"
            + "     ,gdms_markers_onmap.start_position ASC"
            ;
    
    public static final String GET_MAP_INFO_BY_MARKERS_AND_MAP =
            "SELECT DISTINCT "
            + "  gdms_markers_onmap.marker_id"
            + " ,gdms_marker.marker_name"
            + " ,gdms_map.map_name"
            + " ,gdms_map.map_type"
            + " ,gdms_markers_onmap.start_position"
            + " ,gdms_markers_onmap.linkage_group"
            + " ,gdms_map.map_unit"
            + " FROM gdms_map"
            + "     INNER JOIN gdms_markers_onmap ON"
            + "         gdms_map.map_id = gdms_markers_onmap.map_id"
            + "     INNER JOIN gdms_marker ON"
            + "         gdms_marker.marker_id = gdms_markers_onmap.marker_id"
            + " WHERE"
            + "     gdms_markers_onmap.marker_id IN (:markerIdList)"
            + "     AND gdms_markers_onmap.map_id = :mapId"
            + " ORDER BY"
            + "     gdms_map.map_name"
            + "     ,gdms_markers_onmap.linkage_group"
            + "     ,gdms_markers_onmap.start_position ASC"
            ;
    
    public Map() {        
    }

    public Map(Integer mapId, String mapName, String mapType, Integer mpId,
			String mapDesc, String mapUnit) {
		super();
		this.mapId = mapId;
		this.mapName = mapName;
		this.mapType = mapType;
		this.mpId = mpId;
		this.mapDesc = mapDesc;
		this.mapUnit = mapUnit;
	}

    public Map(Integer mapId) {
        this.mapId = mapId;
    }
    
    public Integer getMapId() {
        return mapId;
    }

    
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    
    public String getMapName() {
        return mapName;
    }

    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    
    public String getMapType() {
        return mapType;
    }

    
    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    
    public Integer getMpId() {
        return mpId;
    }

    
    public void setMpId(Integer mpId) {
        this.mpId = mpId;
    }
    
    public String getMapDesc() {
		return mapDesc;
	}

	public void setMapDesc(String mapDesc) {
		this.mapDesc = mapDesc;
	}

	public String getMapUnit() {
		return mapUnit;
	}

	public void setMapUnit(String mapUnit) {
		this.mapUnit = mapUnit;
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Map) {
            Map param = (Map) obj;
            if (this.getMapId().equals(param.getMapId())) {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public int hashCode() {
        return this.mapId;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Map [mapId=");
		builder.append(mapId);
		builder.append(", mapName=");
		builder.append(mapName);
		builder.append(", mapType=");
		builder.append(mapType);
		builder.append(", mpId=");
		builder.append(mpId);
		builder.append(", mapDesc=");
		builder.append(mapDesc);
		builder.append(", mapUnit=");
		builder.append(mapUnit);
		builder.append("]");
		return builder.toString();
	}
    
}

