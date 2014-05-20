package org.generationcp.middleware.pojos.mbdt;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */


@Entity
@Table(name = "mbdt_project")
public class MBDTProjectData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
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

    public MBDTProjectData(Integer projectID, String projectName, Integer userID,
                           Integer mapID, Integer qtlID, Integer phenoDatasetID) {
        this(projectID, projectName, userID, mapID, qtlID, phenoDatasetID, null, null, null);
    }

    public MBDTProjectData(Integer projectID, String projectName, Integer userID, Integer mapID, Integer qtlID,
                           Integer phenoDatasetID, String principalInvestigator, String email, String institute) {
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
        return projectID;
    }


    public Integer getMapID() {
        return mapID;
    }

    public void setMapID(Integer mapID) {
        this.mapID = mapID;
    }

    public Integer getQtlID() {
        return qtlID;
    }

    public void setQtlID(Integer qtlID) {
        this.qtlID = qtlID;
    }

    public Integer getPhenoDatasetID() {
        return phenoDatasetID;
    }

    public void setPhenoDatasetID(Integer phenoDatasetID) {
        this.phenoDatasetID = phenoDatasetID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getPrincipalInvestigator() {
        return principalInvestigator;
    }

    public void setPrincipalInvestigator(String principalInvestigator) {
        this.principalInvestigator = principalInvestigator;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitute() {
        return institute;
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
        return projectName;
    }
}
