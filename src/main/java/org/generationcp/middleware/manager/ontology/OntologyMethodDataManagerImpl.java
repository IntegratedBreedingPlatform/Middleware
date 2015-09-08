
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
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements {@link OntologyMethodDataManager}
 */
@Transactional
public class OntologyMethodDataManagerImpl extends DataManager implements OntologyMethodDataManager {

	private static final String METHOD_DOES_NOT_EXIST = "Method does not exist with that id";
	private static final String TERM_IS_NOT_METHOD = "That term is not a METHOD";
	private static final String METHOD_IS_REFERRED_TO_VARIABLE = "Method is referred to variable.";

	public OntologyMethodDataManagerImpl() {
		super();
	}
	
	public OntologyMethodDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public Method getMethod(int id) throws MiddlewareException {
		CVTerm term = this.getCvTermDao().getById(id);
		this.checkTermIsMethod(term);

		try {
			List<Method> methods = this.getMethods(false, new ArrayList<>(Collections.singletonList(id)));

			if (methods.isEmpty()) {
				return null;
			}

			return methods.get(0);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getMethod :" + e.getMessage(), e);
		}
	}

	@Override
	public List<Method> getAllMethods() throws MiddlewareException {
		try {
			return this.getMethods(true, null);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getAllMethods :" + e.getMessage(), e);
		}
	}

	/**
	 * This will fetch list of methods by passing methodIds
	 * 
	 * @param fetchAll will tell wheather query should get all methods or not.
	 * @param methodIds will tell wheather methodIds should be pass to filter result. Combination of these two will give flexible usage.
	 * @return List<Method>
	 * @throws MiddlewareException
	 */
	private List<Method> getMethods(Boolean fetchAll, List<Integer> methodIds) throws MiddlewareException {

		Map<Integer, Method> map = new HashMap<>();
		if (methodIds == null) {
			methodIds = new ArrayList<>();
		}

		if (!fetchAll && methodIds.size() == 0) {
			return new ArrayList<>();
		}

		try {

			List<CVTerm> terms =
					fetchAll ? this.getCvTermDao().getAllByCvId(CvId.METHODS) : this.getCvTermDao().getAllByCvId(methodIds, CvId.METHODS);

			for (CVTerm m : terms) {
				Method method = new Method(Term.fromCVTerm(m));
				map.put(method.getId(), method);
			}

			// Created, modified from CVTermProperty
			List termProperties = this.getCvTermPropertyDao().getByCvTermIds(new ArrayList<>(map.keySet()));

			for (Object p : termProperties) {
				CVTermProperty property = (CVTermProperty) p;

				Method method = map.get(property.getCvTermId());

				if (method == null) {
					continue;
				}

				if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
					method.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
					method.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
				}
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getProperties :" + e.getMessage(), e);
		}

		ArrayList<Method> methods = new ArrayList<>(map.values());

		Collections.sort(methods, new Comparator<Method>() {

			@Override
			public int compare(Method l, Method r) {
				return l.getName().compareToIgnoreCase(r.getName());
			}
		});

		return methods;
	}

	@Override
	public void addMethod(Method method) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getByNameAndCvId(method.getName(), CvId.METHODS.getId());

		if (term != null) {
			throw new MiddlewareQueryException("Method exist with same name");
		}

		// Constant CvId
		method.setVocabularyId(CvId.METHODS.getId());

		try {
			term = this.getCvTermDao().save(method.getName(), method.getDefinition(), CvId.METHODS);
			method.setId(term.getCvTermId());

			// Save creation time
			this.getCvTermPropertyDao().save(method.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at addMethod" + e.getMessage(), e);
		}
	}

	@Override
	public void updateMethod(Method method) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(method.getId());

		this.checkTermIsMethod(term);

		// Constant CvId
		method.setVocabularyId(CvId.METHODS.getId());

		try {

			term.setName(method.getName());
			term.setDefinition(method.getDefinition());

			this.getCvTermDao().merge(term);

			// Save last modified Time
			this.getCvTermPropertyDao().save(method.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at updateMethod" + e.getMessage(), e);
		}

	}

	@Override
	public void deleteMethod(int id) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(id);

		this.checkTermIsMethod(term);

		if (this.getCvTermRelationshipDao().isTermReferred(id)) {
			throw new MiddlewareException(OntologyMethodDataManagerImpl.METHOD_IS_REFERRED_TO_VARIABLE);
		}

		try {

			// delete properties
			List<CVTermProperty> properties = this.getCvTermPropertyDao().getByCvTermId(term.getCvTermId());
			for (CVTermProperty property : properties) {
				this.getCvTermPropertyDao().makeTransient(property);
			}

			this.getCvTermDao().makeTransient(term);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at deleteMethod" + e.getMessage(), e);
		}
	}

	private void checkTermIsMethod(CVTerm term) throws MiddlewareException {

		if (term == null) {
			throw new MiddlewareException(OntologyMethodDataManagerImpl.METHOD_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.METHODS.getId()) {
			throw new MiddlewareException(OntologyMethodDataManagerImpl.TERM_IS_NOT_METHOD);
		}
	}
}
