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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
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

	public CrossStudyDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public TrialEnvironments getAllTrialEnvironments(boolean includePublicData) throws MiddlewareQueryException {
		return this.getTrialEnvironmentBuilder().getAllTrialEnvironments(includePublicData);
	}

	@Override
	public long countAllTrialEnvironments() throws MiddlewareQueryException {
		return this.getTrialEnvironmentBuilder().countAllTrialEnvironments();
	}

	@Override
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> trialEnvtIds) throws MiddlewareQueryException {
		return this.getTrialEnvironmentBuilder().getPropertiesForTrialEnvironments(trialEnvtIds);
	}

	@Override
	public List<StudyReference> getStudiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getStudyNodeBuilder().getStudiesForTrialEnvironments(environmentIds);
	}

	@Override
	public List<NumericTraitInfo> getTraitsForNumericVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getTraitBuilder().getTraitsForNumericVariates(environmentIds);
	}

	@Override
	public List<CharacterTraitInfo> getTraitsForCharacterVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getTraitBuilder().getTraitsForCharacterVariates(environmentIds);
	}

	@Override
	public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getTraitBuilder().getTraitsForCategoricalVariates(environmentIds);
	}

	@Override
	public List<GermplasmPair> getEnvironmentsForGermplasmPairs(List<GermplasmPair> germplasmPairs, final List<Integer> experimentTypes) throws MiddlewareQueryException {
		return this.getTrialEnvironmentBuilder().getEnvironmentForGermplasmPairs(germplasmPairs, experimentTypes);
	}

	@Override
	public List<Observation> getObservationsForTraitOnGermplasms(List<Integer> traitIds, List<Integer> germplasmIds,
			List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getTraitBuilder().getObservationsForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);
	}

	@Override
	public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getTraitBuilder().getObservationsForTraits(traitIds, environmentIds);
	}

	@Override
	public List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getTraitBuilder().getObservationsForTrait(traitId, environmentIds);
	}

	@Override
	public TrialEnvironments getEnvironmentsForTraits(List<Integer> traitIds) throws MiddlewareQueryException {
		return this.getTrialEnvironmentBuilder().getEnvironmentsForTraits(traitIds);
	}

	@Override
	public List<GermplasmLocationInfo> getGermplasmLocationInfoByEnvironmentIds(Set<Integer> environmentIds)
			throws MiddlewareQueryException {
		List<GermplasmLocationInfo> result = new ArrayList<GermplasmLocationInfo>();
		if (environmentIds != null && !environmentIds.isEmpty()) {
			result.addAll(this.getBreedersQueryDao().getGermplasmLocationInfoByEnvironmentIds(environmentIds));
		}
		return result;
	}

	@Override
	public List<Integer> getTrialEnvironmentIdsForGermplasm(Set<Integer> gids) throws MiddlewareQueryException {
		List<Integer> result = new ArrayList<Integer>();
		if (gids != null && !gids.isEmpty()) {
			result.addAll(this.getBreedersQueryDao().getTrialEnvironmentIdsForGermplasm(gids));
		}
		return result;
	}
}
