package org.generationcp.middleware.dao.audit;

import org.hibernate.SQLQuery;

public class GermplasmNameChangesDAOQuery {

	private final static String BASE_QUERY = "select %s"
		+ "       from names_aud n "
		+ "    inner join udflds user_defined_field on n.ntype = user_defined_field.fldno "
		+ "    inner join location loc on n.nlocn = loc.locid"
		+ " where nid = :nid and gid = :gid";

	private static final String SELECT_EXPRESION = "rev_type as revisionType, user_defined_field.fname as nameType, loc.lname as locationName, "
		+ " str_to_date(n.ndate, '%Y%m%d') as creationDate, n.nval as value, n.created_date as createdDate, n.modified_date as modifiedDate, "
		+ " n.nstat as preferred, "
		+ " (select uname from workbench.users where users.userid = n.created_by) as createdBy, "
		+ " (select uname from workbench.users where users.userid = n.modified_by) as modifiedBy ";

	public static String getSelectBaseQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION);
	}

	public static String getCountBaseQuery() {
		return String.format(BASE_QUERY, " count(1) ");
	}

	public static void addParams(final SQLQuery query, final Integer gid, final Integer nameId) {
		query.setParameter("gid", gid);
		query.setParameter("nid", nameId);
	}

}
