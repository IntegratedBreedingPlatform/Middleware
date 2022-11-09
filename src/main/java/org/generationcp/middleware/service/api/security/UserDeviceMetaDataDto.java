package org.generationcp.middleware.service.api.security;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class UserDeviceMetaDataDto {

	private Integer userId;
	private String deviceDetails;
	private String location;
	private Date lastLoggedIn;

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
	}

	public String getDeviceDetails() {
		return this.deviceDetails;
	}

	public void setDeviceDetails(final String deviceDetails) {
		this.deviceDetails = deviceDetails;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public Date getLastLoggedIn() {
		return this.lastLoggedIn;
	}

	public void setLastLoggedIn(final Date lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
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
