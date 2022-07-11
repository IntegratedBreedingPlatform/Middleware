package org.generationcp.middleware.service.impl.inventory;

import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.DaoFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class LotServiceImplTest {

	@InjectMocks
	private LotServiceImpl lotService;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private LotDAO lotDAO;

	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);

		Mockito.when(this.daoFactory.getLotDao()).thenReturn(this.lotDAO);

		ReflectionTestUtils.setField(this.lotService, "daoFactory", this.daoFactory);
	}

	@Test
	public void testGetCurrentNotificationNumber_NullInventoryIds() throws MiddlewareException {
		final String breederIdentifier = "TR";

		Mockito.doReturn(null).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.lotService.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotificationNumber_EmptyInventoryIds() throws MiddlewareException {
		final String breederIdentifier = "TR";
		Mockito.doReturn(new ArrayList<String>()).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.lotService.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithExisting() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("PRE1-12");
		inventoryIDs.add("PRE1-13");
		inventoryIDs.add("PRE1-14");
		inventoryIDs.add("PRE2-1");
		inventoryIDs.add("PRE3-1");
		inventoryIDs.add("PRE35-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.lotService.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(35, currentNotationNumber.intValue());

	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithExistingCaseInsensitivity() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("PRE1-12");
		inventoryIDs.add("PRE1-13");
		inventoryIDs.add("PRE1-14");
		inventoryIDs.add("PRE2-1");
		inventoryIDs.add("PRE3-1");
		inventoryIDs.add("pre36-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.lotService.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(36, currentNotationNumber.intValue());

	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithNoMatch() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("DUMMY1-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.lotService.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals("0 must be returned because PRE is not found in DUMMY1-1", 0, currentNotationNumber.intValue());

	}


}
