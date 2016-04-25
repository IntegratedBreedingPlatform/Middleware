
package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Strings;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.Clock;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.SystemClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements {@link OntologyPropertyDataManagerImpl}
 */
@Transactional
public class OntologyPropertyDataManagerImpl implements OntologyPropertyDataManager {

	private static final String PROPERTY_EXIST_WITH_SAME_NAME = "Property exist with same name";
	private static final String SHOULD_VALID_TRAIT_CLASS = "Term should be of valid TRAIT_CLASS";
	private static final String PROPERTY_DOES_NOT_EXIST = "Property does not exist with that id";
	private static final String TERM_IS_NOT_PROPERTY = "That term is not a PROPERTY";
	private static final String PROPERTY_IS_REFERRED_TO_VARIABLE = "Property is referred to variable.";

	@Autowired
	private OntologyCommonDAO ontologyCommonDAO;

	@Autowired
	private OntologyDaoFactory ontologyDaoFactory;

	@Autowired
	protected Clock systemClock;

	public OntologyPropertyDataManagerImpl() {
        // no-arg constructor is required by CGLIB proxying used by Spring 3x and older.
	}

    //TODO:This is temporary hack for managerFactory, builder and service. It should refactor to remove this constructor
    public OntologyPropertyDataManagerImpl(HibernateSessionProvider sessionProvider) {
        this.ontologyDaoFactory = new OntologyDaoFactory();
        this.ontologyDaoFactory.setSessionProvider(sessionProvider);
        OntologyCommonDAOImpl ontologyCommonDAOImpl = new OntologyCommonDAOImpl();
        ontologyCommonDAOImpl.setSessionProvider(sessionProvider);
        this.ontologyCommonDAO = ontologyCommonDAOImpl;
        this.systemClock = new SystemClock();
    }

	@Override
	public Property getProperty(int id, boolean filterObsolete) throws MiddlewareException {
		List<Property> properties = this.getProperties(false, new ArrayList<>(Collections.singletonList(id)), filterObsolete);
		if (properties.isEmpty()) {
			return null;
		}
		return properties.get(0);
	}

	@Override
	public List<Property> getAllProperties() throws MiddlewareException {
		return this.getProperties(true, null, true);
	}

	@Override
	public List<Property> getAllPropertiesWithClassAndVariableType(String[] classes, String[] variableTypes) throws MiddlewareException {
		List<Integer> propertyIds = this.ontologyCommonDAO.getAllPropertyIdsWithClassAndVariableType(classes, variableTypes);
		// NOTE: Passing fetchAll as false and filterObsolete as true
		return this.getProperties(false, propertyIds, true);
	}

	/**
	 * This will fetch list of properties by passing propertyIds This method is private and consumed by other methods
	 * 
	 * @param fetchAll will tell weather query should get all properties or not.
	 * @param propertyIds will tell weather propertyIds should be pass to filter result. Combination of these two will give flexible usage.
	 * @return List<Property>
	 * @throws MiddlewareException
	 */
	private List<Property> getProperties(Boolean fetchAll, List propertyIds, boolean filterObsolete) throws MiddlewareException {

		Map<Integer, Property> map = this.ontologyCommonDAO.getPropertiesWithCropOntologyAndTraits(fetchAll, propertyIds, filterObsolete);

		// Created, modified from CVTermProperty
		List propertyProp = this.ontologyDaoFactory.getCvTermPropertyDao().getByCvTermIds(new ArrayList<>(map.keySet()));
		for (Object p : propertyProp) {
			CVTermProperty property = (CVTermProperty) p;

			Property ontologyProperty = map.get(property.getCvTermId());

			if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
				ontologyProperty.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
			} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
				ontologyProperty.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
			}
		}

		ArrayList<Property> properties = new ArrayList<>(map.values());

		Collections.sort(properties, new Comparator<org.generationcp.middleware.domain.ontology.Property>() {

			@Override
			public int compare(Property l, Property r) {
				return l.getName().compareToIgnoreCase(r.getName());
			}
		});

		return properties;
	}

	@Override
	public void addProperty(Property property) throws MiddlewareException {
		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CvTermPropertyDao cvTermPropertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();

		CVTerm term = cvTermDao.getByNameAndCvId(property.getName(), CvId.PROPERTIES.getId());

		if (term != null) {
			throw new MiddlewareException(OntologyPropertyDataManagerImpl.PROPERTY_EXIST_WITH_SAME_NAME);
		}

		// Constant CvId
		property.setVocabularyId(CvId.PROPERTIES.getId());
		CVTerm propertyTerm = cvTermDao.save(property.getName(), property.getDefinition(), CvId.PROPERTIES);
		property.setId(propertyTerm.getCvTermId());

		this.updatePropertyTraitClasses(property.getId(), property.getClasses());

		cvTermPropertyDao.updateOrDeleteProperty(property.getId(), TermId.CROP_ONTOLOGY_ID.getId(), property.getCropOntologyId(), 0);

		property.setDateCreated(systemClock.now());

		String strValueOfDate = ISO8601DateParser.toString(property.getDateCreated());

		// Save creation time
		cvTermPropertyDao.updateOrDeleteProperty(property.getId(), TermId.CREATION_DATE.getId(), strValueOfDate, 0);
	}

	@Override
	public void updateProperty(Property property) throws MiddlewareException {
		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CvTermPropertyDao cvTermPropertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();

		CVTerm term = cvTermDao.getById(property.getId());

		this.checkTermIsProperty(term);

		term.setName(property.getName());
		term.setDefinition(property.getDefinition());

		cvTermDao.merge(term);

		// Save or update crop ontology
		cvTermPropertyDao.updateOrDeleteProperty(property.getId(), TermId.CROP_ONTOLOGY_ID.getId(), property.getCropOntologyId(), 0);

		this.updatePropertyTraitClasses(property.getId(), property.getClasses());

		property.setDateLastModified(systemClock.now());

		String strValueOfDate = ISO8601DateParser.toString(property.getDateLastModified());

		// Save last modified Time
		cvTermPropertyDao.updateOrDeleteProperty(property.getId(), TermId.LAST_UPDATE_DATE.getId(), strValueOfDate, 0);
	}

	void updatePropertyTraitClasses(Integer propertyId, Set<String> classes) {
		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao cvTermRelationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();

		Map<Integer, CVTermRelationship> relationsToDelete = new HashMap<>();
		List<CVTermRelationship> relationships = cvTermRelationshipDao.getBySubjectIdAndTypeId(propertyId, TermId.IS_A.getId());
		for (CVTermRelationship cl : relationships) {
			relationsToDelete.put(cl.getObjectId(), cl);
		}

		List<Term> allClasses = cvTermDao.getTermByCvId(CvId.TRAIT_CLASS.getId());

		for (String className : classes) {
			// Discarding empty or null strings
			className = className.trim();

			if (Strings.isNullOrEmpty(className)) {
				continue;
			}

			Term classTerm = null;

			for (Term tClass : allClasses) {

				if (className.compareToIgnoreCase(tClass.getName()) != 0) {
					continue;
				}

				classTerm = tClass;
				break;
			}

			// Add new term if does not exist
			if (classTerm == null) {
				classTerm = this.addTraitClass(className);
			}

			if (relationsToDelete.containsKey(classTerm.getId())) {
				relationsToDelete.remove(classTerm.getId());
				continue;
			}

			cvTermRelationshipDao.save(propertyId, TermId.IS_A.getId(), classTerm.getId());
		}

		// Removing old classes which are not in used
		for (CVTermRelationship cl : relationsToDelete.values()) {
			cvTermRelationshipDao.makeTransient(cl);
			// Remove trait class if not in used
			this.removeTraitClass(cl.getObjectId());
		}
	}

	@Override
	public void deleteProperty(Integer propertyId) throws MiddlewareException {

		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao cvTermRelationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();
		CvTermPropertyDao cvTermPropertyDao = this.ontologyDaoFactory.getCvTermPropertyDao();

		CVTerm term = cvTermDao.getById(propertyId);

		this.checkTermIsProperty(term);

		if (cvTermRelationshipDao.isTermReferred(propertyId)) {
			throw new MiddlewareException(OntologyPropertyDataManagerImpl.PROPERTY_IS_REFERRED_TO_VARIABLE);
		}

		// Deleting existing relationships for property
		List<CVTermRelationship> relationships = cvTermRelationshipDao.getBySubject(propertyId);
		for (CVTermRelationship cl : relationships) {
			cvTermRelationshipDao.makeTransient(cl);
			// Remove trait class if not in used
			this.removeTraitClass(cl.getObjectId());
		}

		// Deleting existing values for property
		List<CVTermProperty> properties = cvTermPropertyDao.getByCvTermId(propertyId);
		for (CVTermProperty p : properties) {
			cvTermPropertyDao.makeTransient(p);
		}
		cvTermDao.makeTransient(term);
	}

	private void checkTermIsProperty(CVTerm term) throws MiddlewareException {

		if (term == null) {
			throw new MiddlewareException(OntologyPropertyDataManagerImpl.PROPERTY_DOES_NOT_EXIST);
		}

		if (term.getCv() != CvId.PROPERTIES.getId()) {
			throw new MiddlewareException(OntologyPropertyDataManagerImpl.TERM_IS_NOT_PROPERTY);
		}
	}

	private Term addTraitClass(String className) throws MiddlewareException {

		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();

		// Check weather class term exist with CV 1011.
		CVTerm classTerm = cvTermDao.getByNameAndCvId(className, CvId.TRAIT_CLASS.getId());

		// If exist then don't add.
		if (classTerm == null) {
			classTerm = cvTermDao.save(className, null, CvId.TRAIT_CLASS);
		}

		return Term.fromCVTerm(classTerm);
	}

	private void removeTraitClass(Integer termId) throws MiddlewareException {

		CVTermDao cvTermDao = this.ontologyDaoFactory.getCvTermDao();
		CVTermRelationshipDao cvTermRelationshipDao = this.ontologyDaoFactory.getCvTermRelationshipDao();

		CVTerm term = cvTermDao.getById(termId);

		// Validate parent class. Parent class should be from cvId as 1000
		if (term.getCv() != CvId.TRAIT_CLASS.getId()) {
			throw new MiddlewareException(OntologyPropertyDataManagerImpl.SHOULD_VALID_TRAIT_CLASS);
		}

		// Check weather term is referred
		if (cvTermRelationshipDao.getByObjectId(termId).isEmpty() && cvTermRelationshipDao.getBySubject(termId).isEmpty()) {
			// Term is not referred anywhere and can be delete
			cvTermDao.makeTransient(term);
		}

	}
}
