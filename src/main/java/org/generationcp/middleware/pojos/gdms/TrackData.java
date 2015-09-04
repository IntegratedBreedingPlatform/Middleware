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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for the gdms_track_data table.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "gdms_track_data")
public class TrackData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "track_id")
	private Integer trackId;

	@Column(name = "track_name")
	private String trackName;

	@Column(name = "user_id")
	private Integer userId;

	public TrackData() {
	}

	public TrackData(Integer trackId, String trackName, Integer userId) {
		this.trackId = trackId;
		this.trackName = trackName;
		this.userId = userId;
	}

	public Integer getTrackId() {
		return this.trackId;
	}

	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	public String getTrackName() {
		return this.trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.trackId == null ? 0 : this.trackId.hashCode());
		result = prime * result + (this.trackName == null ? 0 : this.trackName.hashCode());
		result = prime * result + (this.userId == null ? 0 : this.userId.hashCode());
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
		TrackData other = (TrackData) obj;
		if (this.trackId == null) {
			if (other.trackId != null) {
				return false;
			}
		} else if (!this.trackId.equals(other.trackId)) {
			return false;
		}
		if (this.trackName == null) {
			if (other.trackName != null) {
				return false;
			}
		} else if (!this.trackName.equals(other.trackName)) {
			return false;
		}
		if (this.userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!this.userId.equals(other.userId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrackData [trackId=");
		builder.append(this.trackId);
		builder.append(", trackName=");
		builder.append(this.trackName);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append("]");
		return builder.toString();
	}

}
