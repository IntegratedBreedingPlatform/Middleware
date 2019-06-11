package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GeolocationPropertyDaoTest extends IntegrationTestBase {

	private GeolocationDao geolocationDao;
	private GeolocationPropertyDao geolocationPropDao;
	private DmsProjectDao dmsProjectDao;
	private ExperimentDao experimentDao;
	private CVTermDao cvTermDao;

	private DmsProject study;
	private CVTerm variable1;
	private CVTerm variable2;

	@Before
	public void test() {
		if (this.geolocationDao == null) {
			this.geolocationDao = new GeolocationDao();
			this.geolocationDao.setSession(this.sessionProvder.getSession());
		}

		if (this.geolocationPropDao == null) {
			this.geolocationPropDao = new GeolocationPropertyDao();
			this.geolocationPropDao.setSession(this.sessionProvder.getSession());
		}

		if (this.dmsProjectDao == null) {
			this.dmsProjectDao = new DmsProjectDao();
			this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		}

		if (this.experimentDao == null) {
			this.experimentDao = new ExperimentDao();
			this.experimentDao.setSession(this.sessionProvder.getSession());
		}

		if (this.cvTermDao == null) {
			this.cvTermDao = new CVTermDao();
			this.cvTermDao.setSession(this.sessionProvder.getSession());
		}

		if (this.study == null) {
			this.study = this.createStudy(RandomStringUtils.randomAlphabetic(20));
		}

		if (this.variable1 == null) {
			this.variable1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
			this.variable2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
			this.cvTermDao.save(this.variable1);
			this.cvTermDao.save(this.variable2);
		}
	}

	@Test
	public void testGetGeolocationPropsAndValuesByGeolocation() {
		final DmsProject dataset =
			this.createDataset(RandomStringUtils.randomAlphabetic(20), DatasetTypeEnum.SUMMARY_DATA.getId(), this.study);
		final Integer geolocationId =
			this.createEnvironmentData(dataset, Arrays.asList(this.variable1.getCvTermId(), this.variable2.getCvTermId()));

		final Map<String, String> propertiesMap =
			this.geolocationPropDao.getGeolocationPropsAndValuesByGeolocation(geolocationId);
		Assert.assertNotNull(propertiesMap);
		Assert.assertEquals(2, propertiesMap.size());
		Assert.assertNotNull(propertiesMap.get(this.variable1.getDefinition()));
		Assert.assertNotNull(propertiesMap.get(this.variable2.getDefinition()));
	}

	@Test
	public void testDeleteGeolocationPropertyValueInProject() {
		final Integer geolocationIdMain =
			this.createEnvironmentData(this.study, Arrays.asList(this.variable1.getCvTermId(), this.variable2.getCvTermId()));
		final DmsProject dataset =
			this.createDataset(RandomStringUtils.randomAlphabetic(20), DatasetTypeEnum.SUMMARY_DATA.getId(), this.study);
		final Integer geolocationIdDataset =
			this.createEnvironmentData(dataset, Arrays.asList(this.variable1.getCvTermId(), this.variable2.getCvTermId()));

		// Verify that geolocation props exist before deletion
		final Map<String, String> studyProperties =
			this.geolocationPropDao.getGeolocationPropsAndValuesByGeolocation(geolocationIdMain);
		Assert.assertNotNull(studyProperties);
		Assert.assertFalse(studyProperties.isEmpty());
		final Map<String, String> datasetProperties =
			this.geolocationPropDao.getGeolocationPropsAndValuesByGeolocation(geolocationIdDataset);
		Assert.assertNotNull(datasetProperties);
		Assert.assertFalse(datasetProperties.isEmpty());
	}

	private DmsProject createStudy(final String name) {
		final DmsProject project = new DmsProject();
		project.setName(name);
		project.setDescription(name);
		this.dmsProjectDao.save(project);
		return project;
	}

	private DmsProject createDataset(final String name, final int datasetType, final DmsProject parent) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(name);
		dataset.setDescription(name);
		dataset.setDatasetType(new DatasetType(datasetType));
		dataset.setParent(parent);
		dataset.setStudy(parent);
		this.dmsProjectDao.save(dataset);
		return dataset;
	}

	private Integer createEnvironmentData(final DmsProject project, final List<Integer> geolocPropVariables) {
		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

		// Create experiments for environment
		for (int j = 0; j < 2; j++) {
			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setProject(project);
			this.experimentDao.saveOrUpdate(experimentModel);
		}

		int rank = 1;
		for (final Integer variableId : geolocPropVariables) {
			final GeolocationProperty prop = new GeolocationProperty();
			prop.setType(variableId);
			prop.setGeolocation(geolocation);
			prop.setRank(rank++);
			prop.setValue(RandomStringUtils.randomAlphabetic(10));
			this.geolocationPropDao.save(prop);
		}

		return geolocation.getLocationId();
	}

}
