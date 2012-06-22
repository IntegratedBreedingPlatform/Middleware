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
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "userid")
    private Integer userid;

    @Column(name = "instalid")
    private Integer instalid;

    @Column(name = "ustatus")
    private Integer status;

    @Column(name = "uaccess")
    private Integer access;

    @Column(name = "utype")
    private Integer type;

    @Column(name = "uname")
    private String name;

    @Column(name = "upswd")
    private String password;

    @Column(name = "personid")
    private Integer personid;

    @Column(name = "adate")
    private Integer adate;

    @Column(name = "cdate")
    private Integer cdate;

    public User() {
    }

    public User(Integer userid) {
        super();
        this.userid = userid;
    }

    public User(Integer userid, Integer instalid, Integer status, Integer access, Integer type, String name, String password, Integer personid, Integer adate, Integer cdate) {
        super();
        this.userid = userid;
        this.instalid = instalid;
        this.status = status;
        this.access = access;
        this.type = type;
        this.name = name;
        this.password = password;
        this.personid = personid;
        this.adate = adate;
        this.cdate = cdate;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getInstalid() {
        return instalid;
    }

    public void setInstalid(Integer instalid) {
        this.instalid = instalid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPersonid() {
        return personid;
    }

    public void setPersonid(Integer personid) {
        this.personid = personid;
    }

    public Integer getAdate() {
        return adate;
    }

    public void setAdate(Integer adate) {
        this.adate = adate;
    }

    public Integer getCdate() {
        return cdate;
    }

    public void setCdate(Integer cdate) {
        this.cdate = cdate;
    }

    @Override
    public String toString() {
        return "User [userid=" + userid + ", instalid=" + instalid + ", status=" + status + ", access=" + access + ", type=" + type + ", name=" + name + ", password=" + password + ", personid="
               + personid + ", adate=" + adate + ", cdate=" + cdate + "]";
    }

}
