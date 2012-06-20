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

package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * POJO for listdata table
 * 
 * @author Kevin Manansala
 * 
 */
@NamedQueries({ @NamedQuery(name = "deleteGermplasmListDataByListId", query = "delete from GermplasmListData where list = :listId") })
@Entity
@Table(name = "listdata")
public class GermplasmListData implements Serializable{

    private static final long serialVersionUID = 1L;

    // string contants for name of queries
    public static final String DELETE_BY_LIST_ID = "deleteGermplasmListDataByListId";

    @Id
    @Basic(optional = false)
    @Column(name = "lrecid")
    private Integer id;

    @ManyToOne(targetEntity = GermplasmList.class)
    @JoinColumn(name = "listid", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private GermplasmList list;

    @Basic(optional = false)
    @Column(name = "gid")
    private Integer gid;

    @Basic(optional = false)
    @Column(name = "entryid")
    private Integer entryId;

    @Column(name = "entrycd")
    private String entryCode;

    @Column(name = "source")
    private String seedSource;

    @Basic(optional = false)
    @Column(name = "desig")
    private String designation;

    @Column(name = "grpname")
    private String groupName;

    @Basic(optional = false)
    @Column(name = "lrstatus")
    private Integer status;

    @Basic(optional = false)
    @Column(name = "llrecid")
    private Integer localRecordId;

    public GermplasmListData() {

    }

    public GermplasmListData(Integer id) {
        super();
        this.id = id;
    }

    public GermplasmListData(Integer id, GermplasmList list, Integer gid, Integer entryId, String entryCode, String seedSource,
            String designation, String groupName, Integer status, Integer localRecordId) {
        super();
        this.id = id;
        this.list = list;
        this.gid = gid;
        this.entryId = entryId;
        this.entryCode = entryCode;
        this.seedSource = seedSource;
        this.designation = designation;
        this.groupName = groupName;
        this.status = status;
        this.localRecordId = localRecordId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GermplasmList getList() {
        return list;
    }

    public void setList(GermplasmList list) {
        this.list = list;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public String getEntryCode() {
        return entryCode;
    }

    public void setEntryCode(String entryCode) {
        this.entryCode = entryCode;
    }

    public String getSeedSource() {
        return seedSource;
    }

    public void setSeedSource(String seedSource) {
        this.seedSource = seedSource;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLocalRecordId() {
        return localRecordId;
    }

    public void setLocalRecordId(Integer localRecordId) {
        this.localRecordId = localRecordId;
    }

    @Override
    public String toString() {
        return "GermplasmListData [id=" + id + ", list=" + list + ", gid=" + gid + ", entryId=" + entryId + ", entryCode=" + entryCode
                + ", seedSource=" + seedSource + ", designation=" + designation + ", groupName=" + groupName + ", status=" + status
                + ", localRecordId=" + localRecordId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GermplasmListData other = (GermplasmListData) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
