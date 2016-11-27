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

package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * POJO for workbench_tool_license_info table.
 *
 */
@Entity
@Table(name = "workbench_tool_license_info")
public class ToolLicenseInfo implements Serializable {

	private static final long serialVersionUID = 3835141759438665433L;

	@Id
	@GeneratedValue
	@Basic(optional = false)
	@Column(name = "tool_license_info_id")
	private Integer licenseInfoId;

	@OneToOne(optional = false)
	@JoinColumn(name = "tool_id")
	private Tool tool;

	@Basic(optional = false)
	@Column(name = "license_path")
	private String licensePath;

	@Basic(optional = true)
	@Column(name = "license_hash")
	private String licenseHash;

	@Basic(optional = true)
	@Column(name = "expiration_date")
	private Date expirationDate;

	public Integer getLicenseInfoId() {
		return this.licenseInfoId;
	}

	public void setLicenseInfoId(final Integer licenseInfoId) {
		this.licenseInfoId = licenseInfoId;
	}

	public Tool getTool() {
		return this.tool;
	}

	public void setTool(final Tool tool) {
		this.tool = tool;
	}

	public String getLicensePath() {
		return this.licensePath;
	}

	public void setLicensePath(final String licensePath) {
		this.licensePath = licensePath;
	}

	public String getLicenseHash() {
		return this.licenseHash;
	}

	public void setLicenseHash(final String licenseHash) {
		this.licenseHash = licenseHash;
	}

	public Date getExpirationDate() {
		return this.expirationDate;
	}

	public void setExpirationDate(final Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ToolLicenseInfo [licenseInfoId=");
		builder.append(this.licenseInfoId);
		builder.append(", tool=");
		builder.append(this.tool);
		builder.append(", licensePath=");
		builder.append(this.licensePath);
		builder.append(", licenseHash=");
		builder.append(this.licenseHash);
		builder.append(", expirationDate=");
		builder.append(this.expirationDate);
		builder.append("]");
		return builder.toString();
	}

}
