package org.generationcp.middleware.dao.audit.germplasm;

import org.generationcp.middleware.dao.audit.AuditConstants;

class GermplasmReferenceAuditDAOQuery {

	static final String VALUE_ALIAS = "value";
	static final String VALUE_CHANGED_ALIAS = "valueChanged";

	private final static String BASE_QUERY = "SELECT %s " // use of SELECT_EXPRESION / COUNT_EXPRESSION
		+ "       FROM bibrefs_aud r_aud "
		+ "	   %s" // use of SELF_JOIN_QUERY -> It's not needed for the count query
		+ " WHERE r_aud.refid = :refId "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = " r_aud.analyt AS " + VALUE_ALIAS + ", "
		+ "  r_aud.rev_type AS " + AuditConstants.REVISION_TYPE_ALIAS + ", "
		+ "  r_aud.created_date AS " + AuditConstants.CREATED_DATE_ALIAS + ", "
		+ "  r_aud.modified_date AS " + AuditConstants.MODIFIED_DATE_ALIAS + ", "
		+ "  (SELECT uname FROM workbench.users WHERE users.userid = r_aud.created_by) AS " + AuditConstants.CREATED_BY_ALIAS + ", "
		+ "  (SELECT uname FROM workbench.users WHERE users.userid = r_aud.modified_by) AS " + AuditConstants.MODIFIED_BY_ALIAS + ", "
		+ "  IF(r_aud.analyt = coalesce(prev_r_aud.analyt, r_aud.analyt), false, true) AS " + VALUE_CHANGED_ALIAS;

	/**
	 * This is used to compare current values with previous ones and check if they have changed.
	 */
	private static final String SELF_JOIN_QUERY = " LEFT JOIN bibrefs_aud prev_r_aud ON prev_r_aud.aud_id = "
		+ " (SELECT inn.aud_id "
		+ "		FROM bibrefs_aud inn "
		+ "			 WHERE inn.refid = :refId AND inn.aud_id < r_aud.aud_id "
		+ " 	ORDER BY inn.aud_id DESC LIMIT 1) ";

	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	private static final String ORDER_EXPRESION = " ORDER BY r_aud.aud_id DESC ";

	static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION, SELF_JOIN_QUERY, ORDER_EXPRESION);
	}

	static String getCountQuery() {
		return String.format(BASE_QUERY, COUNT_EXPRESSION, "", "");
	}

}
