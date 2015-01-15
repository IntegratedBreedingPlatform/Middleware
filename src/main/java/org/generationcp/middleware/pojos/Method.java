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

import org.generationcp.middleware.domain.oms.TermId;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * POJO for methods table.
 * 
 * @author Kevin Manansala, Mark Agarrado
 */
@NamedQueries({ @NamedQuery(name = "getAllMethods", query = "FROM Method") })
@Entity
@Table(name = "methods")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "method")
@XmlType(propOrder = { "mid", "mtype", "mcode", "mname", "mdesc", "mprgn", "mfprg", "mgrp", "mref", "muid", "snametype", "separator", "prefix", "count", "suffix" })
@XmlAccessorType(XmlAccessType.NONE)
public class Method implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public static final List<Integer> BULKED_CLASSES = Arrays.asList(
    		TermId.BULKING_BREEDING_METHOD_CLASS.getId() 
    		, TermId.SEED_INCREASE_METHOD_CLASS.getId() 
    		, TermId.SEED_ACQUISITION_METHOD_CLASS.getId()
    		, TermId.CULTIVAR_FORMATION_METHOD_CLASS.getId());
    
    public static final List<Integer> NON_BULKED_CLASSES = Arrays.asList(
    		TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId());

    public static final String GET_ALL = "getAllMethods";

    @Id
    @Basic(optional = false)
    @Column(name = "mid")
    @XmlElement(name = "methodId")
    private Integer mid;
    
    @Basic(optional = true)
    @Column(name = "program_uuid")
    private String uniqueID;

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
    
    @Basic(optional = true)
    @Column(name = "snametype")
    private Integer snametype;
    
    @Basic(optional = true)
    @Column(name = "[separator]")
    private String separator;
    
    @Basic(optional = true)
    @Column(name = "[prefix]")
    private String prefix;
    
    @Basic(optional = true)
    @Column(name = "[count]")
    private String count;
    
    @Basic(optional = true)
    @Column(name = "[suffix]")
    private String suffix;
    
    
    @Transient
    private Boolean isnew = false;

    public Method() {
    }

    public Method(Integer mid) {
        this.mid = mid;
    }

    public Method(Integer mid, String mtype, String mgrp, String mcode, String mname, String mdesc, Integer mref, Integer mprgn,
            Integer mfprg, Integer mattr, Integer geneq, Integer muid, Integer lmid, Integer mdate, String uniqueID) {
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
        this.uniqueID = uniqueID;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Method other = (Method) obj;
        if (mid == null) {
            if (other.getMid() != null) {
                return false;
            }
        } else if (!mid.equals(other.getMid())) {
            return false;
        }
        return true;
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
        builder.append(", snametype=");
        builder.append(snametype);
        builder.append(", separator=");
        builder.append(separator);
        builder.append(", prefix=");
        builder.append(prefix);
        builder.append(", count=");
        builder.append(count);
        builder.append(", suffix=");
        builder.append(suffix);
        builder.append("]");
        return builder.toString();
    }

	public Boolean getIsnew() {
		return isnew;
	}

	public void setIsnew(Boolean isnew) {
		this.isnew = isnew;
	}

	/**
	 * @return the snametype
	 */
	public Integer getSnametype() {
		return snametype;
	}

	/**
	 * @param snametype the snametype to set
	 */
	public void setSnametype(Integer snametype) {
		this.snametype = snametype;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	@Transient
	public Boolean isBulkingMethod() {
		if (geneq != null) {
			if (BULKED_CLASSES.contains(geneq)) {
				return true;
			}
			else if (NON_BULKED_CLASSES.contains(geneq)) {
				return false;
			}
		}
		return null;
	}
	
}
