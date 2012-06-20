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
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * POJO for locdes table
 * 
 * @author klmanansala
 */
@Entity
@Table(name = "locdes")
public class Locdes implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ldid")
    private Integer ldid;

    @ManyToOne(targetEntity = Location.class)
    @JoinColumn(name = "locid", nullable = false)
    private Location location;

    @ManyToOne(targetEntity = UserDefinedField.class)
    @JoinColumn(name = "dtype", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private UserDefinedField type;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "duid", nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private User user;

    @Basic(optional = false)
    @Column(name = "dval")
    private String dval;

    @Basic(optional = false)
    @Column(name = "ddate")
    private Integer ddate;

    @ManyToOne(targetEntity = Bibref.class)
    @JoinColumn(name = "dref", nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private Bibref reference;

    public Locdes() {
    }

    public Integer getLdid() {
        return ldid;
    }

    public Locdes(Integer ldid, Location location, UserDefinedField type, User user, String dval, Integer ddate, Bibref reference) {
        super();
        this.ldid = ldid;
        this.location = location;
        this.type = type;
        this.user = user;
        this.dval = dval;
        this.ddate = ddate;
        this.reference = reference;
    }

    public void setLdid(Integer ldid) {
        this.ldid = ldid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UserDefinedField getType() {
        return type;
    }

    public void setType(UserDefinedField type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bibref getReference() {
        return reference;
    }

    public void setReference(Bibref reference) {
        this.reference = reference;
    }

    public String getDval() {
        return dval;
    }

    public void setDval(String dval) {
        this.dval = dval;
    }

    public Integer getDdate() {
        return ddate;
    }

    public void setDdate(Integer ddate) {
        this.ddate = ddate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Locdes) {
            Locdes param = (Locdes) obj;
            if (this.getLdid() == param.getLdid()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getLdid();
    }

    @Override
    public String toString() {
        return "Locdes [ldid=" + ldid + ", dval=" + dval + ", ddate=" + ddate + "]";
    }

}
