
package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements {@link OntologyMethodDataManager}
 */
@Transactional
public class OntologyMethodDataManagerImpl implements OntologyMethodDataManager {

	private static final String METHOD_DOES_NOT_EXIST = "Method does not exist with that id";
	private static final String TERM_IS_NOT_METHOD = "That term is not a METHOD";
	private static final String METHOD_IS_REFERRED_TO_VARIABLE = "Method is referred to variable.";

	private DaoFactory daoFactory;

	public OntologyMethodDataManagerImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public OntologyMethodDataManagerImpl(HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public OntologyMethodDataManagerImpl(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public Method getMethod(int id, boolean filterObsolete) {
		List<Method> methods = this.getMethods(false, new ArrayList<>(Collections.singletonList(id)), filterObsolete);
		if (methods.isEmpty()) {
			return null;
		}
		return methods.get(0);
	}

	@Override
	public List<Method> getAllMethods() {
		return this.getMethods(true, null, true);
	}

	/**
	 * This will fetch list of methods by passing methodIds. It does not filter out obsolete methods if filterObsolete is false.
	 *
	 * @param fetchAll will tell wheather query should get all methods or not.
	 * @param methodIds will tell wheather methodIds should be pass to filter result. Combination of these two will give flexible usage.
	 * @param filterObsolete will tell whether obsolete methods will be filtered
	 * @return List<Method>
	 */
	private List<Method> getMethods(Boolean fetchAll, List<Integer> methodIds, boolean filterObsolete) {

		Map<Integer, Method> map = new HashMap<>();
		List<Integer> termIds = methodIds;
		if (termIds == null) {
			termIds = new ArrayList<>();
		}

		List<CVTerm> terms = fetchAll ? this.daoFactory.getCvTermDao().getAllByCvId(CvId.METHODS, filterObsolete)
				: this.daoFactory.getCvTermDao().getAllByCvId(termIds, CvId.METHODS, filterObsolete);

		for (CVTerm m : terms) {
			Method method = new Method(Term.fromCVTerm(m));
			map.put(method.getId(), method);
		}

		// Created, modified from CVTermProperty
		List termProperties = this.daoFactory.getCvTermPropertyDao().getByCvTermIds(new ArrayList<>(map.keySet()));

		for (Object p : termProperties) {
			CVTermProperty property = (CVTermProperty) p;

			Method method = map.get(property.getCvTermId());

			if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
				method.setDateCreated(ISO8601DateParser.tryParseToDateTime(property.getValue()));
			} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
				method.setDateLastModified(ISO8601DateParser.tryParseToDateTime(property.getValue()));
			}
		}

		List<Method> methods = new ArrayList<>(map.values());

		Collections.sort(methods, new Comparator<Method>() {

			@Override
			public int compare(Method l, Method r) {
				return l.getName().compareToIgnoreCase(r.getName());
			}
		});

		return methods;
	}


	@Override
	public void addMethod(Method method) {

		CVTerm term = this.daoFactory.getCvTermDao().getByNameAndCvId(method.getName(), CvId.METHODS.getId());

		if (term != null) {
			throw new MiddlewareQueryException("Method exist with same name");
		}

		// Constant CvId
		method.setVocabularyId(CvId.METHODS.getId());

		term = this.daoFactory.getCvTermDao().save(method.getName(), method.getDefinition(), CvId.METHODS);
		method.setId(term.getCvTermId());

		method.setDateCreated(new Date());

		String stringDateValue = ISO8601DateParser.toString(method.getDateCreated());

		// Save creation time
		this.daoFactory.getCvTermPropertyDao().save(method.getId(), TermId.CREATION_DATE.getId(), stringDateValue, 0);
	}

	@Override
	public void updateMethod(Method method) {

		CVTerm term = this.daoFactory.getCvTermDao().getById(method.getId());

		this.checkTermIsMethod(term);

		// Constant CvId
		method.setVocabularyId(CvId.METHODS.getId());

		term.setName(method.getName());
		term.setDefinition(method.getDefinition());

		this.daoFactory.getCvTermDao().merge(term);

		method.setDateLastModified(new Date());

		// Save last modified Time
		this.daoFactory.getCvTermPropertyDao()
		.save(method.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(method.getDateLastModified()), 0);

	}

	@Override
	public void deleteMethod(int id) {

		CVTerm term = this.daoFactory.getCvTermDao().getById(id);

		this.checkTermIsMethod(term);

		if (this.daoFactory.getCvTermRelationshipDao().isTermReferred(id)) {
			throw new MiddlewareException(OntologyMethodDataManagerImpl.METHOD_IS_REFERRED_TO_VARIABLE);
		}

		// delete properties
		List<CVTermProperty> properties = this.daoFactory.getCvTermPropertyDao().getByCvTermId(term.getCvTermId());
		for (CVTermProperty property : properties) {
			this.daoFactory.getCvTermPropertyDao().makeTransient(property);
		}

		this.daoFactory.getCvTermDao().makeTransient(term);
	}

	private void checkTermIsMethod(CVTerm term) {

		if (term == null) {
			throw new MiddlewareException(OntologyMethodDataManagerImpl.METHOD_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.METHODS.getId()) {
			throw new MiddlewareException(OntologyMethodDataManagerImpl.TERM_IS_NOT_METHOD);
		}
	}
}
