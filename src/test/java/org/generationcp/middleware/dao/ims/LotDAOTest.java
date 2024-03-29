package org.generationcp.middleware.dao.ims;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotAttributeColumnDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.inventory.LotAttributeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LotDAOTest extends IntegrationTestBase {

	private LotDAO lotDAO;
	private LocationDAO locationDAO;
	private GermplasmListDAO germplasmListDAO;
	private CountryDAO countryDAO;
	private TransactionDAO transactionDAO;

	@Autowired
	private GermplasmListManager manager;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private Lot lot1, lot2, lot3;
	private Variable testVariable;
	private Transaction transaction1, transaction2, transaction3;
	private Location location;
	private Germplasm germplasm1, germplasm2;
	private GermplasmList germplasmList;
	private CropType cropType;
	private DaoFactory daoFactory;

	private static final String GERMPLASM = "GERMPLSM";

	private static final String LST = "LST";
	private static final String LIST = "LIST";

	@Autowired
	private LotAttributeService lotAttributeService;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.lotDAO = this.daoFactory.getLotDao();
		this.locationDAO = this.daoFactory.getLocationDAO();
		this.germplasmListDAO = this.daoFactory.getGermplasmListDAO();
		this.countryDAO = this.daoFactory.getCountryDao();
		this.transactionDAO = this.daoFactory.getTransactionDAO();
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.sessionProvder, this.daoFactory);

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);
		this.createLocationForSearchLotTest();
		this.createDataForSearchLotsTest();
	}

	@Test
	public void testGetAvailableBalanceCountAndTotalLotsCount() {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Integer germplasmId = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.lotDAO.save(lot);

		final Transaction transaction = InventoryDetailsTestDataInitializer.createTransaction(5.0, 1,
			TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, LIST, TransactionType.DEPOSIT.getId());
		this.transactionDAO.save(transaction);

		final Map<Integer, Object[]> availableBalanceCountAndTotalLotsCount =
			this.lotDAO.getAvailableBalanceCountAndTotalLotsCount(Lists.newArrayList(germplasmId));

		Assert.assertEquals(1, availableBalanceCountAndTotalLotsCount.size());
		final Object[] balanceValues = availableBalanceCountAndTotalLotsCount.get(germplasmId);

		Assert.assertEquals(1, ((BigInteger) balanceValues[0]).intValue());
		Assert.assertEquals(1, ((BigInteger) balanceValues[1]).intValue());
		Assert.assertEquals("5.0", balanceValues[2].toString());
		Assert.assertEquals(1, ((BigInteger) balanceValues[3]).intValue());
		Assert.assertEquals(8264, ((Integer) balanceValues[4]).intValue());

	}

	@Test
	public void testGetGermplasmsWithOpenLots() {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Integer germplasmId = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final Lot lot =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.lotDAO.save(lot);

		final Transaction transaction =
			InventoryDetailsTestDataInitializer
				.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST",
					TransactionType.DEPOSIT.getId());
		InventoryDetailsTestDataInitializer
			.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, LIST, TransactionType.DEPOSIT.getId());
		this.transactionDAO.save(transaction);

		final Set<Integer> gids = this.lotDAO.getGermplasmsWithOpenLots(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(1, gids.size());
	}

	@Test
	public void testGetGermplasmsWithNoOpenLots() {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Integer germplasmId = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final Lot lot =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 1, 1, "Comments", "InventoryId");
		this.lotDAO.save(lot);

		final Transaction transaction =
			InventoryDetailsTestDataInitializer
				.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST",
					TransactionType.DEPOSIT.getId());
		InventoryDetailsTestDataInitializer
			.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, LIST, TransactionType.DEPOSIT.getId());
		this.transactionDAO.save(transaction);

		final Set<Integer> gids = this.lotDAO.getGermplasmsWithOpenLots(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(0, gids.size());
	}

	@Test
	public void testSearchAllLots() {

		final List<ExtendedLotDto> extendedLotDtos = this.lotDAO.searchLots(new LotsSearchDto(), null, null);
		Assert.assertTrue(extendedLotDtos.size() >= 3);

	}

	@Test
	public void testSearchLotsByAttributeFilter() {
		final Variable variable = this.createInventoryAtrributeVariable();
		final AttributeRequestDto attributeRequestDto = new AttributeRequestDto(variable.getId(), RandomStringUtils.randomNumeric(2),
			"20210316", 1);
		this.daoFactory.getLotAttributeDAO().createAttribute(this.lot1.getId(), attributeRequestDto, variable);

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		final Map<Integer, Object> attributeFilters = new HashMap<>();
		attributeFilters.put(variable.getId(), attributeRequestDto.getValue());
		lotsSearchDto.setAttributes(attributeFilters);
		final List<ExtendedLotDto> extendedLotDtos = this.lotDAO.searchLots(lotsSearchDto, null, null);
		Assert.assertEquals(1, extendedLotDtos.size());
		Assert.assertEquals(attributeRequestDto.getValue(),
			extendedLotDtos.get(0).getAttributeTypesValueMap().get(variable.getId()).toString());
	}

	@Test
	public void testSearchLotsByLotIds() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();

		lotsSearchDto.setLotIds(Lists.newArrayList(this.lot1.getId(), this.lot2.getId()));

		final List<ExtendedLotDto> extendedLotDtos = this.lotDAO.searchLots(lotsSearchDto, null, null);
		Assert.assertEquals(extendedLotDtos.size(), 2);
	}

	@Test
	public void testSearchLotsByLocationIds() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLocationIds(Lists.newArrayList(this.location.getLocid()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotDAO.searchLots(lotsSearchDto, null, null);

		Assert.assertEquals(extendedLotDtos.size(), 1);
	}

	@Test
	public void testSearchLotsByGids() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setGids(Lists.newArrayList(this.germplasm1.getGid()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotDAO.searchLots(lotsSearchDto, null, null);

		Assert.assertEquals(extendedLotDtos.size(), 2);
	}

	@Test
	public void testSearchLotsByAttribute() {
		final Variable testVariable = this.createInventoryAtrributeVariable();

		this.lotAttributeService.createLotAttribute(this.lot1.getId(), new AttributeRequestDto(
			testVariable.getId(), "attribute for lot1", "20220101", 1));
		this.lotAttributeService.createLotAttribute(this.lot2.getId(), new AttributeRequestDto(
			testVariable.getId(), "attribute for lot2", "20220101", 1));
		this.lotAttributeService.createLotAttribute(this.lot3.getId(), new AttributeRequestDto(
			testVariable.getId(), "attribute for lot3", "20220101", 1));

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setAttributes(Collections.singletonMap(testVariable.getId(), "attribute"));

		List<ExtendedLotDto> extendedLotDtos = this.lotDAO.searchLots(lotsSearchDto, null, null);
		Assert.assertEquals(extendedLotDtos.size(), 3);

		lotsSearchDto.setAttributes(Collections.singletonMap(testVariable.getId(), "lot1"));
		extendedLotDtos = this.lotDAO.searchLots(lotsSearchDto, null, null);
		Assert.assertEquals(extendedLotDtos.size(), 1);
	}

	@Test
	public void testGetLotAttributeColumnDtos() {
		List<LotAttributeColumnDto> lotAttributeColumnDtos =
			this.lotDAO.getLotAttributeColumnDtos(RandomStringUtils.randomAlphanumeric(10));
		final Integer previousNumberOfColumns = lotAttributeColumnDtos.size();
		final Variable variable = this.createInventoryAtrributeVariable();
		final AttributeRequestDto attributeRequestDto = new AttributeRequestDto(variable.getId(), RandomStringUtils.randomNumeric(2),
			"20210316", 1);
		this.daoFactory.getLotAttributeDAO().createAttribute(this.lot1.getId(), attributeRequestDto, variable);

		lotAttributeColumnDtos = this.lotDAO.getLotAttributeColumnDtos(RandomStringUtils.randomAlphanumeric(10));
		Assert.assertEquals(previousNumberOfColumns + 1, lotAttributeColumnDtos.size());
		final LotAttributeColumnDto column = lotAttributeColumnDtos.stream().filter(a -> a.getId() == variable.getId()).findFirst().get();
		Assert.assertEquals(variable.getId(), column.getId());
		Assert.assertEquals(variable.getName(), column.getName());
		Assert.assertEquals(variable.getAlias(), column.getAlias());
	}

	@Test
	public void testSearchLotsByGermplasmListIds() {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setGermplasmListIds(Lists.newArrayList(this.germplasmList.getId()));
		final List<ExtendedLotDto> extendedLotDtos = this.lotDAO.searchLots(lotsSearchDto, null, null);

		Assert.assertEquals(extendedLotDtos.size(), 2);
	}

	private void createLocationForSearchLotTest() {
		final Country country = this.countryDAO.getById(1);

		final int ltype = 405;
		final String labbr = RandomStringUtils.randomAlphabetic(7);
		final String lname = RandomStringUtils.randomAlphabetic(9);

		this.location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		this.location.setCountry(country);

		final Location province = this.locationDAO.getById(1001);
		this.location.setProvince(province);

		this.locationDAO.saveOrUpdate(this.location);

	}

	private void createDataForSearchLotsTest() {

		this.germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Integer germplasmId1 =
			this.germplasmTestDataGenerator.addGermplasm(this.germplasm1, this.germplasm1.getPreferredName(), this.cropType);

		this.germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Integer germplasmId2 =
			this.germplasmTestDataGenerator.addGermplasm(this.germplasm2, this.germplasm2.getPreferredName(), this.cropType);

		this.lot1 = InventoryDetailsTestDataInitializer
			.createLot(1, GERMPLASM, germplasmId1, this.location.getLocid(), 8264, 0, 1, "Comments",
				RandomStringUtils.randomAlphabetic(35));

		this.lot2 = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId1, 2, 8267, 0, 1, "Comments",
			RandomStringUtils.randomAlphabetic(35));

		this.lot3 = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId2, 1, 8267, 0, 1, "Comments",
			RandomStringUtils.randomAlphabetic(35));

		this.transaction1 = InventoryDetailsTestDataInitializer
			.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), this.lot1, 1, 1, 1, LIST, TransactionType.DEPOSIT.getId());

		this.transaction2 = InventoryDetailsTestDataInitializer
			.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), this.lot2, 1, 1, 1, LIST, TransactionType.DEPOSIT.getId());

		this.transaction3 = InventoryDetailsTestDataInitializer
			.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), this.lot3, 1, 1, 1, LIST, TransactionType.DEPOSIT.getId());

		this.lotDAO.save(this.lot1);
		this.lotDAO.save(this.lot2);
		this.lotDAO.save(this.lot3);

		this.transactionDAO.save(this.transaction1);
		this.transactionDAO.save(this.transaction2);
		this.transactionDAO.save(this.transaction3);

		this.germplasmList = this.germplasmListDAO.save(GermplasmListTestDataInitializer
			.createGermplasmListTestData(RandomStringUtils.randomAlphabetic(6), RandomStringUtils.randomAlphabetic(6), 20141103, LST, 9999,
				0,
				RandomStringUtils.randomAlphabetic(6), null));

		final GermplasmListData listData1 =
			new GermplasmListData(null, this.germplasmList, this.germplasm1.getGid(), 1, RandomStringUtils.randomAlphabetic(6),
				RandomStringUtils.randomAlphabetic(6), 0, 99995);

		this.manager.addGermplasmListData(listData1);
	}

	private Variable createInventoryAtrributeVariable() {
		final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
			.save(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), CvId.VARIABLES);
		final CVTerm property = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		this.daoFactory.getCvTermRelationshipDao().save(scale.getCvTermId(), TermId.HAS_TYPE.getId(), DataType.NUMERIC_VARIABLE.getId());
		final CVTerm method = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);
		final CVTerm numericDataType = this.daoFactory.getCvTermDao().getById(DataType.NUMERIC_VARIABLE.getId());

		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());

		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(null, cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(),
				VariableType.INVENTORY_ATTRIBUTE.getName(), 0));

		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableId(cvTermVariable.getCvTermId());
		return this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter).values().stream().findFirst().get();
	}
}
