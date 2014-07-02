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
import java.util.Comparator;

/**
 * Placeholder POJO for Allelic Value Element.
 * Used by GenotypicDataManager.getAllelicValuesByGidsAndMarkerNames().
 * 
 * @author Mark Agarrado
 */
public class AllelicValueElement implements Serializable{

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
     * Instantiates a AllelicValueElement object with datasetId, gid, markerName and data(char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values).
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
    public AllelicValueElement(Integer datasetId, Integer gid, Integer markerId, String markerName, String data, Integer peakHeight
    		, Integer markerSampleId, Integer accSampleId) {
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
     * Instantiates a AllelicValueElement object with datasetId, gid, markerId and data(char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values).
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
     * Instantiates a AllelicValueElement object with datasetId, gid, markerName and data(char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values).
     * 
     * @param datasetId
     * @param gid
     * @param markerId
     * @param markerName
     * @param data
     */
    public AllelicValueElement(Integer datasetId, Integer gid, Integer markerId, String markerName, String data) {
        this.datasetId = datasetId;
        this.gid = gid;
        this.data = data;
        this.markerId = markerId;
        this.markerName = markerName;
        this.peakHeight = null;
    }

    /**
     * Instantiates a AllelicValueElement object with gid, data(char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values), marker name.
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
    
    public AllelicValueElement(Integer gid, String data, Integer markerId, String markerName, Integer peakHeight,
            Integer markerSampleId, Integer accSampleId) {
        this.gid = gid;
        this.data = data;
        this.markerId = markerId;
        this.markerName = markerName;
        this.peakHeight = peakHeight;
        this.markerSampleId = markerSampleId;
        this.accSampleId = accSampleId;
    }
    
    public AllelicValueElement(Integer id, Integer datasetId, Integer gid, Integer markerId, String data,
    		Integer markerSampleId, Integer accSampleId) {
    	this.id = id;
    	this.datasetId = datasetId;
    	this.gid = gid;
    	this.markerId = markerId;
    	this.data = data;
        this.markerSampleId = markerSampleId;
        this.accSampleId = accSampleId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    
    public Integer getMarkerId() {
        return markerId;
    }

    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
    
    public Integer getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public String getAlleleBinValue() {
        return alleleBinValue;
    }
    
    public void setAlleleBinValue(String alleleBinValue) {
        this.alleleBinValue = alleleBinValue;
    }
    
    public Integer getPeakHeight() {
		return peakHeight;
	}

	public void setPeakHeight(Integer peakHeight) {
		this.peakHeight = peakHeight;
	}

    public Integer getMarkerSampleId() {
		return markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	public Integer getAccSampleId() {
		return accSampleId;
	}

	public void setAccSampleId(Integer accSampleId) {
		this.accSampleId = accSampleId;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accSampleId == null) ? 0 : accSampleId.hashCode());
		result = prime * result
				+ ((alleleBinValue == null) ? 0 : alleleBinValue.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result + ((gid == null) ? 0 : gid.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result
				+ ((markerName == null) ? 0 : markerName.hashCode());
		result = prime * result
				+ ((markerSampleId == null) ? 0 : markerSampleId.hashCode());
		result = prime * result
				+ ((peakHeight == null) ? 0 : peakHeight.hashCode());
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
		AllelicValueElement other = (AllelicValueElement) obj;
		if (accSampleId == null) {
			if (other.accSampleId != null)
				return false;
		} else if (!accSampleId.equals(other.accSampleId))
			return false;
		if (alleleBinValue == null) {
			if (other.alleleBinValue != null)
				return false;
		} else if (!alleleBinValue.equals(other.alleleBinValue))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (gid == null) {
			if (other.gid != null)
				return false;
		} else if (!gid.equals(other.gid))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (markerId == null) {
			if (other.markerId != null)
				return false;
		} else if (!markerId.equals(other.markerId))
			return false;
		if (markerName == null) {
			if (other.markerName != null)
				return false;
		} else if (!markerName.equals(other.markerName))
			return false;
		if (markerSampleId == null) {
			if (other.markerSampleId != null)
				return false;
		} else if (!markerSampleId.equals(other.markerSampleId))
			return false;
		if (peakHeight == null) {
			if (other.peakHeight != null)
				return false;
		} else if (!peakHeight.equals(other.peakHeight))
			return false;
		return true;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllelicValueElement [id=");
		builder.append(id);
		builder.append(", gid=");
		builder.append(gid);
		builder.append(", data=");
		builder.append(data);
		builder.append(", markerId=");
		builder.append(markerId);
		builder.append(", markerName=");
		builder.append(markerName);
		builder.append(", datasetId=");
		builder.append(datasetId);
		builder.append(", alleleBinValue=");
		builder.append(alleleBinValue);
		builder.append(", peakHeight=");
		builder.append(peakHeight);
		builder.append(", markerSampleId=");
		builder.append(markerSampleId);
		builder.append(", accSampleId=");
		builder.append(accSampleId);
		builder.append("]");
		return builder.toString();
	}



	public static Comparator<AllelicValueElement> AllelicValueElementComparator
                          = new Comparator<AllelicValueElement>() {
        @Override
        public int compare(AllelicValueElement element1, AllelicValueElement element2) {
            Integer gid1 = element1.getGid();
            Integer gid2 = element2.getGid();
            
            int gidComp = gid1.compareTo(gid2);
            
            if (gidComp != 0){
                return gidComp;
            } else {
                String markerName1 = element1.getMarkerName();
                String markerName2 = element2.getMarkerName();
                
                if (markerName1 == null){
                	return (markerName2 == null ? 1 : -1);
                }
                return markerName1.compareToIgnoreCase(markerName2);
            }
        }
 
    };
}
