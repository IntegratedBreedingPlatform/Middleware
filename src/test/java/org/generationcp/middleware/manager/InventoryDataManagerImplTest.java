package org.generationcp.middleware.manager;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.uid.UIDGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InventoryDataManagerImplTest {

	private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static final int CROP_PREFIX_LENGTH = 10;
	private static final String SUFFIX_REGEX = "[a-zA-Z0-9]{" + InventoryDataManagerImpl.SUFFIX_LENGTH + "}";


	@Autowired
	private InventoryDataManagerImpl inventoryDataManager;

	private CropType crop;

	@Before
	public void setup() {
		this.crop = new CropType();
		this.crop.setPlotCodePrefix(RandomStringUtils.randomAlphanumeric(CROP_PREFIX_LENGTH));
		this.crop.setUseUUID(true);
		this.inventoryDataManager = new InventoryDataManagerImpl();
	}

	@Test
	public void testGenerateLotIds_WithExistingUUID() {
		final Lot lot = new Lot();
		final String existingLotId = RandomStringUtils.randomAlphanumeric(20);
		lot.setLotUuId(existingLotId);
		this.inventoryDataManager.generateLotIds(this.crop, Arrays.asList(lot));
		assertEquals(existingLotId, lot.getLotUuId());
	}

	@Test
	public void testGenerateLotIds_UseUUID() {
		final Lot lot = new Lot();
		this.inventoryDataManager.generateLotIds(this.crop, Arrays.asList(lot));
		assertNotNull(lot.getLotUuId());
		assertTrue(lot.getLotUuId().matches(UUID_REGEX));
	}

	@Test
	public void testGenerateLotIds_UseCustomID() {
		this.crop.setUseUUID(false);
		final Lot lot = new Lot();
		this.inventoryDataManager.generateLotIds(this.crop, Arrays.asList(lot));
		final String lotId = lot.getLotUuId();
		assertNotNull(lotId);
		assertFalse(lotId.matches(UUID_REGEX));
		assertEquals(this.crop.getPlotCodePrefix() + UIDGenerator.UID_ROOT.LOT.getRoot(), lotId.substring(0, CROP_PREFIX_LENGTH + 1));
		final String suffix = lotId.substring(CROP_PREFIX_LENGTH + 1);
		assertTrue(suffix.matches(SUFFIX_REGEX));
	}

}
