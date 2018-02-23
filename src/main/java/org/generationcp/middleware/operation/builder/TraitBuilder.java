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
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTerm;

public class TraitBuilder extends Builder {

	private static final List<Integer> NUMERIC_VARIABLE_TYPE = Arrays.asList(TermId.NUMERIC_VARIABLE.getId(),
			TermId.DATE_VARIABLE.getId());

	public TraitBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<NumericTraitInfo> getTraitsForNumericVariates(final List<Integer> environmentIds) {
		final List<NumericTraitInfo> numericTraitInfoList = new ArrayList<>();
		final List<CVTerm> variableTerms = new ArrayList<>();
		final List<Integer> variableIds = new ArrayList<>();

		// Get locationCount, germplasmCount, observationCount, minValue,
		// maxValue
		// Retrieve traits environments
		variableTerms.addAll(this.getCvTermDao().getVariablesByType(TraitBuilder.NUMERIC_VARIABLE_TYPE));
		variableIds.addAll(this.getVariableIds(variableTerms));
		numericTraitInfoList.addAll(this.getPhenotypeDao().getNumericTraitInfoList(environmentIds, variableIds));

		Collections.sort(numericTraitInfoList);

		if (numericTraitInfoList.isEmpty()) {
			return numericTraitInfoList;
		}

		// Get median value
		this.getMedianValues(numericTraitInfoList, environmentIds);

		// Set name and description
		for (final NumericTraitInfo traitInfo : numericTraitInfoList) {
			for (final CVTerm variable : variableTerms) {
				if (traitInfo.getId() == variable.getCvTermId()) {
					traitInfo.setName(variable.getName());
					traitInfo.setDescription(variable.getDefinition());
					break;
				}
			}
		}

		return numericTraitInfoList;
	}

	public List<CharacterTraitInfo> getTraitsForCharacterVariates(final List<Integer> environmentIds) {
		final List<CharacterTraitInfo> characterTraitInfoList = new ArrayList<>();
		final List<CVTerm> variableTerms = new ArrayList<>();

		// Get character variable terms
		variableTerms.addAll(this.getCvTermDao().getVariablesByType(Arrays.asList(TermId.CHARACTER_VARIABLE.getId())));

		// Get location, germplasm and observation counts
		final List<TraitInfo> traitInfoList = this.getTraitCounts(this.getVariableIds(variableTerms), environmentIds);
		// Set name and description
		for (final TraitInfo traitInfo : traitInfoList) {
			for (final CVTerm variable : variableTerms) {
				if (traitInfo.getId() == variable.getCvTermId()) {
					traitInfo.setName(variable.getName());
					traitInfo.setDescription(variable.getDefinition());
					break;
				}
			}
		}

		if (traitInfoList.isEmpty()) {
			return characterTraitInfoList;
		}

		// Create characterTraitInfoList from TraitInfo with counts
		Collections.sort(traitInfoList);
		for (final TraitInfo trait : traitInfoList) {
			characterTraitInfoList.add(new CharacterTraitInfo(trait));
		}

		// Get the distinct phenotype values from the databases
		final Map<Integer, List<String>> localTraitValues = this.getPhenotypeDao()
				.getCharacterTraitInfoValues(environmentIds, characterTraitInfoList);

		for (final CharacterTraitInfo traitInfo : characterTraitInfoList) {
			final List<String> values = new ArrayList<>();
			final int traitId = traitInfo.getId();
			if (localTraitValues != null && localTraitValues.containsKey(traitId)) {
				values.addAll(localTraitValues.get(traitId));
			}
			Collections.sort(values);
			traitInfo.setValues(values);
		}

		return characterTraitInfoList;
	}

	public List<CategoricalTraitInfo> getTraitsForCategoricalVariates(final List<Integer> environmentIds) {
		final List<CategoricalTraitInfo> localCategTraitList = new ArrayList<>();
		final List<CategoricalTraitInfo> finalTraitInfoList = new ArrayList<>();

		// Get locationCount, germplasmCount, observationCount
		final List<TraitInfo> localTraitInfoList = new ArrayList<>();

		localTraitInfoList.addAll(this.getPhenotypeDao().getTraitInfoCounts(environmentIds));

		Collections.sort(localTraitInfoList);

		for (final TraitInfo localObservedTrait : localTraitInfoList) {
			final CategoricalTraitInfo categoricalTrait = new CategoricalTraitInfo(localObservedTrait);
			localCategTraitList.add(categoricalTrait);
		}

		// Set name, description and get categorical domain values and count per
		// value
		if (!localCategTraitList.isEmpty()) {
			finalTraitInfoList.addAll(this.getCvTermDao().setCategoricalVariables(localCategTraitList));
			this.getPhenotypeDao().setCategoricalTraitInfoValues(finalTraitInfoList, environmentIds);
		}

		return finalTraitInfoList;

	}

	private List<TraitInfo> getTraitCounts(final List<Integer> variableIds, final List<Integer> environmentIds) {
		final List<TraitInfo> traitInfoList = new ArrayList<>();
		// Get locationCount, germplasmCount, observationCount
		traitInfoList.addAll(this.getPhenotypeDao().getTraitInfoCounts(environmentIds, variableIds));
		return traitInfoList;
	}

	private List<Integer> getVariableIds(final List<CVTerm> variableTerms) {
		final List<Integer> variableIds = new ArrayList<>();
		for (final CVTerm term : variableTerms) {
			variableIds.add(term.getCvTermId());
		}
		return variableIds;

	}

	private void getMedianValues(final List<NumericTraitInfo> numericTraitInfoList,
			final List<Integer> environmentIds) {

		final Map<Integer, List<Double>> traitValues = new HashMap<>();

		// for large crop, break up DB calls per trait to avoid out of memory
		// error for large DBs
		if (environmentIds.size() > 1000) {
			for (final NumericTraitInfo traitInfo : numericTraitInfoList) {
				traitValues.putAll(this.getPhenotypeDao().getNumericTraitInfoValues(environmentIds, traitInfo.getId()));
				this.getMedianValue(traitValues, traitInfo);
			}
		} else {
			traitValues.putAll(this.getPhenotypeDao().getNumericTraitInfoValues(environmentIds, numericTraitInfoList));
			for (final NumericTraitInfo traitInfo : numericTraitInfoList) {
				this.getMedianValue(traitValues, traitInfo);
			}
		}
	}

	private void getMedianValue(final Map<Integer, List<Double>> traitValues, final NumericTraitInfo traitInfo) {
		final List<Double> values = traitValues.get(traitInfo.getId());
		Collections.sort(values);

		// if the number of values is odd
		double medianValue = values.get(values.size() / 2);

		// change if the number of values is even
		if (values.size() % 2 == 0) {
			final double middleNumOne = values.get(values.size() / 2 - 1);
			final double middleNumTwo = values.get(values.size() / 2);
			medianValue = (middleNumOne + middleNumTwo) / 2;
		}
		traitInfo.setMedianValue(medianValue);
	}

	public List<Observation> getObservationsForTraitOnGermplasms(final List<Integer> traitIds,
			final List<Integer> germplasmIds, final List<Integer> environmentIds) {

		List<Observation> observations = new ArrayList<>();
		if (environmentIds != null && !environmentIds.isEmpty()) {
			observations = this.getPhenotypeDao().getObservationForTraitOnGermplasms(traitIds, germplasmIds,
					environmentIds);
		}
		return observations;
	}

	public List<Observation> getObservationsForTraits(final List<Integer> traitIds,
			final List<Integer> environmentIds) {

		List<Observation> observations = new ArrayList<>();
		if (!environmentIds.isEmpty()) {
			observations = this.getPhenotypeDao().getObservationForTraits(traitIds, environmentIds, 0, 0);
		}
		return observations;
	}

	public List<TraitObservation> getObservationsForTrait(final int traitId, final List<Integer> environmentIds) {
		List<TraitObservation> traitObservations = new ArrayList<>();
		if (!environmentIds.isEmpty()) {
			traitObservations = this.getPhenotypeDao().getObservationsForTrait(traitId, environmentIds);
		}
		return traitObservations;
	}
}
