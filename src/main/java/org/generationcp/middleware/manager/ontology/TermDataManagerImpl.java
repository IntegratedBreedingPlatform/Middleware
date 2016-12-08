 
package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermRelationship;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class TermDataManagerImpl extends DataManager implements TermDataManager {

	public TermDataManagerImpl() {
		
	}
	public TermDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public Term getTermById(Integer termId) throws MiddlewareException {
		return Term.fromCVTerm(this.getCvTermDao().getById(termId));
	}

	@Override
	public Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareException {
		return Term.fromCVTerm(this.getCvTermDao().getByNameAndCvId(name, cvId));
	}

	@Override
	public List<Term> getTermByCvId(int cvId) throws MiddlewareException {
		return this.getCvTermDao().getTermByCvId(cvId);
	}

	@Override
	public boolean isTermReferred(int termId) throws MiddlewareException {
		return this.getCvTermRelationshipDao().isTermReferred(termId);
	}

	@Override
	public List<TermRelationship> getRelationshipsWithObjectAndType(Integer objectId, TermRelationshipId relationshipId)
			throws MiddlewareException {

		List<TermRelationship> termRelationships = new ArrayList<>();

		try {

			SQLQuery query =
					this.getActiveSession()
							.createSQLQuery(
									"select cr.cvterm_relationship_id rid, cr.type_id rtype, "
											+ "s.cv_id scv, s.cvterm_id scvid, s.name sname, s.definition sdescription, "
											+ "o.cv_id ocv, o.cvterm_id ocvid, o.name oname, o.definition odescription "
											+ "from cvterm_relationship cr " + "inner join cvterm s on s.cvterm_id = cr.subject_id "
											+ "inner join cvterm o on o.cvterm_id = cr.object_id "
											+ "where cr.object_id = :objectId and cr.type_id = :typeId")
							.addScalar("rid", new org.hibernate.type.IntegerType())
							.addScalar("rtype", new org.hibernate.type.IntegerType())
							.addScalar("scv", new org.hibernate.type.IntegerType())
							.addScalar("scvid", new org.hibernate.type.IntegerType())
							.addScalar("sname", new org.hibernate.type.StringType())
							.addScalar("sdescription", new org.hibernate.type.StringType())
							.addScalar("ocv", new org.hibernate.type.IntegerType())
							.addScalar("ocvid", new org.hibernate.type.IntegerType())
							.addScalar("oname", new org.hibernate.type.StringType())
							.addScalar("odescription", new org.hibernate.type.StringType());

			query.setParameter("objectId", objectId);
			query.setParameter("typeId", relationshipId.getId());

			List result = query.list();

			for (Object row : result) {
				Object[] items = (Object[]) row;

				// Check is row does have objects to access
				if (items.length == 0) {
					continue;
				}

				TermRelationship relationship = new TermRelationship();
				relationship.setId(this.typeSafeObjectToInteger(items[0]));
				relationship.setRelationshipId(TermRelationshipId.getById(this.typeSafeObjectToInteger(items[1])));
				relationship.setSubjectTerm(new Term(this.typeSafeObjectToInteger(items[3]), (String) items[4], (String) items[5], this
						.typeSafeObjectToInteger(items[2]), false));
				relationship.setObjectTerm(new Term(this.typeSafeObjectToInteger(items[7]), (String) items[8], (String) items[9], this
						.typeSafeObjectToInteger(items[6]), false));
				termRelationships.add(relationship);
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getRelationshipsWithObjectAndType :" + e.getMessage(), e);
		}

		return termRelationships;
	}
	
	@Override
	public List<String> getCategoriesReferredInPhenotype(int scaleId) throws MiddlewareQueryException {
		return this.getCvTermRelationshipDao().getCategoriesReferredInPhenotype(scaleId);
	}
}
