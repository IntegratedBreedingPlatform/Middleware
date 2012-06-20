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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * POJO for listnms table
 * 
 * @author Kevin Manansala, Mark Agarrado
 * 
 */

@Entity
@Table(name = "listnms")
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

    @OneToMany(mappedBy = "list", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GermplasmListData> listData = new ArrayList<GermplasmListData>();

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<GermplasmListData> getListData() {
        return listData;
    }

    public void setListData(List<GermplasmListData> listData) {
        this.listData = listData;
    }

    @Override
    public String toString() {
        return "GermplasmList [id=" + id + ", name=" + name + ", date=" + date + ", type=" + type + ", description=" + description
                + ", status=" + status + "]";
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

}
