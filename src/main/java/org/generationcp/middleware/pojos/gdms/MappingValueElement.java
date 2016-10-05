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

/**
 * Placeholder POJO for Mapping Value Element
 *
 * @author Mark Agarrado
 */
public class MappingValueElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer datasetId;

	private String mappingPopType;

	private Integer parentAGid;

	private Integer parentBGid;

	private Integer gid;

	private Integer markerId;

	private String markerType;

	private Integer markerSampleId;

	private Integer accSampleId;

	public MappingValueElement(Integer datasetId, String mappingType, Integer parentAGid, Integer parentBGid, String markerType,
			Integer markerSampleId, Integer accSampleId) {

		this.datasetId = datasetId;
		this.mappingPopType = mappingType;
		this.parentAGid = parentAGid;
		this.parentBGid = parentBGid;
		this.markerType = markerType;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	public MappingValueElement(Integer datasetId, String mappingType, Integer parentAGid, Integer parentBGid, Integer gid,
			Integer markerId, String markerType, Integer markerSampleId, Integer accSampleId) {

		this(datasetId, mappingType, parentAGid, parentBGid, markerType, markerSampleId, accSampleId);
		this.gid = gid;
		this.markerId = markerId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public String getMappingType() {
		return this.mappingPopType;
	}

	public void setMappingType(String mappingType) {
		this.mappingPopType = mappingType;
	}

	public Integer getParentAGid() {
		return this.parentAGid;
	}

	public void setParentAGid(Integer parentAGid) {
		this.parentAGid = parentAGid;
	}

	public Integer getParentBGid() {
		return this.parentBGid;
	}

	public void setParentBGid(Integer parentBGid) {
		this.parentBGid = parentBGid;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getMarkerType() {
		return this.markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

	public String getMappingPopType() {
		return this.mappingPopType;
	}

	public void setMappingPopType(String mappingPopType) {
		this.mappingPopType = mappingPopType;
	}

	public Integer getMarkerSampleId() {
		return this.markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	public Integer getAccSampleId() {
		return this.accSampleId;
	}

	public void setAccSampleId(Integer accSampleId) {
		this.accSampleId = accSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.accSampleId == null ? 0 : this.accSampleId.hashCode());
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.gid == null ? 0 : this.gid.hashCode());
		result = prime * result + (this.mappingPopType == null ? 0 : this.mappingPopType.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerSampleId == null ? 0 : this.markerSampleId.hashCode());
		result = prime * result + (this.markerType == null ? 0 : this.markerType.hashCode());
		result = prime * result + (this.parentAGid == null ? 0 : this.parentAGid.hashCode());
		result = prime * result + (this.parentBGid == null ? 0 : this.parentBGid.hashCode());
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
		MappingValueElement other = (MappingValueElement) obj;
		if (this.accSampleId == null) {
			if (other.accSampleId != null) {
				return false;
			}
		} else if (!this.accSampleId.equals(other.accSampleId)) {
			return false;
		}
		if (this.datasetId == null) {
			if (other.datasetId != null) {
				return false;
			}
		} else if (!this.datasetId.equals(other.datasetId)) {
			return false;
		}
		if (this.gid == null) {
			if (other.gid != null) {
				return false;
			}
		} else if (!this.gid.equals(other.gid)) {
			return false;
		}
		if (this.mappingPopType == null) {
			if (other.mappingPopType != null) {
				return false;
			}
		} else if (!this.mappingPopType.equals(other.mappingPopType)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.markerSampleId == null) {
			if (other.markerSampleId != null) {
				return false;
			}
		} else if (!this.markerSampleId.equals(other.markerSampleId)) {
			return false;
		}
		if (this.markerType == null) {
			if (other.markerType != null) {
				return false;
			}
		} else if (!this.markerType.equals(other.markerType)) {
			return false;
		}
		if (this.parentAGid == null) {
			if (other.parentAGid != null) {
				return false;
			}
		} else if (!this.parentAGid.equals(other.parentAGid)) {
			return false;
		}
		if (this.parentBGid == null) {
			if (other.parentBGid != null) {
				return false;
			}
		} else if (!this.parentBGid.equals(other.parentBGid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingValueElement [datasetId=");
		builder.append(this.datasetId);
		builder.append(", mappingPopType=");
		builder.append(this.mappingPopType);
		builder.append(", parentAGid=");
		builder.append(this.parentAGid);
		builder.append(", parentBGid=");
		builder.append(this.parentBGid);
		builder.append(", gid=");
		builder.append(this.gid);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", markerType=");
		builder.append(this.markerType);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
