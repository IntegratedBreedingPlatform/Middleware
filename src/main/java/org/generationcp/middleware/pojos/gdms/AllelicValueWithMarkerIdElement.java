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
 * Placeholder POJO for Allelic Value that stores the marker id Used by GenotypicDataManager.getAllelicValuesFromCharValuesByDatasetId().
 * Used by GenotypicDataManager.getAllelicValuesFromAlleleValuesByDatasetId(). Used by
 * GenotypicDataManager.getAllelicValuesFromMappingPopValuesByDatasetId().
 *
 * @author Joyce Avestro
 */
public class AllelicValueWithMarkerIdElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer gid;

	/**
	 * The Data value (char_value for table char_values, allele_bin_value for allele_values table and map_char_value for mapping_pop_values)
	 */
	private String data;

	private Integer markerId;

	private Integer peakHeight;

	private Integer markerSampleId;

	private Integer accSampleId;

	public AllelicValueWithMarkerIdElement(Integer gid, String data, Integer markerId, Integer markerSampleId, Integer accSampleId) {
		this(gid, data, markerId, null, markerSampleId, accSampleId);
	}

	public AllelicValueWithMarkerIdElement(Integer gid, String data, Integer markerId, Integer peakHeight, Integer markerSampleId,
			Integer accSampleId) {
		this.gid = gid;
		this.data = data;
		this.markerId = markerId;
		this.peakHeight = peakHeight;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setmarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getPeakHeight() {
		return this.peakHeight;
	}

	public void setPeakHeight(Integer peakHeight) {
		this.peakHeight = peakHeight;
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
		result = prime * result + (this.data == null ? 0 : this.data.hashCode());
		result = prime * result + (this.gid == null ? 0 : this.gid.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerSampleId == null ? 0 : this.markerSampleId.hashCode());
		result = prime * result + (this.peakHeight == null ? 0 : this.peakHeight.hashCode());
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
		AllelicValueWithMarkerIdElement other = (AllelicValueWithMarkerIdElement) obj;
		if (this.accSampleId == null) {
			if (other.accSampleId != null) {
				return false;
			}
		} else if (!this.accSampleId.equals(other.accSampleId)) {
			return false;
		}
		if (this.data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!this.data.equals(other.data)) {
			return false;
		}
		if (this.gid == null) {
			if (other.gid != null) {
				return false;
			}
		} else if (!this.gid.equals(other.gid)) {
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
		if (this.peakHeight == null) {
			if (other.peakHeight != null) {
				return false;
			}
		} else if (!this.peakHeight.equals(other.peakHeight)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllelicValueWithMarkerIdElement [gid=");
		builder.append(this.gid);
		builder.append(", data=");
		builder.append(this.data);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", peakHeight=");
		builder.append(this.peakHeight);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
