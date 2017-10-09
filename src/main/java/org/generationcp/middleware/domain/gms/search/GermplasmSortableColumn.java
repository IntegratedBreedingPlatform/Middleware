
package org.generationcp.middleware.domain.gms.search;

import org.generationcp.middleware.dao.GermplasmDAO;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum class contains the list of columns that can be sorted in Germplasm Search Tables.
 */
public enum GermplasmSortableColumn {

	// Make sure to add another constant for new sortable column in Germplasm Search Table
	AVAILABLE_BALANCE("AVAILABLE", GermplasmDAO.AVAIL_BALANCE),
	AVAILABLE_LOTS("LOTS", GermplasmDAO.AVAIL_LOTS),
	STOCK_IDS("STOCKID", GermplasmDAO.STOCK_IDS),
	SEED_RES("SEED RES", "seedRes"),
	METHOD_NAME("METHOD NAME", GermplasmDAO.METHOD_NAME),
	LOCATION_NAME("LOCATIONS", GermplasmDAO.LOCATION_NAME),
	METHOD_ABBREVIATION("METHOD ABBREV", GermplasmDAO.METHOD_ABBREVIATION),
	METHOD_NUMBER("METHOD NUMBER", GermplasmDAO.METHOD_NUMBER),
	METHOD_GROUP("METHOD GROUP", GermplasmDAO.METHOD_GROUP),
	PREFERRED_NAME("PREFERRED NAME", GermplasmDAO.PREFERRED_NAME),
	PREFERRED_ID("PREFERRED ID", GermplasmDAO.PREFERRED_ID),
	FEMALE_PARENT_ID("CROSS-FEMALE GID", GermplasmDAO.FEMALE_PARENT_ID),
	FEMALE_PARENT_PREFERRED_NAME("CROSS-FEMALE PREFERRED NAME", GermplasmDAO.FEMALE_PARENT_PREFERRED_NAME),
	MALE_PARENT_ID("CROSS-MALE GID", GermplasmDAO.MALE_PARENT_ID),
	MALE_PARENT_PREFERRED_NAME("CROSS-MALE PREFERRED NAME", GermplasmDAO.MALE_PARENT_PREFERRED_NAME),
	GID("GID", "g.gid"),
	GERMPLASM_DATE("GERMPLASM DATE", "g.gdate"),
	GROUP_ID("GROUP ID", "g.mgid");

	// The corresponding column name in the germplasm search table
	String tableColumnName;

	// The column name used in the main search germplasm query
	// The following are the only allowed columns from the main query: g.*, stockIDs, availInv, seedRes
	String dbColumnName;

	private static final Map<String, GermplasmSortableColumn> LOOKUP = new HashMap<>();

	static {
		for (final GermplasmSortableColumn gsc : EnumSet.allOf(GermplasmSortableColumn.class)) {
			GermplasmSortableColumn.LOOKUP.put(gsc.getTableColumnName(), gsc);
		}
	}

	private GermplasmSortableColumn(final String tableColumnName, final String dbColumnName) {
		this.tableColumnName = tableColumnName;
		this.dbColumnName = dbColumnName;
	}

	public String getDbColumnName() {
		return this.dbColumnName;
	}

	public String getTableColumnName() {
		return this.tableColumnName;
	}

	public static GermplasmSortableColumn get(final String tableColumnName) {
		return GermplasmSortableColumn.LOOKUP.get(tableColumnName);
	}

}
