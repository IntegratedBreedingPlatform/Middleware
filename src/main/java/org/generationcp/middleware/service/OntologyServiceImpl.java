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
	
	public OntologyServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	/* ======================= STANDARD VARIABLE ================================== */

	@Override
	public StandardVariable getStandardVariable(final int stdVariableId, final String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariable(stdVariableId,programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariables(final List<Integer> standardVariableIds, final String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(standardVariableIds,programUUID);
	}

	@Override
	public List<StandardVariableSummary> getStandardVariableSummaries(final List<Integer> standardVariableIds) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getStandardVariableSummaries(standardVariableIds);
	}

	@Override
	public StandardVariable getStandardVariable(final Integer propertyId, final Integer scaleId, final Integer methodId, final String programUUID) throws MiddlewareException {
		final OntologyDataManager manager = this.getOntologyDataManager();
		final Integer standardVariableId = manager
				.getStandardVariableIdByPropertyIdScaleIdMethodId(propertyId,
						scaleId, methodId);
		return manager.getStandardVariable(standardVariableId,programUUID);
		
	}

	@Override
	public List<StandardVariable> getStandardVariables(final String nameOrSynonym, final String programUUID) throws MiddlewareException {
		final List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();
		standardVariables.addAll(this.getOntologyDataManager().findStandardVariablesByNameOrSynonym(nameOrSynonym,programUUID));
		return standardVariables;
	}

	@Override
	public void addStandardVariable(final StandardVariable stdVariable, final String programUUID) throws MiddlewareQueryException {
		this.getOntologyDataManager().addStandardVariable(stdVariable,programUUID);
	}

	@Override
	public List<Term> getAllTermsByCvId(final CvId cvId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getAllTermsByCvId(cvId);
	}

	@Override
	public Integer getStandardVariableIdByTermId(final int cvTermId, final TermId termId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getStandardVariableIdByTermId(cvTermId, termId);
	}

	@Override
	public Set<StandardVariable> getAllStandardVariables(final String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getAllStandardVariables(programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByTraitClass(final Integer traitClassId, final String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(traitClassId, null, null, null, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByProperty(final Integer propertyId, final String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(null, propertyId, null, null, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByMethod(final Integer methodId, final String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(null, null, methodId, null, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariablesByScale(final Integer scaleId, final String programUUID) throws MiddlewareException {
		return this.getOntologyDataManager().getStandardVariables(null, null, null, scaleId, programUUID);
	}

	@Override
	public void saveOrUpdateStandardVariable(final StandardVariable standardVariable, final Operation operation) throws MiddlewareException {
		this.getOntologyDataManager().saveOrUpdateStandardVariable(standardVariable, operation);
	}

	@Override
	public void deleteStandardVariable(final int stdVariableId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteStandardVariable(stdVariableId);
	}

	@Override
	public void addOrUpdateStandardVariableMinMaxConstraints(final int standardVariableId, final VariableConstraints constraints)
			throws MiddlewareException {
		this.getOntologyDataManager().addOrUpdateStandardVariableConstraints(standardVariableId, constraints);
	}

	@Override
	public void deleteStandardVariableMinMaxConstraints(final int standardVariableId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteStandardVariableLocalConstraints(standardVariableId);
	}

	@Override
	public Enumeration addStandardVariableValidValue(final StandardVariable variable, final Enumeration validValue) throws MiddlewareException {
		return this.getOntologyDataManager().addStandardVariableEnumeration(variable, validValue);
	}

	@Override
	public void deleteStandardVariableValidValue(final int standardVariableId, final int validValueId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteStandardVariableEnumeration(standardVariableId, validValueId);
	}

	@Override
	public void saveOrUpdateStandardVariableEnumeration(final StandardVariable variable, final Enumeration enumeration) throws MiddlewareException {
		this.getOntologyDataManager().saveOrUpdateStandardVariableEnumeration(variable, enumeration);
	}

	/* ======================= PROPERTY ================================== */

	@Override
	public Property getProperty(final int id) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getProperty(id);
	}

	@Override
	public Property getProperty(final String name) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getProperty(name);
	}

	@Override
	public List<Property> getAllProperties() throws MiddlewareQueryException {
		final List<Property> properties = new ArrayList<Property>();
		final List<Term> propertyTerms = this.getOntologyDataManager().getAllTermsByCvId(CvId.PROPERTIES);

		for (final Term term : propertyTerms) {
			properties.add(new Property(term));
		}
		return properties;
	}

	@Override
	public Property addProperty(final String name, final String definition, final int isA) throws MiddlewareQueryException {
		return new Property(this.getOntologyDataManager().addProperty(name, definition, isA));
	}

	@Override
	public Property addOrUpdateProperty(final String name, final String definition, final int isAId, final String cropOntologyId) throws MiddlewareException {

		final Property property =
				new Property(this.getOntologyDataManager().addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES,
						TermId.IS_A.getId(), isAId), this.getTermById(isAId));
		this.getOntologyDataManager().addOrUpdateCropOntologyID(property, cropOntologyId);

		return property;
	}

	@Override
	public void updateProperty(final Property property) throws MiddlewareException {
		this.getOntologyDataManager().updateTermAndRelationship(property.getTerm(), TermId.IS_A.getId(), property.getIsA().getId());
	}

	@Override
	public void deleteProperty(final int cvTermId, final int isAId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTermAndRelationship(cvTermId, CvId.PROPERTIES, TermId.IS_A.getId(), isAId);
	}

	/* ======================= SCALE ================================== */

	@Override
	public Scale getScale(final int id) throws MiddlewareQueryException {
		final Term scaleTerm = this.getOntologyDataManager().getTermById(id);
		return scaleTerm != null && scaleTerm.getVocabularyId() == CvId.SCALES.getId() ? new Scale(scaleTerm) : null;
	}

	@Override
	public Scale getScale(final String name) throws MiddlewareQueryException {
		return new Scale(this.getOntologyDataManager().findTermByName(name, CvId.SCALES));
	}

	@Override
	public List<Scale> getAllScales() throws MiddlewareQueryException {
		final List<Scale> scales = new ArrayList<Scale>();
		final List<Term> scaleTerms = this.getOntologyDataManager().getAllTermsByCvId(CvId.SCALES);

		for (final Term term : scaleTerms) {
			scales.add(new Scale(term));
		}
		return scales;
	}

	@Override
	public Scale addScale(final String name, final String definition) throws MiddlewareQueryException {
		return new Scale(this.getOntologyDataManager().addTerm(name, definition, CvId.SCALES));

	}

	@Override
	public Scale addOrUpdateScale(final String name, final String definition) throws MiddlewareException {
		return new Scale(this.getOntologyDataManager().addOrUpdateTerm(name, definition, CvId.SCALES));
	}

	@Override
	public void updateScale(final Scale scale) throws MiddlewareException {
		this.getOntologyDataManager().updateTerm(scale.getTerm());
	}

	@Override
	public void deleteScale(final int cvTermId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTerm(cvTermId, CvId.SCALES);
	}

	/* ======================= METHOD ================================== */

	@Override
	public Method getMethod(final int id) throws MiddlewareQueryException {
		final Term methodTerm = this.getOntologyDataManager().findMethodById(id);
		return methodTerm != null && methodTerm.getVocabularyId() == CvId.METHODS.getId() ? new Method(methodTerm) : null;
	}

	@Override
	public Method getMethod(final String name) throws MiddlewareQueryException {
		return new Method(this.getOntologyDataManager().findMethodByName(name));
	}

	@Override
	public List<Method> getAllMethods() throws MiddlewareQueryException {
		final List<Method> methods = new ArrayList<Method>();
		final List<Term> methodTerms = this.getOntologyDataManager().getAllTermsByCvId(CvId.METHODS);

		for (final Term term : methodTerms) {
			methods.add(new Method(term));
		}
		return methods;
	}

	@Override
	public Method addMethod(final String name, final String definition) throws MiddlewareQueryException {
		return new Method(this.getOntologyDataManager().addTerm(name, definition, CvId.METHODS));
	}

	@Override
	public Method addOrUpdateMethod(final String name, final String definition) throws MiddlewareException {
		return new Method(this.getOntologyDataManager().addOrUpdateTerm(name, definition, CvId.METHODS));
	}

	@Override
	public void updateMethod(final Method method) throws MiddlewareException {
		this.getOntologyDataManager().updateTerm(method.getTerm());
	}

	@Override
	public void deleteMethod(final int cvTermId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTerm(cvTermId, CvId.METHODS);
	}

	/* ======================= OTHERS ================================== */

	@Override
	public List<Term> getAllDataTypes() throws MiddlewareQueryException {
		return this.getOntologyDataManager().getDataTypes();
	}

	@Override
	public List<TraitClassReference> getAllTraitGroupsHierarchy(final boolean includePropertiesAndVariable) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getAllTraitGroupsHierarchy(includePropertiesAndVariable);
	}

	@Override
	public List<Term> getAllRoles() throws MiddlewareQueryException {
		final List<Integer> roleIds = new ArrayList<Integer>();
		roleIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
		roleIds.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
		roleIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
		roleIds.addAll(PhenotypicType.VARIATE.getTypeStorages());

		return this.getOntologyDataManager().getTermsByIds(roleIds);
	}

	@Override
	public Term addTerm(final String name, final String definition, final CvId cvId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().addTerm(name, definition, cvId);
	}

	@Override
	public TraitClass addTraitClass(final String name, final String definition, final int parentTraitClassId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().addTraitClass(name, definition, parentTraitClassId);
	}

	@Override
	public TraitClass addOrUpdateTraitClass(final String name, final String definition, final int parentTraitClassId) throws MiddlewareException {
		final Term term =
				this.getOntologyDataManager().addOrUpdateTermAndRelationship(name, definition, CvId.IBDB_TERMS, TermId.IS_A.getId(),
						parentTraitClassId);
		final Term isA = this.getOntologyDataManager().getTermById(parentTraitClassId);
		return new TraitClass(term, isA);
	}

	@Override
	public TraitClass updateTraitClass(final TraitClass traitClass) throws MiddlewareException {
		final Term term =
				this.getOntologyDataManager().updateTermAndRelationship(traitClass.getTerm(), TermId.IS_A.getId(),
						traitClass.getIsA().getId());
		final Term isA = this.getOntologyDataManager().getTermById(traitClass.getIsA().getId());
		return new TraitClass(term, isA);
	}

	@Override
	public void deleteTraitClass(final int cvTermId) throws MiddlewareQueryException {
		this.getOntologyDataManager().deleteTermAndRelationship(cvTermId, CvId.IBDB_TERMS, TermId.IS_A.getId(),
				TermId.ONTOLOGY_TRAIT_CLASS.getId());
	}

	@Override
	public Term getTermById(final int termId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getTermById(termId);
	}

	@Override
	public PhenotypicType getPhenotypicTypeById(final Integer termId) throws MiddlewareQueryException {
		return PhenotypicType.getPhenotypicTypeById(termId);
	}

	@Override
	public Term findTermByName(final String name, final CvId cvId) throws MiddlewareQueryException {
		return this.getOntologyDataManager().findTermByName(name, cvId);
	}

	@Override
	public List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException {
		return this.getOntologyDataManager().getAllPropertiesWithTraitClass();
	}

	@Override
	public boolean validateDeleteStandardVariableEnumeration(final int standardVariableId, final int enumerationId) throws MiddlewareQueryException {
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
	public List<ValueReference> getDistinctStandardVariableValues(final int stdVarId) throws MiddlewareQueryException {
		return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
	}
}
