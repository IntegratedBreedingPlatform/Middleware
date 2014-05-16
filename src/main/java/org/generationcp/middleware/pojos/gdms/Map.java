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
    
    @Column(name = "genus", columnDefinition = "char(25)")
    private String genus;
    
    @Column(name = "species", columnDefinition = "char(25)")
    private String species;
    
    @Column(name = "institute")
    private String institute;
       
    public Map() {        
    }


    public Map(Integer mapId, String mapName, String mapType, Integer mpId,
			String mapDesc, String mapUnit, String genus, String species,
			String institute) {
		this.mapId = mapId;
		this.mapName = mapName;
		this.mapType = mapType;
		this.mpId = mpId;
		this.mapDesc = mapDesc;
		this.mapUnit = mapUnit;
		this.genus = genus;
		this.species = species;
		this.institute = institute;
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

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((genus == null) ? 0 : genus.hashCode());
		result = prime * result
				+ ((institute == null) ? 0 : institute.hashCode());
		result = prime * result + ((mapDesc == null) ? 0 : mapDesc.hashCode());
		result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
		result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
		result = prime * result + ((mapType == null) ? 0 : mapType.hashCode());
		result = prime * result + ((mapUnit == null) ? 0 : mapUnit.hashCode());
		result = prime * result + ((mpId == null) ? 0 : mpId.hashCode());
		result = prime * result + ((species == null) ? 0 : species.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Map other = (Map) obj;
		if (genus == null) {
			if (other.genus != null)
				return false;
		} else if (!genus.equals(other.genus))
			return false;
		if (institute == null) {
			if (other.institute != null)
				return false;
		} else if (!institute.equals(other.institute))
			return false;
		if (mapDesc == null) {
			if (other.mapDesc != null)
				return false;
		} else if (!mapDesc.equals(other.mapDesc))
			return false;
		if (mapId == null) {
			if (other.mapId != null)
				return false;
		} else if (!mapId.equals(other.mapId))
			return false;
		if (mapName == null) {
			if (other.mapName != null)
				return false;
		} else if (!mapName.equals(other.mapName))
			return false;
		if (mapType == null) {
			if (other.mapType != null)
				return false;
		} else if (!mapType.equals(other.mapType))
			return false;
		if (mapUnit == null) {
			if (other.mapUnit != null)
				return false;
		} else if (!mapUnit.equals(other.mapUnit))
			return false;
		if (mpId == null) {
			if (other.mpId != null)
				return false;
		} else if (!mpId.equals(other.mpId))
			return false;
		if (species == null) {
			if (other.species != null)
				return false;
		} else if (!species.equals(other.species))
			return false;
		return true;
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
		builder.append(", genus=");
		builder.append(genus);
		builder.append(", species=");
		builder.append(species);
		builder.append(", institute=");
		builder.append(institute);
		builder.append("]");
		return builder.toString();
	}
    
}

