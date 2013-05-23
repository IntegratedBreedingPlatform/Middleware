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
 * <b>Description</b>: POJO for Map table.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 9, 2012
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
            if (this.getMapId() == param.getMapId()) {
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

