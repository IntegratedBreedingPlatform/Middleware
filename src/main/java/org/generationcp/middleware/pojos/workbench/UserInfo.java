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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * POJO for workbench_user_info table.
 *  
 */
@Entity
@Table(name = "workbench_user_info")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "login_count")
    private Integer loginCount;

    @Column (name="reset_expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resetExpiryDate;

    @Column (name="reset_token")
    private String resetToken;

    public UserInfo() {
    }

    public UserInfo(int userId, int loginCount) {
        this.userId = userId;
        this.loginCount = loginCount;
    }
    
	public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UserInfo)) {
            return false;
        }

        UserInfo userInfo = (UserInfo) o;

        return new EqualsBuilder()
                .append(userId, userInfo.userId)
                .append(loginCount, userInfo.loginCount)
                .append(resetExpiryDate, userInfo.resetExpiryDate)
                .append(resetToken, userInfo.resetToken)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(loginCount)
                .append(resetExpiryDate)
                .append(resetToken)
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserInfo [userId=");
        builder.append(userId);
        builder.append(", loginCount=");
        builder.append(loginCount);
        builder.append("]");
        return builder.toString();
    }

    public Date getResetExpiryDate() {
        return resetExpiryDate;
    }

    public void setResetExpiryDate(Date resetExpiryDate) {
        this.resetExpiryDate = resetExpiryDate;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
