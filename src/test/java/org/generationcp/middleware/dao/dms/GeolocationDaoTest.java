/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class GeolocationDaoTest extends IntegrationTestBase {

	private static final int NO_OF_GERMPLASM = 5;
	public static final Integer LOCATION_ID = 9001;

	private GeolocationDao geolocationDao;
	private GeolocationPropertyDao geolocationPropertyDao;
	private ExperimentDao experimentDao;
	private StockDao stockDao;
	private GermplasmDAO germplasmDao;
	private DmsProjectDao dmsProjectDao;
	private CVTermDao cvTermDao;
	private PhenotypeDao phenotypeDao;

	private DmsProject study;
	private DmsProject dataset;
	private Geolocation geolocation;
	private List<Germplasm> germplasm;

	@Before
	public void setUp() throws Exception {
		if (this.geolocationDao == null) {
			this.geolocationDao = new GeolocationDao(this.sessionProvder.getSession());
		}

		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());
		}

		if (this.experimentDao == null) {
			this.experimentDao = new ExperimentDao(this.sessionProvder.getSession());
		}

		if (this.stockDao == null) {
			this.stockDao = new StockDao(this.sessionProvder.getSession());
		}

		if (this.dmsProjectDao == null) {
			this.dmsProjectDao = new DmsProjectDao(this.sessionProvder.getSession());
		}

		if (this.cvTermDao == null) {
			this.cvTermDao = new CVTermDao(this.sessionProvder.getSession());
		}

		if (this.phenotypeDao == null) {
			this.phenotypeDao = new PhenotypeDao(this.sessionProvder.getSession());
		}

		if (this.geolocationPropertyDao == null) {
			this.geolocationPropertyDao = new GeolocationPropertyDao(this.sessionProvder.getSession());
		}

		if (this.study == null) {
			this.study = this.createStudy();
			this.dataset =
				this.createDataset(this.study.getName() + " - Environment Dataset", this.study.getProgramUUID(),
					DatasetTypeEnum.SUMMARY_DATA.getId(),
					this.study, this.study);
			this.createGermplasm();
			this.geolocation = this.createEnvironmentData(this.dataset, "1", Collections.<Integer>emptyList(), true, null);
		}
		this.sessionProvder.getSession().flush();

	}

	private DmsProject createStudy() {
		final DmsProject study = new DmsProject();
		study.setName("Test Project " + RandomStringUtils.randomAlphanumeric(10));
		study.setDescription("Test Project");
		study.setProgramUUID(UUID.randomUUID().toString());
		this.dmsProjectDao.save(study);
		return study;
	}

	@Test
	public void testGetStudyEnvironmentDetails() {
		final Integer geolocationId = this.geolocation.getLocationId();
		final List<TrialEnvironment> environments =
			this.geolocationDao.getTrialEnvironmentDetails(Collections.singleton(geolocationId));
		Assert.assertNotNull(environments);
		Assert.assertEquals(1, environments.size());

		final TrialEnvironment trialEnvironment = environments.get(0);
		Assert.assertEquals(geolocationId.intValue(), trialEnvironment.getId());

		final LocationDto locationDto = trialEnvironment.getLocation();
		Assert.assertEquals(LOCATION_ID, locationDto.getId());
		Assert.assertEquals("Africa Rice Centre", locationDto.getLocationName());

		final StudyReference studyReference = trialEnvironment.getStudy();
		Assert.assertEquals(this.study.getProjectId(), studyReference.getId());
		Assert.assertEquals(this.study.getName(), studyReference.getName());
		Assert.assertEquals(this.study.getDescription(), studyReference.getDescription());
	}

	@Test
	public void testGetPropertiesForStudyEnvironments() {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait);
		final String traitValue = String.valueOf(new Random().nextDouble());
		this.createGeolocationProperty(this.geolocation, trait.getCvTermId(), traitValue);

		final Integer geolocationId = this.geolocation.getLocationId();
		final List<TrialEnvironmentProperty> properties = this.geolocationDao
			.getPropertiesForTrialEnvironments(Collections.singletonList(geolocationId));
		Assert.assertNotNull(properties);
		Assert.assertEquals(2, properties.size());
		final TrialEnvironmentProperty property1 = properties.get(0);
		Assert.assertEquals(TermId.LOCATION_ID.getId(), property1.getId().intValue());
		Assert.assertEquals("LOCATION_ID", property1.getName());
		Assert.assertEquals("Location - selected (DBID)", property1.getDescription());
		final Map<Integer, String> environmentValuesMap = property1.getEnvironmentValuesMap();
		Assert.assertEquals(
			LOCATION_ID.toString(), environmentValuesMap.get(geolocationId));
		final TrialEnvironmentProperty property2 = properties.get(1);
		Assert.assertEquals(trait.getCvTermId(), property2.getId());
		Assert.assertEquals(trait.getName(), property2.getName());
		Assert.assertEquals(trait.getDefinition(), property2.getDescription());
		final Map<Integer, String> environmentValuesMap2 = property2.getEnvironmentValuesMap();
		Assert.assertEquals(
			traitValue, environmentValuesMap2.get(geolocationId));
	}

	@Test
	public void testGetEnvironmentGeolocations() {
		Assert.assertEquals(Collections.singletonList(this.geolocation),
			this.geolocationDao.getEnvironmentGeolocations(this.study.getProjectId()));
	}

	@Test
	public void testGetLocationIdByProjectNameAndDescriptionAndProgramUUID() {
		Assert.assertEquals(this.geolocation.getLocationId(), this.geolocationDao
			.getLocationIdByProjectNameAndDescriptionAndProgramUUID(this.study.getName(), "1", this.study.getProgramUUID()));
	}

	@Test
	public void testGetAllTrialEnvironments() {
		final long previousCount = this.geolocationDao.getAllTrialEnvironments().size();
		final Geolocation geolocation2 = this.createEnvironmentData(this.dataset, "2", Collections.<Integer>emptyList(), true, null);
		this.sessionProvder.getSession().flush();

		final List<TrialEnvironment> allTrialEnvironments = this.geolocationDao.getAllTrialEnvironments();
		Assert.assertEquals(previousCount + 1, allTrialEnvironments.size());
		final List<Integer> environmentIds = Lists.transform(allTrialEnvironments, new Function<TrialEnvironment, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final TrialEnvironment input) {
				return input.getId();
			}
		});
		Assert.assertTrue(environmentIds.contains(this.geolocation.getLocationId()));
		Assert.assertTrue(environmentIds.contains(geolocation2.getLocationId()));
	}

	@Test
	public void testGetEnvironmentsForTraits() {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait);
		final List<Integer> traitIds = Collections.singletonList(trait.getCvTermId());
		final Geolocation geolocation2 = this.createEnvironmentData(this.dataset, "2", traitIds, true, null);
		final Geolocation geolocation3 = this.createEnvironmentData(this.dataset, "3", traitIds, true, null);
		this.sessionProvder.getSession().flush();

		final TrialEnvironments environmentsForTraits = this.geolocationDao.getEnvironmentsForTraits(traitIds, this.study.getProgramUUID());
		Assert.assertNotNull(environmentsForTraits);
		final List<Integer> environmentIds =
			Lists.transform(Lists.newArrayList(environmentsForTraits.getTrialEnvironments()), new Function<TrialEnvironment, Integer>() {

				@Nullable
				@Override
				public Integer apply(@Nullable final TrialEnvironment input) {
					return input.getId();
				}
			});
		Assert.assertFalse(environmentIds.contains(this.geolocation.getLocationId()));
		Assert.assertTrue(environmentIds.contains(geolocation2.getLocationId()));
		Assert.assertTrue(environmentIds.contains(geolocation3.getLocationId()));
	}

	@Test
	public void testGetEnvironmentsForTraitsWithPendingExperiment() {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait);
		final List<Integer> traitIds = Collections.singletonList(trait.getCvTermId());
		final Geolocation geolocation2 = this.createEnvironmentData(this.dataset, "2", traitIds, true, null);
		final Geolocation geolocation3 = this.createEnvironmentData(this.dataset, "3", traitIds, false, null);

		final TrialEnvironments environmentsForTraits = this.geolocationDao.getEnvironmentsForTraits(traitIds, this.study.getProgramUUID());
		Assert.assertNotNull(environmentsForTraits);
		final List<Integer> environmentIds =
			Lists.transform(Lists.newArrayList(environmentsForTraits.getTrialEnvironments()), new Function<TrialEnvironment, Integer>() {

				@Nullable
				@Override
				public Integer apply(@Nullable final TrialEnvironment input) {
					return input.getId();
				}
			});
		Assert.assertEquals("Only 1 environment with accepted value", 1, environmentIds.size());
		Assert.assertTrue(environmentIds.contains(geolocation2.getLocationId()));
		Assert.assertFalse(environmentIds.contains(geolocation3.getLocationId()));
	}

	@Test
	public void testGetEnvironmentsForTraitsWithPendingAndApprovedExperiment() {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait);
		final List<Integer> traitIds = Collections.singletonList(trait.getCvTermId());
		final Geolocation geolocation2 = this.createEnvironmentData(this.dataset, "2", traitIds, true, null);
		final Geolocation geolocation3 = this.createEnvironmentData(this.dataset, "3", traitIds, false, null);
		this.createEnvironmentData(this.dataset, "3", traitIds, true, geolocation3);
		this.sessionProvder.getSession().flush();

		final TrialEnvironments environmentsForTraits = this.geolocationDao.getEnvironmentsForTraits(traitIds, this.study.getProgramUUID());
		Assert.assertNotNull(environmentsForTraits);
		final List<Integer> environmentIds =
			Lists.transform(Lists.newArrayList(environmentsForTraits.getTrialEnvironments()), new Function<TrialEnvironment, Integer>() {

				@Nullable
				@Override
				public Integer apply(@Nullable final TrialEnvironment input) {
					return input.getId();
				}
			});
		Assert.assertEquals("Only environments with accepted value will be retrieved.", 2, environmentIds.size());
		Assert.assertTrue(environmentIds.contains(geolocation2.getLocationId()));
		Assert.assertTrue(environmentIds.contains(geolocation3.getLocationId()));
	}

	private DmsProject createDataset(final String name, final String programUUID, final int datasetType, final DmsProject parent,
		final DmsProject study) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(name);
		dataset.setDescription(name);
		dataset.setProgramUUID(programUUID);
		dataset.setDatasetType(new DatasetType(datasetType));
		dataset.setParent(parent);
		dataset.setStudy(study);
		this.dmsProjectDao.save(dataset);
		return dataset;
	}

	private void createGermplasm() {
		this.germplasm = new ArrayList<>();
		for (int i = 0; i < NO_OF_GERMPLASM; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.germplasmDao.save(germplasm);
			this.germplasm.add(germplasm);
		}
	}

	private Geolocation createEnvironmentData(final DmsProject project, final String instance, final List<Integer> traitIds,
		final boolean withValue, Geolocation geolocation) {
		if (geolocation == null) {
			geolocation = new Geolocation();
			geolocation.setDescription(instance);
			this.geolocationDao.saveOrUpdate(geolocation);
			this.createGeolocationProperty(geolocation, TermId.LOCATION_ID.getId(), LOCATION_ID.toString());
		}
		for (final Germplasm germplasm : this.germplasm) {
			final StockModel stockModel = new StockModel();
			stockModel.setIsObsolete(false);
			stockModel.setUniqueName(RandomStringUtils.randomAlphanumeric(10));
			stockModel.setGermplasm(germplasm);
			stockModel.setCross("-");
			stockModel.setProject(this.study);
			this.stockDao.saveOrUpdate(stockModel);

			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setProject(project);
			experimentModel.setStock(stockModel);
			this.experimentDao.saveOrUpdate(experimentModel);

			for (final Integer traitId : traitIds) {
				final Phenotype phenotype = new Phenotype();
				phenotype.setObservableId(traitId);
				phenotype.setExperiment(experimentModel);
				if (withValue) {
					phenotype.setValue(String.valueOf(new Random().nextDouble()));
				}
				this.phenotypeDao.save(phenotype);
			}

		}

		return geolocation;
	}

	private void createGeolocationProperty(final Geolocation geolocation, final Integer variableId, final String value) {
		final GeolocationProperty geolocationProperty = new GeolocationProperty();
		geolocationProperty.setType(variableId);
		geolocationProperty.setValue(value);
		geolocationProperty.setRank(1);
		geolocationProperty.setGeolocation(geolocation);
		this.geolocationPropertyDao.save(geolocationProperty);
	}
}
