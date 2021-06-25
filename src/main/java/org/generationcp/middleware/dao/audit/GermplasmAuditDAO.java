package org.generationcp.middleware.dao.audit;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.service.impl.audit.GermplasmAttributeAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmBasicDetailsAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmNameAuditDTO;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

public class GermplasmAuditDAO {

	static final String REVISION_TYPE_ALIAS = "revisionType";
	static final String CREATED_BY_ALIAS = "createdBy";
	static final String CREATED_DATE_ALIAS = "createdDate";
	static final String MODIFIED_BY_ALIAS = "modifiedBy";
	static final String MODIFIED_DATE_ALIAS = "modifiedDate";

	private final Session session;

	public GermplasmAuditDAO(final Session session) {
		this.session = session;
	}

	public List<GermplasmNameAuditDTO> getNameChangesByNameId(final Integer nameId, final Pageable pageable) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmNameAuditDAOQuery.getSelectQuery());
		query.setParameter("nid", nameId);

		this.addCommonScalars(query);
		query.addScalar(GermplasmNameAuditDAOQuery.NAME_TYPE_ALIAS);
		query.addScalar(GermplasmNameAuditDAOQuery.LOCATION_NAME_ALIAS);
		query.addScalar(GermplasmNameAuditDAOQuery.CREATION_DATE_ALIAS);
		query.addScalar(GermplasmNameAuditDAOQuery.VALUE_ALIAS);
		query.addScalar(GermplasmNameAuditDAOQuery.PREFERRED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmNameAuditDAOQuery.NAME_TYPE_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmNameAuditDAOQuery.LOCATION_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmNameAuditDAOQuery.CREATION_DATE_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmNameAuditDAOQuery.VALUE_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmNameAuditDAOQuery.PREFERRED_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(GermplasmNameAuditDTO.class));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		return (List<GermplasmNameAuditDTO>) query.list();
	}

	public long countNameChangesByNameId(final Integer nameId) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmNameAuditDAOQuery.getCountQuery());
		query.setParameter("nid", nameId);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public List<GermplasmAttributeAuditDTO> getAttributeChangesByAttributeId(final Integer attributeId, final Pageable pageable) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmAttributeAuditDAOQuery.getSelectQuery());
		query.setParameter("aid", attributeId);

		this.addCommonScalars(query);
		query.addScalar(GermplasmAttributeAuditDAOQuery.ATTRIBUTE_TYPE_ALIAS);
		query.addScalar(GermplasmAttributeAuditDAOQuery.LOCATION_NAME_ALIAS);
		query.addScalar(GermplasmAttributeAuditDAOQuery.CREATION_DATE_ALIAS);
		query.addScalar(GermplasmAttributeAuditDAOQuery.VALUE_ALIAS);
		query.addScalar(GermplasmAttributeAuditDAOQuery.ATTRIBUTE_TYPE_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmAttributeAuditDAOQuery.VALUE_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmAttributeAuditDAOQuery.LOCATION_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmAttributeAuditDAOQuery.CREATION_DATE_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(GermplasmAttributeAuditDTO.class));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		return (List<GermplasmAttributeAuditDTO>) query.list();
	}

	public long countAttributeChangesByAttributeId(final Integer nameId) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmAttributeAuditDAOQuery.getCountQuery());
		query.setParameter("aid", nameId);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public List<GermplasmBasicDetailsAuditDTO> getBasicDetailsChangesByGid(final Integer gid, final Pageable pageable) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmBasicDetailsAuditDAOQuery.getSelectQuery());
		query.setParameter("gid", gid);

		this.addCommonScalars(query);
		query.addScalar(GermplasmBasicDetailsAuditDAOQuery.LOCATION_NAME_ALIAS);
		query.addScalar(GermplasmBasicDetailsAuditDAOQuery.CREATION_DATE_ALIAS);
		query.addScalar(GermplasmBasicDetailsAuditDAOQuery.LOCATION_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmBasicDetailsAuditDAOQuery.CREATION_DATE_CHANGED_ALIAS, BooleanType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(GermplasmBasicDetailsAuditDTO.class));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		return (List<GermplasmBasicDetailsAuditDTO>) query.list();
	}

	public long countBasicDetailsChangesByGid(final Integer gid) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmBasicDetailsAuditDAOQuery.getCountQuery());
		query.setParameter("gid", gid);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	private void addCommonScalars(final SQLQuery query) {
		query.addScalar(REVISION_TYPE_ALIAS, RevisionTypeResolver.INSTANCE);
		query.addScalar(CREATED_BY_ALIAS);
		query.addScalar(CREATED_DATE_ALIAS);
		query.addScalar(MODIFIED_BY_ALIAS);
		query.addScalar(MODIFIED_DATE_ALIAS);
	}

}
