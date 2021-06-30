package org.generationcp.middleware.dao.audit.germplasm;

import org.generationcp.middleware.dao.audit.AuditConstants;

class GermplasmOtherProgenitorsAuditDAOQuery {

	static final String PROGENITOR_GID_ALIAS = "progenitorGid";

	private final static String BASE_QUERY = "SELECT %s " // use of SELECT_EXPRESION / COUNT_EXPRESSION
		+ "       FROM progntrs_aud p_aud "
		+ " WHERE p_aud.gid = :gid "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = "p_aud.pid AS " + PROGENITOR_GID_ALIAS + ", "
		+ " p_aud.rev_type AS " + AuditConstants.REVISION_TYPE_ALIAS + ", "
		+ " p_aud.created_date AS " + AuditConstants.CREATED_DATE_ALIAS + ", "
		+ " p_aud.modified_date AS " + AuditConstants.MODIFIED_DATE_ALIAS + ", "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = p_aud.created_by) AS " + AuditConstants.CREATED_BY_ALIAS + ", "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = p_aud.modified_by) AS " + AuditConstants.MODIFIED_BY_ALIAS;

	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	private static final String ORDER_EXPRESION = " ORDER BY p_aud.aud_id DESC ";

	static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION, ORDER_EXPRESION);
	}

	static String getCountQuery() {
		return String.format(BASE_QUERY, COUNT_EXPRESSION, "");
	}

}
