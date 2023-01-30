package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class BasicGermplasmDTO {

	private Integer gid;
	private Integer gpid1;
	private Integer gpid2;
	private Integer gnpgs;
	private Integer mgid;
	private Integer methodId;
	private Integer locationId;

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public Integer getGpid1() {
		return gpid1;
	}

	public void setGpid1(final Integer gpid1) {
		this.gpid1 = gpid1;
	}

	public Integer getGpid2() {
		return gpid2;
	}

	public void setGpid2(final Integer gpid2) {
		this.gpid2 = gpid2;
	}

	public Integer getGnpgs() {
		return gnpgs;
	}

	public void setGnpgs(final Integer gnpgs) {
		this.gnpgs = gnpgs;
	}

	public Integer getMgid() {
		return mgid;
	}

	public void setMgid(final Integer mgid) {
		this.mgid = mgid;
	}

	public Integer getMethodId() {
		return methodId;
	}

	public void setMethodId(final Integer methodId) {
		this.methodId = methodId;
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
