package org.generationcp.middleware.dao.audit;

import org.generationcp.middleware.service.impl.audit.GermplasmNameChangeDTO;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;

import java.util.List;

public class AuditDAO {

	private final static String NAME_CHANGES = "select rev_type as revisionType, user_defined_field.fname as nameType, loc.lname as locationName, "
		+ " str_to_date(n.ndate, '%Y%m%d') as creationDate, n.nval as value, n.created_date as createdDate, n.modified_date as modifiedDate, "
		+ " n.nstat as preferred, "
		+ " (select uname from workbench.users where users.userid = n.created_by) as createdBy, "
		+ " (select uname from workbench.users where users.userid = n.modified_by) as modifiedBy "
		+ "       from names_aud n "
		+ "    inner join udflds user_defined_field on n.ntype = user_defined_field.fldno "
		+ "    inner join location loc on n.nlocn = loc.locid"
		+ " where nid = :nid and gid = :gid";

	private final Session session;

	public AuditDAO(final Session session) {
		this.session = session;
	}

	public List<GermplasmNameChangeDTO> getNameChangesByGidAndNameId(final Integer gid, final Integer nameId) {
		final SQLQuery sqlQuery = this.session.createSQLQuery(NAME_CHANGES);
		sqlQuery.setParameter("gid", gid);
		sqlQuery.setParameter("nid", nameId);

		sqlQuery.addScalar("revisionType", RevisionTypeResolver.INSTANCE);
		sqlQuery.addScalar("nameType");
		sqlQuery.addScalar("locationName");
		sqlQuery.addScalar("creationDate");
		sqlQuery.addScalar("value");
		sqlQuery.addScalar("createdDate");
		sqlQuery.addScalar("modifiedDate");
		sqlQuery.addScalar("preferred", BooleanType.INSTANCE);
		sqlQuery.addScalar("createdBy");
		sqlQuery.addScalar("modifiedBy");
		sqlQuery.setResultTransformer(Transformers.aliasToBean(GermplasmNameChangeDTO.class));
		return (List<GermplasmNameChangeDTO>) sqlQuery.list();
	}

}
