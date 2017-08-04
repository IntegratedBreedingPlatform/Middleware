/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmLocationInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface CrossStudyDataManager {

	/**
	 * Retrieves a list of all trial environments.
	 * 
	 * @param includePublicData flag to indicate whether public (central) data should be querried.
	 * 
	 * @return TrialEnvironments
	 * @throws MiddlewareQueryException
	 */
	TrialEnvironments getAllTrialEnvironments(boolean includePublicData) throws MiddlewareQueryException;

	/**
	 * Returns number of all central and local trial environments
	 * 
	 * @return count
	 */
	long countAllTrialEnvironments() throws MiddlewareQueryException;

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
	 * Retrieves a set of standard variables (traits) used for the numeric variates observed in given list of environments. Numeric variates
	 * are those with type "Numeric variable" (cvterm ID = 1110) or type "Date variable" (cvterm ID = 1117).
	 * 
	 * @param environmentIds
	 * @return List of NumericTraitInfo
	 * @throws MiddlewareQueryException
	 */
	List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> environmentIds) throws MiddlewareQueryException;

	/**
	 * Retrieves a set of standard variables (traits) used for the character variates observed in given list of environments. Character
	 * variates are those with type "Character variable" (cvterm ID = 1120).
	 * 
	 * @param environmentIds
	 * @return List of CharacterTraitInfo
	 * @throws MiddlewareQueryException
	 */
	List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> environmentIds) throws MiddlewareQueryException;

	/**
	 * Retrieve a set of standard variables (traits) used for the categorical variables observed in given list of environments. Categorical
	 * variables are those with type "Categorical variable" (cvterm ID = 1130).
	 * 
	 * @param environmentIds
	 * @return List of CategoricalTraitInfo
	 * @throws MiddlewareQueryException
	 */
	List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> environmentIds) throws MiddlewareQueryException;

	/**
	 * Given a list of pairs of GIDs, return all environments where any of the pair of GIDs have been observed. Both the GIDs in a pair must
	 * have been used in an experiment in a specific environment for that environment to be included in the result, where a filter for experiment types
	 * to include is applied affecting the traits that will be included (eg. for plot experiments, include traits. for summary experiments, include analysis variables).
	 * 
	 * @param germplasmPairs List of germplasm pairs of GIDs
	 * @param experimentTypes - List of experiment type IDs to be included in query (can be for plot and/or analysis types)
	 * @return List of TrialEnvironments corresponding to the list of Germplasm IDs
	 * @throws MiddlewareQueryException
	 */
	List<GermplasmPair> getEnvironmentsForGermplasmPairs(final List<GermplasmPair> germplasmPairs, final List<Integer> experimentTypes) throws MiddlewareQueryException;

	/**
	 * For each combination of trait, germplasm, and environment, the value observed is returned. If there was no observation for a
	 * combination, null is returned. Information to return for each combination of trait, germplasm, environment: - trait id - germplasm id
	 * - environment id - value observed (null if no observation)
	 * 
	 * @param traitIds
	 * @param germplasmIds
	 * @param environmentIds
	 * @return list of observations for traits on germplasms
	 * @throws MiddlewareQueryException
	 */
	List<Observation> getObservationsForTraitOnGermplasms(List<Integer> traitIds, List<Integer> germplasmIds, List<Integer> environmentIds)
			throws MiddlewareQueryException;

	/**
	 * Given a list of traits and environments, return observed data for the list of traits in the given list of environments.
	 * 
	 * With each observation, we need the ff information: - trait - id of trait (standard variable) being observed - environment ID - GID-
	 * GID of germplasm related to observation (experiment) - observed value - phenotype.value
	 * 
	 * @param traitIds
	 * @param environmentIds
	 * @return list of observations for traits
	 * @throws MiddlewareQueryException
	 */
	List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds) throws MiddlewareQueryException;

	/**
	 * For each trait in given trial environments, the observed values from local and central databases are returned
	 * 
	 * @param traitId - phenotype ID
	 * @param environmentIds - List of environment Ids
	 * @return list of trait observations
	 * @throws MiddlewareQueryException
	 */
	List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> environmentIds) throws MiddlewareQueryException;

	/**
	 * Given list of trait (standard variable) IDs, return all environments where any of the traits has been observed. With each
	 * environment, we need the ff information: - environment ID - nd_geolocation record ID - location - location name, province name and
	 * country name of location associated with environment. - name of the study
	 * 
	 * @param traitIds
	 * @return List of TrialEnvironments where any of the traits has been observed
	 * @throws MiddlewareQueryException
	 */
	TrialEnvironments getEnvironmentsForTraits(List<Integer> traitIds) throws MiddlewareQueryException;

	/**
	 * Retrieve a list of germplasm and location information matching a given set of trial environment ids. Empty list if no matches are
	 * found. Never returns {@code null}.
	 *
	 *
	 * @param environmentIds
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<GermplasmLocationInfo> getGermplasmLocationInfoByEnvironmentIds(Set<Integer> environmentIds) throws MiddlewareQueryException;

	/**
	 * Retrieve the environment info for a list of Germplasm. Find out which experiments plants have been involved in.
	 *
	 * @param gids : germplasm ids
	 * @return envIds : environmentIds
	 * @throws MiddlewareQueryException
	 */
	List<Integer> getTrialEnvironmentIdsForGermplasm(Set<Integer> gids) throws MiddlewareQueryException;
}
