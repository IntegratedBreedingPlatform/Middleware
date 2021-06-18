package org.generationcp.middleware.dao.audit;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.service.impl.audit.GermplasmAttributeAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmNameAuditDTO;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

public class GermplasmAuditDAO {

	private final Session session;

	public GermplasmAuditDAO(final Session session) {
		this.session = session;
	}

	public List<GermplasmNameAuditDTO> getNameChangesByNameId(final Integer nameId, final Pageable pageable) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmNameAuditDAOQuery.getSelectQuery());
		query.setParameter("nid", nameId);

		query.addScalar("revisionType", RevisionTypeResolver.INSTANCE);
		query.addScalar("nameType");
		query.addScalar("locationName");
		query.addScalar("creationDate");
		query.addScalar("value");
		query.addScalar("createdDate");
		query.addScalar("modifiedDate");
		query.addScalar("preferred", BooleanType.INSTANCE);
		query.addScalar("createdBy");
		query.addScalar("modifiedBy");
		query.addScalar("nameTypeChanged", BooleanType.INSTANCE);
		query.addScalar("locationChanged", BooleanType.INSTANCE);
		query.addScalar("creationDateChanged", BooleanType.INSTANCE);
		query.addScalar("valueChanged", BooleanType.INSTANCE);
		query.addScalar("preferredChanged", BooleanType.INSTANCE);
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

		query.addScalar("revisionType", RevisionTypeResolver.INSTANCE);
		query.addScalar("attributeType");
		query.addScalar("locationName");
		query.addScalar("creationDate");
		query.addScalar("value");
		query.addScalar("createdDate");
		query.addScalar("modifiedDate");
		query.addScalar("createdBy");
		query.addScalar("modifiedBy");
		query.addScalar("attributeTypeChanged", BooleanType.INSTANCE);
		query.addScalar("valueChanged", BooleanType.INSTANCE);
		query.addScalar("locationChanged", BooleanType.INSTANCE);
		query.addScalar("creationDateChanged", BooleanType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(GermplasmAttributeAuditDTO.class));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		return (List<GermplasmAttributeAuditDTO>) query.list();
	}

	public long countAttributeChangesByAttributeId(final Integer nameId) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmAttributeAuditDAOQuery.getCountQuery());
		query.setParameter("aid", nameId);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

}
