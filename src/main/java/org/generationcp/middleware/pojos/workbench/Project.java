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
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;

/**
 * POJO for workbench_project table.
 * 
 */
@Entity
@Table(name = "workbench_project")
public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ID_NAME = "projectId";

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "project_id")
	private Long projectId;

	@Basic(optional = false)
	@Column(name = "project_uuid")
	private String uniqueID;

	@Basic(optional = false)
	@Column(name = "project_name")
	private String projectName = "";

	@Basic(optional = false)
	@Column(name = "start_date")
	private Date startDate;

	@Basic(optional = false)
	@Column(name = "user_id")
	private int userId;

	@OneToOne
	@JoinColumn(name = "crop_type", referencedColumnName = "crop_name")
	private CropType cropType;

	@Basic(optional = true)
	@Column(name = "last_open_date")
	private Date lastOpenDate;

	@Transient
	private Set<WorkbenchUser> members;

	@Transient
	private Set<Method> methods;

	@Transient
	private Set<Location> locations;

	public Project(final Long projectId, final String uniqueID, final String projectName, final Date startDate, final int userId,
		final CropType cropType, final Date lastOpenDate) {
		this.uniqueID = uniqueID;
		this.projectName = projectName;
		this.startDate = startDate;
		this.userId = userId;
		this.cropType = cropType;
		this.lastOpenDate = lastOpenDate;
		this.projectId = projectId;
	}

	public Project() {
		super();
	}

	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(final Long projectId) {
		this.projectId = projectId;
	}

	public int getUserId() {
		return this.userId;
	}

	public String getUniqueID() {
		return this.uniqueID;
	}

	public void setUniqueID(final String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public void setUserId(final int userId) {
		this.userId = userId;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(final String projectName) {
		this.projectName = projectName;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public CropType getCropType() {
		return this.cropType;
	}

	public void setCropType(final CropType cropType) {
		this.cropType = cropType;
	}

	public void setLastOpenDate(final Date lastOpenDate) {
		this.lastOpenDate = lastOpenDate;
	}

	public Date getLastOpenDate() {
		return this.lastOpenDate;
	}

	public Set<WorkbenchUser> getMembers() {
		return this.members;
	}

	public void setMembers(final Set<WorkbenchUser> members) {
		this.members = members;
	}

	public Set<Method> getMethods() {
		return this.methods;
	}

	public void setMethods(final Set<Method> methods) {
		this.methods = methods;
	}

	public Set<Location> getLocations() {
		return this.locations;
	}

	public void setLocations(final Set<Location> locations) {
		this.locations = locations;
	}

	/**
	 * @return the name of the (IBDB) database where program's breeding activities related information such as Nurseries, Trials, Germplasm,
	 *         Lists, Datasets etc are stored.
	 */
	@Transient
	public String getDatabaseName() {
		return this.cropType.getDbName();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.projectId).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!Project.class.isInstance(obj)) {
			return false;
		}

		final Project otherObj = (Project) obj;

		return new EqualsBuilder().append(this.projectId, otherObj.projectId).isEquals();
	}

	@Override
	public String toString() {
		return "Project{" + "projectId=" + this.projectId + ", uniqueID='" + this.uniqueID + '\'' + ", projectName='" + this.projectName
				+ '\'' + ", startDate=" + this.startDate + ", userId=" + this.userId + ", cropType=" + this.cropType + ", lastOpenDate="
				+ this.lastOpenDate + ", members=" + this.members + ", methods=" + this.methods + ", locations=" + this.locations + '}';
	}
}
