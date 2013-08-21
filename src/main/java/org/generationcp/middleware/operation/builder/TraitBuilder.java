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
import java.util.List;

import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTerm;

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
        List<CVTerm> variableTerms = getCvTermDao().getVariablesByType(Arrays.asList(TermId.NUMERIC_VARIABLE.getId(), TermId.DATE_VARIABLE.getId()));
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
        
        // Get median value
        getMedianValue(numericTraitInfoList, environmentIds);

        // Set name and description
        for (NumericTraitInfo traitInfo : numericTraitInfoList) {
            for (CVTerm variable : variableTerms) {
                if (traitInfo.getTraitId() == variable.getCvTermId()) {
                    traitInfo.setTraitName(variable.getName());
                    traitInfo.setDescription(variable.getDefinition());
                    break;
                }
            }
        }

        return numericTraitInfoList;
    }
    
    public List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        
        // Get character variable terms
        setWorkingDatabase(Database.CENTRAL);
        List<CVTerm> variableTerms = getCvTermDao().getVariablesByType(Arrays.asList(TermId.CHARACTER_VARIABLE.getId()));
       
        List<TraitInfo> traitInfoList = getTraitCounts(variableTerms, environmentIds);
        
        List<CharacterTraitInfo> characaterTraitInfoList = new ArrayList<CharacterTraitInfo>();
        for (TraitInfo trait : traitInfoList){
            CharacterTraitInfo characterTrait = new CharacterTraitInfo(trait);
            setWorkingDatabase(Database.CENTRAL);
            characterTrait.addValues(getPhenotypeDao().getCharacterTraitInfoValues(environmentIds, trait.getTraitId()));
            setWorkingDatabase(Database.LOCAL);
            characterTrait.addValues(getPhenotypeDao().getCharacterTraitInfoValues(environmentIds, trait.getTraitId()));
            characaterTraitInfoList.add(characterTrait);
        }
        
        return characaterTraitInfoList;
    }
    
    public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
        
        // Get categorical variable terms
        setWorkingDatabase(Database.CENTRAL);
        List<CVTerm> variableTerms = getCvTermDao().getVariablesByType(Arrays.asList(TermId.CATEGORICAL_VARIABLE.getId()));
        
        List<TraitInfo> traitInfoList = getTraitCounts(variableTerms, environmentIds);

        
        List<CategoricalTraitInfo> categoricalTraitInfoList = new ArrayList<CategoricalTraitInfo>();
        for (TraitInfo trait : traitInfoList){
            CategoricalTraitInfo categoricalTrait = new CategoricalTraitInfo(trait);
            setWorkingDatabase(Database.CENTRAL);
            categoricalTrait.addValuesCount(getPhenotypeDao().getCategoricalTraitInfoValues(environmentIds, trait.getTraitId()));
            setWorkingDatabase(Database.LOCAL);
            categoricalTrait.addValuesCount(getPhenotypeDao().getCategoricalTraitInfoValues(environmentIds, trait.getTraitId()));
            categoricalTraitInfoList.add(categoricalTrait);
        }
        
        return categoricalTraitInfoList;
    }
    
    private List<TraitInfo> getTraitCounts(List<CVTerm> variableTerms, List<Integer> environmentIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        List<TraitInfo> localTraitInfoList = new ArrayList<TraitInfo>();

        List<Integer> variableIds = getVariableIds(variableTerms);

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

        // Set name and description
        for (TraitInfo traitInfo : traitInfoList) {
            for (CVTerm variable : variableTerms) {
                if (traitInfo.getTraitId() == variable.getCvTermId()) {
                    traitInfo.setTraitName(variable.getName());
                    traitInfo.setDescription(variable.getDefinition());
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
        
    private void getMedianValue(List<NumericTraitInfo> numericTraitInfoList,
            List<Integer> environmentIds) throws MiddlewareQueryException {

        for (NumericTraitInfo traitInfo : numericTraitInfoList) {
            List<Double> phenotypeValues = new ArrayList<Double>();

            setWorkingDatabase(Database.CENTRAL);
            phenotypeValues.addAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, traitInfo.getTraitId()));

            setWorkingDatabase(Database.LOCAL);
            phenotypeValues.addAll(getPhenotypeDao().getNumericTraitInfoValues(environmentIds, traitInfo.getTraitId()));

            Collections.sort(phenotypeValues);
            
            double medianValue = phenotypeValues.get(phenotypeValues.size() / 2); // if the number of values is odd
            if (phenotypeValues.size() % 2 == 0){ // change if the number of values is even
                double middleNumOne = phenotypeValues.get(phenotypeValues.size() / 2 - 1);
                double middleNumTwo =  phenotypeValues.get(phenotypeValues.size() / 2);
                medianValue = (middleNumOne + middleNumTwo) / 2;
            }
            traitInfo.setMedianValue(medianValue);
        }

    }

}
