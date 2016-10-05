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

package org.generationcp.middleware.pojos.report;

import java.io.Serializable;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.pojos.Location;

/**
 * This POJO represents a row in reports about Lot records.
 */
public class LotReportRow implements Serializable {

	private static final long serialVersionUID = 2572260467983831666L;

	private Integer lotId;
	private Integer entityIdOfLot;
	private Double actualLotBalance;
	private Location locationOfLot;
	private Term scaleOfLot;
	private String commentOfLot;

	public Integer getLotId() {
		return this.lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	public Integer getEntityIdOfLot() {
		return this.entityIdOfLot;
	}

	public void setEntityIdOfLot(Integer entityIdOfLot) {
		this.entityIdOfLot = entityIdOfLot;
	}

	public Double getActualLotBalance() {
		return this.actualLotBalance;
	}

	public void setActualLotBalance(Double actualLotBalance) {
		this.actualLotBalance = actualLotBalance;
	}

	public Location getLocationOfLot() {
		return this.locationOfLot;
	}

	public void setLocationOfLot(Location locationOfLot) {
		this.locationOfLot = locationOfLot;
	}

	public Term getScaleOfLot() {
		return this.scaleOfLot;
	}

	public void setScaleOfLot(Term scaleOfLot) {
		this.scaleOfLot = scaleOfLot;
	}

	public String getCommentOfLot() {
		return this.commentOfLot;
	}

	public void setCommentOfLot(String commentOfLot) {
		this.commentOfLot = commentOfLot;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LotReportRow [lotId=");
		builder.append(this.lotId);
		builder.append(", entityIdOfLot=");
		builder.append(this.entityIdOfLot);
		builder.append(", actualLotBalance=");
		builder.append(this.actualLotBalance);
		builder.append(", locationOfLot=");
		builder.append(this.locationOfLot);
		builder.append(", scaleOfLot=");
		builder.append(this.scaleOfLot);
		builder.append(", commentOfLot=");
		builder.append(this.commentOfLot);
		builder.append("]");
		return builder.toString();
	}

}
