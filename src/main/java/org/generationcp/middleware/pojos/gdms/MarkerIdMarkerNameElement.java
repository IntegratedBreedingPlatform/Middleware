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

/**
 * Node element for holding matching Marker ID and Marker Name.
 *
 * @author Michael Blancaflor <br>
 *         <b>File Created</b>: Aug 7, 2012
 */
public class MarkerIdMarkerNameElement {

	private int markerId;
	private String markerName;

	public MarkerIdMarkerNameElement(int markerId, String markerName) {
		this.markerId = markerId;
		this.markerName = markerName;
	}

	public int getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(int markerId) {
		this.markerId = markerId;
	}

	public String getMarkerName() {
		return this.markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerIdMarkerNameElement [markerId=");
		builder.append(this.markerId);
		builder.append(", markerName=");
		builder.append(this.markerName);
		builder.append("]");
		return builder.toString();
	}

}
