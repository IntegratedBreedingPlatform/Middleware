/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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
public class TrackAccession implements Serializable {

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

	public TrackAccession() {
	}

	public TrackAccession(Integer trackAccId, Integer trackId, Integer nid, Integer accSampleId) {
		this.trackAccId = trackAccId;
		this.trackId = trackId;
		this.nid = nid;
		this.accSampleId = accSampleId;
	}

	public Integer getTrackAccId() {
		return this.trackAccId;
	}

	public void setTrackAccId(Integer trackAccId) {
		this.trackAccId = trackAccId;
	}

	public Integer getTrackId() {
		return this.trackId;
	}

	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	public Integer getNid() {
		return this.nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}

	public Integer getAccSampleId() {
		return this.accSampleId;
	}

	public void setAccSampleId(Integer acc_sample_id) {
		this.accSampleId = acc_sample_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.accSampleId == null ? 0 : this.accSampleId.hashCode());
		result = prime * result + (this.nid == null ? 0 : this.nid.hashCode());
		result = prime * result + (this.trackAccId == null ? 0 : this.trackAccId.hashCode());
		result = prime * result + (this.trackId == null ? 0 : this.trackId.hashCode());
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
		TrackAccession other = (TrackAccession) obj;
		if (this.accSampleId == null) {
			if (other.accSampleId != null) {
				return false;
			}
		} else if (!this.accSampleId.equals(other.accSampleId)) {
			return false;
		}
		if (this.nid == null) {
			if (other.nid != null) {
				return false;
			}
		} else if (!this.nid.equals(other.nid)) {
			return false;
		}
		if (this.trackAccId == null) {
			if (other.trackAccId != null) {
				return false;
			}
		} else if (!this.trackAccId.equals(other.trackAccId)) {
			return false;
		}
		if (this.trackId == null) {
			if (other.trackId != null) {
				return false;
			}
		} else if (!this.trackId.equals(other.trackId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrackAccession [trackAccId=");
		builder.append(this.trackAccId);
		builder.append(", trackId=");
		builder.append(this.trackId);
		builder.append(", nid=");
		builder.append(this.nid);
		builder.append(", acc_sample_id=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
