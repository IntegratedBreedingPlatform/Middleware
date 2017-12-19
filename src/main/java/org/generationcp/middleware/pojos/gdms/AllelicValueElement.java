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
import java.util.Comparator;

/**
 * Placeholder POJO for Allelic Value Element. Used by GenotypicDataManager.getAllelicValuesByGidsAndMarkerNames().
 *
 * @author Mark Agarrado
 */
//FIXME should be replaced by CharValueElement wherever is used in GDMS
public class AllelicValueElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer gid;

	private String data;

	private Integer markerId;

	private String markerName;

	private Integer datasetId;

	private String alleleBinValue;

	private Integer peakHeight;

	private Integer markerSampleId;

	private Integer accSampleId;

	/**
	 * Instantiates a AllelicValueElement object with datasetId, gid, markerName and data(char_value for table char_values, allele_bin_value
	 * for allele_values table and map_char_value for mapping_pop_values).
	 * 
	 * @param datasetId
	 * @param gid
	 * @param markerId
	 * @param markerName
	 * @param data
	 * @param peakHeight
	 * @param markerSampleId
	 * @param accSampleIld
	 */
	public AllelicValueElement(Integer datasetId, Integer gid, Integer markerId, String markerName, String data, Integer peakHeight,
			Integer markerSampleId, Integer accSampleId) {
		this.datasetId = datasetId;
		this.gid = gid;
		this.data = data;
		this.markerId = markerId;
		this.markerName = markerName;
		this.peakHeight = peakHeight;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	/**
	 * Instantiates a AllelicValueElement object with datasetId, gid, markerId and data(char_value for table char_values, allele_bin_value
	 * for allele_values table and map_char_value for mapping_pop_values).
	 * 
	 * @param id - anId if from gdms_allele_values and acId if from gdms_char_values
	 * @param datasetId
	 * @param gid
	 * @param markerId
	 * @param alleleBinValue
	 * @param peakHeight
	 * @param markerSampleId
	 * @param accSampleIld
	 */
	public AllelicValueElement(Integer id, Integer datasetId, Integer gid, Integer markerId, String alleleBinValue, Integer peakHeight,
			Integer markerSampleId, Integer accSampleId) {
		this.setId(id);
		this.datasetId = datasetId;
		this.gid = gid;
		this.alleleBinValue = alleleBinValue;
		this.markerId = markerId;
		this.peakHeight = peakHeight;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	/**
	 * Instantiates a AllelicValueElement object with datasetId, gid, markerName and data(char_value for table char_values, allele_bin_value
	 * for allele_values table and map_char_value for mapping_pop_values).
	 * 
	 * @param datasetId
	 * @param gid
	 * @param markerId
	 * @param markerName
	 * @param data
	 * @param markerSampleId
	 * @param accSampleIld
	 */
	public AllelicValueElement(Integer datasetId, Integer gid, Integer markerId, String markerName, String data, Integer markerSampleId,
			Integer accSampleId) {
		this.datasetId = datasetId;
		this.gid = gid;
		this.data = data;
		this.markerId = markerId;
		this.markerName = markerName;
		this.peakHeight = null;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	/**
	 * Instantiates a AllelicValueElement object with gid, data(char_value for table char_values, allele_bin_value for allele_values table
	 * and map_char_value for mapping_pop_values), marker name.
	 * 
	 * @param gid
	 * @param data
	 * @param markerName
	 */
	public AllelicValueElement(Integer gid, String data, String markerName, Integer peakHeight) {
		this.gid = gid;
		this.data = data;
		this.markerName = markerName;
		this.peakHeight = peakHeight;
	}

	public AllelicValueElement(Integer gid, String data, Integer markerId, String markerName, Integer peakHeight, Integer markerSampleId,
			Integer accSampleId) {
		this.gid = gid;
		this.data = data;
		this.markerId = markerId;
		this.markerName = markerName;
		this.peakHeight = peakHeight;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	public AllelicValueElement(Integer id, Integer datasetId, Integer gid, Integer markerId, String data, Integer markerSampleId,
			Integer accSampleId) {
		this.id = id;
		this.datasetId = datasetId;
		this.gid = gid;
		this.markerId = markerId;
		this.data = data;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getMarkerName() {
		return this.markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public String getAlleleBinValue() {
		return this.alleleBinValue;
	}

	public void setAlleleBinValue(String alleleBinValue) {
		this.alleleBinValue = alleleBinValue;
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
		result = prime * result + (this.alleleBinValue == null ? 0 : this.alleleBinValue.hashCode());
		result = prime * result + (this.data == null ? 0 : this.data.hashCode());
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.gid == null ? 0 : this.gid.hashCode());
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerName == null ? 0 : this.markerName.hashCode());
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
		AllelicValueElement other = (AllelicValueElement) obj;
		if (this.accSampleId == null) {
			if (other.accSampleId != null) {
				return false;
			}
		} else if (!this.accSampleId.equals(other.accSampleId)) {
			return false;
		}
		if (this.alleleBinValue == null) {
			if (other.alleleBinValue != null) {
				return false;
			}
		} else if (!this.alleleBinValue.equals(other.alleleBinValue)) {
			return false;
		}
		if (this.data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!this.data.equals(other.data)) {
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
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.markerName == null) {
			if (other.markerName != null) {
				return false;
			}
		} else if (!this.markerName.equals(other.markerName)) {
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
		builder.append("AllelicValueElement [id=");
		builder.append(this.id);
		builder.append(", gid=");
		builder.append(this.gid);
		builder.append(", data=");
		builder.append(this.data);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", markerName=");
		builder.append(this.markerName);
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", alleleBinValue=");
		builder.append(this.alleleBinValue);
		builder.append(", peakHeight=");
		builder.append(this.peakHeight);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

	public static Comparator<AllelicValueElement> AllelicValueElementComparator = new Comparator<AllelicValueElement>() {

		@Override
		public int compare(AllelicValueElement element1, AllelicValueElement element2) {
			Integer gid1 = element1.getGid();
			Integer gid2 = element2.getGid();

			int gidComp = gid1.compareTo(gid2);

			if (gidComp != 0) {
				return gidComp;
			} else {
				String markerName1 = element1.getMarkerName();
				String markerName2 = element2.getMarkerName();

				if (markerName1 == null) {
					return markerName2 == null ? 1 : -1;
				}
				return markerName1.compareToIgnoreCase(markerName2);
			}
		}

	};
}
