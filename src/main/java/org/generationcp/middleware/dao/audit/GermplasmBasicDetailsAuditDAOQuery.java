package org.generationcp.middleware.dao.audit;

class GermplasmBasicDetailsAuditDAOQuery {

	private final static String BASE_QUERY = "SELECT "
		+ " %s " // use of SELECT_EXPRESION
		+ " g_aud.aud_id, "
		+ " IF(g_aud.gdate = coalesce(prev_g_aud.gdate, g_aud.gdate), false, true) as creationDateChanged, "
		+ " IF(g_aud.glocn = coalesce(prev_g_aud.glocn, g_aud.glocn), false, true) as locationChanged "
		+ "       FROM germplsm_aud g_aud "
		+ " 	INNER JOIN location loc ON g_aud.glocn = loc.locid "
		+ "		LEFT JOIN germplsm_aud prev_g_aud ON prev_g_aud.aud_id = "
		+ " 			(SELECT inn.aud_id FROM germplsm_aud inn WHERE inn.gid = :gid AND inn.aud_id < g_aud.aud_id ORDER BY inn.aud_id DESC LIMIT 1) "
		+ " WHERE g_aud.gid = :gid "
		+ " HAVING (creationDateChanged = true OR locationChanged = true OR g_aud.aud_id = (select MIN(aud_id) from germplsm_aud where gid = :gid)) "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = " cast(g_aud.gdate as char) AS creationDate, "
		+ " loc.lname AS locationName, "
		+ " g_aud.rev_type AS revisionType, "
		+ " g_aud.created_date AS createdDate, "
		+ " g_aud.modified_date AS modifiedDate, "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = g_aud.created_by) AS createdBy, "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = g_aud.modified_by) AS modifiedBy, ";

	private static final String SELECT_COUNT_EXPRESSION = " SELECT COUNT(1) FROM (%s) as rowCount";

	private static final String ORDER_EXPRESION = " ORDER BY g_aud.aud_id DESC ";

	public static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION, ORDER_EXPRESION);
	}

	public static String getCountQuery() {
		final String subQuery = String.format(BASE_QUERY, "", "");
		return String.format(SELECT_COUNT_EXPRESSION, subQuery);
	}

}
