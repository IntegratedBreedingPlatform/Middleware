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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.ObservationKey;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTerm;

public class TraitBuilder extends Builder{

	private static final List<Integer> NUMERIC_VARIABLE_TYPE = Arrays.asList(TermId.NUMERIC_VARIABLE.getId(), TermId.DATE_VARIABLE.getId());
    
	public TraitBuilder(HibernateSessionProvider sessionProviderForLocal) {
        super(sessionProviderForLocal);
    }

    public List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        List<NumericTraitInfo> numericTraitInfoList = new ArrayList<NumericTraitInfo>();
        List<CVTerm> variableTerms = new ArrayList<CVTerm>();
        List<Integer> variableIds = new ArrayList<Integer>();

        // Get locationCount, germplasmCount, observationCount, minValue, maxValue
        // Retrieve traits environments
        variableTerms.addAll(getCvTermDao().getVariablesByType(NUMERIC_VARIABLE_TYPE, null));
        variableIds.addAll(getVariableIds(variableTerms));
        numericTraitInfoList.addAll(getPhenotypeDao().getNumericTraitInfoList(environmentIds, variableIds));
        
        Collections.sort(numericTraitInfoList);
        
        if (numericTraitInfoList.isEmpty()){
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
        List<CVTerm> variableTerms = new ArrayList<CVTerm>();

        // Get character variable terms
        variableTerms.addAll(getCvTermDao().getVariablesByType(Arrays.asList(TermId.CHARACTER_VARIABLE.getId()), null));
       
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
        
        if (traitInfoList.isEmpty()){
            return characterTraitInfoList;
        }

        //Create characterTraitInfoList from TraitInfo with counts
        Collections.sort(traitInfoList);
        for (TraitInfo trait : traitInfoList){
            characterTraitInfoList.add( new CharacterTraitInfo(trait));
        }
        
        // Get the distinct phenotype values from the databases
        Map<Integer, List<String>> localTraitValues = getPhenotypeDao().getCharacterTraitInfoValues(environmentIds, characterTraitInfoList);
        
        for (CharacterTraitInfo traitInfo : characterTraitInfoList){
            List<String> values = new ArrayList<String>();
            int traitId = traitInfo.getId();
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
        List<TraitInfo> localTraitInfoList = new ArrayList<TraitInfo>();

        localTraitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds));
        
        // Merge local and central results
        Collections.sort(localTraitInfoList);        
        
        // get traits only observed in local environments
        for (TraitInfo localObservedTrait : localTraitInfoList){
        	CategoricalTraitInfo categoricalTrait = new CategoricalTraitInfo(localObservedTrait);
        	if (!centralCategTraitList.contains(categoricalTrait)){
        		localCategTraitList.add(categoricalTrait);
        	}
        }
        
        // Set name, description and get categorical domain values and count per value from local
        if (!localCategTraitList.isEmpty()){
        	finalTraitInfoList.addAll(getCvTermDao().setCategoricalVariables(localCategTraitList));
        	getPhenotypeDao().setCategoricalTraitInfoValues(localCategTraitList, environmentIds);
        }

        return finalTraitInfoList;
        
    }
    private List<TraitInfo> getTraitCounts(List<Integer> variableIds, List<Integer> environmentIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
         // Get locationCount, germplasmCount, observationCount
        traitInfoList.addAll(getPhenotypeDao().getTraitInfoCounts(environmentIds, variableIds));
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

        Map<Integer, List<Double>> traitValues = new HashMap<Integer, List<Double>>();

        //for large crop, break up DB calls per trait to avoid out of memory error for large DBs
        if (environmentIds.size() > 1000){
        	for (NumericTraitInfo traitInfo : numericTraitInfoList){
        		traitValues.putAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, traitInfo.getId()));
        		getMedianValue(traitValues, traitInfo);
        	}
        } else {
        	traitValues.putAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, numericTraitInfoList));
        	for (NumericTraitInfo traitInfo : numericTraitInfoList) {
        		getMedianValue(traitValues, traitInfo);
        	}
        }
    }

	private void getMedianValue(Map<Integer, List<Double>> traitValues, NumericTraitInfo traitInfo) {
		List<Double> values = traitValues.get(traitInfo.getId());
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
            observations = getPhenotypeDao().getObservationForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
        }
        return observations;
    }

	public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds) 
								throws MiddlewareQueryException{

        List<Observation> observations = new ArrayList<Observation>();
        if (!environmentIds.isEmpty()){
            observations = getPhenotypeDao().getObservationForTraits(traitIds, environmentIds,0,0);
        }
        return observations;
	}
    
	public List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> environmentIds) throws MiddlewareQueryException{
    	List<TraitObservation> traitObservations = new ArrayList<TraitObservation>();
    	if(!environmentIds.isEmpty()){
        	traitObservations = getPhenotypeDao().getObservationsForTrait(traitId, environmentIds);
    	}
    	return traitObservations;
    }
}
