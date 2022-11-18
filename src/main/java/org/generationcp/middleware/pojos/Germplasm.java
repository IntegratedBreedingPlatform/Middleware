/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.pojos;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDAO;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * POJO for germplsm table.
 *
 * @author Kevin Manansala, Mark Agarrado, Dennis Billano
 */
@NamedQueries({
	@NamedQuery(name = "getAllGermplasm", query = "FROM Germplasm"),
	@NamedQuery(name = "countAllGermplasm", query = "SELECT COUNT(g) FROM Germplasm g")
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "getGermplasmDescendants",
		query = "SELECT DISTINCT g.* FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
			+ "WHERE (g.gpid1=:gid OR g.gpid2=:gid OR p.pid=:gid) " + "AND  g.deleted = 0  and g.grplce = 0",
		resultClass = Germplasm.class),

	@NamedNativeQuery(name = "getGermplasmByPrefName",
		query = "SELECT g.* FROM germplsm g LEFT JOIN names n ON g.gid = n.gid " + "AND n.nstat = 1 " + "WHERE n.nval = :name",
		resultClass = Germplasm.class),

	@NamedNativeQuery(name = "getProgenitor1",
		query = "SELECT p.* FROM germplsm g, germplsm p WHERE g.gid = :gid "
			+ "and g.gpid1 = p.gid and p.deleted = 0 and p.grplce = 0",
		resultClass = Germplasm.class),

	@NamedNativeQuery(name = "getProgenitor2",
		query = "SELECT p.* FROM germplsm g, germplsm p WHERE g.gid = :gid "
			+ "and g.gpid2 = p.gid and p.deleted = 0 and p.grplce = 0",
		resultClass = Germplasm.class),

	@NamedNativeQuery(name = "getProgenitor", query = "SELECT g.* FROM germplsm g, progntrs p WHERE g.gid = p.pid "
		+ "and p.gid = :gid and p.pno = :pno and  g.deleted = 0  and g.grplce = 0", resultClass = Germplasm.class)})
@Entity
@Table(name = "germplsm")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "germplasm")
@XmlType(propOrder = {"gid", "gnpgs", "gpid1", "gpid2", "gdate"})
@XmlAccessorType(XmlAccessType.NONE)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "germplsm")
public class Germplasm extends AbstractEntity implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	// string contants for name of queries
	public static final String GET_ALL = "getAllGermplasm";
	public static final String COUNT_ALL = "countAllGermplasm";
	public static final String GET_BY_PREF_NAME = "getGermplasmByPrefName";
	public static final String COUNT_BY_PREF_NAME =
		"SELECT COUNT(g.gid) " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " + "WHERE n.nval = :name";

	public static final String GET_BY_GID_WITH_PREF_NAME =
		"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " + "WHERE g.gid = :gid";

	public static final String GET_DESCENDANTS = "getGermplasmDescendants";

	public static final String COUNT_DESCENDANTS =
		"SELECT COUNT(DISTINCT g.gid) " + "FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
			+ "WHERE (g.gpid1 = :gid OR g.gpid2 = :gid OR p.pid=:gid) " + "AND  g.deleted = 0  and g.grplce = 0";
	public static final String GET_PROGENITOR1 = "getProgenitor1";
	public static final String GET_PROGENITOR2 = "getProgenitor2";
	public static final String GET_PROGENITOR = "getProgenitor";

	public static final String GET_PROGENITORS_BY_GIDS_WITH_PREF_NAME =
		"SELECT p.gid, {g.*}, {n.*}, (select pMale.grpName from listdata pMale where pMale.gid = g.gid limit 1) as malePedigree "
			+ "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
			+ "JOIN progntrs p ON p.pid = g.gid "
			+ "WHERE p.gid in (:gidList) and  g.deleted = 0  and g.grplce = 0 "
			+ "ORDER BY p.gid, p.pno";

	public static final String GET_MANAGEMENT_NEIGHBORS =
		"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
			+ "WHERE g.mgid = :gid AND  g.deleted = 0  and g.grplce = 0 ORDER BY g.gid";

	public static final String GET_GROUP_RELATIVES =
		"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
			+ "JOIN germplsm g2 ON g.gpid1 = g2.gpid1 " + "WHERE g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid "
			+ "AND g.gpid1 != 0 AND  g.deleted = 0  AND g.grplce = 0";

	public static final String GET_DERIVATIVE_CHILDREN =
		"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
			+ "WHERE g.gnpgs = -1 AND g.gpid2 = :gid and  g.deleted = 0  and g.grplce = 0";

	public static final String GET_MAINTENANCE_CHILDREN =
		"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
			+ "JOIN methods m ON g.methn = m.mid AND m.mtype = 'MAN' "
			+ "WHERE g.gnpgs = -1 AND g.gpid2 = :gid and  g.deleted = 0  and g.grplce = 0";

	public static final String GET_BY_NAME_ALL_MODES_USING_EQUAL =
		"SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE  g.deleted = 0  AND g.grplce = 0 AND "
			+ "( nval = :name OR nval = :noSpaceName OR nval = :standardizedName )";

	public static final String COUNT_BY_NAME_ALL_MODES_USING_EQUAL =
		"SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid and n.nstat != 9 WHERE  g.deleted = 0  AND g.grplce = 0 AND "
			+ "( nval = :name OR nval = :noSpaceName OR nval = :standardizedName )";

	public static final String GET_BY_NAME_ALL_MODES_USING_LIKE =
		"SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE  g.deleted = 0  AND g.grplce = 0 AND "
			+ "( nval LIKE :name OR nval LIKE :noSpaceName OR nval LIKE :standardizedName )";

	public static final String COUNT_BY_NAME_ALL_MODES_USING_LIKE =
		"SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid and n.nstat != 9 WHERE  g.deleted = 0  AND g.grplce = 0 AND "
			+ "( nval LIKE :name OR nval LIKE :noSpaceName OR nval LIKE :standardizedName )";

	public static final String GET_GERMPLASM_DATES_BY_GIDS = "SELECT gid, gdate " + "FROM germplsm " + "WHERE gid IN (:gids)";
	public static final String GET_METHOD_IDS_BY_GIDS = "SELECT gid, methn " + "FROM germplsm " + "WHERE gid IN (:gids)";
	public static final String GET_PARENT_NAMES_BY_STUDY_ID =
		"select n.gid, n.ntype, n.nval, n.nid, n.nstat  \n" +
			"from stock s\n" +
			"inner join germplsm g on g.gid = s.dbxref_id\n" +
			"inner join names n on (n.gid = g.gpid1 or n.gid = g.gpid2)\n" +
			"where s.project_id = :projId";

	public static final String GET_KNOWN_PARENT_GIDS_BY_STUDY_ID =
		"select g.gid, g.gpid1, g.gpid2, g.grplce \n" +
			"from stock s\n" +
			"inner join germplsm g on g.gid = s.dbxref_id and g.gnpgs > 0 AND (g.gpid1 > 0 or g.gpid2 > 0)   \n" +
			"where s.project_id = :projId";

	public static final String GET_PREFERRED_NAME_AND_PARENT_FOR_A_GID_LIST = "select g.gid as gid, ld.grpname as pedigree, "
		+ " (select n.nval from names n where n.nstat=1 and n.gid = g.gid limit 1) as nval" + "  from germplsm g "
		+ " inner join listdata ld on (g.gid = ld.gid) " + "where g.gid in (:gidList) " + "group by g.gid";

	public static final String GET_GERMPLASM_OFFSPRING_BY_GID =
		" SELECT DISTINCT \n" + "   g.gid, \n" + "   CONCAT_WS(',', \n" + "     if(g.gpid1 != 0, g.gpid1, NULL), \n"
			+ "     if(g.gpid2 != 0, g.gpid2, NULL), \n" + "     ifnull(p.pid, NULL)) AS parents \n" + " FROM germplsm g \n"
			+ "   LEFT JOIN progntrs p ON g.gid = p.gid \n" + "   LEFT JOIN listdata ld ON g.gid = ld.gid \n"
			+ "   LEFT JOIN listnms l ON ld.listid = l.listid \n"
			+ " WHERE (g.gpid1 in (:gids) OR g.gpid2 in (:gids) OR p.pid in (:gids)) \n" + "   AND g.deleted = 0 \n"
			+ "   AND g.grplce = 0 \n" + "   AND ( l.liststatus != " + GermplasmListDAO.STATUS_DELETED
			+ " OR l.liststatus IS NULL)";

	@Id
	@TableGenerator(name = "germplasmIdGenerator", table = "sequence", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
		pkColumnValue = "germplsm", allocationSize = 500)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "germplasmIdGenerator")
	@Basic(optional = false)
	@Column(name = "gid")
	@XmlElement(name = "gid")
	private Integer gid;

	@Basic(optional = false)
	@Column(name = "gnpgs")
	@XmlElement(name = "numberOfProgenitors")
	private Integer gnpgs;

	/**
	 * Usually female parent.
	 */
	@Basic(optional = false)
	@Column(name = "gpid1")
	@XmlElement(name = "firstParent")
	private Integer gpid1;

	/**
	 * Usually male parent.
	 */
	@Basic(optional = false)
	@Column(name = "gpid2")
	@XmlElement(name = "secondParent")
	private Integer gpid2;

	@Basic(optional = false)
	@Column(name = "glocn")
	private Integer locationId;

	@Basic(optional = false)
	@Column(name = "gdate")
	@XmlElement(name = "creationDate")
	private Integer gdate;

	@Basic(optional = false)
	@Column(name = "gref")
	private Integer referenceId;

	/**
	 * Records deletion or replacement for the current record. 0=unchanged, own GID=deleted, replacement GID=replaced
	 */
	@Basic(optional = false)
	@Column(name = "grplce")
	private Integer grplce;

	/*
	 * If the current germplasm is a managed sample then MGID contains the GID of the germplasm at the root of the management tree, else 0.
	 * This the GROUP_ID of the germplasm.
	 */
	@Basic(optional = false)
	@Column(name = "mgid")
	private Integer mgid;

	@OneToMany(mappedBy = "germplasm", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Name> names = new ArrayList<>();

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "deleted", columnDefinition = "TINYINT")
	private Boolean deleted;

	@Column(name = "germplsm_uuid")
	private String germplasmUUID;

	@OneToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "gref", insertable = false, updatable = false)
	private Bibref bibref;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "methn")
	private Method method;

	@OneToMany(mappedBy = "germplasm", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<GermplasmExternalReference> externalReferences = new ArrayList<>();

	@Basic(optional = false)
	@XmlElement(name = "femaleParent")
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gpid1", insertable = false, updatable = false)
	private Germplasm femaleParent;

	@Basic(optional = false)
	@XmlElement(name = "maleParent")
	@OneToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "gpid2", insertable = false, updatable = false)
	private Germplasm maleParent;

	@OneToMany(mappedBy = "germplasm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Progenitor> otherProgenitors = new ArrayList<>();

	/**
	 * This variable is populated only when the Germplasm POJO is retrieved by using GermplasmDataManager.getGermplasmWithPrefName() and
	 * GermplasmDataManager.getGermplasmWithPrefAbbrev(). Otherwise it is null always.
	 */
	@Transient
	private Name preferredName = null;

	/**
	 * This variable is populated only when the Germplasm POJO is retrieved by using GermplasmDataManager.getGermplasmWithPrefAbbrev().
	 * Otherwise it is null always.
	 */
	@Transient
	private String preferredAbbreviation = null;

	/**
	 * This variable is populated only when the Germplasm POJO is retrieved by using GermplasmDataManager.searchForGermplasm(). Otherwise it
	 * is null always.
	 */
	@Transient
	private GermplasmInventory inventoryInfo;

	/**
	 * This variable is populated only when the Germplasm POJO is retrieved by using GermplasmDataManager.getDirectParentsForStudy().
	 * Otherwise it is null always.
	 */
	@Transient
	private String selectionHistory = null;

	/**
	 * This variable is populated only when the Germplasm POJO is retrieved by using GermplasmDataManager.getDirectParentsForStudy().
	 * Otherwise it is null always.
	 */
	@Transient
	private String crossName = null;

	/**
	 * This variable is populated only when the Germplasm POJO is retrieved by using GermplasmDataManager.getDirectParentsForStudy().
	 * Otherwise it is null always.
	 */
	@Transient
	private String accessionName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 * Previously, germplasm list is loaded and revisit the DB for each germplasm for getting location name.
	 * This problem is removed by introducing this variable.
	 */
	@Transient
	private String locationName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String germplasmPreferredName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String germplasmPreferredId = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String femaleParentPreferredName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String femaleParentPreferredID = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String maleParentPreferredName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String maleParentPreferredID = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String germplasmNamesString = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String germplasmDate = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private Map<String, String> attributeTypesValueMap = new HashMap<>();

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private Map<String, String> nameTypesValueMap = new HashMap<>();

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String groupSourcePreferredName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String groupSourceGID = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String immediateSourcePreferredName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 */
	@Transient
	private String immediateSourceGID = null;

	/**
	 * Don't use it. This constructor is required by hibernate.
	 */
	public Germplasm() {
		this.deleted = false;
	}

	public Germplasm(final Integer gid, final Integer gnpgs, final Integer gpid1, final Integer gpid2,
		final Integer locationId, final Integer gdate, final Integer referenceId,
		final Integer grplce, final Integer mgid, final Name preferredName, final String preferredAbbreviation, final Method method) {
		this(gid);
		this.gid = gid;
		this.gnpgs = gnpgs;
		this.gpid1 = gpid1;
		this.gpid2 = gpid2;
		this.locationId = locationId;
		this.gdate = gdate;
		this.referenceId = referenceId;
		this.grplce = grplce;
		this.mgid = mgid;
		this.preferredName = preferredName;
		this.preferredAbbreviation = preferredAbbreviation;
		this.method = method;
		this.deleted = false;
	}

	//TODO: cleanup - remove it.
	@Deprecated
	public Germplasm(final Integer gid) {
		this.gid = gid;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	/**
	 * Represents the type of genesis and number of progenitors.
	 *
	 * <ul>
	 * <li>For a derivative process GNPGS = -1 and then GPID1 contains the germplasm groupID and GPID2 the source germplasm ID.</li>
	 *
	 * <li>For a generative process GNPGS containsthe number of specified parents. (The number of parents required by a method isrecorded by
	 * NPRGN on the METHODS TABLE). If GNPGS = 1 or 2 then the IDs ofthe progenitors are contained in the GPID1 and GPID2 fields on the
	 * GERMPLSM table. If GNPGS>2 then further IDs are stored on the PROGNTRS table.</li>
	 *
	 * <li>GNPGS = 0 for <a href="https://en.wikipedia.org/wiki/Landrace">landrace</a> or wild species collections or if none of the parents
	 * is known.GNPGS <= NPRGN, but some of the GNPGS specified parents may be unknown inwhich case the corresponding GPIDs are MISSING (0).
	 * For example in a simplecross with only male parent known, GNPGS would have to be 2 with GPID1 = 0 and GPID2 set to GID of the known
	 * male parent.</li>
	 * </ul>
	 */
	public Integer getGnpgs() {
		return this.gnpgs;
	}

	public void setGnpgs(final Integer gnpgs) {
		this.gnpgs = gnpgs;
	}

	public Integer getGpid1() {
		return this.gpid1;
	}

	public void setGpid1(final Integer gpid1) {
		this.gpid1 = gpid1;
	}

	public Integer getGpid2() {
		return this.gpid2;
	}

	public void setGpid2(final Integer gpid2) {
		this.gpid2 = gpid2;
	}

	public Integer getGdate() {
		return this.gdate;
	}

	public void setGdate(final Integer gdate) {
		this.gdate = gdate;
	}

	public Integer getGrplce() {
		return this.grplce;
	}

	public void setGrplce(final Integer grplce) {
		this.grplce = grplce;
	}

	public Integer getMgid() {
		return this.mgid;
	}

	public void setMgid(final Integer mgid) {
		this.mgid = mgid;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public Integer getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(final Integer referenceId) {
		this.referenceId = referenceId;
	}

	public Name getPreferredName() {
		if (this.preferredName == null) {
			for (final Name name : this.getNames()) {
				if (name.getNstat() != null && name.getNstat().equals(new Integer(1))) {
					this.preferredName = name;
				}
			}
		}
		return this.preferredName;
	}

	public void setPreferredName(final Name preferredName) {
		this.preferredName = preferredName;
	}

	public String getPreferredAbbreviation() {
		return this.preferredAbbreviation;
	}

	public void setPreferredAbbreviation(final String preferredAbbreviation) {
		this.preferredAbbreviation = preferredAbbreviation;
	}

	public void setMethod(final Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return this.method;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public List<GermplasmExternalReference> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<GermplasmExternalReference> externalReferences) {
		this.externalReferences = externalReferences;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Germplasm)) {
			return false;
		}

		final Germplasm rhs = (Germplasm) obj;
		return new EqualsBuilder().append(this.gid, rhs.gid).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.gid).toHashCode();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Germplasm [gid=");
		builder.append(this.gid);
		builder.append(", gnpgs=");
		builder.append(this.gnpgs);
		builder.append(", gpid1=");
		builder.append(this.gpid1);
		builder.append(", gpid2=");
		builder.append(this.gpid2);
		builder.append(", createdBy=");
		builder.append(super.getCreatedBy());
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", gdate=");
		builder.append(this.gdate);
		builder.append(", referenceId=");
		builder.append(this.referenceId);
		builder.append(", grplce=");
		builder.append(this.grplce);
		builder.append(", mgid=");
		builder.append(this.mgid);
		builder.append(", preferredName=");
		builder.append(this.preferredName);
		builder.append(", preferredAbbreviation=");
		builder.append(this.preferredAbbreviation);
		builder.append(", method=");
		builder.append(this.method);
		builder.append(", inventoryInfo=");
		builder.append(this.inventoryInfo);
		builder.append(", locationName=");
		builder.append(this.locationName);
		builder.append("]");
		return builder.toString();
	}

	public GermplasmInventory getInventoryInfo() {
		return this.inventoryInfo;
	}

	public void setInventoryInfo(final GermplasmInventory inventoryInfo) {
		this.inventoryInfo = inventoryInfo;
	}

	public String getSelectionHistory() {
		return this.selectionHistory;
	}

	public void setSelectionHistory(final String selectionHistory) {
		this.selectionHistory = selectionHistory;
	}

	public String getCrossName() {
		return this.crossName;
	}

	public void setCrossName(final String crossName) {
		this.crossName = crossName;
	}

	public String getAccessionName() {
		return this.accessionName;
	}

	public void setAccessionName(final String accessionName) {
		this.accessionName = accessionName;
	}

	/**
	 * @return <strong>ALL</strong> name records associated with this germplasm entity.
	 */
	public List<Name> getNames() {
		return this.names;
	}

	public void setNames(final List<Name> names) {
		this.names = names;
	}

	public Name findPreferredName() {
		Name foundPreferredName = null;
		for (final Name name : this.getNames()) {
			if (new Integer(1).equals(name.getNstat())) {
				foundPreferredName = name;
				break;
			}
		}
		return foundPreferredName;
	}

	public Boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(final Boolean deleted) {
		this.deleted = deleted;
	}

	public String getGermplasmPreferredName() {
		return this.germplasmPreferredName;
	}

	public void setGermplasmPreferredName(final String germplasmPreferredName) {
		this.germplasmPreferredName = germplasmPreferredName;
	}

	public String getFemaleParentPreferredName() {
		return this.femaleParentPreferredName;
	}

	public void setFemaleParentPreferredName(final String femaleParentPreferredName) {
		this.femaleParentPreferredName = femaleParentPreferredName;
	}

	public String getFemaleParentPreferredID() {
		return this.femaleParentPreferredID;
	}

	public void setFemaleParentPreferredID(final String femaleParentPreferredID) {
		this.femaleParentPreferredID = femaleParentPreferredID;
	}

	public String getMaleParentPreferredName() {
		return this.maleParentPreferredName;
	}

	public void setMaleParentPreferredName(final String maleParentPreferredName) {
		this.maleParentPreferredName = maleParentPreferredName;
	}

	public String getMaleParentPreferredID() {
		return this.maleParentPreferredID;
	}

	public void setMaleParentPreferredID(final String maleParentPreferredID) {
		this.maleParentPreferredID = maleParentPreferredID;
	}

	public String getGermplasmPreferredId() {
		return this.germplasmPreferredId;
	}

	public void setGermplasmPreferredId(final String germplasmPreferredId) {
		this.germplasmPreferredId = germplasmPreferredId;
	}

	public String getGermplasmNamesString() {
		return this.germplasmNamesString;
	}

	public void setGermplasmNamesString(final String germplasmNamesString) {
		this.germplasmNamesString = germplasmNamesString;
	}

	public String getGermplasmDate() {
		return this.germplasmDate;
	}

	public void setGermplasmDate(final String germplasmDate) {
		this.germplasmDate = germplasmDate;
	}

	public Map<String, String> getAttributeTypesValueMap() {
		return ImmutableMap.copyOf(this.attributeTypesValueMap);
	}

	public void setAttributeTypesValueMap(final Map<String, String> attributeTypesValueMap) {
		if (attributeTypesValueMap == null) {
			throw new NullArgumentException("attributeTypesValueMap must not be null");
		}
		this.attributeTypesValueMap = attributeTypesValueMap;
	}

	public Map<String, String> getNameTypesValueMap() {
		return ImmutableMap.copyOf(this.nameTypesValueMap);
	}

	public void setNameTypesValueMap(final Map<String, String> nameTypesValueMap) {
		if (this.attributeTypesValueMap == null) {
			throw new NullArgumentException("nameTypesValueMap must not be null");
		}
		this.nameTypesValueMap = nameTypesValueMap;
	}

	public String getGroupSourcePreferredName() {
		return this.groupSourcePreferredName;
	}

	public void setGroupSourcePreferredName(final String groupSourcePreferredName) {
		this.groupSourcePreferredName = groupSourcePreferredName;
	}

	public String getGroupSourceGID() {
		return this.groupSourceGID;
	}

	public void setGroupSourceGID(final String groupSourceGID) {
		this.groupSourceGID = groupSourceGID;
	}

	public String getImmediateSourcePreferredName() {
		return this.immediateSourcePreferredName;
	}

	public void setImmediateSourcePreferredName(final String immediateSourcePreferredName) {
		this.immediateSourcePreferredName = immediateSourcePreferredName;
	}

	public String getImmediateSourceGID() {
		return this.immediateSourceGID;
	}

	public void setImmediateSourceGID(final String immediateSourceGID) {
		this.immediateSourceGID = immediateSourceGID;
	}

	public String getGermplasmUUID() {
		return this.germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public Bibref getBibref() {
		return this.bibref;
	}

	public void setBibref(final Bibref bibref) {
		this.bibref = bibref;
	}

	public List<Progenitor> getOtherProgenitors() {
		return this.otherProgenitors;
	}

	public void setOtherProgenitors(final List<Progenitor> otherProgenitors) {
		this.otherProgenitors = otherProgenitors;
	}

	public Germplasm getFemaleParent() {
		return this.femaleParent;
	}

	public void setFemaleParent(final Germplasm femaleParent) {
		this.femaleParent = femaleParent;
	}

	public Germplasm getMaleParent() {
		return this.maleParent;
	}

	public void setMaleParent(final Germplasm maleParent) {
		this.maleParent = maleParent;
	}

	/**
	 * @param gids
	 * @return True if all gids are equals to the ones in otherProgenitors list in any order
	 */
	public boolean otherProgenitorsGidsEquals(final List<Integer> gids) {
		final List<Integer> sortedExistingGids =
			this.otherProgenitors.stream().map(Progenitor::getProgenitorGid).collect(Collectors.toList());
		Collections.sort(sortedExistingGids);

		if (sortedExistingGids.isEmpty() && gids == null) {
			return true;
		}

		if ((sortedExistingGids == null && gids != null)
			|| sortedExistingGids != null && gids == null
			|| sortedExistingGids.size() != gids.size()) {
			return false;
		}

		final List<Integer> sortedGids = new ArrayList<>(gids);
		Collections.sort(sortedGids);

		return sortedExistingGids.equals(sortedGids);
	}

	public boolean isTerminalAncestor() {
		return new Integer(0).equals(this.gpid1) && new Integer(0).equals(this.gpid2);
	}

	public Optional<Progenitor> findByProgNo(final Integer progNo) {
		return this.otherProgenitors.stream().filter(p -> progNo.equals(p.getProgenitorNumber())).findFirst();
	}

	@Override
	public Germplasm clone() {
		Germplasm germplasm;
		try {
			germplasm = (Germplasm) super.clone();
		} catch (final CloneNotSupportedException e) {
			germplasm =  new Germplasm(this.getGid(), this.gnpgs, this.gpid1, this.gpid2, this.locationId, this.gdate,
				this.referenceId, this.grplce, this.mgid, this.preferredName, this.preferredAbbreviation, this.method);
		}
		return germplasm;
	}
}
