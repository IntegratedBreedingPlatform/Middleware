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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.util.Debug;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;

/**
 * POJO for listdata table.
 * 
 * @author Kevin Manansala
 * 
 */
@NamedQueries({ @NamedQuery(name = "deleteGermplasmListDataByListId", query = "UPDATE GermplasmListData SET status = 9 WHERE list = :listId") })
@Entity
@Table(name = "listdata")
@SQLDelete(sql="UPDATE listdata SET lrstatus = 9 WHERE lrecid = ?")
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

    @Basic(optional = false)
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

    @OneToMany(mappedBy = "listData", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ListDataProperty> properties;
    
    @Transient
    private ListDataInventory inventoryInfo;
    
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
    
    public String getStatusString() {
        //TODO: make internationalizable
        if (getStatus().equals(0)) {
            return "Active";
        } else if (getStatus().equals(9)) {
            return "Deleted";
        } else {
            return "";
        }
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

    public List<ListDataProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ListDataProperty> properties) {
		this.properties.clear();
		this.properties.addAll(properties);
	}

	public ListDataInventory getInventoryInfo() {
		return inventoryInfo;
	}

	public void setInventoryInfo(ListDataInventory inventoryInfo) {
		this.inventoryInfo = inventoryInfo;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GermplasmListData [id=");
        builder.append(id);
        builder.append(", gid=");
        builder.append(gid);
        builder.append(", entryId=");
        builder.append(entryId);
        builder.append(", entryCode=");
        builder.append(entryCode);
        builder.append(", seedSource=");
        builder.append(seedSource);
        builder.append(", designation=");
        builder.append(designation);
        builder.append(", groupName=");
        builder.append(groupName);
        builder.append(", status=");
        builder.append(status);
        builder.append(", localRecordId=");
        builder.append(localRecordId);
        builder.append("]");
        return builder.toString();
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
    
    public void print(int indent){
    	Debug.println(indent, toString());
    	for (ListDataProperty property : properties){
    		property.print(indent + 3);
    	}
    }

}
