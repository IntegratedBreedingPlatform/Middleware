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

package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;


public interface CrossStudyDataManager{

    /**
     * Retrieves a list of all trial environments.
     * 
     * @return TrialEnvironments
     * @throws MiddlewareQueryException
     */
    TrialEnvironments getAllTrialEnvironments() throws MiddlewareQueryException;
    
    /**
     * Get all environment properties given a list of environments.
     * 
     * @param trialEnvtIds
     * @return a List of Environment Properties
     * @throws MiddlewareQueryException
     */
    List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> trialEnvtIds) throws MiddlewareQueryException;
    
    /**
     * Get all studies given a list of environments.
     * 
     * @param environmentIds
     * @return a list of Study References
     * @throws MiddlewareQueryException
     */
    List<StudyReference> getStudiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException;
    
    
    /**
     * Retrieves a set of standard variables (traits) used for the numeric variates observed in given list of environments. 
     * Numeric variates are those with type "Numeric variable" (cvterm ID = 1110) or type "Date variable" (cvterm ID = 1117). 
     * 
     * @param environmentIds
     * @return List of NumericTraitInfo
     * @throws MiddlewareQueryException
     */
    List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> environmentIds) throws MiddlewareQueryException;
    

    /**
     * Retrieves a set of standard variables (traits) used for the character
     * variates observed in given list of environments. Character variates are
     * those with type "Character variable" (cvterm ID = 1120).
     * 
     * @param environmentIds
     * @return List of CharacterTraitInfo
     * @throws MiddlewareQueryException
     */
    List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> environmentIds) throws MiddlewareQueryException;
    
    /**
     * Retrieve a set of standard variables (traits) used for the categorical
     * variables observed in given list of environments. Categorical variables
     * are those with type "Categorical variable" (cvterm ID = 1130).
     * 
     * @param environmentIds
     * @return List of CategoricalTraitInfo
     * @throws MiddlewareQueryException
     */
    List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> environmentIds) throws MiddlewareQueryException;

    /**
     * Given a list of pairs of GIDs, return all environments where any of the pair of GIDs have been observed. 
     * Both the GIDs in a pair must have been used in an experiment in a specific environment 
     * for that environment to be included in the result.
     * 
     * @param germplasmPairs List of germplasm pairs of GIDs
     * @return List of TrialEnvironments corresponding to the list of Germplasm IDs
     * @throws MiddlewareQueryException
     */
    List<GermplasmPair> getEnvironmentsForGermplasmPairs(List<GermplasmPair> germplasmPairs) throws MiddlewareQueryException;
    
    /**
     * For each combination of trait, germplasm, and environment, the value observed is returned. 
     * If there was no observation for a combination, null is returned.
     * Information to return for each combination of trait, germplasm, environment: 
     *          - trait id
     *          - germplasm id
     *          - environment id
     *          - value observed (null if no observation)
     * @param traitId
     * @param germplasmIds
     * @param environmentIds
     * @return 
     * @throws MiddlewareQueryException
     */
    List<Observation> getObservationsForTraitOnGermplasms(List<Integer> traitIds, List<Integer> germplasmIds, 
            List<Integer> environmentIds) throws MiddlewareQueryException;

    /**
     * Given a list of traits and environments, return observed data for the list of traits in the given list of environments.
     * 
     * With each observation, we need the ff information:
     * 			- trait - id of trait (standard variable) being observed
     * 			- environment ID
     * 			- GID- GID of germplasm related to observation (experiment)
     * 			- observed value - phenotype.value
     * 
     * @param traitIds
     * @param environmentIds
     * @return
     * @throws MiddlewareQueryException
     */
	List<Observation> getObservationsForTraits(List<Integer> traitIds,
			List<Integer> environmentIds) throws MiddlewareQueryException;
}
