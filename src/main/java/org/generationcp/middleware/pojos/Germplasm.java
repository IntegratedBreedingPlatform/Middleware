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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.auditory.Auditable;
import org.generationcp.middleware.auditory.Auditory;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * POJO for germplsm table.
 *
 * @author Kevin Manansala, Mark Agarrado, Dennis Billano
 */
@NamedQueries({@NamedQuery(name = "getAllGermplasm", query = "FROM Germplasm"),
		@NamedQuery(name = "countAllGermplasm", query = "SELECT COUNT(g) FROM Germplasm g"),

		@NamedQuery(name = "countMatchGermplasmInList", query = "SELECT COUNT(g) FROM Germplasm g WHERE g.gid IN (:gids)"),

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
						+ "WHERE (g.gpid1=:gid OR g.gpid2=:gid OR p.pid=:gid) " + "AND g.gid != g.grplce and g.grplce = 0",
				resultClass = Germplasm.class), //

		@NamedNativeQuery(name = "getGermplasmByPrefName",
				query = "SELECT g.* FROM germplsm g LEFT JOIN names n ON g.gid = n.gid " + "AND n.nstat = 1 " + "WHERE n.nval = :name",
				resultClass = Germplasm.class), //

		@NamedNativeQuery(name = "getProgenitor1",
				query = "SELECT p.* FROM germplsm g, germplsm p WHERE g.gid = :gid "
						+ "and g.gpid1 = p.gid and p.gid != p.grplce and p.grplce = 0",
				resultClass = Germplasm.class), //

		@NamedNativeQuery(name = "getProgenitor2",
				query = "SELECT p.* FROM germplsm g, germplsm p WHERE g.gid = :gid "
						+ "and g.gpid2 = p.gid and p.gid != p.grplce and p.grplce = 0",
				resultClass = Germplasm.class), //

		@NamedNativeQuery(name = "getProgenitor", query = "SELECT g.* FROM germplsm g, progntrs p WHERE g.gid = p.pid "
				+ "and p.gid = :gid and p.pno = :pno and g.gid != g.grplce and g.grplce = 0", resultClass = Germplasm.class)} //
)
@Entity
@Table(name = "germplsm")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "germplasm")
@XmlType(propOrder = {"gid", "gnpgs", "gpid1", "gpid2", "gdate"})
@XmlAccessorType(XmlAccessType.NONE)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "germplsm")
public class Germplasm implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;

	// string contants for name of queries
	public static final String GET_ALL = "getAllGermplasm";
	public static final String COUNT_ALL = "countAllGermplasm";
	public static final String COUNT_MATCH_GERMPLASM_IN_LIST = "countMatchGermplasmInList";
	public static final String GET_BY_PREF_NAME = "getGermplasmByPrefName";
	public static final String COUNT_BY_PREF_NAME =
			"SELECT COUNT(g.gid) " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " + "WHERE n.nval = :name";
	public static final String GET_BY_METHOD_NAME_USING_EQUAL = "getGermplasmByMethodNameUsingEqual";
	public static final String COUNT_BY_METHOD_NAME_USING_EQUAL = "countGermplasmByMethodNameUsingEqual";
	public static final String GET_BY_METHOD_NAME_USING_LIKE = "getGermplasmByMethodNameUsingLike";
	public static final String COUNT_BY_METHOD_NAME_USING_LIKE = "countGermplasmByMethodNameUsingLike";
	public static final String GET_BY_LOCATION_NAME_USING_EQUAL = "getGermplasmByLocationNameUsingEqual";
	public static final String COUNT_BY_LOCATION_NAME_USING_EQUAL = "countGermplasmByLocationNameUsingEqual";
	public static final String GET_BY_LOCATION_NAME_USING_LIKE = "getGermplasmByLocationNameUsingLike";
	public static final String COUNT_BY_LOCATION_NAME_USING_LIKE = "countGermplasmByLocationNameUsingLike";

	public static final String GET_BY_GID_WITH_PREF_NAME =
			"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " + "WHERE g.gid = :gid";

	public static final String GET_BY_GID_WITH_PREF_ABBREV =
			"SELECT {g.*}, {n.*}, {abbrev.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
					+ "LEFT JOIN names abbrev ON g.gid = abbrev.gid AND abbrev.nstat = 2 " + "WHERE g.gid = :gid";
	public static final String GET_DESCENDANTS = "getGermplasmDescendants";

	public static final String COUNT_DESCENDANTS =
			"SELECT COUNT(DISTINCT g.gid) " + "FROM germplsm g LEFT JOIN progntrs p ON g.gid = p.gid "
					+ "WHERE (g.gpid1 = :gid OR g.gpid2 = :gid OR p.pid=:gid) " + "AND g.gid != g.grplce and g.grplce = 0";
	public static final String GET_PROGENITOR1 = "getProgenitor1";
	public static final String GET_PROGENITOR2 = "getProgenitor2";
	public static final String GET_PROGENITOR = "getProgenitor";

	public static final String GET_PROGENITORS_BY_GID_WITH_PREF_NAME =
			"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
					+ "JOIN progntrs p ON p.pid = g.gid " + "WHERE p.gid = :gid and g.gid != g.grplce and g.grplce = 0";

	public static final String GET_MANAGEMENT_NEIGHBORS =
			"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
					+ "WHERE g.mgid = :gid AND g.grplce != g.gid and g.grplce = 0 ORDER BY g.gid";

	public static final String COUNT_MANAGEMENT_NEIGHBORS =
			"SELECT COUNT(g.gid) " + "FROM germplsm g " + "WHERE g.mgid = :gid AND g.grplce != g.gid and g.grplce = 0";
	public static final String GET_GROUP_RELATIVES = "SELECT {g.*}, {n.*} "
			+ "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " + "JOIN germplsm g2 ON g.gpid1 = g2.gpid1 "
			+ "WHERE g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid " + "AND g.gpid1 != 0 AND g.grplce != g.gid AND g.grplce = 0";
	public static final String COUNT_GROUP_RELATIVES =
			"SELECT COUNT(g.gid) " + "FROM germplsm g " + "JOIN germplsm g2 ON g.gpid1 = g2.gpid1 "
					+ "WHERE g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid " + "AND g.gpid1 != 0 AND g.grplce != g.gid AND g.grplce = 0";

	public static final String GET_DERIVATIVE_CHILDREN =
			"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
					+ "WHERE g.gnpgs = -1 AND g.gpid2 = :gid and g.gid != g.grplce and g.grplce = 0";

	public static final String GET_MAINTENANCE_CHILDREN =
			"SELECT {g.*}, {n.*} " + "FROM germplsm g LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 "
					+ "JOIN methods m ON g.methn = m.mid AND m.mtype = 'MAN' "
					+ "WHERE g.gnpgs = -1 AND g.gpid2 = :gid and g.gid != g.grplce and g.grplce = 0";

	public static final String GET_BY_NAME_USING_EQUAL =
			"SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE " + "nval = :name ";

	public static final String COUNT_BY_NAME_USING_EQUAL =
			"SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid WHERE " + "nval = :name ";

	public static final String GET_BY_NAME_USING_LIKE =
			"SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE " + "nval LIKE :name ";

	public static final String COUNT_BY_NAME_USING_LIKE =
			"SELECT COUNT(DISTINCT g.gid) FROM germplsm g JOIN names n ON g.gid = n.gid WHERE g.gid!=g.grplce AND " + "nval LIKE :name";

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
			"SELECT CONVERT(REPLACE(nval, :prefix, ''), SIGNED)+1 AS last_number " + "FROM names " + "WHERE nval REGEXP :prefixRegex "
					+ "ORDER BY last_number DESC LIMIT 1";

	public static final String GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX2 =
			"SELECT CONVERT(REPLACE(nval, :prefix, ''), SIGNED)+1 AS last_number  " + "FROM names "
					+ "WHERE (SUBSTRING(nval, 1, :prefixLen) = :prefix "
					+ "AND substring(nval, :prefixLen+1, LENGTH(nval)-:prefixLen) = concat( '', 0 + substring(nval, :prefixLen+1, LENGTH(nval)-:prefixLen))) "
					+ "OR (SUBSTRING(nval, 1, :prefixLen+1) = :prefix + ' ' "
					+ "AND substring(nval, :prefixLen+2, LENGTH(nval)-:prefixLen+1) = concat( '', 0 + substring(nval, :prefixLen+2, LENGTH(nval)-:prefixLen+1))) "
					+ "ORDER BY last_number DESC LIMIT 1";

	public static final String GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX3 =
			"SELECT CONVERT(LTRIM(REPLACE(UPPER(nval), :prefix, '')), SIGNED)+1 AS next_number " + "FROM names "
					+ "WHERE nval like :prefixLike " + "ORDER BY next_number DESC LIMIT 1";

	public static final String GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_WITH_SPACE =
			"SELECT CONVERT(REPLACE(nval, :prefix, ''), SIGNED)+1 AS last_number " + "FROM names " + "WHERE nval LIKE :prefixLike "
					+ "ORDER BY last_number DESC LIMIT 1";

	public static final String GET_BY_GID_WITH_METHOD_TYPE = "SELECT {g.*}, {m.*} "
			+ "FROM germplsm g LEFT JOIN methods m ON g.methn = m.mid " + "WHERE g.gid = :gid AND g.grplce != g.gid AND g.grplce = 0";

	/**
	 * Used in germplasm data manager searchForGermplasm
	 */
	public static final String GENERAL_SELECT_FROM = "SELECT * FROM ";
	public static final String GERMPLASM_ALIAS = "AS germplasm ";
	public static final String INVENTORY_ALIAS = "AS inventory ";
	public static final String JOIN_ON_GERMPLASM_AND_INVENTORY = "ON germplasm.gid = inventory.entity_id ";
	public static final String SEARCH_GERMPLASM_WITH_INVENTORY =
			"SELECT entity_id, CAST(SUM(CASE WHEN avail_bal = 0 THEN 0 ELSE 1 END) AS UNSIGNED) as availInv, Count(DISTINCT lotid) as seedRes "
					+ "FROM ( SELECT i.lotid, i.eid AS entity_id, " + "SUM(trnqty) AS avail_bal " + "FROM ims_lot i "
					+ "LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
					+ "WHERE i.status = 0 AND i.etype = 'GERMPLSM' " + "GROUP BY i.lotid " + "HAVING avail_bal > -1) inv "
					+ "GROUP BY entity_id";
	public static final String WHERE_WITH_INVENTORY = "WHERE availInv > 0 ";
	public static final String SEARCH_GERMPLASM_BY_GID = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs " + "FROM germplsm g "
			+ "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' " + "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid "
			+ "WHERE g.gid=:gid AND length(g.gid) = :gidLength AND g.gid!=g.grplce AND g.grplce = 0 " + "GROUP BY g.gid" + ") "
			+ Germplasm.GERMPLASM_ALIAS + "LEFT JOIN (" + Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")" + Germplasm.INVENTORY_ALIAS
			+ Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;
	public static final String SEARCH_GERMPLASM_BY_GID_LIKE = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs " + "FROM germplsm g "
			+ "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' " + "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid "
			+ "WHERE g.gid LIKE :gid AND g.gid!=g.grplce AND g.grplce = 0 " + "GROUP BY g.gid" + ") " + Germplasm.GERMPLASM_ALIAS
			+ "LEFT JOIN (" + Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")" + Germplasm.INVENTORY_ALIAS
			+ Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;
	public static final String SEARCH_GERMPLASM_BY_INVENTORY_ID = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs " + "FROM germplsm g "
			+ "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' " + "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid "
			+ ", ims_lot l, ims_transaction t " + "WHERE t.lotid = l.lotid AND l.etype = 'GERMPLSM' AND l.eid = g.gid "
			+ "AND g.grplce != g.gid AND g.grplce = 0 AND t.inventory_id = :inventoryID " + "GROUP BY g.gid" + ") "
			+ Germplasm.GERMPLASM_ALIAS + "LEFT JOIN (" + Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")" + Germplasm.INVENTORY_ALIAS
			+ Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;
	public static final String SEARCH_GERMPLASM_BY_INVENTORY_ID_LIKE = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs " + "FROM germplsm g "
			+ "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' " + "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid "
			+ ", ims_lot l, ims_transaction t " + "WHERE t.lotid = l.lotid AND l.etype = 'GERMPLSM' AND l.eid = g.gid "
			+ "AND g.grplce != g.gid AND g.grplce = 0 AND t.inventory_id LIKE :inventoryID " + "GROUP BY g.gid" + ") "
			+ Germplasm.GERMPLASM_ALIAS + "LEFT JOIN (" + Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")" + Germplasm.INVENTORY_ALIAS
			+ Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;
	public static final String SEARCH_GERMPLASM_BY_GIDS = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs " + "FROM germplsm g "
			+ "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' " + "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid "
			+ "WHERE g.gid IN (:gids) AND g.gid!=g.grplce AND g.grplce = 0 " + "GROUP BY g.gid" + ") " + Germplasm.GERMPLASM_ALIAS
			+ "LEFT JOIN (" + Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")" + Germplasm.INVENTORY_ALIAS
			+ Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;
	public static final String SEARCH_GERMPLASM_BY_GERMPLASM_NAME_LIKE = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT DISTINCT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs "
			+ "FROM names n, germplsm g " + "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' "
			+ "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid " + "WHERE n.gid = g.gid and g.gid != g.grplce and g.grplce = 0 "
			+ "AND n.nstat != :deletedStatus AND (n.nval LIKE :q OR n.nval LIKE :qStandardized OR n.nval LIKE :qNoSpaces) "
			+ "GROUP BY g.gid " + "LIMIT 5000" + ") " + Germplasm.GERMPLASM_ALIAS + "LEFT JOIN ("
			+ Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")" + Germplasm.INVENTORY_ALIAS + Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;
	public static final String SEARCH_GERMPLASM_BY_GERMPLASM_NAME = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT DISTINCT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs "
			+ "FROM names n, germplsm g " + "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' "
			+ "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid " + "WHERE n.gid = g.gid and g.gid != g.grplce and g.grplce = 0 "
			+ "AND n.nstat != :deletedStatus AND (n.nval = :q OR n.nval = :qStandardized OR n.nval = :qNoSpaces) " + "GROUP BY g.gid "
			+ "LIMIT 5000" + ") " + Germplasm.GERMPLASM_ALIAS + "LEFT JOIN (" + Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")"
			+ Germplasm.INVENTORY_ALIAS + Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;
	public static final String SEARCH_LIST_ID_BY_LIST_NAME = "SELECT listid " + "FROM ( " + "    SELECT listnms.*, "
			+ "        (MATCH(listname) AGAINST(:q)) AS searchScore " + "    FROM listnms " + "    WHERE liststatus!=:deletedStatus "
			+ "    GROUP BY listid  " + "    HAVING searchScore>0 " + ") AS searchResults " + "ORDER BY searchScore DESC ";
	public static final String SEARCH_LIST_ID_BY_LIST_NAME_EQUAL =
			"SELECT listid " + "FROM ( " + "    SELECT listnms.*, " + "        (MATCH(listname) AGAINST(:q)) AS searchScore "
					+ "    FROM listnms " + "    WHERE liststatus!=:deletedStatus " + "        AND listname=:q " + "    GROUP BY listid  "
					+ "    HAVING searchScore>0 " + ") AS searchResults " + "ORDER BY searchScore DESC ";
	public static final String SEARCH_GERMPLASM_BY_LIST_ID = "SELECT germplsm.* " + "FROM listdata "
			+ "	LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) " + "WHERE listid IN (:listids) ";
	public static final String GET_GERMPLASM_DATES_BY_GIDS = "SELECT gid, gdate " + "FROM germplsm " + "WHERE gid IN (:gids)";
	public static final String GET_METHOD_IDS_BY_GIDS = "SELECT gid, methn " + "FROM germplsm " + "WHERE gid IN (:gids)";
	public static final String GET_PARENT_NAMES_BY_STUDY_ID = "select N.gid, N.ntype, N.nval, N.nid, N.nstat" + " from names N"
			+ " inner join (" + "	select distinct G.gpid1 gid" + "	from listnms LNAMES" + "	inner join listdata_project LDATAPROJ on"
			+ "	LNAMES.listid = LDATAPROJ.list_id" + "	inner join germplsm G on" + "	G.gid = LDATAPROJ.germplasm_id and"
			+ "	G.gnpgs >= 0" + "	where LNAMES.projectid = :projId" +

	"    union" +

	"	select distinct G.gpid2" + "	from listnms LNAMES" + "	inner join listdata_project LDATAPROJ on"
			+ "	LNAMES.listid = LDATAPROJ.list_id" + "	inner join germplsm G on" + "	G.gid = LDATAPROJ.germplasm_id and"
			+ "	G.gnpgs >= 0" + "	where LNAMES.projectid = :projId" + " ) T on " + " N.gid = T.gid";

	public static final String GET_KNOWN_PARENT_GIDS_BY_STUDY_ID = "select distinct g.gid, G.gpid1, G.gpid2, G.grplce" + " from listnms LNAMES"
			+ " inner join listdata_project LDATAPROJ on" + "	LNAMES.listid = LDATAPROJ.list_id" + " inner join germplsm G on"
			+ "	G.gid = LDATAPROJ.germplasm_id and" + "	G.gnpgs > 0 AND (G.gpid1 > 0 or G.gpid2 > 0)" + " where LNAMES.projectid = :projId";

	public static final String SEARCH_MAINTENANCE_GROUP_MEMBERS_BY_MGID = Germplasm.GENERAL_SELECT_FROM + "("
			+ "SELECT g.*, group_concat(DISTINCT gt.inventory_id ORDER BY gt.inventory_id SEPARATOR ', ') as stockIDs " + "FROM germplsm g "
			+ "LEFT JOIN ims_lot gl ON gl.eid = g.gid AND gl.etype = 'GERMPLSM' " + "LEFT JOIN ims_transaction gt ON gt.lotid = gl.lotid "
			+ "WHERE g.mgid IN (:mgids)" + "GROUP BY g.gid" + ") " + Germplasm.GERMPLASM_ALIAS + "LEFT JOIN ("
			+ Germplasm.SEARCH_GERMPLASM_WITH_INVENTORY + ")" + Germplasm.INVENTORY_ALIAS + Germplasm.JOIN_ON_GERMPLASM_AND_INVENTORY;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	/*
	 * If the current germplasm is a managed sample then MGID contains the GID of the germplasm at the root of the management tree, else 0.
	 * This the GROUP_ID of the germplasm.
	 */
	@Basic(optional = false)
	@Column(name = "mgid")
	private Integer mgid;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "gid")
	private List<Name> names = new ArrayList<Name>();

	/**
	 * @OneToMany(mappedBy = "germplasm") private Set<Progenitor> progntr = new HashSet<Progenitor>();
	 **/

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
	 * This variable is populated only when the Germplasm POJO is retrieved by using GermplasmDataManager.getGermplasmWithMethodType().
	 * Otherwise it is null always.
	 */
	@Transient
	private Method method = null;

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
	* Previously, germplasm list is loaded and revisit the DB for each germplasm for getting method name.
	* This problem is removed by introducing this variable.
	*/
	@Transient
	private String methodName = null;

	/**
	 * This variable is populated when the user tries to search germplasm list.
	 * Previously, germplasm list is loaded and revisit the DB for each germplasm for getting location name.
	 * This problem is removed by introducing this variable.
	 */
	@Transient
	private String locationName = null;

	public Germplasm() {
	}

	public Germplasm(final Integer gid, final Integer methodId, final Integer gnpgs, final Integer gpid1, final Integer gpid2,
			final Integer userId, final Integer lgid, final Integer locationId, final Integer gdate, final Integer referenceId,
			final Integer grplce, final Integer mgid, final Name preferredName, final String preferredAbbreviation, final Method method) {
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

	public Germplasm(final Integer gid, final Integer methodId, final Integer gnpgs, final Integer gpid1, final Integer gpid2,
			final Integer userId, final Integer lgid, final Integer locationId, final Integer gdate, final Name preferredName) {

		// gref =0, grplce = 0, mgid = 0
		this(gid, methodId, gnpgs, gpid1, gpid2, userId, lgid, locationId, gdate, 0, 0, 0, preferredName, null, null);
	}

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

	public Integer getLgid() {
		return this.lgid;
	}

	public void setLgid(final Integer lgid) {
		this.lgid = lgid;
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

	public Integer getMethodId() {
		return this.methodId;
	}

	public void setMethodId(final Integer methodId) {
		this.methodId = methodId;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
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

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
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
		builder.append(", methodId=");
		builder.append(this.methodId);
		builder.append(", gnpgs=");
		builder.append(this.gnpgs);
		builder.append(", gpid1=");
		builder.append(this.gpid1);
		builder.append(", gpid2=");
		builder.append(this.gpid2);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", lgid=");
		builder.append(this.lgid);
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
		builder.append(", methodName=");
		builder.append(this.methodName);
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
		Name preferredName = null;
		for (final Name name : this.getNames()) {
			if (new Integer(1).equals(name.getNstat())) {
				preferredName = name;
				break;
			}
		}
		return preferredName;
	}

	@Override
	public void attachToAuditory(final Auditory auditory) {
		this.referenceId = auditory.getId();
	}
}
