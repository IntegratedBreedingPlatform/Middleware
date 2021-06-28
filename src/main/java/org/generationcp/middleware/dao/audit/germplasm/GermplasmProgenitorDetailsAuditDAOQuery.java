package org.generationcp.middleware.dao.audit.germplasm;

import org.generationcp.middleware.dao.audit.AuditConstants;

class GermplasmProgenitorDetailsAuditDAOQuery {

	static final String BREEDING_METHOD_NAME_ALIAS = "breedingMethodName";
	static final String FEMALE_PARENT_ALIAS = "femaleParent";
	static final String MALE_PARENT_ALIAS = "maleParent";
	static final String PROGENITORS_NUMBER_ALIAS = "progenitorsNumber";

	static final String BREEDING_METHOD_CHANGED_ALIAS = "breedingMethodChanged";
	static final String FEMALE_PARENT_CHANGED_ALIAS = "femaleParentChanged";
	static final String MALE_PARENT_CHANGED_ALIAS = "maleParentChanged";
	static final String PROGENITORS_NUMBER_CHANGED_ALIAS = "progenitorsNumberChanged";

	private final static String BASE_QUERY = "SELECT "
		+ " %s " // use of SELECT_EXPRESION
		+ " g_aud.aud_id, "
		+ " IF(g_aud.methn = coalesce(prev_g_aud.methn, g_aud.methn), false, true) AS " + BREEDING_METHOD_CHANGED_ALIAS + ", "
		+ " IF(g_aud.gpid1 = coalesce(prev_g_aud.gpid1, g_aud.gpid1), false, true) AS " + FEMALE_PARENT_CHANGED_ALIAS + ", "
		+ " IF(g_aud.gpid2 = coalesce(prev_g_aud.gpid2, g_aud.gpid2), false, true) AS " + MALE_PARENT_CHANGED_ALIAS + ", "
		+ " IF(g_aud.gnpgs = coalesce(prev_g_aud.gnpgs, g_aud.gnpgs), false, true) AS " + PROGENITORS_NUMBER_CHANGED_ALIAS
		+ "       FROM germplsm_aud g_aud "
		+ "		INNER JOIN methods method ON g_aud.methn = method.mid"
		+ "		LEFT JOIN germplsm_aud prev_g_aud ON prev_g_aud.aud_id = "
		+ " 			(SELECT inn.aud_id FROM germplsm_aud inn WHERE inn.gid = :gid AND inn.aud_id < g_aud.aud_id ORDER BY inn.aud_id DESC LIMIT 1) "
		+ " WHERE g_aud.gid = :gid "
		+ " HAVING (" + BREEDING_METHOD_CHANGED_ALIAS + " OR " + FEMALE_PARENT_CHANGED_ALIAS + " OR " + MALE_PARENT_CHANGED_ALIAS + " "
		+ "				OR " + PROGENITORS_NUMBER_CHANGED_ALIAS + " OR g_aud.aud_id = (SELECT MIN(aud_id) FROM germplsm_aud WHERE gid = :gid)) "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = " method.mname AS " + BREEDING_METHOD_NAME_ALIAS + ", "
		+ " g_aud.gpid1 AS " + FEMALE_PARENT_ALIAS + ", "
		+ " g_aud.gpid2 AS " + MALE_PARENT_ALIAS + ", "
		+ " g_aud.gnpgs AS " + PROGENITORS_NUMBER_ALIAS + ", "
		+ " g_aud.rev_type AS " + AuditConstants.REVISION_TYPE_ALIAS + ", "
		+ " g_aud.created_date AS " + AuditConstants.CREATED_DATE_ALIAS + ", "
		+ " g_aud.modified_date AS " + AuditConstants.MODIFIED_DATE_ALIAS + ", "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = g_aud.created_by) AS " + AuditConstants.CREATED_BY_ALIAS + ", "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = g_aud.modified_by) AS " + AuditConstants.MODIFIED_BY_ALIAS + ", ";

	private static final String SELECT_COUNT_EXPRESSION = " SELECT COUNT(1) FROM (%s) as rowCount";

	private static final String ORDER_EXPRESION = " ORDER BY g_aud.aud_id DESC ";

	static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION, ORDER_EXPRESION);
	}

	static String getCountQuery() {
		final String subQuery = String.format(BASE_QUERY, "", "");
		return String.format(SELECT_COUNT_EXPRESSION, subQuery);
	}

}
