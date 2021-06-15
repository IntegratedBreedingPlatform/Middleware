package org.generationcp.middleware.dao.audit;

import org.hibernate.SQLQuery;

public class GermplasmNameChangesDAOQuery {

	private final static String BASE_QUERY = "SELECT %s"
		+ "       FROM names_aud n_aud "
		+ "    INNER JOIN udflds user_defined_field ON n_aud.ntype = user_defined_field.fldno "
		+ "    INNER JOIN location loc ON n_aud.nlocn = loc.locid "
		+ " WHERE nid = :nid ";

	private static final String SELECT_EXPRESION = "rev_type AS revisionType, user_defined_field.fname AS nameType, loc.lname AS locationName, "
		+ " str_to_date(n_aud.ndate, '%Y%m%d') AS creationDate, n_aud.nval AS value, n_aud.created_date AS createdDate, n_aud.modified_date AS modifiedDate, "
		+ " n_aud.nstat AS preferred, "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = n_aud.created_by) AS createdBy, "
		+ " (SELECT uname FROM workbench.users WHERE users.userid = n_aud.modified_by) AS modifiedBy ";

	private static final String COUNT_EXPRESSION = " count(1) ";

	private static final String ORDER_EXPRESION = " ORDER BY n_aud.aud_id";

	public static String getSelectQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION) + ORDER_EXPRESION;
	}

	public static String getCountQuery() {
		return String.format(BASE_QUERY, COUNT_EXPRESSION);
	}

	public static void addParams(final SQLQuery query, final Integer nameId) {
		query.setParameter("nid", nameId);
	}

}
