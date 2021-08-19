package org.generationcp.middleware.dao.audit.germplasm;

import org.generationcp.middleware.dao.audit.AuditConstants;

class GermplasmNameAuditDAOQuery {

	static final String NAME_TYPE_ALIAS = "nameType";
	static final String VALUE_ALIAS = "value";
	static final String LOCATION_NAME_ALIAS = "locationName";
	static final String CREATION_DATE_ALIAS = "creationDate";
	static final String PREFERRED_ALIAS = "preferred";

	static final String NAME_TYPE_CHANGED_ALIAS = "nameTypeChanged";
	static final String VALUE_CHANGED_ALIAS = "valueChanged";
	static final String LOCATION_CHANGED_ALIAS = "locationChanged";
	static final String CREATION_DATE_CHANGED_ALIAS = "creationDateChanged";
	static final String PREFERRED_CHANGED_ALIAS = "preferredChanged";

	private final static String BASE_QUERY = "SELECT %s " // use of SELECT_EXPRESION / COUNT_EXPRESSION
		+ "       FROM names_aud n_aud "
		+ "    INNER JOIN udflds user_defined_field ON n_aud.ntype = user_defined_field.fldno "
		+ "    INNER JOIN location loc ON n_aud.nlocn = loc.locid "
		+ "	   %s" // use of SELF_JOIN_QUERY -> It's not needed for the count query
		+ " WHERE n_aud.nid = :nid "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = "user_defined_field.fcode AS " + NAME_TYPE_ALIAS + ", "
		+ "  n_aud.nval AS " + VALUE_ALIAS + ", "
		+ "  loc.lname AS " + LOCATION_NAME_ALIAS + ", "
		+ "  cast(n_aud.ndate as char) AS " + CREATION_DATE_ALIAS + ", "
		+ "  n_aud.nstat AS " + PREFERRED_ALIAS + ", "
		+ "  n_aud.rev_type AS " + AuditConstants.REVISION_TYPE_ALIAS + ", "
		+ "  n_aud.created_date AS " + AuditConstants.CREATED_DATE_ALIAS + ", "
		+ "  n_aud.modified_date AS " + AuditConstants.MODIFIED_DATE_ALIAS + ", "
		+ "  (SELECT uname FROM workbench.users WHERE users.userid = n_aud.created_by) AS " + AuditConstants.CREATED_BY_ALIAS + ", "
		+ "  (SELECT uname FROM workbench.users WHERE users.userid = n_aud.modified_by) AS " + AuditConstants.MODIFIED_BY_ALIAS + ", "
		+ "  IF(n_aud.ntype = coalesce(prev_n_aud.ntype, n_aud.ntype), false, true) as " + NAME_TYPE_CHANGED_ALIAS + ", "
		+ "  IF(n_aud.nval = coalesce(prev_n_aud.nval, n_aud.nval), false, true) as " + VALUE_CHANGED_ALIAS + ", "
		+ "  IF(n_aud.nlocn = coalesce(prev_n_aud.nlocn, n_aud.nlocn), false, true) as " + LOCATION_CHANGED_ALIAS + ", "
		+ "  IF(n_aud.ndate = coalesce(prev_n_aud.ndate, n_aud.ndate), false, true) as " + CREATION_DATE_CHANGED_ALIAS + ", "
		+ "  IF(n_aud.nstat = coalesce(prev_n_aud.nstat, n_aud.nstat), false, true) as " + PREFERRED_CHANGED_ALIAS;

	/**
	 * This is used to compare current values with previous ones and check if they have changed.
	 */
	private static final String SELF_JOIN_QUERY = " LEFT JOIN names_aud prev_n_aud ON prev_n_aud.aud_id = "
		+ " (SELECT inn.aud_id "
		+ "		FROM names_aud inn "
		+ "			 WHERE inn.nid = :nid AND inn.aud_id < n_aud.aud_id "
		+ " 	ORDER BY inn.aud_id DESC LIMIT 1) ";

	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	private static final String ORDER_EXPRESION = " ORDER BY n_aud.aud_id DESC ";

	static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION, SELF_JOIN_QUERY, ORDER_EXPRESION);
	}

	static String getCountQuery() {
		return String.format(BASE_QUERY, COUNT_EXPRESSION, "", "");
	}

}
