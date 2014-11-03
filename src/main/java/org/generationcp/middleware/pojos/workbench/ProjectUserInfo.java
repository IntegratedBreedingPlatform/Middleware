/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * POJO for workbench_project_user_info table.
 *  
 *  @author Aldrin Batac
 *  
 */
@Entity
@Table(name = "workbench_project_user_info")
public class ProjectUserInfo implements Serializable{

    private static final long serialVersionUID = 1L;
   
    
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "user_info_id")
    private Integer userInfoId;
  
 
    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "user_id")
    private Integer userId;

    @Basic(optional = true)
    @Column(name = "last_open_date")
    private Date lastOpenDate; 

    
    public ProjectUserInfo() {
    }

    public ProjectUserInfo(Integer projectId, Integer userId) {
        this.setProjectId(projectId);
        this.setUserId(userId);
    }
    
    public ProjectUserInfo(Integer projectId, Integer userId, Date lastOpenDate) {
        this.setProjectId(projectId);
        this.setUserId(userId);
        this.setLastOpenDate(lastOpenDate);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getUserInfoId()).hashCode();
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
        if (!ProjectUserInfo.class.isInstance(obj)) {
            return false;
        }

        ProjectUserInfo otherObj = (ProjectUserInfo) obj;

        return new EqualsBuilder().append(getUserInfoId(), otherObj.getUserInfoId()).isEquals();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProjectUserInfo [projectUserId=");
        builder.append(getUserInfoId());
        builder.append(", projectId=");
        builder.append(getProjectId());
        builder.append(", userId=");
        builder.append(getUserId());
        builder.append(", lastOpenDate=");
        builder.append(getLastOpenDate());
        builder.append("]");
        return builder.toString();
    }

	public Integer getUserInfoId() {
		return userInfoId;
	}

	public void setUserInfoId(Integer userInfoId) {
		this.userInfoId = userInfoId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getLastOpenDate() {
		return lastOpenDate;
	}

	public void setLastOpenDate(Date lastOpenDate) {
		this.lastOpenDate = lastOpenDate;
	}

}
