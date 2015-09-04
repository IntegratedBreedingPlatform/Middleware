
package org.generationcp.middleware.pojos.mbdt;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

@Entity
@Table(name = "mbdt_project")
public class MBDTProjectData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "project_id")
	private Integer projectID;

	@Basic(optional = false)
	@Column(name = "pname")
	private String projectName;

	@Basic(optional = false)
	@Column(name = "user_id")
	private Integer userID;

	@Column(name = "map_id")
	private Integer mapID;

	@Column(name = "qtl_id")
	private Integer qtlID;

	@Column(name = "phenodataset_id")
	private Integer phenoDatasetID;

	@Column(name = "principal_investigator")
	private String principalInvestigator;

	@Column(name = "email")
	private String email;

	@Column(name = "institute")
	private String institute;

	public MBDTProjectData() {
	}

	public MBDTProjectData(Integer projectID, String projectName, Integer userID, Integer mapID, Integer qtlID, Integer phenoDatasetID) {
		this(projectID, projectName, userID, mapID, qtlID, phenoDatasetID, null, null, null);
	}

	public MBDTProjectData(Integer projectID, String projectName, Integer userID, Integer mapID, Integer qtlID, Integer phenoDatasetID,
			String principalInvestigator, String email, String institute) {
		this.projectID = projectID;
		this.projectName = projectName;
		this.mapID = mapID;
		this.qtlID = qtlID;
		this.phenoDatasetID = phenoDatasetID;
		this.userID = userID;
		this.principalInvestigator = principalInvestigator;
		this.email = email;
		this.institute = institute;
	}

	public Integer getProjectID() {
		return this.projectID;
	}

	public Integer getMapID() {
		return this.mapID;
	}

	public void setMapID(Integer mapID) {
		this.mapID = mapID;
	}

	public Integer getQtlID() {
		return this.qtlID;
	}

	public void setQtlID(Integer qtlID) {
		this.qtlID = qtlID;
	}

	public Integer getPhenoDatasetID() {
		return this.phenoDatasetID;
	}

	public void setPhenoDatasetID(Integer phenoDatasetID) {
		this.phenoDatasetID = phenoDatasetID;
	}

	public Integer getUserID() {
		return this.userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public String getPrincipalInvestigator() {
		return this.principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInstitute() {
		return this.institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return this.projectName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof MBDTProjectData)) {
			return false;
		}

		MBDTProjectData other = (MBDTProjectData) obj;

		if (!other.getProjectID().equals(this.getProjectID())) {
			return false;
		}

		if (!other.getProjectName().equals(this.getProjectName())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.projectID == null ? 0 : this.projectID.hashCode());
		result = prime * result + (this.projectName == null ? 0 : this.projectName.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "MBDTProjectData{" + "projectID=" + this.projectID + ", projectName='" + this.projectName + '\'' + ", userID=" + this.userID
				+ ", mapID=" + this.mapID + ", qtlID=" + this.qtlID + ", phenoDatasetID=" + this.phenoDatasetID
				+ ", principalInvestigator='" + this.principalInvestigator + '\'' + ", email='" + this.email + '\'' + ", institute='"
				+ this.institute + '\'' + '}';
	}
}
