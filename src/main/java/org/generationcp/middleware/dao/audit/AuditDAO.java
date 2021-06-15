package org.generationcp.middleware.dao.audit;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.service.impl.audit.GermplasmNameChangeDTO;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

public class AuditDAO {

	private final Session session;

	public AuditDAO(final Session session) {
		this.session = session;
	}

	public List<GermplasmNameChangeDTO> getNameChangesByNameId(final Integer nameId, final Pageable pageable) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmNameChangesDAOQuery.getSelectQuery(pageable));
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
		query.setResultTransformer(Transformers.aliasToBean(GermplasmNameChangeDTO.class));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		return (List<GermplasmNameChangeDTO>) query.list();
	}

	public long countNameChangesByNameId(final Integer nameId) {
		final SQLQuery query = this.session.createSQLQuery(GermplasmNameChangesDAOQuery.getCountQuery());
		query.setParameter("nid", nameId);
		return ((BigInteger) query.uniqueResult()).longValue();
	}


}
