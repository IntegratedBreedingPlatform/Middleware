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

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntologyDataManagerImpl extends DataManager implements OntologyDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(OntologyDataManagerImpl.class);

    public OntologyDataManagerImpl() {
    }

    public OntologyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public OntologyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
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
    public void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {

            trans = session.beginTransaction();
            // check if scale, property and method exists first
            Term scale = findTermByName(stdVariable.getScale().getName(), CvId.SCALES);
            if (scale == null) {
                stdVariable.setScale(getTermSaver().save(stdVariable.getScale().getName(),
                        stdVariable.getScale().getDefinition(), CvId.SCALES));
                LOG.debug("new scale with id = " + stdVariable.getScale().getId());
            }
            Term property = findTermByName(stdVariable.getProperty().getName(), CvId.PROPERTIES);
            if (property == null) {
                stdVariable.setProperty(getTermSaver().save(stdVariable.getProperty().getName(),
                        stdVariable.getProperty().getDefinition(), CvId.PROPERTIES));
                LOG.debug("new property with id = " + stdVariable.getProperty().getId());
            }
            Term method = findTermByName(stdVariable.getMethod().getName(), CvId.METHODS);
            if (method == null) {
                stdVariable.setMethod(getTermSaver().save(stdVariable.getMethod().getName(),
                        stdVariable.getMethod().getDefinition(), CvId.METHODS));
                LOG.debug("new method with id = " + stdVariable.getMethod().getId());
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

    @Deprecated
    @Override
    public Term addMethod(String name, String definition) throws MiddlewareQueryException {
        return addTerm(name, definition, CvId.METHODS);
    }

    @Override
    public Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym)
            throws MiddlewareQueryException {
        Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
        if (setWorkingDatabase(Database.LOCAL)) {
            standardVariables.addAll(getStandardVariablesByNameOrSynonym(nameOrSynonym));
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            standardVariables.addAll(getStandardVariablesByNameOrSynonym(nameOrSynonym));
        }
        return standardVariables;
    }

    private Set<StandardVariable> getStandardVariablesByNameOrSynonym(String nameOrSynonym)
            throws MiddlewareQueryException {
        Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Term> getAllTermsByCvId(CvId cvId, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countTermsByCvId", "getTermsByCvId");
        Object[] centralParameters = new Object[] { cvId };
        Object[] localParameters = new Object[] { cvId };
        List<CVTerm> cvTerms = getFromCentralAndLocalByMethod(getCvTermDao(), methods, start, numOfRows,
                centralParameters, localParameters, new Class[] { CvId.class });
        List<Term> terms = null;
        if (cvTerms != null && !cvTerms.isEmpty()) {
            terms = new ArrayList<Term>();
            for (CVTerm cvTerm : cvTerms) {
                terms.add(getTermBuilder().mapCVTermToTerm(cvTerm));
            }
        }
        return terms;
    }

    @Override
    public long countTermsByCvId(CvId cvId) throws MiddlewareQueryException {
        setWorkingDatabase(Database.CENTRAL);
        long centralCount = getCvTermDao().countTermsByCvId(cvId);
        setWorkingDatabase(Database.LOCAL);
        long localCount = getCvTermDao().countTermsByCvId(cvId);
        return centralCount + localCount;
    }

    @Override
    public List<Term> getMethodsForTrait(Integer traitId) throws MiddlewareQueryException {
        List<Term> methodTerms = new ArrayList<Term>();
        Set<Integer> methodIds = new HashSet<Integer>();
        if (setWorkingDatabase(Database.CENTRAL)) {
            List<Integer> centralMethodIds = getCvTermDao().findMethodTermIdsByTrait(traitId);
            if (centralMethodIds != null) {
                methodIds.addAll(centralMethodIds);
            }
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            List<Integer> localMethodIds = getCvTermDao().findMethodTermIdsByTrait(traitId);
            if (localMethodIds != null) {
                methodIds.addAll(localMethodIds);
            }
        }
        // iterate list
        for (Integer termId : methodIds) {
            methodTerms.add(getTermBuilder().get(termId));
        }
        return methodTerms;
    }

    @Override
    public List<Term> getScalesForTrait(Integer traitId) throws MiddlewareQueryException {
        List<Term> scaleTerms = new ArrayList<Term>();
        Set<Integer> scaleIds = new HashSet<Integer>();
        if (setWorkingDatabase(Database.CENTRAL)) {
            List<Integer> centralMethodIds = getCvTermDao().findScaleTermIdsByTrait(traitId);
            if (centralMethodIds != null) {
                scaleIds.addAll(centralMethodIds);
            }
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            List<Integer> localMethodIds = getCvTermDao().findScaleTermIdsByTrait(traitId);
            if (localMethodIds != null) {
                scaleIds.addAll(localMethodIds);
            }
        }
        // iterate list
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

        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
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
        
        if (term != null && term.getId() >= 0){
            throw new MiddlewareException("Error in updateTerm: Cannot update terms in central.");
        }
        
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

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

        TreeMap<String, StandardVariable> standardVariables = new TreeMap<String, StandardVariable>();
        List<Integer> centralStdVariableIds = new ArrayList<Integer>();
        List<Integer> localStdVariableIds = new ArrayList<Integer>();

        if (setWorkingDatabase(Database.CENTRAL)) {
            centralStdVariableIds = getCvTermDao().getStandardVariableIdsByPhenotypicType(type);

            for (Integer stdVarId : centralStdVariableIds) {
                StandardVariable sd = getStandardVariableBuilder().create(stdVarId);
                standardVariables.put(sd.getName(), sd);
            }
        }

        if (setWorkingDatabase(Database.LOCAL)) {
            localStdVariableIds = getCvTermDao().getStandardVariableIdsByPhenotypicType(type);

            for (Integer stdVarId : localStdVariableIds) {
                StandardVariable sd = getStandardVariableBuilder().create(stdVarId);
                standardVariables.put(sd.getName(), sd);
            }
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
        List<Term> terms = new ArrayList<Term>();
        List<CVTerm> cvTerms = new ArrayList<CVTerm>();
        List<Integer> termIds = new ArrayList<Integer>();

        if (setWorkingDatabase(Database.LOCAL)) {
            termIds = getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, cvId.getId());
            for (Integer id : termIds) {
                cvTerms.add(getCvTermDao().getById(id));
            }
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            termIds = getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, cvId.getId());
            for (Integer id : termIds) {
                cvTerms.add(getCvTermDao().getById(id));
            }
        }

        for (CVTerm cvTerm : cvTerms) {
            terms.add(getTermBuilder().mapCVTermToTerm(cvTerm));
        }

        return terms;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Term> getIsAOfProperties(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countIsAOfTermsByCvId", "getIsAOfTermsByCvId");
        Object[] centralParameters = new Object[] { CvId.PROPERTIES };
        Object[] localParameters = new Object[] { CvId.PROPERTIES };
        List<CVTerm> cvTerms = getFromCentralAndLocalByMethod(getCvTermDao(), methods, start, numOfRows,
                centralParameters, localParameters, new Class[] { CvId.class });
        List<Term> terms = null;
        if (cvTerms != null && !cvTerms.isEmpty()) {
            terms = new ArrayList<Term>();
            for (CVTerm cvTerm : cvTerms) {
                terms.add(getTermBuilder().mapCVTermToTerm(cvTerm));
            }
        }
        return terms;
    }

    @Override
    public Term addProperty(String name, String definition, int isA) throws MiddlewareQueryException {

        Term term = findTermByName(name, CvId.PROPERTIES);

        if (term != null) {
            return term;
        }

        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
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
    public Term addOrUpdateTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException, MiddlewareException{
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Term term = findTermByName(name, cvId);
        if (term != null && term.getId() >= 0) {
            throw new MiddlewareException(term.getName() + " is retrieved from the central database and cannot be updated.");
        }

        try {
            trans = session.beginTransaction();
            term = saveOrUpdateCvTerm(name, definition, cvId);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addOrUpdateTerm: " + e.getMessage(), e);
        }

        return term;
        
    }
    
    @Override
    public Term addOrUpdateTermAndRelationship(String name, String definition, CvId cvId, int typeId, int objectId) 
            throws MiddlewareQueryException, MiddlewareException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Term term = findTermByName(name, cvId);
        if (term != null && term.getId() >= 0) {
            throw new MiddlewareException(term.getName() + " is retrieved from the central database and cannot be updated.");
        }

        try {
            trans = session.beginTransaction();
            term = saveOrUpdateCvTerm(name, definition, cvId);
            saveOrUpdateCvTermRelationship(term.getId(), objectId, typeId);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in addOrUpdateTermAndRelationship: " + e.getMessage(), e);
        }

        return term;
    }

    public Term updateTermAndRelationship(Term term, int typeId, int objectId) throws MiddlewareQueryException, MiddlewareException{
        
        if (term != null && term.getId() >= 0){
            throw new MiddlewareException("Error in updateTerm: Cannot update terms in central.");
        }
        
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            
            term = getTermSaver().update(term);
            
            saveOrUpdateCvTermRelationship(term.getId(), objectId, typeId);
            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in updateTerm: " + e.getMessage(), e);
        }
        
        return term;
        
    }

	private Term saveOrUpdateCvTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException, MiddlewareException{
        Term term = findTermByName(name, cvId);
        if (term == null){   // If term is not existing, add
            term = getTermSaver().save(name, definition, cvId);
        } else { // If term is existing, update
            term = getTermSaver().saveOrUpdate(name, definition, cvId);
        }
        return term;
	}
	
	private void saveOrUpdateCvTermRelationship(int subjectId, int objectId, int typeId) throws MiddlewareQueryException, MiddlewareException{
        Term typeTerm = getTermById(typeId);
        if (typeTerm != null) {
            CVTermRelationship cvRelationship = getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
            if(cvRelationship == null){ // add the relationship
                getTermRelationshipSaver().save(subjectId, typeId, objectId);
            }else{ // update the existing relationship
                if (cvRelationship.getCvTermRelationshipId() >= 0) { 
                    throw new MiddlewareException("Error in saveOrUpdateCvTermRelationship: Relationship found in central - cannot be updated.");
                }

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
		}
		
		return property;
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
        if (setWorkingDatabase(Database.CENTRAL)) {
            count += getCvTermDao().countIsAOfTermsByCvId(CvId.PROPERTIES);
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getCvTermDao().countIsAOfTermsByCvId(CvId.PROPERTIES);
        }
        return count;
    }

    @Override
    public List<TraitClassReference> getAllTraitGroupsHierarchy(boolean includePropertiesAndVariables) throws MiddlewareQueryException {
        return getTraitGroupBuilder().getAllTraitGroupsHierarchy(includePropertiesAndVariables);
    }

    @Deprecated
    @Override
    public List<TraitClassReference> getTraitGroupsHierarchy(TermId classType) throws MiddlewareQueryException {
        return getTraitGroupBuilder().buildTraitGroupHierarchy(classType);
    }

    @Override
    public List<TraitClassReference> getAllTraitClasses() throws MiddlewareQueryException {
        return getTraitGroupBuilder().getAllTraitClassesHierarchy();
    }
    
    @Deprecated
    @Override
    public List<TraitClassReference> getTraitClasses(TermId classType) throws MiddlewareQueryException {
        return getTraitGroupBuilder().getTraitClasses(classType);
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

        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
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
        Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
        List<Term> terms = getAllTermsByCvId(CvId.VARIABLES);
        for (Term term : terms) {
            standardVariables.add(getStandardVariable(term.getId()));
        }
        return standardVariables;
    }
    
    @Override
    public List<StandardVariable> getStandardVariables(Integer traitClassId, Integer propertyId, Integer methodId,  Integer scaleId) 
                    throws MiddlewareQueryException{
        List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();

        setWorkingDatabase(Database.CENTRAL);
        List<Integer> standardVariableIds = getCvTermDao().getStandardVariableIds(traitClassId, propertyId, methodId, scaleId);
        for (Integer id : standardVariableIds) {
            standardVariables.add(getStandardVariable(id));
        }
        setWorkingDatabase(Database.LOCAL);
        standardVariableIds = getCvTermDao().getStandardVariableIds(traitClassId, propertyId, methodId, scaleId);
        for (Integer id : standardVariableIds) {
            standardVariables.add(getStandardVariable(id));
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
            throw new MiddlewareQueryException(errorCodes);
        }
        
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            if (operation == Operation.ADD) {
                getStandardVariableSaver().save(standardVariable);
            }
            else {
                getStandardVariableSaver().update(standardVariable);
            }

            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in saveOrUpdateStandardVariable " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteTerm(int cvTermId, CvId cvId) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        try {
            
            if (cvTermId >= 0) {
                throw new MiddlewareException(getTermById(cvTermId).getName() + " is retrieved from the central database and cannot be updated.");
            }
            
            if (CvId.VARIABLES.getId() != cvId.getId()) {
                trans = session.beginTransaction();
                getTermSaver().delete(getCvTermDao().getById(cvTermId), cvId);
                trans.commit();
            } else {
                throw new MiddlewareQueryException("variables cannot be used in this method");
            }
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in deleteTerm " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteTermAndRelationship(int cvTermId, CvId cvId, int typeId, int objectId) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            if (cvTermId >= 0) {
                throw new MiddlewareException(getTermById(cvTermId).getName() + " is retrieved from the central database and cannot be updated.");
            }
            
            if (CvId.VARIABLES.getId() != cvId.getId()) {
                trans = session.beginTransaction();
                deleteCvTermRelationship(cvTermId, objectId, typeId);
                getTermSaver().delete(getCvTermDao().getById(cvTermId), cvId);
                trans.commit();
            } else {
                throw new MiddlewareQueryException("variables cannot be used in this method");
            }
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in deleteTermAndRelationship: " + e.getMessage(), e);
        }
    }
        
    private void deleteCvTermRelationship(int subjectId, int objectId, int typeId) throws MiddlewareQueryException, MiddlewareException {
        Term typeTerm = getTermById(typeId);
        if (typeTerm != null) {
            CVTermRelationship cvRelationship = getCvTermRelationshipDao().getRelationshipSubjectIdObjectIdByTypeId(subjectId, objectId, typeId);
            if(cvRelationship != null){ 
                if (cvRelationship.getCvTermRelationshipId() >= 0) { 
                    throw new MiddlewareException("Error in deleteCvTermRelationship: Relationship found in central - cannot be deleted.");
                }
    
                getTermRelationshipSaver().deleteRelationship(cvRelationship);
            }
        }
    }

    @Override
    public List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException {
        List<Property> properties = getPropertyBuilder().getAllPropertiesWithTraitClass();
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
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
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
}











