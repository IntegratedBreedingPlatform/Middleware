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

package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for gdms_map table.
 *
 * @author Michael Blancaflor
 */
@Entity
@Table(name = "gdms_map")
public class Map implements Serializable {

	private static final long serialVersionUID = 1803546446290398372L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public Map(Integer mapId, String mapName, String mapType, Integer mpId, String mapDesc, String mapUnit, String genus, String species,
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
		return this.mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapType() {
		return this.mapType;
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	public Integer getMpId() {
		return this.mpId;
	}

	public void setMpId(Integer mpId) {
		this.mpId = mpId;
	}

	public String getMapDesc() {
		return this.mapDesc;
	}

	public void setMapDesc(String mapDesc) {
		this.mapDesc = mapDesc;
	}

	public String getMapUnit() {
		return this.mapUnit;
	}

	public void setMapUnit(String mapUnit) {
		this.mapUnit = mapUnit;
	}

	public String getGenus() {
		return this.genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getSpecies() {
		return this.species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getInstitute() {
		return this.institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.genus == null ? 0 : this.genus.hashCode());
		result = prime * result + (this.institute == null ? 0 : this.institute.hashCode());
		result = prime * result + (this.mapDesc == null ? 0 : this.mapDesc.hashCode());
		result = prime * result + (this.mapId == null ? 0 : this.mapId.hashCode());
		result = prime * result + (this.mapName == null ? 0 : this.mapName.hashCode());
		result = prime * result + (this.mapType == null ? 0 : this.mapType.hashCode());
		result = prime * result + (this.mapUnit == null ? 0 : this.mapUnit.hashCode());
		result = prime * result + (this.mpId == null ? 0 : this.mpId.hashCode());
		result = prime * result + (this.species == null ? 0 : this.species.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Map other = (Map) obj;
		if (this.genus == null) {
			if (other.genus != null) {
				return false;
			}
		} else if (!this.genus.equals(other.genus)) {
			return false;
		}
		if (this.institute == null) {
			if (other.institute != null) {
				return false;
			}
		} else if (!this.institute.equals(other.institute)) {
			return false;
		}
		if (this.mapDesc == null) {
			if (other.mapDesc != null) {
				return false;
			}
		} else if (!this.mapDesc.equals(other.mapDesc)) {
			return false;
		}
		if (this.mapId == null) {
			if (other.mapId != null) {
				return false;
			}
		} else if (!this.mapId.equals(other.mapId)) {
			return false;
		}
		if (this.mapName == null) {
			if (other.mapName != null) {
				return false;
			}
		} else if (!this.mapName.equals(other.mapName)) {
			return false;
		}
		if (this.mapType == null) {
			if (other.mapType != null) {
				return false;
			}
		} else if (!this.mapType.equals(other.mapType)) {
			return false;
		}
		if (this.mapUnit == null) {
			if (other.mapUnit != null) {
				return false;
			}
		} else if (!this.mapUnit.equals(other.mapUnit)) {
			return false;
		}
		if (this.mpId == null) {
			if (other.mpId != null) {
				return false;
			}
		} else if (!this.mpId.equals(other.mpId)) {
			return false;
		}
		if (this.species == null) {
			if (other.species != null) {
				return false;
			}
		} else if (!this.species.equals(other.species)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Map [mapId=");
		builder.append(this.mapId);
		builder.append(", mapName=");
		builder.append(this.mapName);
		builder.append(", mapType=");
		builder.append(this.mapType);
		builder.append(", mpId=");
		builder.append(this.mpId);
		builder.append(", mapDesc=");
		builder.append(this.mapDesc);
		builder.append(", mapUnit=");
		builder.append(this.mapUnit);
		builder.append(", genus=");
		builder.append(this.genus);
		builder.append(", species=");
		builder.append(this.species);
		builder.append(", institute=");
		builder.append(this.institute);
		builder.append("]");
		return builder.toString();
	}

}
