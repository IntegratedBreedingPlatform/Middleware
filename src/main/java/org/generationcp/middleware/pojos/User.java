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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 * POJO for users table.
 * 
 */
@NamedQueries({ @NamedQuery(name = "getUserByNameUsingEqual", query = "SELECT s FROM User s WHERE s.name = :name"),
    @NamedQuery(name = "getUserByNameUsingLike", query = "SELECT s FROM User s WHERE s.name LIKE :name"),
    @NamedQuery(name = "countUserByNameUsingEqual", query = "SELECT COUNT(s) FROM User s WHERE s.name = :name"),
    @NamedQuery(name = "countUserByNameUsingLike", query = "SELECT COUNT(s) FROM User s WHERE s.name LIKE :name")

})

@NamedNativeQueries({
    @NamedNativeQuery(name = "getAllUsersSorted",
        query = "SELECT u.* FROM users u, persons p " +
        	"WHERE u.personid = p.personid ORDER BY fname, lname", resultClass = User.class)
})

@Entity
@Table(name = "users")
public class User implements Serializable, BeanFormState {

    private static final long serialVersionUID = 1L;
    
    public static final String GET_BY_NAME_USING_EQUAL = "getUserByNameUsingEqual";
    public static final String GET_BY_NAME_USING_LIKE = "getUserByNameUsingLike";
    public static final String COUNT_BY_NAME_USING_EQUAL = "countUserByNameUsingEqual";
    public static final String COUNT_BY_NAME_USING_LIKE = "countUserByNameUsingLike";
    public static final String GET_ALL_USERS_SORTED = "getAllUsersSorted";

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
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<UserRole> roles;
    
    @Transient
    private Boolean isnew = false;

	@Transient
    private Person person;
	
	@Transient
    private Boolean active = false;
	
	@Transient
    private Boolean enabled = true;

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
    
    /**
     * Get a copy of this {@link User} object.
     * Note that this method will not copy the {@link User#userid} field.
     * 
     * @return the copy of the User object
     */
    public User copy() {
        User user = new User();
        user.setInstalid(instalid);
        user.setStatus(status);
        user.setAccess(access);
        user.setType(type);
        user.setName(name);
        user.setPassword(password);
        user.setPersonid(personid);
        user.setAdate(adate);
        user.setCdate(cdate);
        user.setIsNew(isnew);
        user.setActive(active);
        user.setEnabled(enabled);
        return user;
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
    
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    
    public Boolean isNew() {
        return isnew;
    }

    public void setIsNew(Boolean val) {
        this.isnew = val;
    }

	public List<UserRole> getRoles() {
		return roles;
	}

	
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}

	@Override
    public int hashCode() {
        return new HashCodeBuilder().append(userid).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!User.class.isInstance(obj)) {
            return false;
        }

        User otherObj = (User) obj;

        return new EqualsBuilder().append(userid, otherObj.userid).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [userid=");
        builder.append(userid);
        builder.append(", instalid=");
        builder.append(instalid);
        builder.append(", status=");
        builder.append(status);
        builder.append(", access=");
        builder.append(access);
        builder.append(", type=");
        builder.append(type);
        builder.append(", name=");
        builder.append(name);
        builder.append(", password=");
        builder.append(password);
        builder.append(", personid=");
        builder.append(personid);
        builder.append(", adate=");
        builder.append(adate);
        builder.append(", cdate=");
        builder.append(cdate);
        builder.append(", person=");
        builder.append(person); 
        builder.append(", isnew=");
        builder.append(isnew);
        builder.append(", isActive=");
        builder.append(active);
        builder.append(", isEnabled=");
        builder.append(enabled);
        
        builder.append("]");
        return builder.toString();
    }

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return active;
	}

	@Override
	public void setActive(Boolean val) {
		// TODO Auto-generated method stub
		this.active = val;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}

	@Override
	public void setEnabled(Boolean val) {
		this.enabled = val;
		
	}

}
