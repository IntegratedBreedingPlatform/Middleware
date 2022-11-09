package org.generationcp.middleware.service.api.security;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class OneTimePasswordDto {

	private Integer otpCode;
	private Date expires;

	public Integer getOtpCode() {
		return this.otpCode;
	}

	public void setOtpCode(final Integer otpCode) {
		this.otpCode = otpCode;
	}

	public Date getExpires() {
		return this.expires;
	}

	public void setExpires(final Date expires) {
		this.expires = expires;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
