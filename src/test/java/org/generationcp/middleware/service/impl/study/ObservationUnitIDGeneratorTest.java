package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ObservationUnitIDGeneratorTest {

	public static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static final int CROP_PREFIX_LENGTH = 10;
	private static final String SUFFIX_REGEX = "[a-zA-Z0-9]{" + ObservationUnitIDGenerator.SUFFIX_LENGTH + "}";

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
		ObservationUnitIDGenerator.generateObservationUnitIds(this.crop, Arrays.asList(experiment));
		assertEquals(existingObsUnitId, experiment.getObsUnitId());
	}

	@Test
	public void testGenerateObservationUnitIds_UseUUID() {
		final ExperimentModel experiment = new ExperimentModel();
		ObservationUnitIDGenerator.generateObservationUnitIds(this.crop, Arrays.asList(experiment));
		assertNotNull(experiment.getObsUnitId());
		assertTrue(experiment.getObsUnitId().matches(UUID_REGEX));
	}

	@Test
	public void testGenerateObservationUnitIds_UseCustomID() {
		this.crop.setUseUUID(false);
		final ExperimentModel experiment = new ExperimentModel();
		ObservationUnitIDGenerator.generateObservationUnitIds(this.crop, Arrays.asList(experiment));
		final String obsUnitId = experiment.getObsUnitId();
		assertNotNull(obsUnitId);
		assertFalse(obsUnitId.matches(UUID_REGEX));
		assertEquals(this.crop.getPlotCodePrefix() + ObservationUnitIDGenerator.UID_ROOT.getRoot(), obsUnitId.substring(0, CROP_PREFIX_LENGTH + 1));
		final String suffix = obsUnitId.substring(CROP_PREFIX_LENGTH + 1, obsUnitId.length());
		assertTrue(suffix.matches(SUFFIX_REGEX));
	}

}
