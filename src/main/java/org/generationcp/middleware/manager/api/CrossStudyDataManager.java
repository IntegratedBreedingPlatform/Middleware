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

import org.generationcp.middleware.domain.dms.TrialInstanceProperty;
import org.generationcp.middleware.domain.dms.TrialInstances;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmLocationInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;

import java.util.List;
import java.util.Set;

public interface CrossStudyDataManager {

	/**
	 * Retrieves a list of all trial instances.
	 *
	 * @return TrialInstances
	 */
	TrialInstances getAllTrialInstances();

	/**
	 * Returns number of all central and local trial instances
	 *
	 * @return count
	 */
	long countAllTrialInstances();

	/**
	 * Get all instance properties given a list of instance.
	 *
	 * @param instanceIds
	 * @return a List of Instance Properties
	 */
	List<TrialInstanceProperty> getPropertiesForTrialInstances(List<Integer> instanceIds);

	/**
	 * Retrieves a set of standard variables (traits) used for the numeric variates observed in given list of instances. Numeric variates
	 * are those with type "Numeric variable" (cvterm ID = 1110) or type "Date variable" (cvterm ID = 1117).
	 *
	 * @param instanceIds
	 * @return List of NumericTraitInfo
	 */
	List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> instanceIds);

	/**
	 * Retrieves a set of standard variables (traits) used for the character variates observed in given list of instances. Character
	 * variates are those with type "Character variable" (cvterm ID = 1120).
	 *
	 * @param instanceIds
	 * @return List of CharacterTraitInfo
	 */
	List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> instanceIds);

	/**
	 * Retrieve a set of standard variables (traits) used for the categorical variables observed in given list of instances. Categorical
	 * variables are those with type "Categorical variable" (cvterm ID = 1130).
	 *
	 * @param instanceIds
	 * @return List of CategoricalTraitInfo
	 */
	List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> instanceIds);

	/**
	 * Given a list of pairs of GIDs, return all instances where any of the pair of GIDs have been observed. Both the GIDs in a pair must
	 * have been used in an experiment in a specific instances for that instances to be included in the result, where a filter for
	 * experiment types to include is applied affecting the traits that will be included (eg. for plot experiments, include traits. for
	 * summary experiments, include analysis variables).
	 *
	 * @param germplasmPairs  List of germplasm pairs of GIDs
	 * @param experimentTypes - List of experiment type IDs to be included in query (can be for plot and/or analysis types)
	 * @param programUUID     - unique identifier for current program
	 * @return List of TrialInstances corresponding to the list of Germplasm IDs
	 */
	List<GermplasmPair> getInstancesForGermplasmPairs(
		final List<GermplasmPair> germplasmPairs, final List<Integer> experimentTypes,
		final String programUUID);

	/**
	 * For each combination of trait, germplasm, and instance, the value observed is returned. If there was no observation for a
	 * combination, null is returned. Information to return for each combination of trait, germplasm, instance: - trait id - germplasm id
	 * - instance id - value observed (null if no observation)
	 *
	 * @param traitIds
	 * @param germplasmIds
	 * @param instanceIds
	 * @return list of observations for traits on germplasms
	 */
	List<Observation> getObservationsForTraitOnGermplasms(List<Integer> traitIds, List<Integer> germplasmIds, List<Integer> instanceIds);

	/**
	 * Given a list of traits and instances, return observed data for the list of traits in the given list of instances.
	 * <p>
	 * With each observation, we need the ff information: - trait - id of trait (standard variable) being observed - instance ID - GID-
	 * GID of germplasm related to observation (experiment) - observed value - phenotype.value
	 *
	 * @param traitIds
	 * @param instanceIds
	 * @return list of observations for traits
	 */
	List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> instanceIds);

	/**
	 * For each trait in given trial instances, the observed values from local and central databases are returned
	 *
	 * @param traitId        - phenotype ID
	 * @param instanceIds - List of instance Ids
	 * @return list of trait observations
	 */
	List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> instanceIds);

	/**
	 * Given list of trait (standard variable) IDs, return all instances where any of the traits has been observed. With each
	 * instance, we need the ff information: - instance ID - nd_geolocation record ID - location - location name, province name and
	 * country name of location associated with instance. - name of the study
	 *
	 * @param traitIds
	 * @param programUUID
	 * @return List of TrialInstances where any of the traits has been observed
	 */
	TrialInstances getTrialInstancesForTraits(List<Integer> traitIds, final String programUUID);

	/**
	 * Retrieve a list of germplasm and location information matching a given set of trial instance ids. Empty list if no matches are
	 * found. Never returns {@code null}.
	 *
	 * @param instanceIds
	 * @return
	 */
	List<GermplasmLocationInfo> getGermplasmLocationInfoByInstanceIds(Set<Integer> instanceIds);

	/**
	 * Retrieve the instance info for a list of Germplasm. Find out which experiments plants have been involved in.
	 *
	 * @param gids : germplasm ids
	 * @return envIds : instanceIds
	 */
	List<Integer> getTrialInstanceIdsForGermplasm(Set<Integer> gids);
}
