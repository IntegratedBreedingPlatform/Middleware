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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * The POJO for the workbench user's security questions.
 * 
 *  @author Mark Agarrado
 *  
 */
@Entity
@Table(name = "workbench_security_question")
public class SecurityQuestion implements Serializable{

    private static final long serialVersionUID = -7342162622987974268L;

    @Id
    @GeneratedValue
    @Basic(optional = false)
    @Column(name = "security_question_id")
    private Integer securityQuestionId;
    
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;
    
    @Basic(optional = false)
    @Column(name = "security_question")
    private String securityQuestion;
    
    @Basic(optional = false)
    @Column(name = "security_answer")
    private String securityAnswer;
    
    public SecurityQuestion() {
    }

    
    /**
     * @return the securityQuestionId
     */
    public Integer getSecurityQuestionId() {
        return securityQuestionId;
    }

    
    /**
     * @param securityQuestionId the securityQuestionId to set
     */
    public void setSecurityQuestionId(Integer securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    
    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    
    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(securityQuestionId).hashCode();
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
        if (!SecurityQuestion.class.isInstance(obj)) {
            return false;
        }

        SecurityQuestion otherObj = (SecurityQuestion) obj;

        return new EqualsBuilder().append(securityQuestionId, otherObj.securityQuestionId).isEquals();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityQuestion [securityQuestionId=");
        builder.append(securityQuestionId);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", securityQuestion=");
        builder.append(securityQuestion);
        builder.append(", securityAnswer=");
        builder.append(securityAnswer);
        builder.append("]");
        return builder.toString();
    }
    
}
