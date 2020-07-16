
package org.generationcp.middleware.service;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.operation.builder.LotBuilder;
import org.generationcp.middleware.operation.builder.TransactionBuilder;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceImplTest {

	private static final String TEST_LIST_NAME = "LIST-TEST";
	public static final int USER_ID = 1;
	public static final int PERSON_ID = 2;

	@Mock
	private HibernateSessionProvider sessionProvider;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private LotDAO lotDAO;

	@Mock
	private TransactionDAO transactionDAO;

	@Mock
	private GermplasmListDAO germplasmListDAO;

	@Mock
	private GermplasmListDataDAO germplasmListDataDAO;

	@Mock
	private LocationDAO locationDAO;

	@Mock
	private CVTermDao cvTermDAO;

	@Mock
	private LotBuilder lotBuilder;

	@Mock
	private TransactionBuilder transactionBuilder;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private UserService userService;

	@InjectMocks
	private final InventoryServiceImpl inventoryServiceImpl = new InventoryServiceImpl();

	@Mock
	private CropType cropType;

	@Before
	public void setup() {

		when(this.daoFactory.getTransactionDAO()).thenReturn(this.transactionDAO);
		when(this.daoFactory.getLotDao()).thenReturn(this.lotDAO);
		when(this.daoFactory.getLocationDAO()).thenReturn(this.locationDAO);
		when(this.daoFactory.getGermplasmListDataDAO()).thenReturn(this.germplasmListDataDAO);
		when(this.daoFactory.getGermplasmListDAO()).thenReturn(this.germplasmListDAO);
		when(this.daoFactory.getCvTermDao()).thenReturn(this.cvTermDAO);

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		final Person person = new Person();
		person.setId(PERSON_ID);
		workbenchUser.setUserid(USER_ID);
		workbenchUser.setPerson(person);
		when(this.userService.getUserById(USER_ID)).thenReturn(workbenchUser);
	}

	@Test
	public void testGetCurrentNotificationNumber_NullInventoryIds() throws MiddlewareException {
		final String breederIdentifier = "TR";

		Mockito.doReturn(null).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(0, currentNotificationNumber.intValue());
	}

	@Test
	public void testGetCurrentNotificationNumber_EmptyInventoryIds() throws MiddlewareException {
		final String breederIdentifier = "TR";
		Mockito.doReturn(new ArrayList<String>()).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotificationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
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
		final Integer currentNotationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals(35, currentNotationNumber.intValue());

	}

	@Test
	public void testGetCurrentNotationNumberForBreederIdentifier_WithNoMatch() throws MiddlewareException {
		final List<String> inventoryIDs = new ArrayList<>();
		inventoryIDs.add("DUMMY1-1");

		final String breederIdentifier = "PRE";
		Mockito.doReturn(inventoryIDs).when(this.lotDAO).getInventoryIDsWithBreederIdentifier(breederIdentifier);
		final Integer currentNotationNumber = this.inventoryServiceImpl.getCurrentNotationNumberForBreederIdentifier(breederIdentifier);
		Assert.assertEquals("0 must be returned because PRE is not found in DUMMY1-1", 0, currentNotationNumber.intValue());

	}

	private GermplasmList createGermplasmListTestData(final Integer listId, final String germplasmListType) {
		final GermplasmList germplasmList = new GermplasmList(listId);
		germplasmList.setType(germplasmListType);
		germplasmList.setName(TEST_LIST_NAME);
		return germplasmList;
	}

	@Test
	public void testGetGermplasmListData_TypeLST() {
		final Integer listId = 1;
		final String germplasmListType = GermplasmListType.LST.name();
		final GermplasmList germplasmList = this.createGermplasmListTestData(listId, germplasmListType);

		this.inventoryServiceImpl.getGermplasmListData(germplasmList, germplasmListType);

		Mockito.verify(this.germplasmListDataDAO).getByListId(listId);
	}

}
