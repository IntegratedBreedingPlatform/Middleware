package org.generationcp.middleware.domain.gms.search;

import org.generationcp.middleware.dao.GermplasmSearchDAO;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum class contains the list of columns that can be sorted in Germplasm Search Tables.
 */
public enum GermplasmSortableColumn {

    // Make sure to add another constant for new sortable column in Germplasm Search Table
    AVAILABLE_BALANCE("AVAILABLE", GermplasmSearchDAO.AVAIL_BALANCE),
    LOT_UNITS("LOT_UNITS", GermplasmSearchDAO.LOT_UNITS),
    AVAILABLE_LOTS("LOTS", GermplasmSearchDAO.AVAIL_LOTS),
    STOCK_IDS("STOCKID", GermplasmSearchDAO.STOCK_IDS),
    SEED_RES("SEED RES", "seedRes"),
    METHOD_NAME("METHOD NAME", GermplasmSearchDAO.METHOD_NAME),
    LOCATION_NAME("LOCATIONS", GermplasmSearchDAO.LOCATION_NAME),
    METHOD_ABBREVIATION("METHOD ABBREV", GermplasmSearchDAO.METHOD_ABBREVIATION),
    METHOD_NUMBER("METHOD NUMBER", GermplasmSearchDAO.METHOD_NUMBER),
    METHOD_GROUP("METHOD GROUP", GermplasmSearchDAO.METHOD_GROUP),
    PREFERRED_NAME("PREFERRED NAME", GermplasmSearchDAO.PREFERRED_NAME),
    PREFERRED_ID("PREFERRED ID", GermplasmSearchDAO.PREFERRED_ID),
    FEMALE_PARENT_ID("FGID", GermplasmSearchDAO.FEMALE_PARENT_ID),
    FEMALE_PARENT_PREFERRED_NAME("CROSS-FEMALE PREFERRED NAME", GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME),
    MALE_PARENT_ID("MGID", GermplasmSearchDAO.MALE_PARENT_ID),
    MALE_PARENT_PREFERRED_NAME("CROSS-MALE PREFERRED NAME", GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME),
    GID("GID", GermplasmSearchDAO.GID),
    GERMPLASM_DATE("GERMPLASM DATE", GermplasmSearchDAO.GERMPLASM_DATE),
    GROUP_ID("GROUP ID", GermplasmSearchDAO.GROUP_ID),
    NAMES("NAMES", GermplasmSearchDAO.NAMES),
    GROUP_SOURCE_GID("GROUP SOURCE GID", GermplasmSearchDAO.GROUP_SOURCE_GID),
    GROUP_SOURCE_PREFERRED_NAME("GROUP SOURCE", GermplasmSearchDAO.GROUP_SOURCE_PREFERRED_NAME),
    IMMEDIATE_SOURCE_GID("IMMEDIATE SOURCE GID", GermplasmSearchDAO.IMMEDIATE_SOURCE_GID),
    IMMEDIATE_SOURCE_NAME("IMMEDIATE SOURCE", GermplasmSearchDAO.IMMEDIATE_SOURCE_NAME);

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

    GermplasmSortableColumn(final String tableColumnName, final String dbColumnName) {
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
