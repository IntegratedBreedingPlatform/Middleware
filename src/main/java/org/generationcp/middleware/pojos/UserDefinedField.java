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

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * POJO for udflds table.
 * 
 * @author Kevin Manansala, Mark Agarrado
 */
@Entity
@Table(name = "udflds")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "udfield")
@XmlType(propOrder = { "fldno", "fcode", "fname", "fdesc" })
@XmlAccessorType(XmlAccessType.NONE)
public class UserDefinedField implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "fldno")
    @XmlElement(name = "udfieldId")
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer fldno;

    @Basic(optional = false)
    @Column(name = "ftable")
    private String ftable;

    @Basic(optional = false)
    @Column(name = "ftype")
    private String ftype;

    @Basic(optional = false)
    @Column(name = "fcode")
    @XmlElement(name = "code")
    private String fcode;

    @Basic(optional = false)
    @Column(name = "fname")
    @XmlElement(name = "name")
    private String fname;

    @Basic(optional = false)
    @Column(name = "ffmt")
    private String ffmt;

    @Basic(optional = false)
    @Column(name = "fdesc")
    @XmlElement(name = "description")
    private String fdesc;

    @Basic(optional = false)
    @Column(name = "lfldno")
    private Integer lfldno;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "fuid", nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private User user;

    @Basic(optional = false)
    @Column(name = "fdate")
    private Integer fdate;

    @Column(name = "scaleid")
    private Integer scaleid;

    public UserDefinedField() {
    }

    public UserDefinedField(Integer fldno) {
        this.fldno = fldno;
    }

    public UserDefinedField(Integer fldno, String ftable, String ftype, String fcode, String fname, String ffmt, String fdesc,
            Integer lfldno, User user, Integer fdate, Integer scaleid) {
        super();
        this.fldno = fldno;
        this.ftable = ftable;
        this.ftype = ftype;
        this.fcode = fcode;
        this.fname = fname;
        this.ffmt = ffmt;
        this.fdesc = fdesc;
        this.lfldno = lfldno;
        this.user = user;
        this.fdate = fdate;
        this.scaleid = scaleid;
    }

    public Integer getFldno() {
        return fldno;
    }

    public void setFldno(Integer fldno) {
        this.fldno = fldno;
    }

    public String getFtable() {
        return ftable;
    }

    public void setFtable(String ftable) {
        this.ftable = ftable;
    }

    public String getFtype() {
        return ftype;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFfmt() {
        return ffmt;
    }

    public void setFfmt(String ffmt) {
        this.ffmt = ffmt;
    }

    public String getFdesc() {
        return fdesc;
    }

    public void setFdesc(String fdesc) {
        this.fdesc = fdesc;
    }

    public Integer getLfldno() {
        return lfldno;
    }

    public void setLfldno(Integer lfldno) {
        this.lfldno = lfldno;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getFdate() {
        return fdate;
    }

    public void setFdate(Integer fdate) {
        this.fdate = fdate;
    }

    public Integer getScaleid() {
        return scaleid;
    }

    public void setScaleid(Integer scaleid) {
        this.scaleid = scaleid;
    }

    @Override
    public int hashCode() {
        return this.getFldno();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof UserDefinedField) {
            UserDefinedField param = (UserDefinedField) obj;
            if (this.getFldno().equals(param.getFldno())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserDefinedField [fldno=");
        builder.append(fldno);
        builder.append(", ftable=");
        builder.append(ftable);
        builder.append(", ftype=");
        builder.append(ftype);
        builder.append(", fcode=");
        builder.append(fcode);
        builder.append(", fname=");
        builder.append(fname);
        builder.append(", ffmt=");
        builder.append(ffmt);
        builder.append(", fdesc=");
        builder.append(fdesc);
        builder.append(", lfldno=");
        builder.append(lfldno);
        builder.append(", user=");
        builder.append(user);
        builder.append(", fdate=");
        builder.append(fdate);
        builder.append(", scaleid=");
        builder.append(scaleid);
        builder.append("]");
        return builder.toString();
    }

}
