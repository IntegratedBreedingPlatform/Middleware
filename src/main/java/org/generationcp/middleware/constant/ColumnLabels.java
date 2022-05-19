package org.generationcp.middleware.constant;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.util.StringUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ColumnLabels {

	AVAILABLE_INVENTORY(TermId.AVAILABLE_INVENTORY, "LOTS"), //
	BREEDING_METHOD_NAME(TermId.BREEDING_METHOD_NAME, "METHOD NAME"), //
	BREEDING_METHOD_ABBREVIATION(TermId.BREEDING_METHOD_ABBREVIATION, "METHOD ABBREV"), //
	BREEDING_METHOD_NUMBER(TermId.BREEDING_METHOD_NUMBER, "METHOD NUMBER"), //
	BREEDING_METHOD_GROUP(TermId.BREEDING_METHOD_GROUP, "METHOD GROUP"), //

	CROSS_FEMALE_GID(TermId.CROSS_FEMALE_GID, "CROSS-FEMALE GID"), //
	CROSS_FEMALE_PREFERRED_NAME(TermId.CROSS_FEMALE_PREFERRED_NAME, "CROSS-FEMALE PREFERRED NAME"), //
	GROUP_SOURCE_GID(null, "GROUP SOURCE GID"), //
	GROUP_SOURCE_PREFERRED_NAME(null, "GROUP SOURCE"), //

	CROSS_MALE_GID(TermId.CROSS_MALE_GID, "CROSS-MALE GID"), //
	CROSS_MALE_PREFERRED_NAME(TermId.CROSS_MALE_PREFERRED_NAME, "CROSS-MALE PREFERRED NAME"), //
	IMMEDIATE_SOURCE_GID(null, "IMMEDIATE SOURCE GID"), //
	IMMEDIATE_SOURCE_NAME(null, "IMMEDIATE SOURCE"), //

	DESIGNATION(TermId.DESIG, "DESIGNATION"), //
	ENTRY_CODE(TermId.ENTRY_CODE, "ENTRY CODE"), //
	ENTRY_ID(TermId.ENTRY_NO, "ENTRY_ID"), //
	FEMALE_PARENT(TermId.FEMALE_PARENT, "Female Parent"), //
	FGID(TermId.FGID, "FGID"), //
	GERMPLASM_DATE(TermId.GERMPLASM_DATE, "GERMPLASM DATE"), //
	GERMPLASM_LOCATION(TermId.GERMPLASM_LOCATION, "LOCATIONS"), //
	GID(TermId.GID, "GID"), //
	MALE_PARENT(TermId.MALE_PARENT, "Male Parent"), //
	MGID(TermId.MGID, "MGID"), //
	PARENTAGE(TermId.CROSS, "PARENTAGE"), //
	PREFERRED_ID(TermId.PREFERRED_ID, "PREFERRED ID"), //
	PREFERRED_NAME(TermId.PREFERRED_NAME, "PREFERRED NAME"), //
	SEED_RESERVATION(TermId.SEED_RESERVATION, "TOTAL WITHDRAWALS"), //
	GERMPLASM_SOURCE(null, "GERMPLASM_SOURCE"), //
	STOCKID(null, "STOCKID"), //
	SEED_AMOUNT_G(TermId.SEED_AMOUNT_G, "SEED_AMOUNT_G"),//
	TAG(null, "TAG"), //
	GROUP_ID(TermId.GROUP_ID, "GROUP ID"), //
	FEMALE_PLOT(TermId.FEMALE_PLOT, "FEMALE PLOT"), //
	MALE_PLOT(TermId.MALE_PLOT, "MALE PLOT"), //
	CROSSING_DATE(TermId.CROSSING_DATE, "CROSSING DATE"), //
	MALE_NURSERY_NAME(TermId.MALE_NURSERY, "MALE NURSERY"), //
	STATUS(TermId.STATUS, "STATUS"), //
	ACTUAL_BALANCE(TermId.ACTUAL_BALANCE, "ACTUAL BALANCE"), //
	PLOT_NO(TermId.PLOT_NO, "PLOT NO"), //
	RESERVATION(TermId.RESERVATION, "RESERVATION"), //
	LOT_STATUS(TermId.LOT_STATUS, "LOT STATUS"), //

	// Trial things
	TRIAL_INSTANCE(TermId.TRIAL_INSTANCE_FACTOR, "TRIAL_INSTANCE"), //
	REP_NO(TermId.REP_NO, "REP_NO"), //

	// INVENTORY TABLE COLUMNS
	AMOUNT(TermId.AMOUNT_INVENTORY, "AMOUNT"), //
	BULK_WITH(TermId.BULK_WITH, "BULK WITH"), //
	BULK_COMPL(TermId.BULK_COMPL, "BULK COMPL?"), //
	COMMENT(TermId.COMMENT_INVENTORY, "COMMENT"), //
	STOCKID_INVENTORY(TermId.STOCKID_INVENTORY, "STOCKID_INVENTORY"),
	DUPLICATE(TermId.DUPLICATE, "DUPLICATE"), //
	LOT_ID(TermId.LOT_ID_INVENTORY, "LOT_ID"), //
	LOT_LOCATION(TermId.LOT_LOCATION_INVENTORY, "LOCATION"), //
	NEWLY_RESERVED(TermId.NEW_RESERVED_INVENTORY, "NEW_RES"), //
	RESERVED(TermId.RESERVED_INVENTORY, "RES"), //
	TOTAL(TermId.TOTAL_INVENTORY, "AVAILABLE"), //
	UNITS(TermId.UNITS_INVENTORY, "UNITS"), //

	// GERMPLASM CHECK
	ENTRY_TYPE(TermId.ENTRY_TYPE, "CHECK TYPE"), //
	GROUPGID(TermId.GROUPGID, "GROUPGID"),

	HAS_PROGENY(null, "HAS PROGENY"),
	USED_IN_LOCKED_STUDY(null, "USED IN LOCKED STUDY"),
	USED_IN_LOCKED_LIST(null, "USED IN LOCKED LIST");

	private final TermId termId;
	private final String name;
	private static final Map<String, ColumnLabels> LOOKUP = new HashMap<>();
	private static final List<String> ADDABLE_GERMPLASM_COLUMNS = new ArrayList<>();

	static {
		for (final ColumnLabels cl : EnumSet.allOf(ColumnLabels.class)) {
			ColumnLabels.LOOKUP.put(cl.getName(), cl);
		}

		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.PREFERRED_ID.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.PREFERRED_NAME.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.GERMPLASM_DATE.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.GERMPLASM_LOCATION.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.BREEDING_METHOD_NAME.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.BREEDING_METHOD_NUMBER.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.BREEDING_METHOD_GROUP.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.FGID.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.MGID.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName());

		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.GROUP_SOURCE_GID.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.IMMEDIATE_SOURCE_GID.getName());
		ColumnLabels.ADDABLE_GERMPLASM_COLUMNS.add(ColumnLabels.IMMEDIATE_SOURCE_NAME.getName());

	}

	ColumnLabels(final TermId termId, final String name) {
		this.name = name;
		this.termId = termId;
	}

	public String getName() {
		return this.name;
	}

	public TermId getTermId() {
		return this.termId;

	}

	public String getTermNameFromOntology(final OntologyDataManager ontologyDataManager) {
		if (this.termId != null) {
			final Term term = ontologyDataManager.getTermById(this.termId.getId());
			if (term != null && !StringUtil.isEmpty(term.getName())) {
				return term.getName();
			}
		}
		return this.name;
	}

	public static ColumnLabels get(final String name) {
		return ColumnLabels.LOOKUP.get(name);
	}

	public static List<String> getAddableGermplasmColumns() {
		return ColumnLabels.ADDABLE_GERMPLASM_COLUMNS;
	}

}
