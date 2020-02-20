package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * Created by clarysabel on 11/7/19.
 */
@AutoProperty
public class LotDto {

	private Integer lotId;
	private String stockId;
	private Integer gid;
	private Integer locationId;
	private Integer unitId;
	private String comments;
	private String status;

	public Integer getLotId() {
		return lotId;
	}

	public void setLotId(final Integer lotId) {
		this.lotId = lotId;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(final Integer unitId) {
		this.unitId = unitId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
