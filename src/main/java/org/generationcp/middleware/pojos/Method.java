/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos;

import org.generationcp.middleware.domain.oms.TermId;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Represents breeding methods. The ICIS model recognizes three classes of breeding methods by which genetic material is advanced:
 * <ul>
 * <li>Generative methods: intended to increase allelic diversity by combining alleles from different progenitors through crossing or
 * mutating genes through mutagenesis, introducing new genes through transformation or combining whole genomes through polyploidization.</li>
 * <li>Derivative methods: are processes applied to a single source of seed and are designed to reduce or repartition genetic variation.
 * Example methods are self-fertilization of lines in segregating populations, which reduces allelic diversity through inbreeding (in turn
 * increasing homozygosity), production of double haploid lines, or randomly mating selected plants within a population.</li>
 * <li>Maintenance methods: again applied to a single source of seed, represent deliberate attempts to maintain a specific level of genetic
 * variation with the objective of creating new instances of germplasm that are as similar to the source germplasm as possible. Common
 * examples would be methods used for increases of germplasm accessions, genetic stocks, or foundation seed.</li>
 * </ul>
 * 
 */
@NamedQueries({@NamedQuery(name = "getAllMethods", query = "FROM Method"),
				@NamedQuery(name = "getFavoriteMethodsByMethodType", query = "Select m FROM Method m, ProgramFavorite pf WHERE pf.entityId = m.mid AND m.mtype=:mType AND pf.uniqueID=:programUUID")})
@Entity
@Table(name = "methods")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "method")
@XmlType(propOrder = {"mid", "mtype", "mcode", "mname", "mdesc", "mprgn", "mfprg", "mgrp", "mref", "muid", "snametype", "separator",
		"prefix", "count", "suffix"})
@XmlAccessorType(XmlAccessType.NONE)
public class Method implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	public static final List<Integer> BULKED_CLASSES = Arrays.asList(TermId.BULKING_BREEDING_METHOD_CLASS.getId(),
			TermId.SEED_INCREASE_METHOD_CLASS.getId(), TermId.SEED_ACQUISITION_METHOD_CLASS.getId(),
			TermId.CULTIVAR_FORMATION_METHOD_CLASS.getId());

	public static final List<Integer> NON_BULKED_CLASSES = Arrays.asList(TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId());

	public static final Integer LOCAL_METHOD_ID_DEFAULT = 0;
	public static final Integer METHOD_ATTRIBUTE_DEFAULT = 0;
	public static final Integer NO_FEMALE_PARENTS_DEFAULT = 0;
	public static final Integer METHOD_REFERENCE_DEFAULT = 0;
	public static final String METHOD_DEFAULT = "";

	public static final String GET_ALL = "getAllMethods";
	
	public static final String GET_FAVORITE_METHODS_BY_TYPE = "getFavoriteMethodsByMethodType";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
		return this.mid;
	}

	public void setMid(Integer mid) {
		this.mid = mid;
	}

	public String getMtype() {
		return this.mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getMgrp() {
		return this.mgrp;
	}

	public void setMgrp(String mgrp) {
		this.mgrp = mgrp;
	}

	public String getMcode() {
		return this.mcode;
	}

	public void setMcode(String mcode) {
		this.mcode = mcode;
	}

	public String getMname() {
		return this.mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getMdesc() {
		return this.mdesc;
	}

	public void setMdesc(String mdesc) {
		this.mdesc = mdesc;
	}

	public Integer getMprgn() {
		return this.mprgn;
	}

	public void setMprgn(Integer mprgn) {
		this.mprgn = mprgn;
	}

	public Integer getMfprg() {
		return this.mfprg;
	}

	public void setMfprg(Integer mfprg) {
		this.mfprg = mfprg;
	}

	public Integer getMattr() {
		return this.mattr;
	}

	public void setMattr(Integer mattr) {
		this.mattr = mattr;
	}

	/**
	 * ID of a CVTerm that defines a "method class".
	 * 
	 * METHN of a basic method which has equivalent genetic relationship between progenitors and offspring for the purpose of computing
	 * coefficients of parentage
	 */
	public Integer getGeneq() {
		return this.geneq;
	}

	public void setGeneq(Integer geneq) {
		this.geneq = geneq;
	}

	public Integer getLmid() {
		return this.lmid;
	}

	public void setLmid(Integer lmid) {
		this.lmid = lmid;
	}

	public Integer getMdate() {
		return this.mdate;
	}

	public void setMdate(Integer mdate) {
		this.mdate = mdate;
	}

	public Integer getReference() {
		return this.mref;
	}

	public void setReference(Integer mref) {
		this.mref = mref;
	}

	public Integer getUser() {
		return this.muid;
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Method other = (Method) obj;
		if (this.mid == null) {
			if (other.getMid() != null) {
				return false;
			}
		} else if (!this.mid.equals(other.getMid())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Method [mid=");
		builder.append(this.mid);
		builder.append(", mtype=");
		builder.append(this.mtype);
		builder.append(", mgrp=");
		builder.append(this.mgrp);
		builder.append(", mcode=");
		builder.append(this.mcode);
		builder.append(", mname=");
		builder.append(this.mname);
		builder.append(", mdesc=");
		builder.append(this.mdesc);
		builder.append(", mref=");
		builder.append(this.mref);
		builder.append(", mprgn=");
		builder.append(this.mprgn);
		builder.append(", mfprg=");
		builder.append(this.mfprg);
		builder.append(", mattr=");
		builder.append(this.mattr);
		builder.append(", geneq=");
		builder.append(this.geneq);
		builder.append(", muid=");
		builder.append(this.muid);
		builder.append(", lmid=");
		builder.append(this.lmid);
		builder.append(", mdate=");
		builder.append(this.mdate);
		builder.append(", snametype=");
		builder.append(this.snametype);
		builder.append(", separator=");
		builder.append(this.separator);
		builder.append(", prefix=");
		builder.append(this.prefix);
		builder.append(", count=");
		builder.append(this.count);
		builder.append(", suffix=");
		builder.append(this.suffix);
		builder.append("]");
		return builder.toString();
	}

	public Boolean getIsnew() {
		return this.isnew;
	}

	public void setIsnew(Boolean isnew) {
		this.isnew = isnew;
	}

	/**
	 * @return the snametype
	 */
	public Integer getSnametype() {
		return this.snametype;
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
		return this.separator;
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
		return this.prefix;
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
		return this.count;
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
		return this.suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Transient
	public Boolean isBulkingMethod() {
		return MethodHelper.isBulkingMethod(this.geneq);
	}

	@Transient
	public boolean isGenerative() {
		return this.mtype != null && MethodType.GENERATIVE.getCode().equals(this.mtype.trim());
	}

	@Transient
	public boolean isDerivativeOrMaintenance() {
		return this.mtype != null && (MethodType.DERIVATIVE.getCode().equals(this.mtype.trim()) || MethodType.MAINTENANCE.getCode()
			.equals(this.mtype.trim()));
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return new Method(this.mid, this.mtype, this.mgrp, this.mcode, this.mname, this.mdesc, this.mref, this.mprgn,
				this.mfprg, this.mattr, this.geneq, this.muid, this.lmid, this.mdate);
		}
	}
}
