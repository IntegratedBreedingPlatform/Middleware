/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.User;

@Entity
@Table(name = "workbench_project_user_mysql_account")
public class ProjectUserMysqlAccount implements Serializable{

    private static final long serialVersionUID = 4888126472761792565L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "project_user_mysql_id")
    private Integer id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Basic(optional = false)
    @Column(name ="mysql_username")
    private String mysqlUsername;
    
    @Basic(optional = false)
    @Column(name = "mysql_password")
    private String mysqlPassword;

    public ProjectUserMysqlAccount() {
    }
    
    public ProjectUserMysqlAccount(Integer id, Project project, User user, String mysqlUsername, String mysqlPassword) {
        super();
        this.id = id;
        this.project = project;
        this.user = user;
        this.mysqlUsername = mysqlUsername;
        this.mysqlPassword = mysqlPassword;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getMysqlUsername() {
        return mysqlUsername;
    }
    
    public void setMysqlUsername(String mysqlUsername) {
        this.mysqlUsername = mysqlUsername;
    }
    
    public String getMysqlPassword() {
        return mysqlPassword;
    }
    
    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!ProjectUserMysqlAccount.class.isInstance(obj)) {
            return false;
        }

        ProjectUserMysqlAccount otherObj = (ProjectUserMysqlAccount) obj;

        return new EqualsBuilder().append(id, otherObj.id).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder toreturn = new StringBuilder();
        toreturn.append("ProjectUserMysqlAccount [id=");
        toreturn.append(id);
        toreturn.append(", project=");
        toreturn.append(project);
        toreturn.append(", user=");
        toreturn.append(user);
        toreturn.append(", mysqlUsername=");
        toreturn.append(mysqlUsername);
        toreturn.append(", mysqlPassword=");
        toreturn.append(mysqlPassword);
        toreturn.append("]");
        return toreturn.toString();
    }
    
}
