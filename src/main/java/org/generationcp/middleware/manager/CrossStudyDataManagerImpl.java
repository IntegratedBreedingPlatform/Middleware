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

import org.generationcp.middleware.domain.dms.TrialInstanceProperty;
import org.generationcp.middleware.domain.dms.TrialInstances;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmLocationInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.TrialInstanceBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the CrossStudyDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class CrossStudyDataManagerImpl extends DataManager implements CrossStudyDataManager {


	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private TrialInstanceBuilder trialInstanceBuilder;

	public CrossStudyDataManagerImpl() {
	}

	public CrossStudyDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public TrialInstances getAllTrialInstances() {
		return this.trialInstanceBuilder.getAllTrialInstances();
	}

	@Override
	public long countAllTrialInstances() {
		return this.trialInstanceBuilder.countAllTrialInstances();
	}

	@Override
	public List<TrialInstanceProperty> getPropertiesForTrialInstances(final List<Integer> instanceIds) {
		return this.trialInstanceBuilder.getPropertiesForTrialInstances(instanceIds);
	}

	@Override
	public List<NumericTraitInfo> getTraitsForNumericVariates(final List<Integer> instanceIds) {
		return this.getTraitBuilder().getTraitsForNumericVariates(instanceIds);
	}

	@Override
	public List<CharacterTraitInfo> getTraitsForCharacterVariates(final List<Integer> instanceIds) {
		return this.getTraitBuilder().getTraitsForCharacterVariates(instanceIds);
	}

	@Override
	public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(final List<Integer> instanceIds) {
		return this.getTraitBuilder().getTraitsForCategoricalVariates(instanceIds);
	}

	@Override
	public List<GermplasmPair> getInstancesForGermplasmPairs(
		final List<GermplasmPair> germplasmPairs,
		final List<Integer> experimentTypes, final String programUUID) {
		return this.trialInstanceBuilder.getInstanceForGermplasmPairs(germplasmPairs, experimentTypes, programUUID);
	}

	@Override
	public List<Observation> getObservationsForTraitOnGermplasms(
		final List<Integer> traitIds, final List<Integer> germplasmIds,
		final List<Integer> instanceIds) {
		return this.getTraitBuilder().getObservationsForTraitOnGermplasms(traitIds, germplasmIds, instanceIds);
	}

	@Override
	public List<Observation> getObservationsForTraits(final List<Integer> traitIds, final List<Integer> instanceIds) {
		return this.getTraitBuilder().getObservationsForTraits(traitIds, instanceIds);
	}

	@Override
	public List<TraitObservation> getObservationsForTrait(final int traitId, final List<Integer> instanceIds) {
		return this.getTraitBuilder().getObservationsForTrait(traitId, instanceIds);
	}

	@Override
	public TrialInstances getTrialInstancesForTraits(final List<Integer> traitIds, final String programUUID) {
		return this.trialInstanceBuilder.getInstancesForTraits(traitIds, programUUID);
	}

	@Override
	public List<GermplasmLocationInfo> getGermplasmLocationInfoByInstanceIds(final Set<Integer> instanceIds) {
		final List<GermplasmLocationInfo> result = new ArrayList<>();
		if (instanceIds != null && !instanceIds.isEmpty()) {
			result.addAll(this.getBreedersQueryDao().getGermplasmLocationInfoByInstanceIds(instanceIds));
		}
		return result;
	}

	@Override
	public List<Integer> getTrialInstanceIdsForGermplasm(final Set<Integer> gids) {
		final List<Integer> result = new ArrayList<>();
		if (gids != null && !gids.isEmpty()) {
			result.addAll(this.getBreedersQueryDao().getTrialInstanceIdsForGermplasm(gids));
		}
		return result;
	}
}
