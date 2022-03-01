
package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermRelationship;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class TermDataManagerImpl extends DataManager implements TermDataManager {

	private DaoFactory daoFactory;

	public TermDataManagerImpl() {

	}

	public TermDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Term getTermById(final Integer termId) throws MiddlewareException {
		return Term.fromCVTerm(daoFactory.getCvTermDao().getById(termId));
	}

	@Override
	public Term getTermByNameAndCvId(final String name, final int cvId) throws MiddlewareException {
		return Term.fromCVTerm(daoFactory.getCvTermDao().getByNameAndCvId(name, cvId));
	}

	@Override
	public Term getTermByName(final String name) throws MiddlewareException {
		return Term.fromCVTerm(daoFactory.getCvTermDao().getByName(name));
	}

	@Override
	public List<Term> getTermByCvId(final int cvId) throws MiddlewareException {
		return daoFactory.getCvTermDao().getTermByCvId(cvId);
	}

	@Override
	public boolean isTermReferred(final int termId) {
		return daoFactory.getCvTermRelationshipDao().isTermReferred(termId);
	}

	@Override
	public List<TermRelationship> getRelationshipsWithObjectAndType(final Integer objectId, final TermRelationshipId relationshipId)
			throws MiddlewareException {

		final List<TermRelationship> termRelationships = new ArrayList<>();

		try {

			final SQLQuery query = this.getActiveSession()
					.createSQLQuery("select cr.cvterm_relationship_id rid, cr.type_id rtype, "
							+ "s.cv_id scv, s.cvterm_id scvid, s.name sname, s.definition sdescription, "
							+ "o.cv_id ocv, o.cvterm_id ocvid, o.name oname, o.definition odescription " + "from cvterm_relationship cr "
							+ "inner join cvterm s on s.cvterm_id = cr.subject_id " + "inner join cvterm o on o.cvterm_id = cr.object_id "
							+ "where cr.object_id = :objectId and cr.type_id = :typeId")
					.addScalar("rid", new org.hibernate.type.IntegerType()).addScalar("rtype", new org.hibernate.type.IntegerType())
					.addScalar("scv", new org.hibernate.type.IntegerType()).addScalar("scvid", new org.hibernate.type.IntegerType())
					.addScalar("sname", new org.hibernate.type.StringType()).addScalar("sdescription", new org.hibernate.type.StringType())
					.addScalar("ocv", new org.hibernate.type.IntegerType()).addScalar("ocvid", new org.hibernate.type.IntegerType())
					.addScalar("oname", new org.hibernate.type.StringType()).addScalar("odescription", new org.hibernate.type.StringType());

			query.setParameter("objectId", objectId);
			query.setParameter("typeId", relationshipId.getId());

			final List result = query.list();

			for (final Object row : result) {
				final Object[] items = (Object[]) row;

				// Check is row does have objects to access
				if (items.length == 0) {
					continue;
				}

				final TermRelationship relationship = new TermRelationship();
				relationship.setId(Util.typeSafeObjectToInteger(items[0]));
				relationship.setRelationshipId(TermRelationshipId.getById(Util.typeSafeObjectToInteger(items[1])));
				relationship.setSubjectTerm(new Term(Util.typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5],
					Util.typeSafeObjectToInteger(items[2]), false));
				relationship.setObjectTerm(new Term(Util.typeSafeObjectToInteger(items[7]), (String) items[8], (String) items[9],
					Util.typeSafeObjectToInteger(items[6]), false));
				termRelationships.add(relationship);
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getRelationshipsWithObjectAndType :" + e.getMessage(), e);
		}

		return termRelationships;
	}

	@Override
	public Set<String> getCategoriesInUse(final int scaleId) {
		return daoFactory.getCvTermRelationshipDao().getCategoriesInUse(scaleId);
	}

}
