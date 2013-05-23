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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <b>Description</b>: Placeholder POJO for Allelic Value that stores the marker id
 * Used by GenotypicDataManager.getAllelicValuesFromCharValuesByDatasetId().
 * Used by GenotypicDataManager.getAllelicValuesFromAlleleValuesByDatasetId().
 * Used by GenotypicDataManager.getAllelicValuesFromMappingPopValuesByDatasetId().
 * 
 * <br>
 * 
 * <b>Author</b>: Joyce Avestro <br>
 * <b>File Created</b>: Aug 07, 2012
 */
public class AllelicValueWithMarkerIdElement implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer gid;
    
    /** The Data value (char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values) */
    private String data;
    
    private Integer markerId;
    
    private Integer peakHeight;

    public AllelicValueWithMarkerIdElement(Integer gid, String data, Integer markerId) {
        this(gid, data, markerId, null);
    }

    public AllelicValueWithMarkerIdElement(Integer gid, String data, Integer markerId, Integer peakHeight) {
        this.gid = gid;
        this.data = data;
        this.markerId = markerId;
        this.peakHeight = peakHeight;
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

    public void setmarkerId(Integer markerId) {
        this.markerId = markerId;
    }

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}
    
    public Integer getPeakHeight() {
		return peakHeight;
	}

	public void setPeakHeight(Integer peakHeight) {
		this.peakHeight = peakHeight;
	}
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AllelicValueWithMarkerIdElement [gid=");
        builder.append(gid);
        builder.append(", data=");
        builder.append(data);
        builder.append(", markerId=");
        builder.append(markerId);
        builder.append(", peakHeight=");
        builder.append(peakHeight);
        builder.append("]");
        return builder.toString();
    }

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AllelicValueWithMarkerIdElement)) {
            return false;
        }

        AllelicValueWithMarkerIdElement rhs = (AllelicValueWithMarkerIdElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(gid, rhs.gid)
                .append(data, rhs.data)
                .append(markerId, rhs.markerId)
                .append(peakHeight, rhs.peakHeight)
                .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 97).append(gid)
                .append(data)
                .append(markerId).toHashCode();
    }
}
