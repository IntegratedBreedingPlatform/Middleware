package org.generationcp.middleware.dao.audit.germplasm;

import org.generationcp.middleware.dao.audit.AuditConstants;

class GermplasmBasicDetailsAuditDAOQuery {

	static final String LOCATION_NAME_ALIAS = "locationName";
	static final String CREATION_DATE_ALIAS = "creationDate";
	static final String GROUP_ID_ALIAS = "groupId";

	static final String LOCATION_CHANGED_ALIAS = "locationChanged";
	static final String CREATION_DATE_CHANGED_ALIAS = "creationDateChanged";
	static final String GROUP_ID_CHANGED_ALIAS = "groupIdChanged";

	private final static String BASE_QUERY = "SELECT "
		+ " %s " // use of SELECT_EXPRESION
		+ " g_aud.aud_id, "
		+ " IF(g_aud.gdate = coalesce(prev_g_aud.gdate, g_aud.gdate), false, true) as " + CREATION_DATE_CHANGED_ALIAS + ", "
		+ " IF(g_aud.glocn = coalesce(prev_g_aud.glocn, g_aud.glocn), false, true) as " + LOCATION_CHANGED_ALIAS + ", "
		+ " IF(g_aud.mgid = coalesce(prev_g_aud.mgid, g_aud.mgid), false, true) as " + GROUP_ID_CHANGED_ALIAS
		+ "       FROM germplsm_aud g_aud "
		+ " 	INNER JOIN location loc ON g_aud.glocn = loc.locid "
		+ "		LEFT JOIN germplsm_aud prev_g_aud ON prev_g_aud.aud_id = "
		+ " 			(SELECT inn.aud_id FROM germplsm_aud inn WHERE inn.gid = :gid AND inn.aud_id < g_aud.aud_id ORDER BY inn.aud_id DESC LIMIT 1) "
		+ " WHERE g_aud.gid = :gid "
		+ " HAVING (" + CREATION_DATE_CHANGED_ALIAS + " OR " + LOCATION_CHANGED_ALIAS + " OR " + GROUP_ID_CHANGED_ALIAS + " OR "
		+ "				g_aud.aud_id = (SELECT MIN(aud_id) FROM germplsm_aud where gid = :gid)) "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = " cast(g_aud.gdate as char) AS " + CREATION_DATE_ALIAS + ", "
		+ " loc.lname AS " + LOCATION_NAME_ALIAS + ", "
		+ " g_aud.mgid AS " + GROUP_ID_ALIAS + ", "
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
