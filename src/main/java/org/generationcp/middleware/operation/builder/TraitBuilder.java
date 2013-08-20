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

import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
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
        List<CVTerm> numericVariableTerms = getNumericVariableTerms();
        List<Integer> numericVariableIds = getVariableIds(numericVariableTerms);

        // Get locationCount, germplasmCount, observationCount, minValue, maxValue
        setWorkingDatabase(Database.CENTRAL);
        numericTraitInfoList.addAll(getPhenotypeDao().getNumericTraitInfoList(environmentIds, numericVariableIds));

        setWorkingDatabase(Database.LOCAL);
        localNumericTraitInfoList.addAll(getPhenotypeDao().getNumericTraitInfoList(environmentIds, numericVariableIds));
        
        // Merge local and central results
        numericTraitInfoList = mergeNumericTraits(numericTraitInfoList, localNumericTraitInfoList);
        
        // Get median value
        getMedianValue(numericTraitInfoList, environmentIds);

        // Set name and description
        for (NumericTraitInfo traitInfo : numericTraitInfoList) {
            for (CVTerm variable : numericVariableTerms) {
                if (traitInfo.getTraitId() == variable.getCvTermId()) {
                    traitInfo.setTraitName(variable.getName());
                    traitInfo.setDescription(variable.getDefinition());
                    break;
                }
            }
        }

        return numericTraitInfoList;
    }

    private List<CVTerm> getNumericVariableTerms() throws MiddlewareQueryException {
        setWorkingDatabase(Database.CENTRAL);
        return getCvTermDao().getVariablesByType(Arrays.asList(TermId.NUMERIC_VARIABLE.getId(), TermId.DATE_VARIABLE.getId()));
    }

    private List<Integer> getVariableIds(List<CVTerm> variableTerms) {
        List<Integer> variableIds = new ArrayList<Integer>();
        for (CVTerm term : variableTerms) {
            variableIds.add(term.getCvTermId());
        }
        return variableIds;

    }

    // Merge the local and central numeric trait info - get the total of counts, get lower min and higher max
    private List<NumericTraitInfo> mergeNumericTraits(List<NumericTraitInfo> numericTraitInfoList, 
                List<NumericTraitInfo> localNumericTraitInfoList){
        
        // Sort by trait id for faster merging
        Collections.sort(numericTraitInfoList);
        Collections.sort(localNumericTraitInfoList);
        
        for (NumericTraitInfo centralTrait : numericTraitInfoList){
            for (NumericTraitInfo localTrait : localNumericTraitInfoList){
                if (centralTrait.equals(localTrait)){
                    centralTrait.setLocationCount(centralTrait.getLocationCount() + localTrait.getLocationCount());
                    centralTrait.setGermplasmCount(centralTrait.getGermplasmCount() + localTrait.getGermplasmCount());
                    centralTrait.setLocationCount(centralTrait.getObservationCount() + localTrait.getObservationCount());
                    centralTrait.setMinValue(centralTrait.getMinValue() < localTrait.getMinValue() ? 
                            centralTrait.getMinValue() : localTrait.getMinValue());
                    centralTrait.setMaxValue(centralTrait.getMaxValue() > localTrait.getMaxValue() ? 
                            centralTrait.getMaxValue() : localTrait.getMaxValue());
                    break;
                }                
            }
        }
        return numericTraitInfoList;
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
