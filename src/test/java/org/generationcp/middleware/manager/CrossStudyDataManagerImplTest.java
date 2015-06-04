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

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;

public class CrossStudyDataManagerImplTest extends DataManagerIntegrationTest {

	private static CrossStudyDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		CrossStudyDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getCrossStudyDataManager();
	}

	@Test
	public void testGetAllTrialEnvironments() throws Exception {
		TrialEnvironments environments = CrossStudyDataManagerImplTest.manager.getAllTrialEnvironments(false);
		environments.print(MiddlewareIntegrationTest.INDENT);
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + environments.size());
	}

	@Test
	public void testCountAllTrialEnvironments() throws Exception {
		long count = CrossStudyDataManagerImplTest.manager.countAllTrialEnvironments();
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + count);
	}

	@Test
	public void testGetPropertiesForTrialEnvironments() throws Exception {
		List<Integer> environmentIds = Arrays.asList(5770, 10081, -1);
		Debug.println("testGetPropertiesForTrialEnvironments = " + environmentIds);
		List<TrialEnvironmentProperty> properties = CrossStudyDataManagerImplTest.manager.getPropertiesForTrialEnvironments(environmentIds);
		for (TrialEnvironmentProperty property : properties) {
			property.print(0);
		}
		Debug.println("#RECORDS: " + properties.size());
	}

	@Test
	public void testGetStudiesForTrialEnvironments() throws Exception {
		List<Integer> environmentIds = Arrays.asList(5770, 10081);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetStudiesForTrialEnvironments = " + environmentIds);
		List<StudyReference> studies = CrossStudyDataManagerImplTest.manager.getStudiesForTrialEnvironments(environmentIds);
		for (StudyReference study : studies) {
			study.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + studies.size());
	}

	@Test
	public void testGetTraitsForNumericVariates() throws Exception {
		List<Integer> environmentIds = Arrays.asList(10081, 10082, 10083, 10084, 10085, 10086, 10087); // Rice
		List<NumericTraitInfo> result = CrossStudyDataManagerImplTest.manager.getTraitsForNumericVariates(environmentIds);
		for (NumericTraitInfo trait : result) {
			trait.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetTraitsForCharacterVariates() throws Exception {
		List<Integer> environmentIds = Arrays.asList(10040, 10050, 10060, 10070); // Rice
		List<CharacterTraitInfo> result = CrossStudyDataManagerImplTest.manager.getTraitsForCharacterVariates(environmentIds);
		for (CharacterTraitInfo trait : result) {
			trait.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetTraitsForCategoricalVariates() throws Exception {
		List<Integer> environmentIds = Arrays.asList(10010, 10020, 10030, 10040, 10050, 10060, 10070); // Rice
		List<CategoricalTraitInfo> result = CrossStudyDataManagerImplTest.manager.getTraitsForCategoricalVariates(environmentIds);
		for (CategoricalTraitInfo trait : result) {
			trait.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetEnvironmentsForGermplasmPairs() throws Exception {
		List<GermplasmPair> pairs = new ArrayList<GermplasmPair>();

		// Case 1: Central - Central
		pairs.add(new GermplasmPair(2434138, 1356114));

		// Case 2: Local - Local
		pairs.add(new GermplasmPair(-1, -2));

		// Case 3: Central - Local

		List<GermplasmPair> result = CrossStudyDataManagerImplTest.manager.getEnvironmentsForGermplasmPairs(pairs);
		for (GermplasmPair pair : result) {
			pair.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetObservationsForTraitOnGermplasms() throws Exception {

		List<Integer> traitIds = Arrays.asList(18020, 18180, 18190, 18200);
		List<Integer> germplasmIds = Arrays.asList(1709);
		List<Integer> environmentIds = Arrays.asList(10081, 10084, 10085, 10086);

		List<Observation> result =
				CrossStudyDataManagerImplTest.manager.getObservationsForTraitOnGermplasms(traitIds, germplasmIds, environmentIds);

		for (Observation observation : result) {
			observation.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetObservationsForTrait() throws Exception {
		int traitId = 22574;
		List<Integer> environmentIds = Arrays.asList(5771, 5772, 5773, 5774, 5775, 5776); // Rice
		List<TraitObservation> result = CrossStudyDataManagerImplTest.manager.getObservationsForTrait(traitId, environmentIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetEnvironmentsForTraits() throws Exception {
		List<Integer> traitIds = Arrays.asList(22006, 22485);
		TrialEnvironments environments = CrossStudyDataManagerImplTest.manager.getEnvironmentsForTraits(traitIds);
		environments.print(MiddlewareIntegrationTest.INDENT);
		Debug.println(MiddlewareIntegrationTest.INDENT, "#RECORDS: " + environments.size());
	}
}
