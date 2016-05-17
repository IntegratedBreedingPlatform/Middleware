
package org.generationcp.middleware.domain.gms.search;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum class contains the list of columns that can be sorted in Germplasm Search Tables.
 */
public enum GermplasmSortableColumn {
	// For now there are only 3 columns allowed to be sorted in Germplasm Search Table
	// Make sure to add another constant for new sortable column in Germplasm Search Table
	STOCK_IDS("STOCKID", "stockIDs"), SEED_RES("SEED RES", "seedRes"), GROUP_ID("GROUP ID", "g.mgid");

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
