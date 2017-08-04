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

package org.generationcp.middleware.manager;

import java.util.ArrayList;
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
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the CrossStudyDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 *
 */
@Transactional
public class CrossStudyDataManagerImpl extends DataManager implements CrossStudyDataManager {

	public CrossStudyDataManagerImpl() {
	}

	public CrossStudyDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public TrialEnvironments getAllTrialEnvironments() {
		return this.getTrialEnvironmentBuilder().getAllTrialEnvironments();
	}

	@Override
	public long countAllTrialEnvironments() {
		return this.getTrialEnvironmentBuilder().countAllTrialEnvironments();
	}

	@Override
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(final List<Integer> trialEnvtIds) {
		return this.getTrialEnvironmentBuilder().getPropertiesForTrialEnvironments(trialEnvtIds);
	}

	@Override
	public List<StudyReference> getStudiesForTrialEnvironments(final List<Integer> environmentIds) {
		return this.getStudyNodeBuilder().getStudiesForTrialEnvironments(environmentIds);
	}

	@Override
	public List<NumericTraitInfo> getTraitsForNumericVariates(final List<Integer> environmentIds) {
		return this.getTraitBuilder().getTraitsForNumericVariates(environmentIds);
	}

	@Override
	public List<CharacterTraitInfo> getTraitsForCharacterVariates(final List<Integer> environmentIds) {
		return this.getTraitBuilder().getTraitsForCharacterVariates(environmentIds);
	}

	@Override
	public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(final List<Integer> environmentIds) {
		return this.getTraitBuilder().getTraitsForCategoricalVariates(environmentIds);
	}

	@Override
	public List<GermplasmPair> getEnvironmentsForGermplasmPairs(final List<GermplasmPair> germplasmPairs,
			final List<Integer> experimentTypes) {
		return this.getTrialEnvironmentBuilder().getEnvironmentForGermplasmPairs(germplasmPairs, experimentTypes);
	}

	@Override
	public List<Observation> getObservationsForTraitOnGermplasms(final List<Integer> traitIds, final List<Integer> germplasmIds,
			final List<Integer> environmentIds) {
		return this.getTraitBuilder().getObservationsForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
	}

	@Override
	public List<Observation> getObservationsForTraits(final List<Integer> traitIds, final List<Integer> environmentIds) {
		return this.getTraitBuilder().getObservationsForTraits(traitIds, environmentIds);
	}

	@Override
	public List<TraitObservation> getObservationsForTrait(final int traitId, final List<Integer> environmentIds) {
		return this.getTraitBuilder().getObservationsForTrait(traitId, environmentIds);
	}

	@Override
	public TrialEnvironments getEnvironmentsForTraits(final List<Integer> traitIds) {
		return this.getTrialEnvironmentBuilder().getEnvironmentsForTraits(traitIds);
	}

	@Override
	public List<GermplasmLocationInfo> getGermplasmLocationInfoByEnvironmentIds(final Set<Integer> environmentIds) {
		final List<GermplasmLocationInfo> result = new ArrayList<>();
		if (environmentIds != null && !environmentIds.isEmpty()) {
			result.addAll(this.getBreedersQueryDao().getGermplasmLocationInfoByEnvironmentIds(environmentIds));
		}
		return result;
	}

	@Override
	public List<Integer> getTrialEnvironmentIdsForGermplasm(final Set<Integer> gids) {
		final List<Integer> result = new ArrayList<>();
		if (gids != null && !gids.isEmpty()) {
			result.addAll(this.getBreedersQueryDao().getTrialEnvironmentIdsForGermplasm(gids));
		}
		return result;
	}
}
