package org.generationcp.middleware.dao.audit.germplasm;

import org.generationcp.middleware.dao.audit.AuditConstants;

class GermplasmAttributeAuditDAOQuery {

	static final String ATTRIBUTE_TYPE_ALIAS = "attributeType";
	static final String VALUE_ALIAS = "value";
	static final String LOCATION_NAME_ALIAS = "locationName";
	static final String CREATION_DATE_ALIAS = "creationDate";

	static final String ATTRIBUTE_TYPE_CHANGED_ALIAS = "attributeTypeChanged";
	static final String VALUE_CHANGED_ALIAS = "valueChanged";
	static final String LOCATION_CHANGED_ALIAS = "locationChanged";
	static final String CREATION_DATE_CHANGED_ALIAS = "creationDateChanged";

	private final static String BASE_QUERY = "SELECT %s " // use of SELECT_EXPRESION / COUNT_EXPRESSION
		+ "       FROM atributs_aud a_aud "
		+ "    INNER JOIN udflds user_defined_field ON a_aud.atype = user_defined_field.fldno "
		+ "    INNER JOIN location loc ON a_aud.alocn = loc.locid "
		+ "	   %s" // use of SELF_JOIN_QUERY -> It's not needed for the count query
		+ " WHERE a_aud.aid = :aid "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = "user_defined_field.fcode AS " + ATTRIBUTE_TYPE_ALIAS + ", "
		+ " a_aud.aval AS " + VALUE_ALIAS + ", "
		+ " loc.lname AS " + LOCATION_NAME_ALIAS + ", "
		+ " cast(a_aud.adate as char) AS " + CREATION_DATE_ALIAS + ", "
		+ " a_aud.rev_type AS " + AuditConstants.REVISION_TYPE_ALIAS + ", "
		+ " a_aud.created_date AS " + AuditConstants.CREATED_DATE_ALIAS + ", "
		+ " a_aud.modified_date AS " + AuditConstants.MODIFIED_DATE_ALIAS + ", "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = a_aud.created_by) AS " + AuditConstants.CREATED_BY_ALIAS + ", "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = a_aud.modified_by) AS " + AuditConstants.MODIFIED_BY_ALIAS + ", "
		+ " IF(a_aud.atype = coalesce(prev_a_aud.atype, a_aud.atype), false, true) as " + ATTRIBUTE_TYPE_CHANGED_ALIAS + ", "
		+ " IF(a_aud.aval = coalesce(prev_a_aud.aval, a_aud.aval), false, true) as " + VALUE_CHANGED_ALIAS + ", "
		+ " IF(a_aud.alocn = coalesce(prev_a_aud.alocn, a_aud.alocn), false, true) as " + LOCATION_CHANGED_ALIAS + ", "
		+ " IF(a_aud.adate = coalesce(prev_a_aud.adate, a_aud.adate), false, true) as " + CREATION_DATE_CHANGED_ALIAS;

	/**
	 * This is used to compare current values with previous ones and check if they have changed.
	 */
	private static final String SELF_JOIN_QUERY = " LEFT JOIN atributs_aud prev_a_aud ON prev_a_aud.aud_id = "
		+ " (SELECT inn.aud_id "
		+ "		FROM atributs_aud inn "
		+ "			 WHERE inn.aid = :aid AND inn.aud_id < a_aud.aud_id "
		+ " 	ORDER BY inn.aud_id DESC LIMIT 1) ";

	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	private static final String ORDER_EXPRESION = " ORDER BY a_aud.aud_id DESC ";

	static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION, SELF_JOIN_QUERY, ORDER_EXPRESION);
	}

	static String getCountQuery() {
		return String.format(BASE_QUERY, COUNT_EXPRESSION, "", "");
	}

}
