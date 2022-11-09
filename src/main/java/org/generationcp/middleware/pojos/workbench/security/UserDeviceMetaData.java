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
 * POJO for user_device_meta_data table.
 */
@Entity
@Table(name = "user_device_meta_data")
@AutoProperty
public class UserDeviceMetaData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Basic(optional = false)
	@Column(name = "userid")
	private Integer userId;

	@Basic(optional = false)
	@Column(name = "device_details")
	private String deviceDetails;

	@Basic(optional = false)
	@Column(name = "location")
	private String location;

	@Basic(optional = false)
	@Column(name = "last_logged_in")
	private Date lastLoggedIn;

	public UserDeviceMetaData() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

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
