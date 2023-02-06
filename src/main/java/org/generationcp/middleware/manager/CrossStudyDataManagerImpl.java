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

import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.operation.builder.TrialEnvironmentBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Implementation of the CrossStudyDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class CrossStudyDataManagerImpl extends DataManager implements CrossStudyDataManager {

	private DaoFactory daoFactory;

	@Resource
	private TrialEnvironmentBuilder trialEnvironmentBuilder;
	
	public CrossStudyDataManagerImpl() {
	}

	public CrossStudyDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public TrialEnvironments getAllTrialEnvironments() {
		return this.trialEnvironmentBuilder.getAllTrialEnvironments();
	}

	@Override
	public long countAllTrialEnvironments() {
		return this.trialEnvironmentBuilder.countAllTrialEnvironments();
	}

	@Override
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(final List<Integer> trialEnvtIds) {
		return this.trialEnvironmentBuilder.getPropertiesForTrialEnvironments(trialEnvtIds);
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
	public List<GermplasmPair> getEnvironmentsForGermplasmPairs(
		final List<GermplasmPair> germplasmPairs,
		final List<Integer> experimentTypes, final String programUUID) {
		return this.trialEnvironmentBuilder.getEnvironmentForGermplasmPairs(germplasmPairs, experimentTypes, programUUID);
	}

	@Override
	public List<Observation> getObservationsForTraitOnGermplasms(
		final List<Integer> traitIds, final List<Integer> germplasmIds,
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
	public TrialEnvironments getEnvironmentsForTraits(final List<Integer> traitIds, final String programUUID) {
		return this.trialEnvironmentBuilder.getEnvironmentsForTraits(traitIds, programUUID);
	}

}
