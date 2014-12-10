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

package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.h2h.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTerm;

import java.util.*;

public class TraitBuilder extends Builder{

	private static final List<Integer> NUMERIC_VARIABLE_TYPE = Arrays.asList(TermId.NUMERIC_VARIABLE.getId(), TermId.DATE_VARIABLE.getId());

	public TraitBuilder(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        List<NumericTraitInfo> numericTraitInfoList = new ArrayList<NumericTraitInfo>();
        List<NumericTraitInfo> localNumericTraitInfoList = new ArrayList<NumericTraitInfo>();
        List<CVTerm> variableTerms = new ArrayList<CVTerm>();
        List<Integer> variableIds = new ArrayList<Integer>();

        // Get numeric variables from central
        variableTerms.addAll(getVariablesForVariableTypes(Database.CENTRAL, NUMERIC_VARIABLE_TYPE));
        variableIds.addAll(getVariableIds(variableTerms));

        // Get locationCount, germplasmCount, observationCount, minValue, maxValue
        numericTraitInfoList.addAll(getPhenotypeDao().getNumericTraitInfoList(environmentIds, variableIds));

        //retrieve traits in local environments
        variableTerms.addAll(getVariablesForVariableTypes(Database.LOCAL, NUMERIC_VARIABLE_TYPE));
        variableIds.addAll(getVariableIds(variableTerms));
        localNumericTraitInfoList.addAll(getPhenotypeDao().getNumericTraitInfoList(environmentIds, variableIds));
        
        // Merge local and central results, if they have common traits
        Collections.sort(numericTraitInfoList);
        Collections.sort(localNumericTraitInfoList);
        
        for (NumericTraitInfo centralTrait : numericTraitInfoList){
            for (NumericTraitInfo localTrait : localNumericTraitInfoList){
                if (centralTrait.equals(localTrait)){
                    centralTrait.setLocationCount(centralTrait.getLocationCount() + localTrait.getLocationCount());
                    centralTrait.setGermplasmCount(centralTrait.getGermplasmCount() + localTrait.getGermplasmCount());
                    centralTrait.setObservationCount(centralTrait.getObservationCount() + localTrait.getObservationCount());
                    centralTrait.setMinValue(centralTrait.getMinValue() < localTrait.getMinValue() ? 
                            centralTrait.getMinValue() : localTrait.getMinValue());
                    centralTrait.setMaxValue(centralTrait.getMaxValue() > localTrait.getMaxValue() ? 
                            centralTrait.getMaxValue() : localTrait.getMaxValue());
                    break;
                }                
            }
        }
        
        // add traits observed only in local environments
        List<NumericTraitInfo> finalNumericTraitList = new ArrayList<NumericTraitInfo>();
        finalNumericTraitList.addAll(numericTraitInfoList);
        for (NumericTraitInfo localObservedTrait : localNumericTraitInfoList){
        	if (!numericTraitInfoList.contains(localObservedTrait)){
        		finalNumericTraitList.add(localObservedTrait);
        	}
        }
        if (finalNumericTraitList.size() == 0){
            return finalNumericTraitList;
        }
        
        // Get median value
        getMedianValues(finalNumericTraitList, environmentIds);

        // Set name and description
        for (NumericTraitInfo traitInfo : finalNumericTraitList) {
            for (CVTerm variable : variableTerms) {
                if (traitInfo.getId() == variable.getCvTermId()) {
                    traitInfo.setName(variable.getName());
                    traitInfo.setDescription(variable.getDefinition());
                    break;
                }
            }
        }

        return finalNumericTraitList;
    }
    
    public List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        List<CharacterTraitInfo> characterTraitInfoList = new ArrayList<CharacterTraitInfo>();
        List<CVTerm> variableTerms = new ArrayList<CVTerm>();

        // Get character variable terms
        variableTerms.addAll(getVariablesForVariableTypes(Database.CENTRAL, Arrays.asList(TermId.CHARACTER_VARIABLE.getId())));
        variableTerms.addAll(getVariablesForVariableTypes(Database.LOCAL, Arrays.asList(TermId.CHARACTER_VARIABLE.getId())));
       
        // Get location, germplasm and observation counts 
        List<TraitInfo> traitInfoList = getTraitCounts(getVariableIds(variableTerms), environmentIds);
        // Set name and description
        for (TraitInfo traitInfo : traitInfoList) {
            for (CVTerm variable : variableTerms) {
                if (traitInfo.getId() == variable.getCvTermId()) {
                    traitInfo.setName(variable.getName());
                    traitInfo.setDescription(variable.getDefinition());
                    break;
                }
            }
        }
        
        if (traitInfoList.size() == 0){
            return characterTraitInfoList;
        }

        //Create characterTraitInfoList from TraitInfo with counts
        Collections.sort(traitInfoList);
        for (TraitInfo trait : traitInfoList){
            characterTraitInfoList.add( new CharacterTraitInfo(trait));
        }
        
        // Get the distinct phenotype values from the databases
        setWorkingDatabase(Database.CENTRAL);
        Map<Integer, List<String>> centralTraitValues = getPhenotypeDao().getCharacterTraitInfoValues(environmentIds, characterTraitInfoList);
        setWorkingDatabase(Database.LOCAL);
        Map<Integer, List<String>> localTraitValues = getPhenotypeDao().getCharacterTraitInfoValues(environmentIds, characterTraitInfoList);
        
        for (CharacterTraitInfo traitInfo : characterTraitInfoList){
                List<String> values = new ArrayList<String>();
                int traitId = traitInfo.getId();
				if (centralTraitValues != null && centralTraitValues.containsKey(traitId)){
                	values.addAll(centralTraitValues.get(traitId));
                }
                if (localTraitValues != null && localTraitValues.containsKey(traitId)){
                    values.addAll(localTraitValues.get(traitId));
                }
                Collections.sort(values);
                traitInfo.setValues(values);
        }
        
        return characterTraitInfoList;
    }
    
    public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
    	List<CategoricalTraitInfo> centralCategTraitList = new ArrayList<CategoricalTraitInfo>();
    	List<CategoricalTraitInfo> localCategTraitList = new ArrayList<CategoricalTraitInfo>();
    	List<CategoricalTraitInfo> finalTraitInfoList = new ArrayList<CategoricalTraitInfo>();

        // Get locationCount, germplasmCount, observationCount
        List<TraitInfo> centraltraitInfoList = new ArrayList<TraitInfo>();
        List<TraitInfo> localTraitInfoList = new ArrayList<TraitInfo>();

        setWorkingDatabase(Database.CENTRAL);
        centraltraitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds));
        setWorkingDatabase(Database.LOCAL);
        localTraitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds));
        
        // Merge local and central results
        Collections.sort(centraltraitInfoList);
        Collections.sort(localTraitInfoList);        
        for (TraitInfo centralTrait : centraltraitInfoList){
            for (TraitInfo localTrait : localTraitInfoList){
                if (centralTrait.equals(localTrait)){
                    centralTrait.setLocationCount(centralTrait.getLocationCount() + localTrait.getLocationCount());
                    centralTrait.setGermplasmCount(centralTrait.getGermplasmCount() + localTrait.getGermplasmCount());
                    centralTrait.setObservationCount(centralTrait.getObservationCount() + localTrait.getObservationCount());
                    break;
                }                
            }
            centralCategTraitList.add(new CategoricalTraitInfo(centralTrait));
        }
        
        // get traits only observed in local environments
        for (TraitInfo localObservedTrait : localTraitInfoList){
        	CategoricalTraitInfo categoricalTrait = new CategoricalTraitInfo(localObservedTrait);
        	if (!centralCategTraitList.contains(categoricalTrait)){
        		localCategTraitList.add(categoricalTrait);
        	}
        }
        
        // Set name, description and get categorical domain values and count per value from central
        if (!centralCategTraitList.isEmpty()){
        	setWorkingDatabase(Database.CENTRAL);
        	finalTraitInfoList.addAll(getCvTermDao().setCategoricalVariables(centralCategTraitList));
        	getPhenotypeDao().setCategoricalTraitInfoValues(centralCategTraitList, environmentIds);
        }
        
        // Set name, description and get categorical domain values and count per value from local
        if (!localCategTraitList.isEmpty()){
        	setWorkingDatabase(Database.LOCAL);
        	finalTraitInfoList.addAll(getCvTermDao().setCategoricalVariables(localCategTraitList));
        	getPhenotypeDao().setCategoricalTraitInfoValues(localCategTraitList, environmentIds);
        }

        return finalTraitInfoList;
        
    }
    
    private List<TraitInfo> getTraitCounts(List<Integer> variableIds, List<Integer> environmentIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        List<TraitInfo> localTraitInfoList = new ArrayList<TraitInfo>();

         // Get locationCount, germplasmCount, observationCount
        setWorkingDatabase(Database.CENTRAL);
        traitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds, variableIds));

        setWorkingDatabase(Database.LOCAL);
        localTraitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds, variableIds));
        
        // Merge local and central results, if they have common traits
        Collections.sort(traitInfoList);
        Collections.sort(localTraitInfoList);        
        for (TraitInfo centralTrait : traitInfoList){
            for (TraitInfo localTrait : localTraitInfoList){
                if (centralTrait.equals(localTrait)){
                    centralTrait.setLocationCount(centralTrait.getLocationCount() + localTrait.getLocationCount());
                    centralTrait.setGermplasmCount(centralTrait.getGermplasmCount() + localTrait.getGermplasmCount());
                    centralTrait.setObservationCount(centralTrait.getObservationCount() + localTrait.getObservationCount());
                    break;
                }                
            }
        }
        // add traits only observed in local environments
        List<TraitInfo> finalTraitInfoList = new ArrayList<TraitInfo>();
        finalTraitInfoList.addAll(traitInfoList);
        for (TraitInfo localObservedTrait : localTraitInfoList){
        	if (!traitInfoList.contains(localObservedTrait)){
        		finalTraitInfoList.add(localObservedTrait);
        	}
        }
        

        return finalTraitInfoList;        
    }


    private List<CVTerm> getVariablesForVariableTypes(Database database, List<Integer> types) throws MiddlewareQueryException{
    	setWorkingDatabase(database);
    	return getCvTermDao().getVariablesByType(types, null);
    }
    
    private List<Integer> getVariableIds(List<CVTerm> variableTerms) {
        List<Integer> variableIds = new ArrayList<Integer>();
        for (CVTerm term : variableTerms) {
            variableIds.add(term.getCvTermId());
        }
        return variableIds;

    }
    
    private void getMedianValues(List<NumericTraitInfo> numericTraitInfoList,
            List<Integer> environmentIds) throws MiddlewareQueryException {

        Map<Integer, List<Double>> centralTraitValues = new HashMap<Integer, List<Double>>();
        Map<Integer, List<Double>> localTraitValues = new HashMap<Integer, List<Double>>();

        //for large crop, break up central DB calls per trait to avoid out of memory error for large DBs
        if (environmentIds.size() > 1000){
        	setWorkingDatabase(Database.LOCAL);
        	localTraitValues.putAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, numericTraitInfoList));
        	
        	setWorkingDatabase(Database.CENTRAL);
        	for (NumericTraitInfo traitInfo : numericTraitInfoList){
        		centralTraitValues.putAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, traitInfo.getId()));
        		getMedianValue(centralTraitValues, localTraitValues, traitInfo);
        	}
        } else {
        	setWorkingDatabase(Database.CENTRAL);
        	centralTraitValues.putAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, numericTraitInfoList));
        	
        	setWorkingDatabase(Database.LOCAL);
        	localTraitValues.putAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, numericTraitInfoList));
        	
        	for (NumericTraitInfo traitInfo : numericTraitInfoList) {
        		getMedianValue(centralTraitValues, localTraitValues, traitInfo);
        	}
        }


    }

	private void getMedianValue(Map<Integer, List<Double>> centralTraitValues,
			Map<Integer, List<Double>> localTraitValues,
			NumericTraitInfo traitInfo) {
		List<Double> values = new ArrayList<Double>();
		if (centralTraitValues.get(traitInfo.getId()) != null){
			values.addAll(centralTraitValues.get(traitInfo.getId()));
		}
		if (localTraitValues.get(traitInfo.getId()) != null){
		    values.addAll(localTraitValues.get(traitInfo.getId()));
		}
		Collections.sort(values);
		
		double medianValue = values.get(values.size() / 2); // if the number of values is odd
		if (values.size() % 2 == 0){ // change if the number of values is even
		    double middleNumOne = values.get(values.size() / 2 - 1);
		    double middleNumTwo =  values.get(values.size() / 2);
		    medianValue = (middleNumOne + middleNumTwo) / 2;
		}
		traitInfo.setMedianValue(medianValue);
	}

    public List<Observation> getObservationsForTraitOnGermplasms(List<Integer> traitIds, 
            List<Integer> germplasmIds, List<Integer> environmentIds) throws MiddlewareQueryException{
    	
        List<Integer> localEnvironments = new ArrayList<Integer>();
        List<Integer> centralEnvironments = new ArrayList<Integer>();

        // Separate local and central environments
        for (int i = 0; i < environmentIds.size(); i++){
            Integer environmentId = environmentIds.get(i);
			if (environmentId < 0){
                localEnvironments.add(environmentId);
            } else {
                centralEnvironments.add(environmentId);
            }
        }
        
        List<Observation> localObservations = new ArrayList<Observation>();
        List<Observation> centralObservations = new ArrayList<Observation>();

        if (centralEnvironments.size() > 0){
            setWorkingDatabase(Database.CENTRAL);
            centralObservations = getPhenotypeDao().getObservationForTraitOnGermplasms(traitIds, germplasmIds, centralEnvironments);
        }
        if (localEnvironments.size() > 0){
            setWorkingDatabase(Database.LOCAL);
            localObservations = getPhenotypeDao().getObservationForTraitOnGermplasms(traitIds, germplasmIds, localEnvironments);
        }
        
        centralObservations.addAll(localObservations);        
        return centralObservations;
    }
    
    

	public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds) 
								throws MiddlewareQueryException{

        List<Integer> localEnvironments = new ArrayList<Integer>();
        List<Integer> centralEnvironments = new ArrayList<Integer>();

        // Separate local and central environments
        for (int i = 0; i < environmentIds.size(); i++){
            Integer environmentId = environmentIds.get(i);
			if (environmentId < 0){
                localEnvironments.add(environmentId);
            } else {
                centralEnvironments.add(environmentId);
            }
        }
        
        List<Observation> localObservations = new ArrayList<Observation>();
        List<Observation> centralObservations = new ArrayList<Observation>();

        if (centralEnvironments.size() > 0){
            setWorkingDatabase(Database.CENTRAL);
            centralObservations = getPhenotypeDao().getObservationForTraits(traitIds, centralEnvironments,0,0);
        }
        if (localEnvironments.size() > 0){
            setWorkingDatabase(Database.LOCAL);
            localObservations = getPhenotypeDao().getObservationForTraits(traitIds, localEnvironments,0,0);
        }
        
        centralObservations.addAll(localObservations);        
        return centralObservations;
	}
    
    

	public void buildObservations(List<Observation> centralObservations, List<Observation> localObservations, List<Integer> traitIds, List<Integer> environmentIds) 
								throws MiddlewareQueryException{
        
        // Separate local and central observations - environmentIds determine where to get the data from
        for (int i = 0; i < environmentIds.size(); i++){
            Observation observation = new Observation(
                    new ObservationKey(traitIds.get(i), environmentIds.get(i)));
            if (environmentIds.get(i) < 0){
                localObservations.add(observation);
            } else {
                centralObservations.add(observation);
            }
        }
	}
	
	public List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> environmentIds) throws MiddlewareQueryException{
    	List<TraitObservation> localTraitObservations = new ArrayList<TraitObservation>();
    	List<TraitObservation> centralTraitObservations = new ArrayList<TraitObservation>();
    	
    	List<Integer> localEnvironmentIds = new ArrayList<Integer>();
    	List<Integer> centralEnvironmentIds = new ArrayList<Integer>();
    	
        for (int i = 0; i < environmentIds.size(); i++){
        	int envId = environmentIds.get(i); 
            if ( envId < 0){
                localEnvironmentIds.add(envId);
            } else {
            	centralEnvironmentIds.add(envId);
            }
        }
    	
        if( centralEnvironmentIds.size() > 0 ){
        	setWorkingDatabase(Database.CENTRAL);
        	centralTraitObservations = getPhenotypeDao().getObservationsForTrait(traitId, centralEnvironmentIds);
        }
    	
    	
    	if( localEnvironmentIds.size() > 0 ){
    		setWorkingDatabase(Database.LOCAL);
        	localTraitObservations = getPhenotypeDao().getObservationsForTrait(traitId, localEnvironmentIds);
    	}
    	
    	centralTraitObservations.addAll(localTraitObservations);
    	
    	return centralTraitObservations;
    }


}
