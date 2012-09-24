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
package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "instln")
public class Installation implements Serializable{

    private static final long serialVersionUID = 2860835541958463594L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "instalid")
    private Long id;
    
    @Basic(optional = false)
    @Column(name = "admin")
    private Long adminId;
    
    @Column(name = "udate")
    private Long lastUpdateDate;
    
    @Basic(optional = false)
    @Column(name = "ugid")
    private Long lastGermplasmUpdated;
    
    @Column(name = "ulocn")
    private Long lastLocationUpdated;
    
    @Basic(optional = false)
    @Column(name = "ucid")
    private Long lastChangeRecordUpdated;
    
    @Basic(optional = false)
    @Column(name = "unid")
    private Long lastNameUpdated;
    
    @Basic(optional = false)
    @Column(name = "uaid")
    private Long lastAttributeUpdated;
    
    @Basic(optional = false)
    @Column(name = "uldid")
    private Long lastLocationDescriptorUpdated;
    
    @Column(name = "umethn")
    private Long lastMethodUpdated;
    
    @Column(name = "ufldno")
    private Long lastUserFieldUpdated;
    
    @Column(name = "urefno")
    private Long lastReferenceUpdated;
    
    @Column(name = "upid")
    private Long lastPersonUpdated;
    
    @Column(name = "ulistid")
    private Long lastListUpdated;
    
    @Basic(optional = false)
    @Column(name = "idesc")
    private String description;
    
    @Column(name = "dms_status")
    private Integer dmsStatus;
    
    @Column(name = "ulrecid")
    private Long lastLrecUpdated;

    public Installation() {
        
    }
    
    public Installation(Long id, Long adminId, Long lastUpdateDate, Long lastGermplasmUpdated, Long lastLocationUpdated,
            Long lastChangeRecordUpdated, Long lastNameUpdated, Long lastAttributeUpdated, Long lastLocationDescriptorUpdated,
            Long lastMethodUpdated, Long lastUserFieldUpdated, Long lastReferenceUpdated, Long lastPersonUpdated, Long lastListUpdated,
            String description, Integer dmsStatus, Long lastLrecUpdated) {
        super();
        this.id = id;
        this.adminId = adminId;
        this.lastUpdateDate = lastUpdateDate;
        this.lastGermplasmUpdated = lastGermplasmUpdated;
        this.lastLocationUpdated = lastLocationUpdated;
        this.lastChangeRecordUpdated = lastChangeRecordUpdated;
        this.lastNameUpdated = lastNameUpdated;
        this.lastAttributeUpdated = lastAttributeUpdated;
        this.lastLocationDescriptorUpdated = lastLocationDescriptorUpdated;
        this.lastMethodUpdated = lastMethodUpdated;
        this.lastUserFieldUpdated = lastUserFieldUpdated;
        this.lastReferenceUpdated = lastReferenceUpdated;
        this.lastPersonUpdated = lastPersonUpdated;
        this.lastListUpdated = lastListUpdated;
        this.description = description;
        this.dmsStatus = dmsStatus;
        this.lastLrecUpdated = lastLrecUpdated;
    }

    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public Long getAdminId() {
        return adminId;
    }

    
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    
    public Long getLastUpdateDate() {
        return lastUpdateDate;
    }

    
    public void setLastUpdateDate(Long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    
    public Long getLastGermplasmUpdated() {
        return lastGermplasmUpdated;
    }

    
    public void setLastGermplasmUpdated(Long lastGermplasmUpdated) {
        this.lastGermplasmUpdated = lastGermplasmUpdated;
    }

    
    public Long getLastLocationUpdated() {
        return lastLocationUpdated;
    }

    
    public void setLastLocationUpdated(Long lastLocationUpdated) {
        this.lastLocationUpdated = lastLocationUpdated;
    }

    
    public Long getLastChangeRecordUpdated() {
        return lastChangeRecordUpdated;
    }

    
    public void setLastChangeRecordUpdated(Long lastChangeRecordUpdated) {
        this.lastChangeRecordUpdated = lastChangeRecordUpdated;
    }

    
    public Long getLastNameUpdated() {
        return lastNameUpdated;
    }

    
    public void setLastNameUpdated(Long lastNameUpdated) {
        this.lastNameUpdated = lastNameUpdated;
    }

    
    public Long getLastAttributeUpdated() {
        return lastAttributeUpdated;
    }

    
    public void setLastAttributeUpdated(Long lastAttributeUpdated) {
        this.lastAttributeUpdated = lastAttributeUpdated;
    }

    
    public Long getLastLocationDescriptorUpdated() {
        return lastLocationDescriptorUpdated;
    }

    
    public void setLastLocationDescriptorUpdated(Long lastLocationDescriptorUpdated) {
        this.lastLocationDescriptorUpdated = lastLocationDescriptorUpdated;
    }

    
    public Long getLastMethodUpdated() {
        return lastMethodUpdated;
    }

    
    public void setLastMethodUpdated(Long lastMethodUpdated) {
        this.lastMethodUpdated = lastMethodUpdated;
    }

    
    public Long getLastUserFieldUpdated() {
        return lastUserFieldUpdated;
    }

    
    public void setLastUserFieldUpdated(Long lastUserFieldUpdated) {
        this.lastUserFieldUpdated = lastUserFieldUpdated;
    }

    
    public Long getLastReferenceUpdated() {
        return lastReferenceUpdated;
    }

    
    public void setLastReferenceUpdated(Long lastReferenceUpdated) {
        this.lastReferenceUpdated = lastReferenceUpdated;
    }

    
    public Long getLastPersonUpdated() {
        return lastPersonUpdated;
    }

    
    public void setLastPersonUpdated(Long lastPersonUpdated) {
        this.lastPersonUpdated = lastPersonUpdated;
    }

    
    public Long getLastListUpdated() {
        return lastListUpdated;
    }

    
    public void setLastListUpdated(Long lastListUpdated) {
        this.lastListUpdated = lastListUpdated;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public Integer getDmsStatus() {
        return dmsStatus;
    }

    
    public void setDmsStatus(Integer dmsStatus) {
        this.dmsStatus = dmsStatus;
    }

    
    public Long getLastLrecUpdated() {
        return lastLrecUpdated;
    }

    
    public void setLastLrecUpdated(Long lastLrecUpdated) {
        this.lastLrecUpdated = lastLrecUpdated;
    }

    @Override
    public String toString() {
        return "Installation [id=" + id + ", adminId=" + adminId + ", lastUpdateDate=" + lastUpdateDate + ", lastGermplasmUpdated="
                + lastGermplasmUpdated + ", lastLocationUpdated=" + lastLocationUpdated + ", lastChangeRecordUpdated="
                + lastChangeRecordUpdated + ", lastNameUpdated=" + lastNameUpdated + ", lastAttributeUpdated=" + lastAttributeUpdated
                + ", lastLocationDescriptorUpdated=" + lastLocationDescriptorUpdated + ", lastMethodUpdated=" + lastMethodUpdated
                + ", lastUserFieldUpdated=" + lastUserFieldUpdated + ", lastReferenceUpdated=" + lastReferenceUpdated
                + ", lastPersonUpdated=" + lastPersonUpdated + ", lastListUpdated=" + lastListUpdated + ", description=" + description
                + ", dmsStatus=" + dmsStatus + ", lastLrecUpdated=" + lastLrecUpdated + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Lot)) {
            return false;
        }

        Installation rhs = (Installation) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}
