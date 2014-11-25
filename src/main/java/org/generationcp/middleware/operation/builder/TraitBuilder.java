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

    public TraitBuilder(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        List<NumericTraitInfo> numericTraitInfoList = new ArrayList<NumericTraitInfo>();
        List<NumericTraitInfo> localNumericTraitInfoList = new ArrayList<NumericTraitInfo>();

        // Get numeric variables from central
        setWorkingDatabase(Database.CENTRAL);
        List<CVTerm> variableTerms = getCvTermDao().getVariablesByType(Arrays.asList(TermId.NUMERIC_VARIABLE.getId(), TermId.DATE_VARIABLE.getId()), null);
        List<Integer> variableIds = getVariableIds(variableTerms);

        // Get locationCount, germplasmCount, observationCount, minValue, maxValue
        setWorkingDatabase(Database.CENTRAL);
        numericTraitInfoList.addAll(getPhenotypeDao().getNumericTraitInfoList(environmentIds, variableIds));

        setWorkingDatabase(Database.LOCAL);
        localNumericTraitInfoList.addAll(getPhenotypeDao().getNumericTraitInfoList(environmentIds, variableIds));
        
        // Merge local and central results
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
        
        if (numericTraitInfoList.size() == 0){
            return numericTraitInfoList;
        }
        
        // Get median value
        getMedianValues(numericTraitInfoList, environmentIds);

        // Set name and description
        for (NumericTraitInfo traitInfo : numericTraitInfoList) {
            for (CVTerm variable : variableTerms) {
                if (traitInfo.getId() == variable.getCvTermId()) {
                    traitInfo.setName(variable.getName());
                    traitInfo.setDescription(variable.getDefinition());
                    break;
                }
            }
        }

        return numericTraitInfoList;
    }
    
    public List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        List<CharacterTraitInfo> characterTraitInfoList = new ArrayList<CharacterTraitInfo>();

        // Get character variable terms
        setWorkingDatabase(Database.CENTRAL);
        List<CVTerm> variableTerms = getCvTermDao().getVariablesByType(Arrays.asList(TermId.CHARACTER_VARIABLE.getId()), null);
       
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
                List<String> values = centralTraitValues.get(traitInfo.getId());
                if (localTraitValues.get(traitInfo.getId()) != null){
                    values.addAll(localTraitValues.get(traitInfo.getId()));
                }
                Collections.sort(values);
                traitInfo.setValues(values);
        }
        
        return characterTraitInfoList;
    }
    
    public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        List<CategoricalTraitInfo> categoricalTraitInfoList = new ArrayList<CategoricalTraitInfo>();

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
            categoricalTraitInfoList.add(new CategoricalTraitInfo(centralTrait));
        }
        
        if (categoricalTraitInfoList.size() == 0){
            return categoricalTraitInfoList;
        }
        
        // Set name, description and get categorical domain values from central
        setWorkingDatabase(Database.CENTRAL);
        categoricalTraitInfoList = getCvTermDao().setCategoricalVariables(categoricalTraitInfoList);
        
        // Get categorical values count from phenotype
        setWorkingDatabase(Database.CENTRAL);
        getPhenotypeDao().setCategoricalTraitInfoValues(categoricalTraitInfoList, environmentIds);
        setWorkingDatabase(Database.LOCAL);
        getPhenotypeDao().setCategoricalTraitInfoValues(categoricalTraitInfoList, environmentIds);
        

        return categoricalTraitInfoList;
        
    }
    
    private List<TraitInfo> getTraitCounts(List<Integer> variableIds, List<Integer> environmentIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        List<TraitInfo> localTraitInfoList = new ArrayList<TraitInfo>();

         // Get locationCount, germplasmCount, observationCount
        setWorkingDatabase(Database.CENTRAL);
        traitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds, variableIds));

        setWorkingDatabase(Database.LOCAL);
        localTraitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds, variableIds));
        
        // Merge local and central results
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

        return traitInfoList;        
    }


    private List<Integer> getVariableIds(List<CVTerm> variableTerms) {
        List<Integer> variableIds = new ArrayList<Integer>();
        for (CVTerm term : variableTerms) {
            variableIds.add(term.getCvTermId());
        }
        return variableIds;

    }
    
    private void getMedianValues(List<NumericTraitInfo> numericTraitInfoList, List<Integer> environmentIds) throws MiddlewareQueryException {

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
		List<Double> values = centralTraitValues.get(traitInfo.getId());
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
    	
        List<Observation> observations = new ArrayList<Observation>();
        if (environmentIds != null && !environmentIds.isEmpty()){
            setWorkingDatabase(Database.LOCAL);
            observations = getPhenotypeDao().getObservationForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
        }
        return observations;
    }
    
    

	public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds) 
								throws MiddlewareQueryException{

        List<Observation> observations = new ArrayList<Observation>();
        if (environmentIds.size() > 0){
            setWorkingDatabase(Database.LOCAL);
            observations = getPhenotypeDao().getObservationForTraits(traitIds, environmentIds,0,0);
        }
        return observations;
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
    	List<TraitObservation> traitObservations = new ArrayList<TraitObservation>();
    	if(!environmentIds.isEmpty()){
    		setWorkingDatabase(Database.LOCAL);
        	traitObservations = getPhenotypeDao().getObservationsForTrait(traitId, environmentIds);
    	}
    	return traitObservations;
    }
}
