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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@NamedQueries({ @NamedQuery(name = "getUserByNameUsingEqual", query = "SELECT s FROM userdetails s WHERE s.name = :name"),
    @NamedQuery(name = "getUserByNameUsingLike", query = "SELECT s FROM userdetails s WHERE s.name LIKE :name"),
    @NamedQuery(name = "countUserByNameUsingEqual", query = "SELECT COUNT(s) FROM userdetails s WHERE s.name = :name"),
    @NamedQuery(name = "countUserByNameUsingLike", query = "SELECT COUNT(s) FROM userdetails s WHERE s.name LIKE :name")

})

@Entity
@Table(name = "userdetails")
public class UserDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "userdetailsid")
    private Integer userdetailsid;

    @Column(name = "uname")
    private String uname;

    @Column(name = "ulogincnt")
    private Integer ulogincnt;

    
    	

    public UserDetails() {
    }

    public UserDetails(String uname) {
        super();
        this.uname = uname;
    }

    public UserDetails(Integer userdetailsid, String uname, Integer ulogincnt) {
        super();
        this.userdetailsid = userdetailsid;
        
        this.uname = uname;
        this.ulogincnt = ulogincnt;
        
       
    }
    
    /**
     * Get a copy of this {@link UserDetails} object.
     * Note that this method will not copy the {@link UserDetails#userid} field.
     * 
     * @return
     */
    public UserDetails copy() {
        UserDetails user = new UserDetails();
        user.setUname(uname);
        user.setUlogincnt(ulogincnt);
        
        return user;
    }

    
    
    public Integer getUserdetailsid() {
		return userdetailsid;
	}

	public void setUserdetailsid(Integer userdetailsid) {
		this.userdetailsid = userdetailsid;
	}

	public String getName() {
		return uname;
	}

	public void setName(String uname) {
		this.uname = uname;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public Integer getUlogincnt() {
		return ulogincnt;
	}

	public void setUlogincnt(Integer ulogincnt) {
		this.ulogincnt = ulogincnt;
	}

	@Override
    public int hashCode() {
        return new HashCodeBuilder().append(userdetailsid).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!UserDetails.class.isInstance(obj)) {
            return false;
        }

        UserDetails otherObj = (UserDetails) obj;

        return new EqualsBuilder().append(userdetailsid, otherObj.userdetailsid).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [userdetailsid=");
        builder.append(userdetailsid);
        builder.append(", uname=");
        builder.append(uname);
        builder.append(", ulogincnt=");
        builder.append(ulogincnt);
        builder.append("]");
        return builder.toString();
    }

}
