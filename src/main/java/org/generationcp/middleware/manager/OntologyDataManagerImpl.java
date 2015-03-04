/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.PropertyReference;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntologyDataManagerImpl extends DataManager implements OntologyDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(OntologyDataManagerImpl.class);

	public static final String DELETE_TERM_ERROR_MESSAGE = "The term you selected cannot be deleted";

    public OntologyDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public Term getTermById(int termId) throws MiddlewareQueryException {
        return getTermBuilder().get(termId);
    }

    @Override
    public StandardVariable getStandardVariable(int stdVariableId) throws MiddlewareQueryException {
        return getStandardVariableBuilder().create(stdVariableId);
    }
    
	@Override
	public List<StandardVariable> getStandardVariables(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		return getStandardVariableBuilder().create(standardVariableIds);
	}
	
	@Override
	public List<StandardVariableSummary> getStandardVariableSummaries(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		return getStandardVariableBuilder().getStandardVariableSummaries(standardVariableIds);
	}
	
	@Override
	public StandardVariableSummary getStandardVariableSummary(Integer standardVariableId) throws MiddlewareQueryException {
		return getStandardVariableBuilder().getStandardVariableSummary(standardVariableId);
	}

    @Override
    public void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;
        
        Term existingStdVar = findTermByName(stdVariable.getName(), CvId.VARIABLES);
        if (existingStdVar != null){
        	 throw new MiddlewareQueryException(String.format("Error in addStandardVariable, Variable with name \"%s\" already exists", stdVariable.getName()));
        }

        try {

            trans = session.beginTransaction();
            // check if scale, property and method exists first
        	Term scale = findTermByName(stdVariable.getScale().getName(), CvId.SCALES);
            if (scale == null) {
                stdVariable.setScale(getTermSaver().save(stdVariable.getScale().getName(),
                        stdVariable.getScale().getDefinition(), CvId.SCALES));
                if (LOG.isDebugEnabled()){
                    LOG.debug("new scale with id = " + stdVariable.getScale().getId());
                }
            }
	            
            Term property = findTermByName(stdVariable.getProperty().getName(), CvId.PROPERTIES);
            if (property == null) {
                stdVariable.setProperty(getTermSaver().save(stdVariable.getProperty().getName(),
                stdVariable.getProperty().getDefinition(), CvId.PROPERTIES));
                if (LOG.isDebugEnabled()){
                	LOG.debug("new property with id = " + stdVariable.getProperty().getId());
            	}
            }
            
            Term method = findTermByName(stdVariable.getMethod().getName(), CvId.METHODS);
            if (method == null) {
            	stdVariable.setMethod(getTermSaver().save(stdVariable.getMethod().getName(),
            	stdVariable.getMethod().getDefinition(), CvId.METHODS));
            	if (LOG.isDebugEnabled()){
            		LOG.debug("new method with id = " + stdVariable.getMethod().getId());
            	}
            }
            
            if (findStandardVariableByTraitScaleMethodNames(stdVariable.getProperty().getName(), stdVariable.getScale()
                    .getName(), stdVariable.getMethod().getName()) == null) {
                getStandardVariableSaver().save(stdVariable);
            }
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addStandardVariable " + e.getMessage(), e);
        }
    }
    
    @Override
    public void addStandardVariable(List<StandardVariable> stdVariableList) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = session.beginTransaction();

        try {

        	 for (StandardVariable stdVariable : stdVariableList){getStandardVariableSaver().save(stdVariable);
        	 }
            
            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addStandardVariable " + e.getMessage(), e);
        }
        
        
    }

    @Deprecated
    @Override
    public Term addMethod(String name, String definition) throws MiddlewareQueryException {
        return addTerm(name, definition, CvId.METHODS);
    }

    @Override
    public Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym)
            throws MiddlewareQueryException {
        Set<StandardVariable> standardVariables = new HashSet<>();
        standardVariables.addAll(getStandardVariablesByNameOrSynonym(nameOrSynonym));
        return standardVariables;
    }

    private Set<StandardVariable> getStandardVariablesByNameOrSynonym(String nameOrSynonym)
            throws MiddlewareQueryException {
        Set<StandardVariable> standardVariables = new HashSet<>();
        List<Integer> stdVarIds = getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, CvId.VARIABLES.getId());
        for (Integer stdVarId : stdVarIds) {
            standardVariables.add(getStandardVariable(stdVarId));
        }
        return standardVariables;
    }

    @Override
    public Term findMethodById(int id) throws MiddlewareQueryException {
        return getMethodBuilder().findMethodById(id);
    }

    @Override
    public Term findMethodByName(String name) throws MiddlewareQueryException {
        return getMethodBuilder().findMethodByName(name);
    }

    @Override
    public Integer getStandardVariableIdByPropertyScaleMethod(Integer propertyId, Integer scaleId, Integer methodId)
            throws MiddlewareQueryException {
        return getStandardVariableBuilder().getIdByPropertyScaleMethod(propertyId, scaleId, methodId);
    }

    @Override
    public StandardVariable findStandardVariableByTraitScaleMethodNames(String property, String scale, String method)
            throws MiddlewareQueryException {
        Term termProperty, termScale, termMethod;
        Integer propertyId = null, scaleId = null, methodId = null;

        termProperty = findTermByName(property, CvId.PROPERTIES);
        termScale = findTermByName(scale, CvId.SCALES);
        termMethod = findTermByName(method, CvId.METHODS);

        if (termProperty != null) {
            propertyId = termProperty.getId();
        }

        if (termScale != null) {
            scaleId = termScale.getId();
        }

        if (termMethod != null) {
            methodId = termMethod.getId();
        }

        return getStandardVariableBuilder().getByPropertyScaleMethod(propertyId, scaleId, methodId);
    }

    @Override
    public List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException {
        return getTermBuilder().getTermsByCvId(cvId);
    }

    @Override
    public long countTermsByCvId(CvId cvId) throws MiddlewareQueryException {
        return getCvTermDao().countTermsByCvId(cvId);
    }

    @Override
    public List<Term> getMethodsForTrait(Integer traitId) throws MiddlewareQueryException {
        List<Term> methodTerms = new ArrayList<>();
        Set<Integer> methodIds = new HashSet<>();
        List<Integer> localMethodIds = getCvTermDao().findMethodTermIdsByTrait(traitId);
        if (localMethodIds != null) {
            methodIds.addAll(localMethodIds);
        }
        for (Integer termId : methodIds) {
            methodTerms.add(getTermBuilder().get(termId));
        }
        return methodTerms;
    }

    @Override
    public List<Term> getScalesForTrait(Integer traitId) throws MiddlewareQueryException {
        List<Term> scaleTerms = new ArrayList<>();
        Set<Integer> scaleIds = new HashSet<>();
        List<Integer> localMethodIds = getCvTermDao().findScaleTermIdsByTrait(traitId);
        if (localMethodIds != null) {
            scaleIds.addAll(localMethodIds);
        }
        for (Integer termId : scaleIds) {
            scaleTerms.add(getTermBuilder().get(termId));
        }
        return scaleTerms;
    }

    @Override
    public Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException {
        return getTermBuilder().findTermByName(name, cvId);
    }

    @Override
    public Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException {
        Term term = findTermByName(name, cvId);

        if (term != null) {
            return term;
        }

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            if (CvId.VARIABLES.getId() != cvId.getId()) {
                trans = session.beginTransaction();
                term = getTermSaver().save(name, definition, cvId);
                trans.commit();
            } else {
                throw new MiddlewareQueryException("Variables cannot be used in this method.");
            }
            return term;
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addTerm: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updateTerm(Term term) throws MiddlewareException, MiddlewareQueryException{       
        
        Session session = getCurrentSession();
        Transaction trans = null;

        if (term == null){
        	return;
        }
        
        try {
            trans = session.beginTransaction();
            getTermSaver().update(term);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in updateTerm: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Term> getDataTypes() throws MiddlewareQueryException {
        List<Integer> dataTypeIds = Arrays.asList(TermId.CLASS.getId(), TermId.NUMERIC_VARIABLE.getId(),
                TermId.DATE_VARIABLE.getId(), TermId.NUMERIC_DBID_VARIABLE.getId(),
                TermId.CHARACTER_DBID_VARIABLE.getId(), TermId.CHARACTER_VARIABLE.getId(),
                TermId.TIMESTAMP_VARIABLE.getId(), TermId.CATEGORICAL_VARIABLE.getId());
        return getTermBuilder().getTermsByIds(dataTypeIds);
    }

    @Override
    public Map<String, StandardVariable> getStandardVariablesForPhenotypicType(PhenotypicType type, int start,
            int numOfRows) throws MiddlewareQueryException {

        TreeMap<String, StandardVariable> standardVariables = new TreeMap<>();
        List<Integer> localStdVariableIds = getCvTermDao().getStandardVariableIdsByPhenotypicType(type);

        for (Integer stdVarId : localStdVariableIds) {
            StandardVariable sd = getStandardVariableBuilder().create(stdVarId);
            standardVariables.put(sd.getName(), sd);
        }

        Set<String> standardVariablesSet = standardVariables.keySet();
        Object[] list = standardVariablesSet.toArray();

        String startSD = list[start].toString();

        int end = ((start + numOfRows) > list.length - 1) ? list.length - 1 : (start + numOfRows);
        String endSD = list[end].toString();

        return standardVariables.subMap(startSD, true, endSD, true);
    }

    @Override
    public Map<String, List<StandardVariable>> getStandardVariablesInProjects(List<String> headers)
            throws MiddlewareQueryException {
        return getStandardVariableBuilder().getStandardVariablesInProjects(headers);
    }

    @Override
    public List<Term> findTermsByNameOrSynonym(String nameOrSynonym, CvId cvId) throws MiddlewareQueryException {
        List<Term> terms = new ArrayList<>();
        List<CVTerm> cvTerms = new ArrayList<>();
        List<Integer> termIds = getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, cvId.getId());
        for (Integer id : termIds) {
            cvTerms.add(getCvTermDao().getById(id));
        }
        for (CVTerm cvTerm : cvTerms) {
            terms.add(TermBuilder.mapCVTermToTerm(cvTerm));
        }
        return terms;
    }

    @Override
    public Term addProperty(String name, String definition, int isA) throws MiddlewareQueryException {

        Term term = findTermByName(name, CvId.PROPERTIES);

        if (term != null) {
            return term;
        }

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            term = getTermSaver().save(name, definition, CvId.PROPERTIES);
            Term isATerm = getTermById(isA);

            if (isATerm != null) {
                getTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), isATerm.getId());
            } else {
                throw new MiddlewareQueryException("The isA passed is not a valid Class term: " + isA);
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addProperty " + e.getMessage(), e);
        }

		return term;
	}

    @Override
    public Property addProperty(String name, String definition, String cropOntologyId, List<String> classes) throws MiddlewareQueryException{
        Term term = findTermByName(name, CvId.PROPERTIES);
        if(term != null) throw new MiddlewareQueryException("Property is already available with name: " + name);

        Property p;

        Session session = getCurrentSession();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction();
            term = getTermSaver().save(name, definition, CvId.PROPERTIES);
            p = new Property(term);
            
            if (cropOntologyId != null) {
                p.setCropOntologyId(cropOntologyId);
                getStandardVariableSaver().saveOrUpdateCropOntologyId(term.getId(), cropOntologyId);
            }
            
            List<Term> allClasses = getAllTraitClass();

            for (String sClass : classes) {
                for (Term tClass : allClasses) {
                    if (!sClass.equals(tClass.getName())) continue;
                    getTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), tClass.getId());
                    p.addClass(tClass);
                    break;
                }
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addProperty " + e.getMessage(), e);
        }

        return p;
    }
        
    @Override
    public Term addOrUpdateTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException, MiddlewareException{
        Session session = getCurrentSession();
        Transaction trans = null;
        
        Term term;
        
        try {
            trans = session.beginTransaction();
            term = saveOrUpdateCvTerm(name, definition, cvId);
            trans.commit();
        } catch (MiddlewareQueryException e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException(e.getCode(), e);
        }

        return term;
    }
    
    @Override
    public Term addOrUpdateTermAndRelationship(String name, String definition, CvId cvId, int typeId, int objectId, String cropOntologyId) 
            throws MiddlewareQueryException, MiddlewareException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Term term = findTermByName(name, cvId);        
        
        try {
            trans = session.beginTransaction();
            term = saveOrUpdateCvTerm(name, definition, cvId);
            saveOrUpdateCvTermRelationship(term.getId(), objectId, typeId);
            
            if (cropOntologyId != null) {
                getStandardVariableSaver().saveOrUpdateCropOntologyId(term.getId(), cropOntologyId);
            }
            
            trans.commit();
        } catch (MiddlewareQueryException e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException(e.getCode(), e);
        }

        return term;
    }

    public Term updateTermAndRelationship(Term term, int typeId, int objectId) throws MiddlewareQueryException, MiddlewareException{

        if (term == null){
            return null;
        }
        
        Session session = getCurrentSession();
        Transaction trans = null;
		Term updatedTerm;

        try {
            trans = session.beginTransaction();
            
            updatedTerm = getTermSaver().update(term);
            
            saveOrUpdateCvTermRelationship(updatedTerm.getId(), objectId, typeId);
            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in updateTerm: " + e.getMessage(), e);
        }
        
        return updatedTerm;
    }

	private Term saveOrUpdateCvTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException, MiddlewareException{
        Term term = findTermByName(name, cvId);

		// If term is not existing, add
        if (term == null){
            term = getTermSaver().save(name, definition, cvId);
		// If term is existing, update
        } else {
            term = getTermSaver().saveOrUpdate(name, definition, cvId);
        }
        return term;
	}
	
	private void saveOrUpdateCvTermRelationship(int subjectId, int objectId, int typeId) throws MiddlewareQueryException, MiddlewareException{
        Term typeTerm = getTermById(typeId);
        if (typeTerm != null) {
            CVTermRelationship cvRelationship = getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
			// add the relationship
            if(cvRelationship == null){
                getTermRelationshipSaver().save(subjectId, typeId, objectId);
			// update the existing relationship
            }else{                
                cvRelationship.setObjectId(objectId);
                getTermRelationshipSaver().saveOrUpdateRelationship(cvRelationship);
            }
        } else {
            throw new MiddlewareException("Error in saveOrUpdateCvTermRelationship: The relationship type passed is not a valid value.");
        }
	}
	
    @Override
	public Property getProperty(int termId) throws MiddlewareQueryException {
		Property property = new Property();
		
		property.setTerm(getTermBuilder().getTermOfProperty(termId, CvId.PROPERTIES.getId()));
		if(property.getTerm()!=null) {
			property.setIsA(getTermBuilder().getTermOfClassOfProperty(termId, CvId.PROPERTIES.getId(), TermId.IS_A.getId()));
			property.setCropOntologyId(getStandardVariableBuilder().getCropOntologyId(property.getTerm()));
		}
		return property;
	}

    @Override
    public Property getPropertyById(int propertyId) throws MiddlewareQueryException {
        return getPropertyDao().getPropertyById(propertyId);
    }

    @Override
    public List<Property> getAllProperties() throws MiddlewareQueryException {
        return getPropertyDao().getAllProperties();
    }

    @Override
    public List<Property> getAllPropertiesWithClass(String className) throws MiddlewareQueryException {
        return getPropertyDao().getAllPropertiesWithClass(className);
    }

    @Override
    public List<Property> getAllPropertiesWithClasses(List<String> classes) throws MiddlewareQueryException {
        return getPropertyDao().getAllPropertiesWithClasses(classes);
    }

    @Override
    public List<Property> searchProperties(String filter) throws MiddlewareQueryException {
        return getPropertyDao().searchProperties(filter);
    }

    @Override
	public Property getProperty(String name) throws MiddlewareQueryException {
		Property property = new Property();
		
		property.setTerm(findTermByName(name, CvId.PROPERTIES));
		if(property.getTerm()!=null) {
			property.setIsA(getTermBuilder().getTermOfClassOfProperty(property.getTerm().getId(), 
			        CvId.PROPERTIES.getId(), TermId.IS_A.getId()));
		}
		
		return property;
	}

    @Override
    public long countIsAOfProperties() throws MiddlewareQueryException {
        long count = 0;
        count += getCvTermDao().countIsAOfTermsByCvId(CvId.PROPERTIES);
        return count;
    }

    @Override
    public List<TraitClassReference> getAllTraitGroupsHierarchy(boolean includePropertiesAndVariables) throws MiddlewareQueryException {
        return getTraitGroupBuilder().getAllTraitGroupsHierarchy(includePropertiesAndVariables);
    }

    @Override
    public List<Term> getTermsByIds(List<Integer> ids) throws MiddlewareQueryException {
        return getTermBuilder().getTermsByIds(ids);
    }

    @Override
    public TraitClass addTraitClass(String name, String definition, int parentTraitClassId) throws MiddlewareQueryException {
        Term term = findTermByName(name, CvId.IBDB_TERMS);

        if (term != null) {
            return new TraitClass(term, getTermById(parentTraitClassId));
        }

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            term = getTermSaver().save(name, definition, CvId.IBDB_TERMS);

            if (term != null) {
                getTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), parentTraitClassId);
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addTraitClass " + e.getMessage(), e);
        }
        return new TraitClass(term, getTermById(parentTraitClassId));

    }
    
    @Override
    public Set<StandardVariable> getAllStandardVariables() throws MiddlewareQueryException {
        Set<StandardVariable> standardVariables = new HashSet<>();
        List<Term> terms = getAllTermsByCvId(CvId.VARIABLES);
        for (Term term : terms) {
            standardVariables.add(getStandardVariable(term.getId()));
        }
        return standardVariables;
    }
    
    @Override
    public List<StandardVariable> getStandardVariables(Integer traitClassId, Integer propertyId, Integer methodId,  Integer scaleId) 
                    throws MiddlewareQueryException{
        List<StandardVariable> standardVariables = new ArrayList<>();

        if (traitClassId != null){
            standardVariables.addAll(getStandardVariablesOfTraitClass(Database.LOCAL, traitClassId));
            return standardVariables;
        }
        
        // For property, scale, method
        List<Integer> standardVariableIds = getCvTermDao().getStandardVariableIds(traitClassId, propertyId, methodId, scaleId);
        for (Integer id : standardVariableIds) {
            standardVariables.add(getStandardVariable(id));
        }
        
        return standardVariables;
    }
    
    private List<StandardVariable> getStandardVariablesOfTraitClass(Database instance, Integer traitClassId) throws MiddlewareQueryException{
        List<StandardVariable> standardVariables = new ArrayList<>();
        List<PropertyReference> properties = getCvTermDao().getPropertiesOfTraitClass(traitClassId);

        List<Integer> propertyIds = new ArrayList<>();
        for (PropertyReference property : properties){
            propertyIds.add(property.getId());
        }
        
        Map<Integer, List<StandardVariableReference>> propertyVars = getCvTermDao().getStandardVariablesOfProperties(propertyIds);
        
        for (Integer propId : propertyIds){
            List<StandardVariableReference> stdVarRefs = propertyVars.get(propId);
            if (stdVarRefs != null){
                for (StandardVariableReference stdVarRef : stdVarRefs){
                    standardVariables.add(getStandardVariable(stdVarRef.getId()));
                }
            }
        }
        
        return standardVariables;

    }
    
    @Override
    public Integer getStandardVariableIdByTermId(int cvTermId, TermId termId) throws MiddlewareQueryException {
        return getStandardVariableBuilder().getIdByTermId(cvTermId, termId);
    }

    @Override
    public void saveOrUpdateStandardVariable(StandardVariable standardVariable, Operation operation) 
            throws MiddlewareQueryException, MiddlewareException {

        standardVariable.setProperty(getTermBuilder().findOrSaveTermByName(standardVariable.getProperty().getName(), CvId.PROPERTIES));
        standardVariable.setScale(getTermBuilder().findOrSaveTermByName(standardVariable.getScale().getName(), CvId.SCALES));
        standardVariable.setMethod(getTermBuilder().findOrSaveTermByName(standardVariable.getMethod().getName(), CvId.METHODS));
        
        String errorCodes = getStandardVariableSaver().validate(standardVariable, operation);
        
        if (errorCodes != null && !errorCodes.isEmpty()) {
            throw new MiddlewareQueryException(errorCodes, "The variable you entered is invalid");
        }
        
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            if (operation == Operation.ADD) {
                getStandardVariableSaver().save(standardVariable);
            } else {
                getStandardVariableSaver().update(standardVariable);
            }

            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in saveOrUpdateStandardVariable " + e.getMessage(), e);
        }
    }
    
    @Override
    public void addOrUpdateStandardVariableConstraints(int standardVariableId, VariableConstraints constraints) 
            throws MiddlewareException, MiddlewareQueryException{
        
    	Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getStandardVariableSaver().saveConstraints(standardVariableId, constraints);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addOrUpdateStandardVariableConstraints: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteStandardVariableLocalConstraints(int standardVariableId) 
            throws MiddlewareQueryException{
        
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            StandardVariable stdVar = getStandardVariable(standardVariableId);
            
            getStandardVariableSaver().deleteConstraints(stdVar);

            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in deleteStandardVariableLocalConstraints " + e.getMessage(), e);
        }
    }
    
    @Override
    public Enumeration addStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration) 
            throws MiddlewareQueryException, MiddlewareException{
        
        if (variable.getEnumeration(enumeration.getName(), enumeration.getDescription()) != null) {
            throw new MiddlewareException(
                    "Error in addStandardVariableEnumeration(). Enumeration with the same name and description exists.");
        }

        Integer cvId = getEnumerationCvId(variable);

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getStandardVariableSaver().saveEnumeration(variable, enumeration, cvId);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addStandardVariableEnumeration: " + e.getMessage(), e);
        }
        
        return enumeration;
    }
    
    @Override
    public void saveOrUpdateStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration)  
            throws MiddlewareQueryException, MiddlewareException{

        if (enumeration.getId() == null && 
                variable.getEnumeration(enumeration.getName(), enumeration.getDescription()) != null) {
            throw new MiddlewareException(
                    "Error in saveOrUpdateStandardVariableEnumeration(). " +
                    "Enumeration id is null and an Enumeration with the same name and description exists." +
                    "Add fails. ");
        }

        Integer cvId = getEnumerationCvId(variable);
        
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
			// Operation is ADD
            if (enumeration.getId() == null){
                getStandardVariableSaver().saveEnumeration(variable, enumeration, cvId);

			// Operation is UPDATE
            } else {
        		getTermSaver().update(new Term(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
            }
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in saveOrUpdateStandardVariableEnumeration: " + e.getMessage(), e);
        }
        
    }
    
    private Integer getEnumerationCvId(StandardVariable variable) throws MiddlewareQueryException {

        // Check if cv entry of enumeration already exists
        // Add cv entry of the standard variable if none found
        Integer cvId = getCvDao().getIdByName(String.valueOf(variable.getId()));
                
        if (cvId == null){
            cvId = getStandardVariableSaver().createCv(variable).getCvId();
        }
        
        return cvId;
    }

    
    @Override
    public void deleteStandardVariableEnumeration(int standardVariableId, int enumerationId) throws MiddlewareQueryException{
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            StandardVariable stdVar = getStandardVariable(standardVariableId);
            getStandardVariableSaver().deleteEnumeration(standardVariableId, stdVar.getEnumeration(enumerationId));

            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in deleteStandardVariableEnumeration " + e.getMessage(), e);
        }
    }

        
    @Override
    public void deleteTerm(int cvTermId, CvId cvId) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;
        
        try {
                        
            if (CvId.VARIABLES.getId() != cvId.getId()) {
                trans = session.beginTransaction();
                getTermSaver().delete(getCvTermDao().getById(cvTermId), cvId);
                trans.commit();
            } else {
                throw new MiddlewareQueryException("variables cannot be used in this method");
            }
        } catch (MiddlewareQueryException e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException(e.getCode(), e);
        }
    }
    
    @Override
    public void deleteTermAndRelationship(int cvTermId, CvId cvId, int typeId, int objectId) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            if (getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId) != null) {
                if (getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId).getTypeId().equals(TermId.IS_A.getId())) {
                    if (getCvTermDao().getById(getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId).getSubjectId()).getCv().equals(CvId.PROPERTIES.getId())) {
                        throw new MiddlewareQueryException(ErrorCode.ONTOLOGY_HAS_LINKED_PROPERTY.getCode(), DELETE_TERM_ERROR_MESSAGE);
                    } else {
                        throw new MiddlewareQueryException(ErrorCode.ONTOLOGY_HAS_IS_A_RELATIONSHIP.getCode(), DELETE_TERM_ERROR_MESSAGE);
                    }
                } else {
                    throw new MiddlewareQueryException(ErrorCode.ONTOLOGY_HAS_LINKED_VARIABLE.getCode(), DELETE_TERM_ERROR_MESSAGE);
                }
            }
            
            if (CvId.VARIABLES.getId() != cvId.getId()) {
                trans = session.beginTransaction();
                deleteCvTermRelationship(cvTermId, typeId);
                getTermSaver().delete(getCvTermDao().getById(cvTermId), cvId);
                trans.commit();
            } else {
                throw new MiddlewareQueryException("variables cannot be used in this method");
            }
        } catch (MiddlewareQueryException e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException(e.getCode(), e);
        } catch (MiddlewareException e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException(e.getMessage(), e);
        }
    }
    
    private void deleteCvTermRelationship(int subjectId, int typeId) throws MiddlewareQueryException, MiddlewareException {
        Term typeTerm = getTermById(typeId);
        if (typeTerm != null) {
            CVTermRelationship cvRelationship = getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
            if(cvRelationship != null){ 
                getTermRelationshipSaver().deleteRelationship(cvRelationship);
            }
        }
    }

    @Override
    public List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException {
        List<Property> properties = getCvTermDao().getAllPropertiesWithTraitClass();

        Collections.sort(properties, new Comparator<Property>() {

            @Override
            public int compare(Property o1, Property o2) {
                return o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
            }
        });
        return properties;
    }
    
    @Override
    public void deleteStandardVariable(int stdVariableId) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            StandardVariable stdVar = getStandardVariable(stdVariableId);
            getStandardVariableSaver().delete(stdVar);
            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in deleteStandardVariable " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getStandardVariableIdByPropertyScaleMethodRole(String property, String scale, String method, PhenotypicType role)
            throws MiddlewareQueryException {
        
        Integer propertyId = findTermIdByName(property, CvId.PROPERTIES);
        Integer scaleId = findTermIdByName(scale, CvId.SCALES);
        Integer methodId = findTermIdByName(method, CvId.METHODS);
        
        return getStandardVariableBuilder().getIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
    }
    
    @Override
    public Integer getStandardVariableIdByPropertyScaleMethodRole(Integer propertyId, Integer scaleId, Integer methodId, PhenotypicType role)
            throws MiddlewareQueryException {
        
        return getStandardVariableBuilder().getIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
        
    }
    
    private Integer findTermIdByName(String name, CvId cvType)  throws MiddlewareQueryException {
        Term term = findTermByName(name, cvType);
        if (term != null) {
            return term.getId();
        }
        return null;
    }
    
    @Override
    public boolean validateDeleteStandardVariableEnumeration(int standardVariableId, int enumerationId) throws MiddlewareQueryException {
    	return getStandardVariableBuilder().validateEnumerationUsage(standardVariableId, enumerationId);
    }

    
	@Override
	public List<NameSynonym> getSynonymsOfTerm(Integer termId) throws MiddlewareQueryException {
		List<CVTermSynonym> synonyms = getNameSynonymBuilder().findSynonyms(termId);
		return getNameSynonymBuilder().create(synonyms);
	}

    @Override
    public List<Term> getAllTraitClass() throws MiddlewareQueryException {
        return getCvTermDao().getAllClasses();
    }
}