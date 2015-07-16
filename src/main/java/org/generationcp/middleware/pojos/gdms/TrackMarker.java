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
 * POJO for the gdms_track_markers table.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "gdms_track_markers")
public class TrackMarker implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tmarker_id")
	private Integer trackMarkerId;

	@Column(name = "track_id")
	private Integer trackId;

	@Column(name = "marker_id")
	private Integer markerId;

	@Column(name = "marker_sample_id")
	private Integer markerSampleId;

	public TrackMarker() {
	}

	public TrackMarker(Integer trackMarkerId, Integer trackId, Integer markerId, Integer markerSampleId) {
		this.trackMarkerId = trackMarkerId;
		this.trackId = trackId;
		this.markerId = markerId;
		this.markerSampleId = markerSampleId;
	}

	public Integer getTrackMarkerId() {
		return this.trackMarkerId;
	}

	public void setTrackMarkerId(Integer trackMarkerId) {
		this.trackMarkerId = trackMarkerId;
	}

	public Integer getTrackId() {
		return this.trackId;
	}

	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getMarkerSampleId() {
		return this.markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerSampleId == null ? 0 : this.markerSampleId.hashCode());
		result = prime * result + (this.trackId == null ? 0 : this.trackId.hashCode());
		result = prime * result + (this.trackMarkerId == null ? 0 : this.trackMarkerId.hashCode());
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
		TrackMarker other = (TrackMarker) obj;
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
		if (this.trackId == null) {
			if (other.trackId != null) {
				return false;
			}
		} else if (!this.trackId.equals(other.trackId)) {
			return false;
		}
		if (this.trackMarkerId == null) {
			if (other.trackMarkerId != null) {
				return false;
			}
		} else if (!this.trackMarkerId.equals(other.trackMarkerId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrackMarkers [trackMarkerId=");
		builder.append(this.trackMarkerId);
		builder.append(", trackId=");
		builder.append(this.trackId);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append("]");
		return builder.toString();
	}

}
