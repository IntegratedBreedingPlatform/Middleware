package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ObservationUnitIDGeneratorImplTest {
	
	public static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static final int CROP_PREFIX_LENGTH = 10;
	private static final String SUFFIX_REGEX = "[a-zA-Z0-9]{" + ObservationUnitIDGeneratorImpl.SUFFIX_LENGTH + "}";
	
	private ObservationUnitIDGeneratorImpl obsIDGenerator = new ObservationUnitIDGeneratorImpl();
	private Integer studyId = new Random().nextInt();
	private CropType crop;

	@Before
	public void setup() {
		this.crop = new CropType();
		this.crop.setPlotCodePrefix(RandomStringUtils.randomAlphanumeric(CROP_PREFIX_LENGTH));
		this.crop.setUseUUID(true);
	}

	@Test
	public void testGenerateObservationUnitIds_WithExistingUUID() {
		final ExperimentModel experiment = new ExperimentModel();
		final String existingObsUnitId = RandomStringUtils.randomAlphanumeric(20);
		experiment.setObsUnitId(existingObsUnitId);
		this.obsIDGenerator.generateObservationUnitIds(this.crop, Arrays.asList(experiment));
		assertEquals(existingObsUnitId, experiment.getObsUnitId());
	}
	
	@Test
	public void testGenerateObservationUnitIds_UseUUID() {
		final ExperimentModel experiment = new ExperimentModel();
		this.obsIDGenerator.generateObservationUnitIds(this.crop, Arrays.asList(experiment));
		assertNotNull(experiment.getObsUnitId());
		assertTrue(experiment.getObsUnitId().matches(UUID_REGEX));
	}
	
	@Test
	public void testGenerateObservationUnitIds_UseCustomID() {
		this.crop.setUseUUID(false);
		final ExperimentModel experiment = new ExperimentModel();
		this.obsIDGenerator.generateObservationUnitIds(this.crop, Arrays.asList(experiment));
		final String obsUnitId = experiment.getObsUnitId();
		assertNotNull(obsUnitId);
		assertFalse(obsUnitId.matches(UUID_REGEX));
		assertEquals(this.crop.getPlotCodePrefix() + ObservationUnitIDGeneratorImpl.MID_STRING, obsUnitId.substring(0, CROP_PREFIX_LENGTH + 1));
		final String suffix = obsUnitId.substring(CROP_PREFIX_LENGTH + 1, obsUnitId.length());
		assertTrue(suffix.matches(SUFFIX_REGEX));
	}

}
