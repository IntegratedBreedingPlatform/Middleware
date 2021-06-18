package org.generationcp.middleware.dao.audit;

public class GermplasmNameAuditDAOQuery {

	private final static String BASE_QUERY = "SELECT %s " // use of SELECT_EXPRESION / COUNT_EXPRESSION
		+ "       FROM names_aud n_aud "
		+ "    INNER JOIN udflds user_defined_field ON n_aud.ntype = user_defined_field.fldno "
		+ "    INNER JOIN location loc ON n_aud.nlocn = loc.locid "
		+ "	   %s" // use of SELF_JOIN_QUERY -> It's not needed for the count query
		+ " WHERE n_aud.nid = :nid "
		+ " %s"; // use of ORDER_EXPRESION -> It's not needed for the count query

	private static final String SELECT_EXPRESION = "user_defined_field.fcode AS nameType, "
		+ "  n_aud.nval AS value, "
		+ "  loc.lname AS locationName, "
		+ "  Str_to_date(n_aud.ndate, '%Y%m%d') AS creationDate, "
		+ "  n_aud.nstat AS preferred, "
		+ "  n_aud.rev_type AS revisionType, "
		+ "  n_aud.created_date AS createdDate, "
		+ "  n_aud.modified_date AS modifiedDate, "
		+ "  (SELECT uname FROM workbench.users WHERE users.userid = n_aud.created_by) AS createdBy, "
		+ "  (SELECT uname FROM workbench.users WHERE users.userid = n_aud.modified_by) AS modifiedBy, "
		+ "  IF(n_aud.ntype = coalesce(prev_n_aud.ntype, n_aud.ntype), false, true) as nameTypeChanged, "
		+ "  IF(n_aud.nval = coalesce(prev_n_aud.nval, n_aud.nval), false, true) as valueChanged, "
		+ "  IF(n_aud.nlocn = coalesce(prev_n_aud.nlocn, n_aud.nlocn), false, true) as locationChanged, "
		+ "  IF(n_aud.ndate = coalesce(prev_n_aud.ndate, n_aud.ndate), false, true) as creationDateChanged, "
		+ "  IF(n_aud.nstat = coalesce(prev_n_aud.nstat, n_aud.nstat), false, true) as preferredChanged";

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

	public static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION, SELF_JOIN_QUERY, ORDER_EXPRESION);
	}

	public static String getCountQuery() {
		return String.format(BASE_QUERY, COUNT_EXPRESSION, "", "");
	}

}
