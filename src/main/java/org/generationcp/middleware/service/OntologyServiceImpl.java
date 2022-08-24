/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.service.api.OntologyService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyServiceImpl extends Service implements OntologyService {

	public OntologyServiceImpl() {
		super();
	}
	
	public OntologyServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	/* ======================= STANDARD VARIABLE ================================== */

	@Override
	public StandardVariable getStandardVariable(int stdVariableId, String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariable(stdVariableId,programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariables(List<Integer> standardVariableIds, String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(standardVariableIds,programUUID);
	}

	@Override
	public List<StandardVariableSummary> getStandardVariableSummaries(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getStandardVariableSummaries(standardVariableIds);
	}

	@Override
	public StandardVariable getStandardVariable(Integer propertyId, Integer scaleId, Integer methodId, String programUUID) throws MiddlewareException {
		OntologyDataManager manager = this.getOntologyDataManager();
		Integer standardVariableId = manager
				.getStandardVariableIdByPropertyIdScaleIdMethodId(propertyId,
						scaleId, methodId);
		return manager.getStandardVariable(standardVariableId,programUUID);
		
	}

	@Override
	public List<StandardVariable> getStandardVariables(String nameOrSynonym,String programUUID) throws MiddlewareException {
		List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();
		standardVariables.addAll(this.getOntologyDataManager().findStandardVariablesByNameOrSynonym(nameOrSynonym,programUUID));
		return standardVariables;
	}

	@Override
	public void addStandardVariable(StandardVariable stdVariable,String programUUID) throws MiddlewareQueryException {
		this.getOntologyDataManager().addStandardVariable(stdVariable,programUUID);
	}

	@Override
	public List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getAllTermsByCvId(cvId);
	}

	@Override
	public Integer getStandardVariableIdByTermId(int cvTermId, TermId termId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getStandardVariableIdByTermId(cvTermId, termId);
	}

	@Override
	public Set<StandardVariable> getAllStandardVariables(String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getAllStandardVariables(programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByTraitClass(Integer traitClassId,String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(traitClassId, null, null, null, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByProperty(Integer propertyId, String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(null, propertyId, null, null, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByMethod(Integer methodId, String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(null, null, methodId, null, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByScale(Integer scaleId, String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(null, null, null, scaleId, programUUID);
	}

	@Override
	public void saveOrUpdateStandardVariable(StandardVariable standardVariable, Operation operation) throws MiddlewareException {
		this.getOntologyDataManager().saveOrUpdateStandardVariable(standardVariable, operation);
	}

	@Override
	public void deleteStandardVariable(int stdVariableId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteStandardVariable(stdVariableId);
	}

	@Override
	public void addOrUpdateStandardVariableMinMaxConstraints(int standardVariableId, VariableConstraints constraints)
			throws MiddlewareException {
		this.getOntologyDataManager().addOrUpdateStandardVariableConstraints(standardVariableId, constraints);
	}

	@Override
	public void deleteStandardVariableMinMaxConstraints(int standardVariableId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteStandardVariableLocalConstraints(standardVariableId);
	}

	@Override
	public Enumeration addStandardVariableValidValue(StandardVariable variable, Enumeration validValue) throws MiddlewareException {
		return this.getOntologyDataManager().addStandardVariableEnumeration(variable, validValue);
	}

	@Override
	public void deleteStandardVariableValidValue(int standardVariableId, int validValueId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteStandardVariableEnumeration(standardVariableId, validValueId);
	}

	@Override
	public void saveOrUpdateStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException {
		this.getOntologyDataManager().saveOrUpdateStandardVariableEnumeration(variable, enumeration);
	}

	/* ======================= PROPERTY ================================== */

	@Override
	public Property getProperty(int id) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getProperty(id);
	}

	@Override
	public Property getProperty(String name) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getProperty(name);
	}

	@Override
	public List<Property> getAllProperties() throws MiddlewareQueryException {
		List<Property> properties = new ArrayList<Property>();
		List<Term> propertyTerms = this.getOntologyDataManager().getAllTermsByCvId(CvId.PROPERTIES);

		for (Term term : propertyTerms) {
			properties.add(new Property(term));
		}
		return properties;
	}

	@Override
	public Property addProperty(String name, String definition, int isA) throws MiddlewareQueryException {
		return new Property(this.getOntologyDataManager().addProperty(name, definition, isA));
	}

	@Override
	public Property addOrUpdateProperty(String name, String definition, int isAId, String cropOntologyId) throws MiddlewareException {

		Property property =
				new Property(this.getOntologyDataManager().addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES,
						TermId.IS_A.getId(), isAId), this.getTermById(isAId));
		this.getOntologyDataManager().addOrUpdateCropOntologyID(property, cropOntologyId);

		return property;
	}

	@Override
	public void updateProperty(Property property) throws MiddlewareException {
		this.getOntologyDataManager().updateTermAndRelationship(property.getTerm(), TermId.IS_A.getId(), property.getIsA().getId());
	}

	@Override
	public void deleteProperty(int cvTermId, int isAId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTermAndRelationship(cvTermId, CvId.PROPERTIES, TermId.IS_A.getId(), isAId);
	}

	/* ======================= SCALE ================================== */

	@Override
	public Scale getScale(int id) throws MiddlewareQueryException {
		Term scaleTerm = this.getOntologyDataManager().getTermById(id);
		return scaleTerm != null && scaleTerm.getVocabularyId() == CvId.SCALES.getId() ? new Scale(scaleTerm) : null;
	}

	@Override
	public Scale getScale(String name) throws MiddlewareQueryException {
		return new Scale(this.getOntologyDataManager().findTermByName(name, CvId.SCALES));
	}

	@Override
	public List<Scale> getAllScales() throws MiddlewareQueryException {
		List<Scale> scales = new ArrayList<Scale>();
		List<Term> scaleTerms = this.getOntologyDataManager().getAllTermsByCvId(CvId.SCALES);

		for (Term term : scaleTerms) {
			scales.add(new Scale(term));
		}
		return scales;
	}

	@Override
	public Scale addScale(String name, String definition) throws MiddlewareQueryException {
		return new Scale(this.getOntologyDataManager().addTerm(name, definition, CvId.SCALES));

	}

	@Override
	public Scale addOrUpdateScale(String name, String definition) throws MiddlewareException {
		return new Scale(this.getOntologyDataManager().addOrUpdateTerm(name, definition, CvId.SCALES));
	}

	@Override
	public void updateScale(Scale scale) throws MiddlewareException {
		this.getOntologyDataManager().updateTerm(scale.getTerm());
	}

	@Override
	public void deleteScale(int cvTermId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTerm(cvTermId, CvId.SCALES);
	}

	/* ======================= METHOD ================================== */

	@Override
	public Method getMethod(int id) throws MiddlewareQueryException {
		Term methodTerm = this.getOntologyDataManager().findMethodById(id);
		return methodTerm != null && methodTerm.getVocabularyId() == CvId.METHODS.getId() ? new Method(methodTerm) : null;
	}

	@Override
	public Method getMethod(String name) throws MiddlewareQueryException {
		return new Method(this.getOntologyDataManager().findMethodByName(name));
	}

	@Override
	public List<Method> getAllMethods() throws MiddlewareQueryException {
		List<Method> methods = new ArrayList<Method>();
		List<Term> methodTerms = this.getOntologyDataManager().getAllTermsByCvId(CvId.METHODS);

		for (Term term : methodTerms) {
			methods.add(new Method(term));
		}
		return methods;
	}

	@Override
	public Method addMethod(String name, String definition) throws MiddlewareQueryException {
		return new Method(this.getOntologyDataManager().addTerm(name, definition, CvId.METHODS));
	}

	@Override
	public Method addOrUpdateMethod(String name, String definition) throws MiddlewareException {
		return new Method(this.getOntologyDataManager().addOrUpdateTerm(name, definition, CvId.METHODS));
	}

	@Override
	public void updateMethod(Method method) throws MiddlewareException {
		this.getOntologyDataManager().updateTerm(method.getTerm());
	}

	@Override
	public void deleteMethod(int cvTermId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTerm(cvTermId, CvId.METHODS);
	}

	/* ======================= OTHERS ================================== */

	@Override
	public List<Term> getAllDataTypes() throws MiddlewareQueryException {
		return this.getOntologyDataManager().getDataTypes();
	}

	@Override
	public List<TraitClassReference> getAllTraitGroupsHierarchy(boolean includePropertiesAndVariable) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getAllTraitGroupsHierarchy(includePropertiesAndVariable);
	}

	@Override
	public List<Term> getAllRoles() throws MiddlewareQueryException {
		List<Integer> roleIds = new ArrayList<Integer>();
		roleIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
		roleIds.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
		roleIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
		roleIds.addAll(PhenotypicType.VARIATE.getTypeStorages());

		return this.getOntologyDataManager().getTermsByIds(roleIds);
	}

	@Override
	public Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().addTerm(name, definition, cvId);
	}

	@Override
	public TraitClass addTraitClass(String name, String definition, int parentTraitClassId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().addTraitClass(name, definition, parentTraitClassId);
	}

	@Override
	public TraitClass addOrUpdateTraitClass(String name, String definition, int parentTraitClassId) throws MiddlewareException {
		Term term =
				this.getOntologyDataManager().addOrUpdateTermAndRelationship(name, definition, CvId.IBDB_TERMS, TermId.IS_A.getId(),
						parentTraitClassId);
		Term isA = this.getOntologyDataManager().getTermById(parentTraitClassId);
		return new TraitClass(term, isA);
	}

	@Override
	public TraitClass updateTraitClass(TraitClass traitClass) throws MiddlewareException {
		Term term =
				this.getOntologyDataManager().updateTermAndRelationship(traitClass.getTerm(), TermId.IS_A.getId(),
						traitClass.getIsA().getId());
		Term isA = this.getOntologyDataManager().getTermById(traitClass.getIsA().getId());
		return new TraitClass(term, isA);
	}

	@Override
	public void deleteTraitClass(int cvTermId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTermAndRelationship(cvTermId, CvId.IBDB_TERMS, TermId.IS_A.getId(),
				TermId.ONTOLOGY_TRAIT_CLASS.getId());
	}

	@Override
	public Term getTermById(int termId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getTermById(termId);
	}

	@Override
	public PhenotypicType getPhenotypicTypeById(Integer termId) throws MiddlewareQueryException {
		return PhenotypicType.getPhenotypicTypeById(termId);
	}

	@Override
	public Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().findTermByName(name, cvId);
	}

	@Override
	public List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException {
		return this.getOntologyDataManager().getAllPropertiesWithTraitClass();
	}

	@Override
	public boolean validateDeleteStandardVariableEnumeration(int standardVariableId, int enumerationId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().validateDeleteStandardVariableEnumeration(standardVariableId, enumerationId);
	}

	@Override
	public List<Scale> getAllInventoryScales() throws MiddlewareQueryException {
		return this.getTermBuilder().getAllInventoryScales();
	}
	
	@Override
	public Scale getInventoryScaleByName(final String name) throws MiddlewareQueryException {
		return this.getTermBuilder().getInventoryScaleByName(name);
	}

	/**
	 * Get All distinct values given a standard variable id.
	 *
	 * @param stdVarId the std var id
	 * @return the distinct standard variable values
	 * @throws MiddlewareQueryException the middleware query exception
	 */

	@Override
	public List<ValueReference> getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException {
		return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
	}
}
