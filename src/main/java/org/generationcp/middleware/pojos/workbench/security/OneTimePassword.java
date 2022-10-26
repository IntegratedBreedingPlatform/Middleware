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

package org.generationcp.middleware.pojos.workbench.security;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * POJO for one_time_password table.
 */
@Entity
@Table(name = "one_time_password")
@AutoProperty
public class OneTimePassword implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Basic(optional = false)
	@Column(name = "otp_code")
	private Integer otpCode;

	@Basic(optional = false)
	@Column(name = "expires")
	private Date expires;

	public OneTimePassword() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

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
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
