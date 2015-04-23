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

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * POJO for germplsm table.
 * 
 * @author Kevin Manansala, Mark Agarrado, Dennis Billano
 */
@NamedQueries({ @NamedQuery(name = "getAllGermplasm", query = "FROM Germplasm"),
        @NamedQuery(name = "countAllGermplasm", query = "SELECT COUNT(g) FROM Germplasm g"),

		@NamedQuery(name = "getGermplasmByMethodNameUsingEqual",
                query = "SELECT g FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname = :name"),

		@NamedQuery(name = "countGermplasmByMethodNameUsingEqual",
                query = "SELECT COUNT(g) FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname = :name"),

		@NamedQuery(name = "getGermplasmByMethodNameUsingLike",
                query = "SELECT g FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname like :name"),

		@NamedQuery(name = "countGermplasmByMethodNameUsingLike",
                query = "SELECT COUNT(g) FROM Germplasm g, Method m WHERE g.methodId = m.mid AND m.mname like :name"),

		@NamedQuery(name = "getGermplasmByLocationNameUsingEqual",
                query = "SELECT g FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname = :name"),

		@NamedQuery(name = "countGermplasmByLocationNameUsingEqual",
                query = "SELECT COUNT(g) FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname = :name"),

		@NamedQuery(name = "getGermplasmByLocationNameUsingLike",
                query = "SELECT g FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname like :name"),

		@NamedQuery(name = "countGermplasmByLocationNameUsingLike",
                query = "SELECT COUNT(g) FROM Germplasm g, Location l WHERE g.locationId = l.locid AND l.lname like :name")

})
@NamedNativeQueries({
        @NamedNativeQuery(name = "getGermplasmDescendants",
                query = "SELECT DISTINCT g.* FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
                        + "WHERE (g.gpid1=:gid OR g.gpid2=:gid OR p.pid=:gid) "
                        + "AND g.gid != g.grplce and g.grplce = 0", resultClass = Germplasm.class),
        @NamedNativeQuery(name = "getGermplasmByPrefName", query = "SELECT g.* FROM germplsm g LEFT JOIN names n ON g.gid = n.gid "
                + "AND n.nstat = 1 " + "WHERE n.nval = :name", resultClass = Germplasm.class),
        @NamedNativeQuery(name = "getProgenitor1",
        		query = "SELECT p.* FROM germplsm g, germplsm p WHERE g.gid = :gid " 
        			+ "and g.gpid1 = p.gid and p.gid != p.grplce and p.grplce = 0", resultClass = Germplasm.class),
        @NamedNativeQuery(name = "getProgenitor2",
        		query = "SELECT p.* FROM germplsm g, germplsm p WHERE g.gid = :gid " 
       				 + "and g.gpid2 = p.gid and p.gid != p.grplce and p.grplce = 0", resultClass = Germplasm.class),
        @NamedNativeQuery(name = "getProgenitor",
        		query = "SELECT g.* FROM germplsm g, progntrs p WHERE g.gid = p.pid " 
          			 + "and p.gid = :gid and p.pno = :pno and g.gid != g.grplce and g.grplce = 0", resultClass = Germplasm.class)
})
@Entity
@Table(name = "germplsm")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "germplasm")
@XmlType(propOrder = { "gid", "gnpgs", "gpid1", "gpid2", "gdate" })

@XmlAccessorType(XmlAccessType.NONE)
public class Germplasm implements Serializable{

    private static final long serialVersionUID = 1L;

    // string contants for name of queries
    public static final String GET_ALL = "getAllGermplasm";
    public static final String COUNT_ALL = "countAllGermplasm";
    public static final String GET_BY_PREF_NAME = "getGermplasmByPrefName";
    public static final String COUNT_BY_PREF_NAME = 
            "SELECT COUNT(g.gid) " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
            "WHERE n.nval = :name";
    public static final String GET_BY_METHOD_NAME_USING_EQUAL = "getGermplasmByMethodNameUsingEqual";
    public static final String COUNT_BY_METHOD_NAME_USING_EQUAL = "countGermplasmByMethodNameUsingEqual";
    public static final String GET_BY_METHOD_NAME_USING_LIKE = "getGermplasmByMethodNameUsingLike";
    public static final String COUNT_BY_METHOD_NAME_USING_LIKE = "countGermplasmByMethodNameUsingLike";
    public static final String GET_BY_LOCATION_NAME_USING_EQUAL = "getGermplasmByLocationNameUsingEqual";
    public static final String COUNT_BY_LOCATION_NAME_USING_EQUAL = "countGermplasmByLocationNameUsingEqual";
    public static final String GET_BY_LOCATION_NAME_USING_LIKE = "getGermplasmByLocationNameUsingLike";
    public static final String COUNT_BY_LOCATION_NAME_USING_LIKE = "countGermplasmByLocationNameUsingLike";

    public static final String GET_BY_GID_WITH_PREF_NAME = 
            "SELECT {g.*}, {n.*} " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
            "WHERE g.gid = :gid";

    public static final String GET_BY_GID_WITH_PREF_ABBREV = 
            "SELECT {g.*}, {n.*}, {abbrev.*} " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
                            "LEFT JOIN names abbrev ON g.gid = abbrev.gid AND abbrev.nstat = 2 " +
            "WHERE g.gid = :gid";
    public static final String GET_DESCENDANTS = "getGermplasmDescendants";

    public static final String COUNT_DESCENDANTS = 
            "SELECT COUNT(DISTINCT g.gid) " +
            "FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid " +
            "WHERE (g.gpid1 = :gid OR g.gpid2 = :gid OR p.pid=:gid) " +
            "AND g.gid != g.grplce and g.grplce = 0";
    public static final String GET_PROGENITOR1 = "getProgenitor1";
    public static final String GET_PROGENITOR2 = "getProgenitor2";
    public static final String GET_PROGENITOR = "getProgenitor";

    public static final String GET_PROGENITORS_BY_GID_WITH_PREF_NAME = 
            "SELECT {g.*}, {n.*} " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
            "JOIN progntrs p ON p.pid = g.gid " +
            "WHERE p.gid = :gid and g.gid != g.grplce and g.grplce = 0";

    public static final String GET_MANAGEMENT_NEIGHBORS = 
            "SELECT {g.*}, {n.*} " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
            "WHERE g.mgid = :gid AND g.grplce != g.gid and g.grplce = 0 ORDER BY g.gid";

    public static final String COUNT_MANAGEMENT_NEIGHBORS = 
            "SELECT COUNT(g.gid) " +
            "FROM germplsm g " +
            "WHERE g.mgid = :gid AND g.grplce != g.gid and g.grplce = 0";
    public static final String GET_GROUP_RELATIVES = 
            "SELECT {g.*}, {n.*} " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
            "JOIN germplsm g2 ON g.gpid1 = g2.gpid1 " +
            "WHERE g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid " + 
            "AND g.gpid1 != 0 AND g.grplce != g.gid AND g.grplce = 0";
    public static final String COUNT_GROUP_RELATIVES = 
	        "SELECT COUNT(g.gid) " +
	        "FROM germplsm g " +
	        "JOIN germplsm g2 ON g.gpid1 = g2.gpid1 " +
	        "WHERE g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid " + 
	        "AND g.gpid1 != 0 AND g.grplce != g.gid AND g.grplce = 0";

    public static final String GET_DERIVATIVE_CHILDREN = 
            "SELECT {g.*}, {n.*} " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
            "WHERE g.gnpgs = -1 AND g.gpid2 = :gid and g.gid != g.grplce and g.grplce = 0";

    public static final String GET_MAINTENANCE_CHILDREN = 
            "SELECT {g.*}, {n.*} " +
            "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
            "JOIN methods m ON g.methn = m.mid AND m.mtype = 'MAN' " +
            "WHERE g.gnpgs = -1 AND g.gpid2 = :gid and g.gid != g.grplce and g.grplce = 0";

    public static final String GET_BY_NAME_USING_EQUAL =
            "SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE "
          + "nval = :name ";
    
    public static final String COUNT_BY_NAME_USING_EQUAL =
            "SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid WHERE "
            + "nval = :name ";       

    public static final String GET_BY_NAME_USING_LIKE =
            "SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE "
                    + "nval LIKE :name ";       
    
    public static final String COUNT_BY_NAME_USING_LIKE =
            "SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid WHERE g.gid!=g.grplce AND "
                    + "nval LIKE :name";       
    
    public static final String GET_BY_NAME_ALL_MODES_USING_EQUAL =
            "SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE g.gid!=g.grplce AND g.grplce = 0 AND "
          + "( nval = :name OR nval = :noSpaceName OR nval = :standardizedName )";
    
    public static final String COUNT_BY_NAME_ALL_MODES_USING_EQUAL =
            "SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid WHERE g.gid!=g.grplce AND g.grplce = 0 AND "
            + "( nval = :name OR nval = :noSpaceName OR nval = :standardizedName )";       

    public static final String GET_BY_NAME_ALL_MODES_USING_LIKE =
            "SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE g.gid!=g.grplce AND g.grplce = 0 AND "
                    + "( nval LIKE :name OR nval LIKE :noSpaceName OR nval LIKE :standardizedName )";       
    
    public static final String COUNT_BY_NAME_ALL_MODES_USING_LIKE =
            "SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid WHERE g.gid!=g.grplce AND g.grplce = 0 AND "
                    + "( nval LIKE :name OR nval LIKE :noSpaceName OR nval LIKE :standardizedName )";  
    
    public static final String GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX =
            "SELECT CONVERT(REPLACE(nval, :prefix, ''), SIGNED)+1 AS last_number " +
            "FROM names " +
            "WHERE nval REGEXP :prefixRegex " +
            "ORDER BY last_number DESC LIMIT 1";

    public static final String GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX2 =
    		"SELECT CONVERT(REPLACE(nval, :prefix, ''), SIGNED)+1 AS last_number  " +
	    	"FROM names " +
	    	"WHERE (SUBSTRING(nval, 1, :prefixLen) = :prefix " +
	    	"AND substring(nval, :prefixLen+1, LENGTH(nval)-:prefixLen) = concat( '', 0 + substring(nval, :prefixLen+1, LENGTH(nval)-:prefixLen))) " +
	    	"OR (SUBSTRING(nval, 1, :prefixLen+1) = :prefix + ' ' " + 
	    	"AND substring(nval, :prefixLen+2, LENGTH(nval)-:prefixLen+1) = concat( '', 0 + substring(nval, :prefixLen+2, LENGTH(nval)-:prefixLen+1))) " +
	    	"ORDER BY last_number DESC LIMIT 1";

    public static final String GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX3 =
    		"SELECT CONVERT(LTRIM(REPLACE(UPPER(nval), :prefix, '')), SIGNED)+1 AS next_number " +
    		"FROM names " +
    		"WHERE nval like :prefixLike " +
    		"ORDER BY next_number DESC LIMIT 1";    
    
    public static final String GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_WITH_SPACE =
        "SELECT CONVERT(REPLACE(nval, :prefix, ''), SIGNED)+1 AS last_number " +
        "FROM names " +
        "WHERE nval LIKE :prefixLike " +
        "ORDER BY last_number DESC LIMIT 1";
    
    public static final String GET_BY_GID_WITH_METHOD_TYPE = 
            "SELECT {g.*}, {m.*} " +
            "FROM germplsm g LEFT JOIN methods m ON g.methn = m.mid " +
            "WHERE g.gid = :gid AND g.grplce != g.gid AND g.grplce = 0";
    
    /**
     * Used in germplasm data manager searchForGermplasm
     */
    public static final String SEARCH_GERMPLASM_BY_GID = 
    		"SELECT germplsm.* " +
    		"FROM germplsm " +
    		"WHERE gid=:gid AND length(gid) = :gidLength AND gid!=grplce AND grplce = 0";
    public static final String SEARCH_GERMPLASM_BY_GID_LIKE = 
    		"SELECT germplsm.* " +
    		"FROM germplsm " +
    		"WHERE gid LIKE :gid AND gid!=grplce AND grplce = 0";
    public static final String SEARCH_GERMPLASM_BY_INVENTORY_ID =
    		"SELECT g.* FROM germplsm g, ims_lot l, ims_transaction t " +
    		"WHERE t.lotid = l.lotid AND l.etype = 'GERMPLSM' AND l.eid = g.gid " +
    		"AND g.grplce != g.gid AND g.grplce = 0 AND t.inventory_id = :inventoryID";
    public static final String SEARCH_GERMPLASM_BY_INVENTORY_ID_LIKE = 
    		"SELECT g.* FROM germplsm g, ims_lot l, ims_transaction t " +
    		"WHERE t.lotid = l.lotid AND l.etype = 'GERMPLSM' AND l.eid = g.gid " +
    		"AND g.grplce != g.gid AND g.grplce = 0 AND t.inventory_id LIKE :inventoryID";
    public static final String SEARCH_GERMPLASM_BY_GIDS = 
    		"SELECT germplsm.* " +
    		"FROM germplsm " +
    		"WHERE gid IN (:gids) AND gid!=grplce AND grplce = 0";
    public static final String SEARCH_GERMPLASM_BY_GERMPLASM_NAME = 
    		"SELECT DISTINCT g.* " +
    		"FROM names n, germplsm g " +
    		"WHERE n.gid = g.gid and g.gid != g.grplce and g.grplce = 0 " +
    		"AND n.nstat != :deletedStatus AND (n.nval LIKE :q OR n.nval LIKE :qStandardized OR n.nval LIKE :qNoSpaces) " +
    		"LIMIT 5000";
    public static final String SEARCH_GERMPLASM_BY_GERMPLASM_NAME_EQUAL = 
    		"SELECT DISTINCT g.* " +
    		"FROM names n, germplsm g " +
    		"WHERE n.gid = g.gid and g.gid != g.grplce and g.grplce = 0 " +
    		"AND n.nstat != :deletedStatus AND (n.nval = :q OR n.nval = :qStandardized OR n.nval = :qNoSpaces) " +
    		"LIMIT 5000";
    public static final String SEARCH_LIST_ID_BY_LIST_NAME =
    		"SELECT listid " +
    		"FROM ( " +
    		"    SELECT listnms.*, " +
            "        (MATCH(listname) AGAINST(:q)) AS searchScore " +
            "    FROM listnms " +
            "    WHERE liststatus!=:deletedStatus " +
            "    GROUP BY listid  " +
            "    HAVING searchScore>0 " +
            ") AS searchResults " +
            "ORDER BY searchScore DESC ";
    public static final String SEARCH_LIST_ID_BY_LIST_NAME_EQUAL =
    		"SELECT listid " +
    		"FROM ( " +
    		"    SELECT listnms.*, " +
            "        (MATCH(listname) AGAINST(:q)) AS searchScore " +
            "    FROM listnms " +
            "    WHERE liststatus!=:deletedStatus " +
            "        AND listname=:q " +
            "    GROUP BY listid  " +
            "    HAVING searchScore>0 " +
            ") AS searchResults " +
            "ORDER BY searchScore DESC ";
    public static final String SEARCH_GERMPLASM_BY_LIST_ID = 
    		"SELECT germplsm.* " +
    		"FROM listdata " +
    		"	LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) " +
        	"WHERE listid IN (:listids) ";
    public static final String GET_GERMPLASM_DATES_BY_GIDS =
		"SELECT gid, gdate " +
		"FROM germplsm " +
		"WHERE gid IN (:gids)";
    public static final String GET_METHOD_IDS_BY_GIDS =
    		"SELECT gid, methn " +
    		"FROM germplsm " +
    		"WHERE gid IN (:gids)";
    
    		
    		

    
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

    @Basic(optional = false)
    @Column(name = "germuid")
    private Integer userId;

    @Basic(optional = false)
    @Column(name = "lgid")
    private Integer lgid;

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

    /** Records deletion or replacement for the current record. 0=unchanged, own GID=deleted, replacement GID=replaced */
    @Basic(optional = false)
    @Column(name = "grplce")
    private Integer grplce;

    /*If the current germplasm is a managed sample then MGID contains the GID of the germplasm 
     * at the root of the management tree, else 0. */
    @Basic(optional = false)
    @Column(name = "mgid")
    private Integer mgid;

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
    
    /**
     * This variable is populated only when the Germplasm POJO is retrieved by
     * using GermplasmDataManager.getGermplasmWithMethodType(). Otherwise it is null
     * always.
     */
    @Transient
    private Method method = null;

    public Germplasm() {
    }

    public Germplasm(Integer gid, Integer methodId, Integer gnpgs, Integer gpid1, Integer gpid2, Integer userId,
            Integer lgid, Integer locationId, Integer gdate, Integer referenceId, Integer grplce, Integer mgid,
            Name preferredName, String preferredAbbreviation, Method method) {
        super();
        this.gid = gid;
        this.methodId = methodId;
        this.gnpgs = gnpgs;
        this.gpid1 = gpid1;
        this.gpid2 = gpid2;
        this.userId = userId;
        this.lgid = lgid;
        this.locationId = locationId;
        this.gdate = gdate;
        this.referenceId = referenceId;
        this.grplce = grplce;
        this.mgid = mgid;
        this.preferredName = preferredName;
        this.preferredAbbreviation = preferredAbbreviation;
        this.method = method;
    }
    
    public Germplasm(Integer gid, Integer methodId, Integer gnpgs, Integer gpid1, Integer gpid2, Integer userId,
            Integer lgid, Integer locationId, Integer gdate, Name preferredName) {
        
        // gref =0, grplce = 0, mgid = 0
        this(gid, methodId, gnpgs, gpid1, gpid2, userId, lgid, locationId, gdate
                , 0, 0, 0, preferredName, null, null);
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

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }


    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }


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
    
    public void setMethod(Method method) {
    	this.method = method;
    }
    
    public Method getMethod() {
    	return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Germplasm)) {
            return false;
        }

        Germplasm rhs = (Germplasm) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(gid, rhs.gid).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(gid).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Germplasm [gid=");
        builder.append(gid);
        builder.append(", methodId=");
        builder.append(methodId);
        builder.append(", gnpgs=");
        builder.append(gnpgs);
        builder.append(", gpid1=");
        builder.append(gpid1);
        builder.append(", gpid2=");
        builder.append(gpid2);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", lgid=");
        builder.append(lgid);
        builder.append(", locationId=");
        builder.append(locationId);
        builder.append(", gdate=");
        builder.append(gdate);
        builder.append(", referenceId=");
        builder.append(referenceId);
        builder.append(", grplce=");
        builder.append(grplce);
        builder.append(", mgid=");
        builder.append(mgid);
        builder.append(", preferredName=");
        builder.append(preferredName);
        builder.append(", preferredAbbreviation=");
        builder.append(preferredAbbreviation);
        builder.append(", method=");
        builder.append(method);
        builder.append("]");
        return builder.toString();
    }
    
    

}
