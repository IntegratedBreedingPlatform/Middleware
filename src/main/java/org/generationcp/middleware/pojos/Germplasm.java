package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * POJO for germplsm table
 * 
 * @author Kevin Manansala, Mark Agarrado
 */
@NamedQueries({
	@NamedQuery(name = "findAllGermplasm", query = "FROM Germplasm"),
	@NamedQuery(name = "countAllGermplasm", query = "SELECT COUNT(g) FROM Germplasm g"),
	// @NamedQuery
	// (
	// name = "findGermplasmByMethodNameUsingEqual",
	// query = "FROM Germplasm g WHERE g.method.mname = :name"
	// ),
	@NamedQuery(name = "findGermplasmByMethodNameUsingEqual", query = "SELECT g FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname = :name"),
	// @NamedQuery
	// (
	// name = "countGermplasmByMethodNameUsingEqual",
	// query =
	// "SELECT COUNT(g) FROM Germplasm g WHERE g.method.mname = :name"
	// ),
	@NamedQuery(name = "countGermplasmByMethodNameUsingEqual", query = "SELECT COUNT(g) FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname = :name"),
	// @NamedQuery
	// (
	// name = "findGermplasmByMethodNameUsingLike",
	// query = "FROM Germplasm g WHERE g.method.mname like :name"
	// ),
	@NamedQuery(name = "findGermplasmByMethodNameUsingLike", query = "SELECT g FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname like :name"),
	// @NamedQuery
	// (
	// name = "countGermplasmByMethodNameUsingLike",
	// query =
	// "SELECT COUNT(g) FROM Germplasm g WHERE g.method.mname like :name"
	// ),
	@NamedQuery(name = "countGermplasmByMethodNameUsingLike", query = "SELECT COUNT(g) FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname like :name"),
	// @NamedQuery
	// (
	// name = "findGermplasmByLocationNameUsingEqual",
	// query = "FROM Germplasm g WHERE g.location.lname = :name"
	// ),
	@NamedQuery(name = "findGermplasmByLocationNameUsingEqual", query = "SELECT g FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname = :name"),
	// @NamedQuery
	// (
	// name = "countGermplasmByLocationNameUsingEqual",
	// query =
	// "SELECT COUNT(g) FROM Germplasm g WHERE g.location.lname = :name"
	// ),
	@NamedQuery(name = "countGermplasmByLocationNameUsingEqual", query = "SELECT COUNT(g) FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname = :name"),
	// @NamedQuery
	// (
	// name = "findGermplasmByLocationNameUsingLike",
	// query = "FROM Germplasm g WHERE g.location.lname like :name"
	// ),
	@NamedQuery(name = "findGermplasmByLocationNameUsingLike", query = "SELECT g FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname like :name"),
	// @NamedQuery
	// (
	// name = "countGermplasmByLocationNameUsingLike",
	// query =
	// "SELECT COUNT(g) FROM Germplasm g WHERE g.location.lname like :name"
	// ),
	@NamedQuery(name = "countGermplasmByLocationNameUsingLike", query = "SELECT COUNT(g) FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname like :name")
// @NamedQuery
// (
// name = "getGermplasmByGIDWithPrefName",
// query =
// "SELECT g, n FROM Germplasm g left join g.names n WITH n.nstat = 1 WHERE g.gid = :gid"
// )
// @NamedQuery
// (
// name = "getGermplasmByGIDWithPrefAbbrev",
// query =
// "SELECT g, n, abbrev.nval FROM Germplasm g left join g.names n WITH n.nstat = 1 left join g.names abbrev WITH abbrev.nstat = 2 WHERE g.gid = :gid"
// )
// @NamedQuery
// (
// name = "getProgenitorsByGIDWithPrefName",
// query =
// "SELECT g, n FROM Germplasm g LEFT JOIN g.names n WITH n.nstat = 1, Progenitor p "
// +
// "WHERE p.pid = g.gid AND p.progntrsPK.gid = :gid"
// )
// @NamedQuery
// (
// name = "countGermplasmDescendants",
// query =
// "SELECT count(distinct g) FROM Germplasm g LEFT JOIN g.progntr p WHERE g.gpid1=:gid or g.gpid2=:gid or p.pid=:gid"
// ),
// @NamedQuery
// (
// name = "getGermplasmManagementNeighbors",
// query =
// "SELECT g, n FROM Germplasm g left join g.names n WITH n.nstat = 1 WHERE g.mgid = :gid"
// ),
// @NamedQuery
// (
// name = "getGermplasmGroupRelatives",
// query =
// "SELECT g, n FROM Germplasm g left join g.names n WITH n.nstat = 1, Germplasm g2 "
// +
// "WHERE g.gpid1 = g2.gpid1 AND g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid"
// )
// @NamedQuery
// (
// name = "getGermplasmDerivativeChildren",
// query =
// "SELECT g, n FROM Germplasm g left join g.names n WITH n.nstat = 1 WHERE g.gnpgs = -1 AND g.gpid2 = :gid"
// )
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "getGermplasmDescendants", query = "SELECT DISTINCT g.* FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
		+ "WHERE g.gpid1=:gid OR g.gpid2=:gid OR p.pid=:gid", resultClass = Germplasm.class),
	@NamedNativeQuery(name = "findGermplasmByPrefName", query = "SELECT g.* FROM germplsm g LEFT JOIN names n ON g.gid = n.gid "
		+ "AND n.nstat = 1 " + "WHERE n.nval = :name", resultClass = Germplasm.class),
	@NamedNativeQuery(name = "getProgenitor1", query = "select * from germplsm g1 where g1.gid = (SELECT g.gpid1 FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
		+ "WHERE g.gid = :gid)", resultClass = Germplasm.class),
	@NamedNativeQuery(name = "getProgenitor2", query = "select * from germplsm g1 where g1.gid = (SELECT g.gpid2 FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
		+ "WHERE g.gid = :gid)", resultClass = Germplasm.class),
	@NamedNativeQuery(name = "getProgenitor", query = "select * from germplsm g1 where g1.gid = (SELECT p.pid FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
		+ "WHERE g.gid = :gid and p.pno=:pno)", resultClass = Germplasm.class)

})
@Entity
@Table(name = "germplsm")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "germplasm")
@XmlType(propOrder = { "gid", "gnpgs", "gpid1", "gpid2", "gdate" })
// @XmlType(propOrder = {"gid", "gnpgs", "gpid1", "gpid2", "gdate",
// "methodMname", "locationLname"})
@XmlAccessorType(XmlAccessType.NONE)
public class Germplasm implements Serializable {
    private static final long serialVersionUID = 1L;

    // string contants for name of queries
    public static final String FIND_ALL = "findAllGermplasm";
    public static final String COUNT_ALL = "countAllGermplasm";
    public static final String FIND_BY_PREF_NAME = "findGermplasmByPrefName";
    public static final String COUNT_BY_PREF_NAME = "SELECT COUNT(g.gid) FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 WHERE n.nval = :name";
    public static final String FIND_BY_METHOD_NAME_USING_EQUAL = "findGermplasmByMethodNameUsingEqual";
    public static final String COUNT_BY_METHOD_NAME_USING_EQUAL = "countGermplasmByMethodNameUsingEqual";
    public static final String FIND_BY_METHOD_NAME_USING_LIKE = "findGermplasmByMethodNameUsingLike";
    public static final String COUNT_BY_METHOD_NAME_USING_LIKE = "countGermplasmByMethodNameUsingLike";
    public static final String FIND_BY_LOCATION_NAME_USING_EQUAL = "findGermplasmByLocationNameUsingEqual";
    public static final String COUNT_BY_LOCATION_NAME_USING_EQUAL = "countGermplasmByLocationNameUsingEqual";
    public static final String FIND_BY_LOCATION_NAME_USING_LIKE = "findGermplasmByLocationNameUsingLike";
    public static final String COUNT_BY_LOCATION_NAME_USING_LIKE = "countGermplasmByLocationNameUsingLike";
    // public static final String GET_BY_GID_WITH_PREF_NAME =
    // "getGermplasmByGIDWithPrefName";
    public static final String GET_BY_GID_WITH_PREF_NAME = "SELECT {g.*}, {n.*} FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 WHERE g.gid = :gid";
    // public static final String GET_BY_GID_WITH_PREF_ABBREV =
    // "getGermplasmByGIDWithPrefAbbrev";
    public static final String GET_BY_GID_WITH_PREF_ABBREV = "SELECT {g.*}, {n.*}, {abbrev.*} FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
	    + "LEFT JOIN names abbrev ON g.gid = abbrev.gid AND abbrev.nstat = 2 WHERE g.gid = :gid";
    public static final String FIND_DESCENDANTS = "getGermplasmDescendants";
    // public static final String COUNT_DESCENDANTS =
    // "countGermplasmDescendants";
    public static final String COUNT_DESCENDANTS = "SELECT COUNT(DISTINCT g.gid) FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
	    + "WHERE g.gpid1=:gid OR g.gpid2=:gid OR p.pid=:gid";
    public static final String FIND_PROGENITOR1 = "getProgenitor1";
    public static final String FIND_PROGENITOR2 = "getProgenitor2";
    public static final String FIND_PROGENITOR = "getProgenitor";
    // public static final String GET_PROGENITORS_BY_GID_WITH_PREF_NAME =
    // "getProgenitorsByGIDWithPrefName";
    public static final String GET_PROGENITORS_BY_GID_WITH_PREF_NAME = "SELECT {g.*}, {n.*} FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
	    + "JOIN progntrs p ON p.pid = g.gid WHERE p.gid = :gid";
    // public static final String GET_MANAGEMENT_NEIGHBORS =
    // "getGermplasmManagementNeighbors";
    public static final String GET_MANAGEMENT_NEIGHBORS = "SELECT {g.*}, {n.*} FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 WHERE g.mgid = :gid";
    // public static final String GET_GROUP_RELATIVES =
    // "getGermplasmGroupRelatives";
    public static final String GET_GROUP_RELATIVES = "SELECT {g.*}, {n.*} FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
	    + "JOIN germplsm g2 ON g.gpid1 = g2.gpid1 WHERE g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid";
    // public static final String GET_DERIVATIVE_CHILDREN =
    // "getGermplasmDerivativeChildren";
    public static final String GET_DERIVATIVE_CHILDREN = "SELECT {g.*}, {n.*} FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
	    + "WHERE g.gnpgs = -1 AND g.gpid2 = :gid";

    @Id
    @Basic(optional = false)
    @Column(name = "gid")
    @XmlElement(name = "gid")
    private Integer gid;

    @Basic(optional = false)
    @Column(name = "methn")
    private Integer methodId;

    @Basic(optional = false)
    @Column(name = "gnpgs")
    @XmlElement(name = "numberOfProgenitors")
    private Integer gnpgs;

    @Basic(optional = false)
    @Column(name = "gpid1")
    @XmlElement(name = "firstParent")
    private Integer gpid1;

    @Basic(optional = false)
    @Column(name = "gpid2")
    @XmlElement(name = "secondParent")
    private Integer gpid2;

    /**
     * @ManyToOne(targetEntity = User.class)
     * @JoinColumn(name = "germuid", nullable = false)
     * @NotFound(action = NotFoundAction.IGNORE) private User user;
     **/

    @Basic(optional = false)
    @Column(name = "germuid")
    private Integer userId;

    @Basic(optional = false)
    @Column(name = "lgid")
    private Integer lgid;

    /**
     * @ManyToOne(targetEntity = Location.class)
     * @JoinColumn(name = "glocn", nullable = true)
     * @NotFound(action = NotFoundAction.IGNORE) private Location location;
     **/

    @Basic(optional = false)
    @Column(name = "glocn")
    private Integer locationId;

    @Basic(optional = false)
    @Column(name = "gdate")
    @XmlElement(name = "creationDate")
    private Integer gdate;

    /**
     * @ManyToOne(targetEntity = Bibref.class)
     * @JoinColumn(name = "gref", nullable = true)
     * @NotFound(action = NotFoundAction.IGNORE) private Bibref reference;
     **/

    @Basic(optional = false)
    @Column(name = "gref")
    private Integer referenceId;

    @Basic(optional = false)
    @Column(name = "grplce")
    private Integer grplce;

    @Basic(optional = false)
    @Column(name = "mgid")
    private Integer mgid;

    // @Column(name = "cid")
    // private Integer cid;
    //
    // @Column(name = "sid")
    // private Integer sid;
    //
    // @Column(name = "gchange")
    // private Integer gchange;

    // @OneToMany(mappedBy = "germplasm")
    // private Set<Attribute> attributes = new HashSet<Attribute>();

    // @OneToMany(mappedBy = "germplasm")
    // private Set<Name> names = new HashSet<Name>();

    /**
     * @OneToMany(mappedBy = "germplasm") private Set<Progenitor> progntr = new
     *                     HashSet<Progenitor>();
     **/

    /**
     * This variable is populated only when the Germplasm POJO is retrieved by
     * using GermplasmDataManager.getGermplasmWithPrefName() and
     * GermplasmDataManager.getGermplasmWithPrefAbbrev(). Otherwise it is null
     * always.
     */
    @Transient
    private Name preferredName = null;

    /**
     * This variable is populated only when the Germplasm POJO is retrieved by
     * using GermplasmDataManager.getGermplasmWithPrefAbbrev(). Otherwise it is
     * null always.
     */
    @Transient
    private String preferredAbbreviation = null;

    public Germplasm() {
    }

    public Germplasm(Integer gid) {
	this.gid = gid;
    }

    public Integer getGid() {
	return gid;
    }

    public void setGid(Integer gid) {
	this.gid = gid;
    }

    public Integer getGnpgs() {
	return gnpgs;
    }

    public void setGnpgs(Integer gnpgs) {
	this.gnpgs = gnpgs;
    }

    public Integer getGpid1() {
	return gpid1;
    }

    public void setGpid1(Integer gpid1) {
	this.gpid1 = gpid1;
    }

    public Integer getGpid2() {
	return gpid2;
    }

    public void setGpid2(Integer gpid2) {
	this.gpid2 = gpid2;
    }

    public Integer getLgid() {
	return lgid;
    }

    public void setLgid(Integer lgid) {
	this.lgid = lgid;
    }

    public Integer getGdate() {
	return gdate;
    }

    public void setGdate(Integer gdate) {
	this.gdate = gdate;
    }

    public Integer getGrplce() {
	return grplce;
    }

    public void setGrplce(Integer grplce) {
	this.grplce = grplce;
    }

    public Integer getMgid() {
	return mgid;
    }

    public void setMgid(Integer mgid) {
	this.mgid = mgid;
    }

    // public Integer getCid()
    // {
    // return cid;
    // }
    //
    // public void setCid(Integer cid)
    // {
    // this.cid = cid;
    // }
    //
    // public Integer getSid()
    // {
    // return sid;
    // }
    //
    // public void setSid(Integer sid)
    // {
    // this.sid = sid;
    // }
    //
    // public Integer getGchange()
    // {
    // return gchange;
    // }

    // public void setGchange(Integer gchange)
    // {
    // this.gchange = gchange;
    // }

    /**
     * public Set<Attribute> getAttributes() { return attributes; }
     * 
     * public void setAttributes(Set<Attribute> attributes) { this.attributes =
     * attributes; }
     **/

    /**
     * public Method getMethod() { return method; }
     * 
     * @XmlElement(name = "creationMethod") public String getMethodMname() {
     *                  return method.getMname(); }
     * 
     *                  public void setMethod(Method method) { this.method =
     *                  method; }
     **/

    public Integer getMethodId() {
	return methodId;
    }

    public void setMethodId(Integer methodId) {
	this.methodId = methodId;
    }

    /**
     * public User getUser() { return user; }
     * 
     * public void setUser(User user) { this.user = user; }
     **/

    public Integer getUserId() {
	return userId;
    }

    public void setUserId(Integer userId) {
	this.userId = userId;
    }

    /**
     * public Location getLocation() { return location; }
     * 
     * @XmlElement(name = "location") public String getLocationLname() { return
     *                  location.getLname(); }
     * 
     *                  public void setLocation(Location location) {
     *                  this.location = location; }
     **/

    public Integer getLocationId() {
	return locationId;
    }

    public void setLocationId(Integer locationId) {
	this.locationId = locationId;
    }

    /**
     * public Bibref getReference() { return reference; }
     * 
     * public void setReference(Bibref reference) { this.reference = reference;
     * }
     **/

    public Integer getReferenceId() {
	return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
	this.referenceId = referenceId;
    }

    /**
     * public Set<Name> getNames() { return names; }
     * 
     * public void setNames(Set<Name> names) { this.names = names; }
     **/

    public Name getPreferredName() {
	return preferredName;
    }

    public void setPreferredName(Name preferredName) {
	this.preferredName = preferredName;
    }

    public String getPreferredAbbreviation() {
	return preferredAbbreviation;
    }

    public void setPreferredAbbreviation(String preferredAbbreviation) {
	this.preferredAbbreviation = preferredAbbreviation;
    }

    /**
     * public Set<Progenitor> getProgenitor() { return progntr; }
     * 
     * public void setProgenitor(Set<Progenitor> progntr) { this.progntr =
     * progntr; }
     **/

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (obj == this)
	    return true;
	if (!(obj instanceof Germplasm))
	    return false;

	Germplasm rhs = (Germplasm) obj;
	return new EqualsBuilder().appendSuper(super.equals(obj))
		.append(gid, rhs.gid).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 37).append(gid).toHashCode();
    }

    @Override
    public String toString() {
	return "Germplsm [gid=" + gid + ", gnpgs=" + gnpgs + ", gpid1=" + gpid1
		+ ", gpid2=" + gpid2 + ", " + "lgid=" + lgid + ", gdate="
		+ gdate + ", grplce=" + grplce + ", mgid=" + mgid /*
								   * + ", cid="
								   * + cid +
								   * ", sid=" +
								   * sid +
								   * ", gchange="
								   * + gchange
								   */+ "]";
    }

}
