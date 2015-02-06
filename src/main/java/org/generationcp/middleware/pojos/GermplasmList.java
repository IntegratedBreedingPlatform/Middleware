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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO for listnms table.
 * 
 * @author Kevin Manansala, Mark Agarrado
 * 
 */

@Entity
@Table(name = "listnms")
@SQLDelete(sql="UPDATE listnms SET liststatus = 9 WHERE listid = ?")
public class GermplasmList implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "listid")
    private Integer id;

    @Column(name = "listname")
    private String name;

    @Column(name = "listdate")
    private Long date;

    @Column(name = "listtype")
    private String type;

    @Column(name = "listuid")
    private Integer userId;

    @Column(name = "listdesc")
    private String description;

    @ManyToOne(targetEntity = GermplasmList.class)
    @JoinColumn(name = "lhierarchy")
    @NotFound(action = NotFoundAction.IGNORE)
    private GermplasmList parent;

    @Column(name = "liststatus")
    private Integer status;
    
    @Column(name = "sdate")
    private Integer  sDate;
    
    @Column(name = "eDate")
    private Integer  eDate;
    
    @Column(name = "listlocn")
    private Integer  listLocation;
    
    @Column(name = "listref")
    private Integer  listRef;
    
    @Column(name = "projectId")
    private Integer  projectId; 
    
    @Column(name = "notes")
    private String notes;
    
    @OneToMany(mappedBy = "list", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("entryId asc")
    private List<GermplasmListData> listData = new ArrayList<GermplasmListData>();

    public static final String GET_GERMPLASM_LIST_TYPES = 
    		"SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid " +
    		"FROM udflds " +
    		"WHERE ftable = 'LISTNMS' AND ftype = 'LISTTYPE' and fcode not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES')";

    public static final String GET_GERMPLASM_NAME_TYPES = 
    		"SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid " +
    		"FROM udflds " +
    		"WHERE ftable = 'NAMES' AND ftype = 'NAME'";
    
    public static final String SEARCH_FOR_GERMPLASM_LIST =
    		"SELECT DISTINCT listnms.* " +
            "FROM listnms " +
            "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) " +
            "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) " +
            "WHERE listtype not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES') AND liststatus!=9 AND listtype!='FOLDER' AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) " +
		    "      OR desig LIKE :q OR listname LIKE :q " +
		    "      OR desig LIKE :qNoSpaces " +
		    "      OR desig LIKE :qStandardized " +
		    ")";
		    
    public static final String SEARCH_FOR_GERMPLASM_LIST_GID_LIKE =
    		"SELECT DISTINCT listnms.* " +
            "FROM listnms " +
            "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) " +
            "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) " +
            "WHERE listtype not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES') AND liststatus!=9 AND listtype!='FOLDER' AND (listdata.gid LIKE :gid " +
            "      OR desig LIKE :q OR listname LIKE :q" +
            "      OR desig LIKE :qNoSpaces " +
            "      OR desig LIKE :qStandardized " +
            ")";
    
    public static final String SEARCH_FOR_GERMPLASM_LIST_EQUAL =
    		"SELECT DISTINCT listnms.* " +
            "FROM listnms " +
            "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) " +
            "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) " +
            "WHERE " + 
            " listtype not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES') AND liststatus!=9 AND listtype!='FOLDER' AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) " +
            "      OR desig = :q OR listname = :q " +
            "      OR desig = :qNoSpaces " +
            "      OR desig = :qStandardized " +
            ")";
    
    public GermplasmList() {

    }

    public GermplasmList(Integer id) {
        super();
        this.id = id;
    }

    public GermplasmList(Integer id, String name, Long date, String type, Integer userId, String description, GermplasmList parent,
            Integer status) {
        super();
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
        this.userId = userId;
        this.description = description;
        this.parent = parent;
        this.status = status;
    }
    
    public GermplasmList(Integer id, String name, Long date, String type, Integer userId, String description, GermplasmList parent,
            Integer status, String notes) {
        super();
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
        this.userId = userId;
        this.description = description;
        this.parent = parent;
        this.status = status;
        this.notes = notes;
    }

    
    
    public GermplasmList(Integer id, String name, Long date, String type, Integer userId, String description,
            GermplasmList parent, Integer status, Integer sDate, Integer eDate, Integer listLocation, Integer listRef,
            Integer projectId, String notes, List<GermplasmListData> listData) {
        super();
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
        this.userId = userId;
        this.description = description;
        this.parent = parent;
        this.status = status;
        this.sDate = sDate;
        this.eDate = eDate;
        this.listLocation = listLocation;
        this.listRef = listRef;
        this.projectId = projectId;
        this.notes = notes;
        this.listData = listData;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GermplasmList getParent() {
        return parent;
    }

    public void setParent(GermplasmList parent) {
        this.parent = parent;
    }
    
    public String getStatusString() {
        //TODO: make internationalizable
        List<String> listStatus = new ArrayList<String> ();
        String status = String.format("%04d", getStatus());
        
        if (status.charAt(0) == '1') {
            listStatus.add("Final");
        }
        if (status.charAt(1) == '1') {
            listStatus.add("Locked");
        }
        if (status.charAt(2) == '1') {
            listStatus.add("Hidden");
        }
        if (status.charAt(3) == '1') {
            listStatus.add("List");
        } 
        if (status.charAt(3) == '0') {
            listStatus.add("Folder");
        }
        if (status.charAt(3) == '9') {
            listStatus.add("Deleted");
        }
        
        StringBuilder sb = new StringBuilder();
        for (String str : listStatus) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(str);
        }
        
        return sb.toString();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    
    public Integer getsDate() {
        return sDate;
    }

    
    public void setsDate(Integer sDate) {
        this.sDate = sDate;
    }

    
    public Integer geteDate() {
        return eDate;
    }

    
    public void seteDate(Integer eDate) {
        this.eDate = eDate;
    }

    
    public Integer getListLocation() {
        return listLocation;
    }

    
    public void setListLocation(Integer listLocation) {
        this.listLocation = listLocation;
    }

    
    public Integer getListRef() {
        return listRef;
    }

    
    public void setListRef(Integer listRef) {
        this.listRef = listRef;
    }

    
    public Integer getProjectId() {
        return projectId;
    }

    
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getNotes(){
    	return notes;
    }
    
    public void setNotes(String notes){
    	this.notes = notes;
    }

    public List<GermplasmListData> getListData() {
        return listData;
    }

    public void setListData(List<GermplasmListData> listData) {
    	this.listData.clear();
        this.listData.addAll(listData);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GermplasmList [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", date=");
        builder.append(date);
        builder.append(", type=");
        builder.append(type);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", description=");
        builder.append(description);
        builder.append(", status=");
        builder.append(status);
        builder.append(", notes=");
        builder.append(notes);
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
        GermplasmList other = (GermplasmList) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
    
    public Integer getParentId() {
    	return getParent()!=null?getParent().getId():null;
    }
    
    public boolean isFolder() {
    	return getType()!=null && getType().equalsIgnoreCase("FOLDER")?true:false;
    }
    
    public boolean isList() {
    	return getType()!=null && getType().equalsIgnoreCase("LST")?true:false;
    }
    
    public boolean hasParent() {
    	return getParent()!=null?true:false;
    }
    
    public boolean isLocalList() {
        return this.getId() < 0;
    }
    
    public boolean isLockedList() {
        return this.getStatus() >= 100;
    }

}
