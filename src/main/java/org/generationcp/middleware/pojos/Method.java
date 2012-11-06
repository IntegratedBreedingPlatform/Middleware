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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * POJO for methods table
 * 
 * @author Kevin Manansala, Mark Agarrado
 */
@NamedQueries({ @NamedQuery(name = "getAllMethods", query = "FROM Method") })
@Entity
@Table(name = "methods")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "method")
@XmlType(propOrder = { "mid", "mtype", "mcode", "mname", "mdesc", "mprgn", "mfprg", "mgrp", "mref", "muid" })
@XmlAccessorType(XmlAccessType.NONE)
public class Method implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final String GET_ALL = "getAllMethods";

    @Id
    @Basic(optional = false)
    @Column(name = "mid")
    @XmlElement(name = "methodId")
    private Integer mid;

    @Basic(optional = false)
    @Column(name = "mtype")
    @XmlElement(name = "type")
    private String mtype;

    @Basic(optional = false)
    @Column(name = "mgrp")
    @XmlElement(name = "breedingSystem")
    private String mgrp;

    @Basic(optional = false)
    @Column(name = "mcode")
    @XmlElement(name = "code")
    private String mcode;

    @Basic(optional = false)
    @Column(name = "mname")
    @XmlElement(name = "name")
    private String mname;

    @Basic(optional = false)
    @Column(name = "mdesc")
    @XmlElement(name = "description")
    private String mdesc;

    @Basic(optional = false)
    @Column(name = "mref")
    @XmlElement(name = "reference")
    private Integer mref;

    @Basic(optional = false)
    @Column(name = "mprgn")
    @XmlElement(name = "numberOfProgenitors")
    private Integer mprgn;

    @Basic(optional = false)
    @Column(name = "mfprg")
    @XmlElement(name = "numberOfFemaleParents")
    private Integer mfprg;

    @Basic(optional = false)
    @Column(name = "mattr")
    private Integer mattr;

    @Basic(optional = false)
    @Column(name = "geneq")
    private Integer geneq;

    @Basic(optional = false)
    @Column(name = "muid")
    @XmlElement(name = "userid")
    private Integer muid;

    @Basic(optional = false)
    @Column(name = "lmid")
    private Integer lmid;

    @Basic(optional = false)
    @Column(name = "mdate")
    private Integer mdate;

    public Method() {
    }

    public Method(Integer mid) {
        this.mid = mid;
    }

    public Method(Integer mid, String mtype, String mgrp, String mcode, String mname, String mdesc, Integer mref, Integer mprgn,
            Integer mfprg, Integer mattr, Integer geneq, Integer muid, Integer lmid, Integer mdate) {
        super();
        this.mid = mid;
        this.mtype = mtype;
        this.mgrp = mgrp;
        this.mcode = mcode;
        this.mname = mname;
        this.mdesc = mdesc;
        this.mref = mref;
        this.mprgn = mprgn;
        this.mfprg = mfprg;
        this.mattr = mattr;
        this.geneq = geneq;
        this.muid = muid;
        this.lmid = lmid;
        this.mdate = mdate;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getMgrp() {
        return mgrp;
    }

    public void setMgrp(String mgrp) {
        this.mgrp = mgrp;
    }

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMdesc() {
        return mdesc;
    }

    public void setMdesc(String mdesc) {
        this.mdesc = mdesc;
    }

    public Integer getMprgn() {
        return mprgn;
    }

    public void setMprgn(Integer mprgn) {
        this.mprgn = mprgn;
    }

    public Integer getMfprg() {
        return mfprg;
    }

    public void setMfprg(Integer mfprg) {
        this.mfprg = mfprg;
    }

    public Integer getMattr() {
        return mattr;
    }

    public void setMattr(Integer mattr) {
        this.mattr = mattr;
    }

    public Integer getGeneq() {
        return geneq;
    }

    public void setGeneq(Integer geneq) {
        this.geneq = geneq;
    }

    public Integer getLmid() {
        return lmid;
    }

    public void setLmid(Integer lmid) {
        this.lmid = lmid;
    }

    public Integer getMdate() {
        return mdate;
    }

    public void setMdate(Integer mdate) {
        this.mdate = mdate;
    }

    public Integer getReference() {
        return mref;
    }

    public void setReference(Integer mref) {
        this.mref = mref;
    }

    public Integer getUser() {
        return muid;
    }

    public void setUser(Integer muid) {
        this.muid = muid;
    }

    @Override
    public int hashCode() {
        return this.getMid();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Method) {
            Method param = (Method) obj;
            if (this.getMid() == param.getMid()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Method [mid=");
        builder.append(mid);
        builder.append(", mtype=");
        builder.append(mtype);
        builder.append(", mgrp=");
        builder.append(mgrp);
        builder.append(", mcode=");
        builder.append(mcode);
        builder.append(", mname=");
        builder.append(mname);
        builder.append(", mdesc=");
        builder.append(mdesc);
        builder.append(", mref=");
        builder.append(mref);
        builder.append(", mprgn=");
        builder.append(mprgn);
        builder.append(", mfprg=");
        builder.append(mfprg);
        builder.append(", mattr=");
        builder.append(mattr);
        builder.append(", geneq=");
        builder.append(geneq);
        builder.append(", muid=");
        builder.append(muid);
        builder.append(", lmid=");
        builder.append(lmid);
        builder.append(", mdate=");
        builder.append(mdate);
        builder.append("]");
        return builder.toString();
    }

}
