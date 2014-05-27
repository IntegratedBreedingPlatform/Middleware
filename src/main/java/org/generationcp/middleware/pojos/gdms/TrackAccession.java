/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for the gdms_track_acc table.
 * 
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "gdms_track_acc")
public class TrackAccession implements Serializable{

    private static final long serialVersionUID = 1L;   
    
    @Id
    @Column(name = "tacc_id")
    private Integer trackAccId;

    @Column(name = "track_id")
    private Integer trackId;
    
    @Column(name = "nid")
    private Integer nid;
    
    @Column(name = "acc_sample_id")
    private Integer accSampleId;

    public TrackAccession(){
    }

    public TrackAccession(Integer trackAccId, Integer trackId, Integer nid,
			Integer acc_sample_id) {
		this.trackAccId = trackAccId;
		this.trackId = trackId;
		this.nid = nid;
		this.accSampleId = acc_sample_id;
	}

	public Integer getTrackAccId() {
		return trackAccId;
	}

	public void setTrackAccId(Integer trackAccId) {
		this.trackAccId = trackAccId;
	}

	public Integer getTrackId() {
		return trackId;
	}

	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}

	public Integer getAcc_sample_id() {
		return accSampleId;
	}

	public void setAcc_sample_id(Integer acc_sample_id) {
		this.accSampleId = acc_sample_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accSampleId == null) ? 0 : accSampleId.hashCode());
		result = prime * result + ((nid == null) ? 0 : nid.hashCode());
		result = prime * result
				+ ((trackAccId == null) ? 0 : trackAccId.hashCode());
		result = prime * result + ((trackId == null) ? 0 : trackId.hashCode());
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
		TrackAccession other = (TrackAccession) obj;
		if (accSampleId == null) {
			if (other.accSampleId != null)
				return false;
		} else if (!accSampleId.equals(other.accSampleId))
			return false;
		if (nid == null) {
			if (other.nid != null)
				return false;
		} else if (!nid.equals(other.nid))
			return false;
		if (trackAccId == null) {
			if (other.trackAccId != null)
				return false;
		} else if (!trackAccId.equals(other.trackAccId))
			return false;
		if (trackId == null) {
			if (other.trackId != null)
				return false;
		} else if (!trackId.equals(other.trackId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrackAccession [trackAccId=");
		builder.append(trackAccId);
		builder.append(", trackId=");
		builder.append(trackId);
		builder.append(", nid=");
		builder.append(nid);
		builder.append(", acc_sample_id=");
		builder.append(accSampleId);
		builder.append("]");
		return builder.toString();
	}    

}
