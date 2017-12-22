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
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO This test has no assertions (= not a test!) needs data setup so that assertions can be added.
public class CrossStudyDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private CrossStudyDataManager crossStudyDataManager;

	@Test
	public void testGetAllTrialEnvironments() {
		final TrialEnvironments environments = this.crossStudyDataManager.getAllTrialEnvironments(	);
		environments.print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + environments.size());
	}

	@Test
	public void testCountAllTrialEnvironments() {
		final long count = this.crossStudyDataManager.countAllTrialEnvironments();
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + count);
	}

	@Test
	public void testGetPropertiesForTrialEnvironments() {
		final List<Integer> environmentIds = Arrays.asList(5770, 10081, -1);
		Debug.println("testGetPropertiesForTrialEnvironments = " + environmentIds);
		final List<TrialEnvironmentProperty> properties = this.crossStudyDataManager.getPropertiesForTrialEnvironments(environmentIds);
		for (final TrialEnvironmentProperty property : properties) {
			property.print(0);
		}
		Debug.println("#RECORDS: " + properties.size());
	}

	@Test
	public void testGetStudiesForTrialEnvironments() {
		final List<Integer> environmentIds = Arrays.asList(5770, 10081);
		Debug.println(IntegrationTestBase.INDENT, "testGetStudiesForTrialEnvironments = " + environmentIds);
		final List<StudyReference> studies = this.crossStudyDataManager.getStudiesForTrialEnvironments(environmentIds);
		for (final StudyReference study : studies) {
			study.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + studies.size());
	}

	@Test
	public void testGetTraitsForNumericVariates() {
		final List<Integer> environmentIds = Arrays.asList(10081, 10082, 10083, 10084, 10085, 10086, 10087); // Rice
		final List<NumericTraitInfo> result = this.crossStudyDataManager.getTraitsForNumericVariates(environmentIds);
		for (final NumericTraitInfo trait : result) {
			trait.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetTraitsForCharacterVariates() {
		final List<Integer> environmentIds = Arrays.asList(10040, 10050, 10060, 10070); // Rice
		final List<CharacterTraitInfo> result = this.crossStudyDataManager.getTraitsForCharacterVariates(environmentIds);
		for (final CharacterTraitInfo trait : result) {
			trait.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetTraitsForCategoricalVariates() {
		final List<Integer> environmentIds = Arrays.asList(10010, 10020, 10030, 10040, 10050, 10060, 10070); // Rice
		final List<CategoricalTraitInfo> result = this.crossStudyDataManager.getTraitsForCategoricalVariates(environmentIds);
		for (final CategoricalTraitInfo trait : result) {
			trait.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetEnvironmentsForGermplasmPairs() {
		final List<GermplasmPair> pairs = new ArrayList<>();
		// Case 1: Central - Central
		pairs.add(new GermplasmPair(2434138, 1356114));

		// Include both traits and analysis variables
		final List<Integer> experimentTypes = Arrays.asList(TermId.PLOT_EXPERIMENT.getId(), TermId.AVERAGE_EXPERIMENT.getId());
		final List<GermplasmPair> result = this.crossStudyDataManager.getEnvironmentsForGermplasmPairs(pairs, experimentTypes, null);
		for (final GermplasmPair pair : result) {
			pair.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetObservationsForTraitOnGermplasms() {

		final List<Integer> traitIds = Arrays.asList(18020, 18180, 18190, 18200);
		final List<Integer> germplasmIds = Arrays.asList(1709);
		final List<Integer> environmentIds = Arrays.asList(10081, 10084, 10085, 10086);

		final List<Observation> result =
				this.crossStudyDataManager.getObservationsForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);

		for (final Observation observation : result) {
			observation.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetObservationsForTrait() {
		final int traitId = 22574;
		final List<Integer> environmentIds = Arrays.asList(5771, 5772, 5773, 5774, 5775, 5776); // Rice
		final List<TraitObservation> result = this.crossStudyDataManager.getObservationsForTrait(traitId, environmentIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetEnvironmentsForTraits() {
		final List<Integer> traitIds = Arrays.asList(22006, 22485);
		final TrialEnvironments environments = this.crossStudyDataManager.getEnvironmentsForTraits(traitIds);
		environments.print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + environments.size());
	}
}
