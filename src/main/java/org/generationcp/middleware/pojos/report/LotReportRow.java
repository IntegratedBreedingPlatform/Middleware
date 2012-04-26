package org.generationcp.middleware.pojos.report;

import java.io.Serializable;

import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Scale;

public class LotReportRow implements Serializable {

	private static final long serialVersionUID = 2572260467983831666L;
	
	private Integer lotId;
	private Integer entityIdOfLot;
	private Long actualLotBalance;
	private Location locationOfLot;
	private Scale scaleOfLot;
	
	public Integer getLotId() {
		return lotId;
	}
	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}
	public Integer getEntityIdOfLot() {
		return entityIdOfLot;
	}
	public void setEntityIdOfLot(Integer entityIdOfLot) {
		this.entityIdOfLot = entityIdOfLot;
	}
	public Long getActualLotBalance() {
		return actualLotBalance;
	}
	public void setActualLotBalance(Long actualLotBalance) {
		this.actualLotBalance = actualLotBalance;
	}
	public Location getLocationOfLot() {
		return locationOfLot;
	}
	public void setLocationOfLot(Location locationOfLot) {
		this.locationOfLot = locationOfLot;
	}
	public Scale getScaleOfLot() {
		return scaleOfLot;
	}
	public void setScaleOfLot(Scale scaleOfLot) {
		this.scaleOfLot = scaleOfLot;
	}
	
	@Override
	public String toString()
	{
		return "LotReportRow [lotId=" + lotId + ", entityIdOfLot=" + entityIdOfLot
				+ ", actualLotBalance=" + actualLotBalance + ", locationOfLot="
				+ locationOfLot + ", scaleOfLot=" + scaleOfLot + "]";
	}
	
}
