/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.service.api.OntologyService;

public class OntologyServiceImpl extends Service implements OntologyService {

    public OntologyServiceImpl(
            HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    /*======================= STANDARD VARIABLE ================================== */

    @Override
    public StandardVariable getStandardVariable(int stdVariableId) throws MiddlewareQueryException {
        return getOntologyDataManager().getStandardVariable(stdVariableId);
    }

    @Override
    public StandardVariable getStandardVariable(Integer propertyId, Integer scaleId, Integer methodId)
            throws MiddlewareQueryException {
        OntologyDataManager manager = getOntologyDataManager();
        Integer standardVariableId = manager.getStandardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId);
        return manager.getStandardVariable(standardVariableId);
    }

    @Override
    public List<StandardVariable> getStandardVariables(String nameOrSynonym) throws MiddlewareQueryException {
        List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();
        standardVariables.addAll(getOntologyDataManager().findStandardVariablesByNameOrSynonym(nameOrSynonym));
        return standardVariables;
    }
    
    @Override
    public void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException {
        getOntologyDataManager().addStandardVariable(stdVariable);
    }
    
    @Override
    public List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException {
        return getOntologyDataManager().getAllTermsByCvId(cvId);
    }
    
    @Override
    public Integer getStandardVariableIdByTermId(int cvTermId, TermId termId) throws MiddlewareQueryException {
        return getOntologyDataManager().getStandardVariableIdByTermId(cvTermId, termId);
    }
    
    /*======================= PROPERTY ================================== */


    @Override
    public Property getProperty(int id) throws MiddlewareQueryException {
        return getOntologyDataManager().getProperty(id);
    }

    @Override
    public Property getProperty(String name) throws MiddlewareQueryException {
        return getOntologyDataManager().getProperty(name);
    }

    @Override
    public List<Property> getAllProperties() throws MiddlewareQueryException {
        List<Property> properties = new ArrayList<Property>();
        List<Term> propertyTerms = getOntologyDataManager().getAllTermsByCvId(CvId.PROPERTIES);
        
        for (Term term : propertyTerms){
            properties.add(new Property(term));
        }
        return properties;        
    }

    @Override
    public Property addProperty(String name, String definition, int isA) throws MiddlewareQueryException {
        return new Property(getOntologyDataManager().addProperty(name, definition, isA));
    }
    

    @Override
    public Property addOrUpdateProperty(String name, String definition, int isAId) throws MiddlewareQueryException, MiddlewareException {
        return new Property(getOntologyDataManager().addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES,  TermId.IS_A.getId(), isAId),
                            getTermById(isAId));
    }
    
    
    /*======================= SCALE ================================== */

    @Override
    public Term saveProperty(int propertyId, int isA) throws MiddlewareQueryException {

        //we need to delete old isA relationship
        
        //we need to save new relationship
        Term term = getOntologyDataManager().addPropertyIsARelationship(propertyId, isA);
        return term;
    }

    @Override
    public Scale getScale(int id) throws MiddlewareQueryException {
        return new Scale(getOntologyDataManager().getTermById(id));
    }

    @Override
    public Scale getScale(String name) throws MiddlewareQueryException{
        return new Scale(getOntologyDataManager().findTermByName(name, CvId.SCALES));
    }

    
    @Override
    public List<Scale> getAllScales() throws MiddlewareQueryException {
        List<Scale> scales = new ArrayList<Scale>();
        List<Term> scaleTerms = getOntologyDataManager().getAllTermsByCvId(CvId.SCALES);
        
        for (Term term : scaleTerms){
            scales.add(new Scale(term));
        }
        return scales;        
    }
    
    @Override
    public Scale addScale(String name, String definition) throws MiddlewareQueryException {
        return new Scale(getOntologyDataManager().addTerm(name, definition, CvId.SCALES));

    }
    
    @Override
    public Scale addOrUpdateScale(String name, String definition) throws MiddlewareQueryException, MiddlewareException {
        return new Scale(getOntologyDataManager().addOrUpdateTerm(name, definition, CvId.SCALES));
    }
    

    /*======================= METHOD ================================== */
    
    @Override
    public Method getMethod(int id) throws MiddlewareQueryException {
        return new Method(getOntologyDataManager().findMethodById(id));
    }

    @Override
    public Method getMethod(String name) throws MiddlewareQueryException {
        return new Method(getOntologyDataManager().findMethodByName(name));
    }
    
    @Override
    public List<Method> getAllMethods() throws MiddlewareQueryException {
        List<Method> methods = new ArrayList<Method>();
        List<Term> methodTerms = getOntologyDataManager().getAllTermsByCvId(CvId.METHODS);
        
        for (Term term : methodTerms){
            methods.add(new Method(term));
        }
        return methods; 
    }
    
    @Override
    public Method addMethod(String name, String definition) throws MiddlewareQueryException {
        return new Method(getOntologyDataManager().addTerm(name, definition, CvId.METHODS));
    }
    
    @Override
    public Method addOrUpdateMethod(String name, String definition) throws MiddlewareQueryException, MiddlewareException {
        return new Method(getOntologyDataManager().addOrUpdateTerm(name, definition, CvId.METHODS));
    }
    

    
    /*======================= OTHERS ================================== */

    @Override
    public List<Term> getAllDataTypes() throws MiddlewareQueryException {
        return getOntologyDataManager().getDataTypes();
    }
    
    @Override
    public List<TraitReference> getTraitGroups() throws MiddlewareQueryException {
        return getOntologyDataManager().getTraitGroups();
    }

    @Override
    public List<TraitReference> getAllTraitClasses() throws MiddlewareQueryException{
        return getOntologyDataManager().getAllTraitClasses();
    }

    @Override
    public List<Term> getAllRoles() throws MiddlewareQueryException{
        List<Integer> roleIds = new ArrayList<Integer>();
        roleIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
        roleIds.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
        roleIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
        roleIds.addAll(PhenotypicType.VARIATE.getTypeStorages());
        
        return getOntologyDataManager().getTermsByIds(roleIds);
    }

    @Override
    public long countProjectsByVariable(int variableId) throws MiddlewareQueryException {
        return getStudyDataManager().countProjectsByVariable(variableId);
    }

    @Override
    public long countExperimentsByVariable(int variableId, int storedInId) throws MiddlewareQueryException {
        return getStudyDataManager().countExperimentsByVariable(variableId, storedInId);
    }
    
    @Override 
    public Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException {
        return getOntologyDataManager().addTerm(name, definition, cvId);
    }

    @Override
    public Term addTraitClass(String name, String definition) throws MiddlewareQueryException {
        return getOntologyDataManager().addTraitClass(name, definition);
    }
    
    
    @Override
    public Term addOrUpdateTraitClass(String name, String definition) throws MiddlewareQueryException, MiddlewareException {
        return getOntologyDataManager()
                .addOrUpdateTermAndRelationship(name, definition, CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
    }
    
    
    @Override
    public Term getTermById(int termId) throws MiddlewareQueryException {
        return getOntologyDataManager().getTermById(termId);
    }
    
    @Override
    public PhenotypicType getPhenotypicTypeById(Integer termId) throws MiddlewareQueryException {
        return PhenotypicType.getPhenotypicTypeById(termId);
    }
    
    @Override
    public Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException {
        return getOntologyDataManager().findTermByName(name, cvId);
    }
    
    @Override
    public Set<StandardVariable> getAllStandardVariables() throws MiddlewareQueryException {
        return getOntologyDataManager().getAllStandardVariables();
    }

    @Override
    public void saveOrUpdateStandardVariable(StandardVariable standardVariable,
            Operation operation) throws MiddlewareQueryException,
            MiddlewareException {
        
        getOntologyDataManager().saveOrUpdateStandardVariable(standardVariable, operation);
    }
}
