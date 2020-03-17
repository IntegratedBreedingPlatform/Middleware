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

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// TODO This test has no assertions (= not a test!) needs data setup so that assertions can be added.
public class CrossStudyDataManagerImplTest extends IntegrationTestBase {


	@Autowired
	private CrossStudyDataManager crossStudyDataManager;

	private DaoFactory daoFactory;

	@Before
	public void setUp() throws Exception {

		this.daoFactory = new DaoFactory(this.sessionProvder);

	}

	@Test
	public void testGetAllStudyEnvironments() {
		final TrialEnvironments environments = this.crossStudyDataManager.getAllTrialEnvironments();
		environments.print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + environments.size());
	}

	@Test
	public void testCountAllStudyEnvironments() {
		final long count = this.crossStudyDataManager.countAllTrialEnvironments();
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + count);
	}

	@Test
	public void testGetPropertiesForStudyEnvironments() {
		final List<Integer> environmentIds = Arrays.asList(5770, 10081, -1);
		Debug.println("testGetPropertiesForStudyEnvironments = " + environmentIds);
		final List<TrialEnvironmentProperty> properties = this.crossStudyDataManager.getPropertiesForTrialEnvironments(environmentIds);
		for (final TrialEnvironmentProperty property : properties) {
			property.print(0);
		}
		Debug.println("#RECORDS: " + properties.size());
	}

	@Test
	public void testGetTraitsForNumericVariates() {
		final List<Integer> environmentIds = Arrays.asList(10081, 10082, 10083, 10084, 10085, 10086, 10087); // Rice
		final List<Integer> traitIds = Arrays.asList(20081, 20082, 20083);
		final List<NumericTraitInfo> result = this.crossStudyDataManager.getTraitsForNumericVariates(environmentIds, traitIds);
		for (final NumericTraitInfo trait : result) {
			trait.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetTraitsForCharacterVariates() {
		final List<Integer> environmentIds = Arrays.asList(10040, 10050, 10060, 10070); // Rice
		final List<Integer> traitIds = Arrays.asList(20081, 20082, 20083);
		final List<CharacterTraitInfo> result = this.crossStudyDataManager.getTraitsForCharacterVariates(environmentIds, traitIds);
		for (final CharacterTraitInfo trait : result) {
			trait.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, "#RECORDS: " + result.size());
	}

	@Test
	public void testGetTraitsForCategoricalVariates() {
		final List<Integer> environmentIds = Arrays.asList(10010, 10020, 10030, 10040, 10050, 10060, 10070); // Rice
		final List<Integer> traitIds = Arrays.asList(20081, 20082, 20083);
		final List<CategoricalTraitInfo> result = this.crossStudyDataManager.getTraitsForCategoricalVariates(environmentIds, traitIds);
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
		final List<Integer> germplasmIds = Collections.singletonList(1709);
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

		final String study1 = "Study1";
		final String study2 = "Study2";
		final String study3 = "Study3";
		final String firstProgramUUID = UUID.randomUUID().toString();

		final String afghanistanLocationId = "1";
		final String albaniaLocationId = "2";
		final int trait1TermId = this.createTestData(study1, firstProgramUUID, afghanistanLocationId);
		final int trait2TermId = this.createTestData(study2, firstProgramUUID, albaniaLocationId);

		final String secondProgramUUID = UUID.randomUUID().toString();
		final int trait3TermId = this.createTestData(study3, secondProgramUUID, "3");

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSessionFactory().getCurrentSession().flush();

		final TrialEnvironments environments = this.crossStudyDataManager.getEnvironmentsForTraits(
			Arrays.asList(trait1TermId, trait2TermId, trait3TermId),
			firstProgramUUID);

		final List<TrialEnvironment> trialEnvironments = new ArrayList<>(environments.getTrialEnvironments());

		// Only return environments with traits that belong to the specified program
		Assert.assertEquals(2, environments.size());
		Assert.assertEquals(study1, trialEnvironments.get(0).getStudy().getName());
		Assert.assertEquals("Afghanistan", trialEnvironments.get(0).getLocation().getLocationName());
		Assert.assertEquals(study2, trialEnvironments.get(1).getStudy().getName());
		Assert.assertEquals("Albania", trialEnvironments.get(1).getLocation().getLocationName());

	}

	private int createTestData(final String studyName, final String programUUID, final String locationId) {

		final DmsProject study = new DmsProject();
		study.setName(studyName);
		study.setDescription(studyName);
		study.setProgramUUID(programUUID);
		this.daoFactory.getDmsProjectDAO().save(study);

		final DmsProject plot = new DmsProject();
		plot.setName(studyName + " - Plot Dataset");
		plot.setDescription(studyName + " - Plot Dataset");
		plot.setProgramUUID(programUUID);
		plot.setParent(study);
		plot.setStudy(study);
		this.daoFactory.getDmsProjectDAO().save(plot);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.daoFactory.getGermplasmDao().save(germplasm);

		final StockModel stockModel = new StockModel();
		stockModel.setName("Germplasm 1");
		stockModel.setIsObsolete(false);
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setUniqueName("1");
		stockModel.setGermplasm(germplasm);
		this.daoFactory.getStockDao().saveOrUpdate(stockModel);

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(plot);
		experimentModel.setStock(stockModel);
		final String customUnitID = RandomStringUtils.randomAlphabetic(10);
		experimentModel.setObsUnitId(customUnitID);
		this.daoFactory.getExperimentDao().saveOrUpdate(experimentModel);

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(TermId.LOCATION_ID.getId());
		experimentProperty.setRank(1);
		experimentProperty.setValue(locationId);
		this.daoFactory.getExperimentPropertyDao().saveOrUpdate(experimentProperty);

		final CVTerm trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.daoFactory.getCvTermDao().save(trait);

		final Phenotype phenotype = new Phenotype();
		phenotype.setObservableId(trait.getCvTermId());
		phenotype.setExperiment(experimentModel);
		phenotype.setValue("data");
		this.daoFactory.getPhenotypeDAO().save(phenotype);

		return trait.getCvTermId();

	}

}
