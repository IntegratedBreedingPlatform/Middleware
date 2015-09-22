
package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Strings;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements {@link OntologyPropertyDataManagerImpl}
 */
@Transactional
public class OntologyPropertyDataManagerImpl extends DataManager implements OntologyPropertyDataManager {

	private static final String SHOULD_VALID_TRAIT_CLASS = "Term should be of valid TRAIT_CLASS";
	private static final String PROPERTY_DOES_NOT_EXIST = "Property does not exist with that id";
	private static final String TERM_IS_NOT_PROPERTY = "That term is not a PROPERTY";
	private static final String PROPERTY_IS_REFERRED_TO_VARIABLE = "Property is referred to variable.";

	public OntologyPropertyDataManagerImpl() {
		super();
	}
	
	public OntologyPropertyDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public Property getProperty(int id) throws MiddlewareException {
		return this.getProperty(id, true);
	}

	@Override
	public Property getProperty(int id, boolean filterObsolete) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(id);

		this.checkTermIsProperty(term);

		try {
			List<Property> properties = this.getProperties(false, new ArrayList<>(Collections.singletonList(id)), filterObsolete);
			if (properties.size() == 0) {
				return null;
			}
			return properties.get(0);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getProperty :" + e.getMessage(), e);
		}
	}

	@Override
	public List<Property> getAllProperties() throws MiddlewareException {
		try {
			return this.getProperties(true, null);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getAllProperties :" + e.getMessage(), e);
		}
	}

	@Override
	public List<Property> getAllPropertiesWithClass(String className) throws MiddlewareException {
		return this.getAllPropertiesWithClass(new String[] {className});
	}

	@Override
	public List<Property> getAllPropertiesWithClassAndVariableType(String[] classes, String[] variableTypes) throws MiddlewareException {
		try {
			String classFilter = !(Objects.equals(classes, null) || classes.length == 0) ? " and dt.name in (:classes) " : "";
			String variableTypeFilter =
					!(Objects.equals(variableTypes, null) || variableTypes.length == 0) ? " and c.value in (:variableTypes) " : "";

			SQLQuery query = getActiveSession().createSQLQuery(
                    "SELECT DISTINCT p.cvterm_id FROM cvterm p join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id " +
                            " inner join cvterm dt on dt.cvterm_id = cvtr.object_id where cvtr.type_id = " + TermId.IS_A.getId() +
							" and p.cv_id = 1010 and p.is_obsolete = 0 " + classFilter + " and exists " +
							" (SELECT 1 from cvtermprop c INNER JOIN cvterm_relationship pvtr on c.cvterm_id = pvtr.subject_id " +
							" where c.type_id = " + TermId.VARIABLE_TYPE.getId() + " and pvtr.object_id = p.cvterm_id" + variableTypeFilter
							+ ")");

			if (!classFilter.isEmpty()) {
				query.setParameterList("classes", classes);
			}

			if (!variableTypeFilter.isEmpty()) {
				query.setParameterList("variableTypes", variableTypes);
			}

			List propertyIds = query.list();

			return this.getProperties(false, propertyIds);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getAllPropertiesWithClass :" + e.getMessage(), e);
		}
	}

	@Override
	public List<Property> getAllPropertiesWithClass(String[] classes) throws MiddlewareException {
		try {

			SQLQuery query = this.getActiveSession().createSQLQuery(
					"SELECT DISTINCT p.cvterm_id FROM cvterm p join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id"
							+ " inner join cvterm dt on dt.cvterm_id = cvtr.object_id" + " where cvtr.type_id = " + TermId.IS_A.getId()
							+ " and p.cv_id = " + CvId.PROPERTIES.getId() + " and p.is_obsolete = 0" + " and dt.name in (:classes)");

			query.setParameterList("classes", classes);

			List propertyIds = query.list();

			return this.getProperties(false, propertyIds);

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getAllPropertiesWithClass :" + e.getMessage(), e);
		}
	}

	/**
	 * This will fetch list of non-obsolete properties by passing propertyIds. This method is private and consumed by other methods.
	 *
	 * @param fetchAll will tell whether query should get all non-obsolete properties or not.
	 * @param propertyIds will tell whether propertyIds should be pass to filter result. Combination of these two will give flexible usage.
	 * @return List<Property>
	 * @throws MiddlewareException
	 */
	private List<Property> getProperties(Boolean fetchAll, List propertyIds) throws MiddlewareException {
		return this.getProperties(fetchAll, propertyIds, true);
	}

	/**
	 * This will fetch list of properties by passing propertyIds. This method is private and consumed by other methods. Obsolete properties
	 * will be filtered if filterObsolete is true
	 *
	 * @param fetchAll will tell whether query should get all properties or not.
	 * @param propertyIds will tell whether propertyIds should be pass to filter result. Combination of these two will give flexible usage.
	 * @param filterObsolete will tell whether obsolete properties will be filtered
	 * @return List<Property>
	 * @throws MiddlewareException
	 */
	private List<Property> getProperties(Boolean fetchAll, List propertyIds, boolean filterObsolete) throws MiddlewareException {

		Map<Integer, Property> map = new HashMap<>();

		if (propertyIds == null) {
			propertyIds = new ArrayList<>();
		}

		if (!fetchAll && propertyIds.size() == 0) {
			return new ArrayList<>();
		}

		try {

			String filterClause = "";
			if (propertyIds.size() > 0) {
				filterClause = " and p.cvterm_id in (:propertyIds)";
			}

			String filterObsoleteClause = "";
			if (filterObsolete) {
				filterObsoleteClause = " and p." + this.getCvTermDao().SHOULD_NOT_OBSOLETE;
			}

			SQLQuery query =
					this.getActiveSession()
					.createSQLQuery(
							"select p.cvterm_id pId, p.name pName, p.definition pDescription, p.cv_id pVocabularyId, p.is_obsolete pObsolete"
									+ ", tp.value cropOntologyId"
									+ ", GROUP_CONCAT(cs.name SEPARATOR ',') AS classes"
									+ "  from cvterm p"
									+ " LEFT JOIN cvtermprop tp ON tp.cvterm_id = p.cvterm_id AND tp.type_id = "
									+ TermId.CROP_ONTOLOGY_ID.getId()
									+ " LEFT JOIN (select cvtr.subject_id PropertyId, o.cv_id, o.cvterm_id, o.name, o.definition, o.is_obsolete "
									+ " from cvterm o inner join cvterm_relationship cvtr on cvtr.object_id = o.cvterm_id and cvtr.type_id = "
									+ TermId.IS_A.getId() + ")" + " cs on cs.PropertyId = p.cvterm_id" + " where p.cv_id = "
							+ CvId.PROPERTIES.getId() + filterObsoleteClause + filterClause
									+ " Group BY p.cvterm_id Order BY p.name ")
					.addScalar("pId", new org.hibernate.type.IntegerType()).addScalar("pName").addScalar("pDescription")
					.addScalar("pVocabularyId", new org.hibernate.type.IntegerType())
					.addScalar("pObsolete", new org.hibernate.type.IntegerType()).addScalar("cropOntologyId").addScalar("classes");

			if (propertyIds.size() > 0) {
				query.setParameterList("propertyIds", propertyIds);
			}

			List result = query.list();

			for (Object row : result) {
				Object[] items = (Object[]) row;

				// Check is row does have objects to access
				if (items.length == 0) {
					continue;
				}

				// Check if Property term is already added to Map. We are iterating multiple classes for property
				Property property =
						new Property(new Term((Integer) items[0], (String) items[1], (String) items[2], (Integer) items[3],
								this.typeSafeObjectToBoolean(items[4])));

				if (items[5] != null) {
					property.setCropOntologyId((String) items[5]);
				}

				if (items[6] != null) {
					String classes = (String) items[6];
					for (String c : classes.split(",")) {
						if (Strings.isNullOrEmpty(c)) {
							continue;
						}
						property.addClass(c.trim());
					}
				}

				map.put(property.getId(), property);
			}

			// Created, modified from CVTermProperty
			List propertyProp = this.getCvTermPropertyDao().getByCvId(CvId.PROPERTIES.getId());
			for (Object p : propertyProp) {
				CVTermProperty property = (CVTermProperty) p;

				Property ontologyProperty = map.get(property.getCvTermId());

				if (ontologyProperty == null) {
					continue;
				}

				if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
					ontologyProperty.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
				} else if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
					ontologyProperty.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
				}
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getProperties :" + e.getMessage(), e);
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

		CVTerm term = this.getCvTermDao().getByNameAndCvId(property.getName(), CvId.PROPERTIES.getId());

		if (term != null) {
			throw new MiddlewareException("Property exist with same name");
		}

		// Constant CvId
		property.setVocabularyId(CvId.PROPERTIES.getId());

		List<Term> allClasses = this.getCvTermDao().getTermByCvId(CvId.TRAIT_CLASS.getId());

		try {

			CVTerm propertyTerm = this.getCvTermDao().save(property.getName(), property.getDefinition(), CvId.PROPERTIES);
			property.setId(propertyTerm.getCvTermId());

			if (property.getCropOntologyId() != null) {
				this.getCvTermPropertyDao().save(property.getId(), TermId.CROP_ONTOLOGY_ID.getId(), property.getCropOntologyId(), 0);
			}

			for (String c : property.getClasses()) {

				// Discarding empty or null strings
				if (c == null) {
					continue;
				}

				c = c.trim();

				if (Strings.isNullOrEmpty(c.trim())) {
					continue;
				}

				Term classTerm = null;
				for (Term tClass : allClasses) {

					if (c.compareToIgnoreCase(tClass.getName()) != 0) {
						continue;
					}

					classTerm = tClass;
					break;
				}

				// Add new term if does not exist
				if (classTerm == null) {
					classTerm = this.addTraitClass(c);
				}

				this.getCvTermRelationshipDao().save(property.getId(), TermId.IS_A.getId(), classTerm.getId());
			}

			// Save creation time
			this.getCvTermPropertyDao().save(property.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);


		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at addProperty :" + e.getMessage(), e);
		}
	}

	@Override
	public void updateProperty(Property property) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(property.getId());

		this.checkTermIsProperty(term);

		List<Term> allClasses = this.getCvTermDao().getTermByCvId(CvId.TRAIT_CLASS.getId());

		try {

			term.setName(property.getName());
			term.setDefinition(property.getDefinition());

			this.getCvTermDao().merge(term);

			// Save or update crop ontology
			if (property.getCropOntologyId() != null) {
				this.getCvTermPropertyDao().save(property.getId(), TermId.CROP_ONTOLOGY_ID.getId(), property.getCropOntologyId(), 0);
			} else {
				CVTermProperty cropProperty =
						this.getCvTermPropertyDao().getOneByCvTermAndType(property.getId(), TermId.CROP_ONTOLOGY_ID.getId());
				if (cropProperty != null) {
					this.getCvTermPropertyDao().makeTransient(cropProperty);
				}
			}

			// Prepare list of relations
			Map<Integer, CVTermRelationship> relationsToDelete = new HashMap<>();
			List<CVTermRelationship> relationships =
					this.getCvTermRelationshipDao().getBySubjectIdAndTypeId(property.getId(), TermId.IS_A.getId());
			for (CVTermRelationship cl : relationships) {
				relationsToDelete.put(cl.getObjectId(), cl);
			}

			for (String c : property.getClasses()) {
				// Discarding empty or null strings
				if (c == null) {
					continue;
				}

				c = c.trim();

				if (Strings.isNullOrEmpty(c.trim())) {
					continue;
				}

				Term classTerm = null;

				for (Term tClass : allClasses) {

					if (c.compareToIgnoreCase(tClass.getName()) != 0) {
						continue;
					}

					classTerm = tClass;
					break;
				}

				// Add new term if does not exist
				if (classTerm == null) {
					classTerm = this.addTraitClass(c);
				}

				if (relationsToDelete.containsKey(classTerm.getId())) {
					relationsToDelete.remove(classTerm.getId());
					continue;
				}

				this.getCvTermRelationshipDao().save(property.getId(), TermId.IS_A.getId(), classTerm.getId());
			}

			// Removing old classes which are not in used
			for (CVTermRelationship cl : relationsToDelete.values()) {
				this.getCvTermRelationshipDao().makeTransient(cl);
				// Remove trait class if not in used
				this.removeTraitClass(cl.getObjectId());
			}

			// Save last modified Time
			this.getCvTermPropertyDao().save(property.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error at updateProperty" + e.getMessage(), e);
		}
	}

	@Override
	public void deleteProperty(Integer propertyId) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(propertyId);

		this.checkTermIsProperty(term);

		if (this.getCvTermRelationshipDao().isTermReferred(propertyId)) {
			throw new MiddlewareException(OntologyPropertyDataManagerImpl.PROPERTY_IS_REFERRED_TO_VARIABLE);
		}

		try {

			// Deleting existing relationships for property
			List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(propertyId);
			for (CVTermRelationship cl : relationships) {
				this.getCvTermRelationshipDao().makeTransient(cl);
				// Remove trait class if not in used
				this.removeTraitClass(cl.getObjectId());
			}

			// Deleting existing values for property
			List<CVTermProperty> properties = this.getCvTermPropertyDao().getByCvTermId(propertyId);
			for (CVTermProperty p : properties) {
				this.getCvTermPropertyDao().makeTransient(p);
			}

			this.getCvTermDao().makeTransient(term);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at deleteProperty" + e.getMessage(), e);
		}
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

		// Check weather class term exist with CV 1011.
		CVTerm classTerm = this.getCvTermDao().getByNameAndCvId(className, CvId.TRAIT_CLASS.getId());

		// If exist then don't add.
		if (classTerm == null) {
			classTerm = this.getCvTermDao().save(className, null, CvId.TRAIT_CLASS);
		}

		return Term.fromCVTerm(classTerm);
	}

	private void removeTraitClass(Integer termId) throws MiddlewareException {

		CVTerm term = this.getCvTermDao().getById(termId);

		// Validate parent class. Parent class should be from cvId as 1000
		if (term.getCv() != CvId.TRAIT_CLASS.getId()) {
			throw new MiddlewareException(OntologyPropertyDataManagerImpl.SHOULD_VALID_TRAIT_CLASS);
		}

		// Check weather term is referred
		if (this.getCvTermRelationshipDao().getByObjectId(termId).isEmpty()
				&& this.getCvTermRelationshipDao().getBySubject(termId).isEmpty()) {
			// Term is not referred anywhere and can be delete
			this.getCvTermDao().makeTransient(term);
		}

	}
}
