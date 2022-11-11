package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class BasicNameDTO {

	private Integer nid;
	private Integer gid;
	private Integer typeId;
	private Integer nstat;
	private String nval;
	private Integer locationId;

	public Integer getNid() {
		return nid;
	}

	public void setNid(final Integer nid) {
		this.nid = nid;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(final Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getNstat() {
		return nstat;
	}

	public void setNstat(final Integer nstat) {
		this.nstat = nstat;
	}

	public String getNval() {
		return nval;
	}

	public void setNval(final String nval) {
		this.nval = nval;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
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
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
