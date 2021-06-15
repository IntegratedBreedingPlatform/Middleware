package org.generationcp.middleware.dao.audit;

import org.springframework.data.domain.Pageable;

public class GermplasmNameChangesDAOQuery {

	private final static String BASE_QUERY = "SELECT %s " // use of SELECT_EXPRESION / COUNT_EXPRESSION
		+ "       FROM names_aud n_aud "
		+ "    INNER JOIN udflds user_defined_field ON n_aud.ntype = user_defined_field.fldno "
		+ "    INNER JOIN location loc ON n_aud.nlocn = loc.locid "
		+ "	   %s" // use of SQL_VARS_SELECT_EXPRESSION
		+ " WHERE nid = :nid "
		+ " %s"; // use of ORDER_EXPRESION

	private static final String SELECT_EXPRESION = "rev_type AS revisionType, "
		+ " user_defined_field.fname AS nameType, "
		+ "		if(@lastNameType = n_aud.ntype, false, true) as nameTypeChanged, @lastNameType \\:= n_aud.ntype, "
		+ " loc.lname AS locationName, "
		+ "		if(@lastLocation = n_aud.nlocn, false, true) as locationChanged, @lastLocation \\:= n_aud.nlocn, "
		+ " str_to_date(n_aud.ndate, '%Y%m%d') AS creationDate, "
		+ "		if(@lastCreationDate = n_aud.ndate, false, true) as creationDateChanged, @lastCreationDate \\:= n_aud.ndate, "
		+ " n_aud.nval AS value, "
		+ "		if(@lastValue = n_aud.nval, false, true) as valueChanged, @lastValue \\:= n_aud.nval, "
		+ " n_aud.nstat AS preferred, "
		+ "		if(@lastPreferred = n_aud.nstat, false, true) as preferredChanged, @lastPreferred \\:= n_aud.nstat, "
		+ " n_aud.created_date AS createdDate, n_aud.modified_date AS modifiedDate, "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = n_aud.created_by) AS createdBy, "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = n_aud.modified_by) AS modifiedBy ";

	private static final String SQL_VARS_SELECT_EXPRESSION = ", (SELECT @lastNameType \\:= inn.ntype, @lastLocation \\:= inn.nlocn, "
		+ " @lastCreationDate \\:= inn.ndate, @lastValue \\:= inn.nval, @lastPreferred \\:= inn.nstat "
		+ "		FROM names_aud inn WHERE inn.nid = :nid LIMIT %s) AS SQLVars ";

	private static final String COUNT_EXPRESSION = " count(1) ";

	private static final String ORDER_EXPRESION = " ORDER BY n_aud.aud_id";

	public static String getSelectQuery(final Pageable pageable) {
		// Get the last row of the previous page to compare changes
		final String sqlVarsLimit;
		if (pageable.getPageNumber() == 0) {
			sqlVarsLimit = "1";
		} else {
			sqlVarsLimit = String.format("%s, 1", pageable.getPageSize() * pageable.getPageNumber() - 1);
		}

		final String sqlVarsLimitClause = String.format(SQL_VARS_SELECT_EXPRESSION, sqlVarsLimit);
		return String.format(BASE_QUERY, SELECT_EXPRESION, sqlVarsLimitClause, ORDER_EXPRESION);
	}

	public static String getCountQuery() {
		return String.format(BASE_QUERY, COUNT_EXPRESSION, "", "");
	}

}
