package org.generationcp.middleware.service.impl.inventory;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.inventory.study.StudyTransactionsDto;
import org.generationcp.middleware.api.inventory.study.StudyTransactionsRequest;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.domain.inventory.planting.PlantingRequestDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class PlantingServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private LocationDataManager locationDataManager;

	private Project commonTestProject;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private DataSetupTest dataSetupTest;
	private DaoFactory daoFactory;

	private TransactionServiceImpl transactionService;
	private PlantingServiceImpl plantingService;
	private LotServiceImpl lotService;

	private Integer studyId;
	private Integer observationDatasetId;
	private Integer userId;
	private Integer storageLocationId;
	public String unitName;
	private List<Experiment> experiments;

	public static final int UNIT_ID = TermId.SEED_AMOUNT_G.getId();

	@Before
	public void setUp() {
		this.transactionService = new TransactionServiceImpl(this.sessionProvder);
		this.lotService = new LotServiceImpl(this.sessionProvder);
		this.plantingService = new PlantingServiceImpl(this.sessionProvder);
		this.plantingService.setDatasetService(datasetService);
		this.plantingService.setLotService(lotService);
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.lotService.setTransactionService(transactionService);

		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmDataManager, daoFactory);
		}

		if (this.studyId == null) {
			this.createTestStudyWithObservationDataset();
		}
		this.userId = this.findAdminUser();
		this.resolveStorageLocation();
		this.unitName = this.daoFactory.getCvTermDao().getById(UNIT_ID).getName();
		experiments = studyDataManager.getExperiments(this.observationDatasetId, 0, 40);

	}

	@Test
	public void test_SavePlanting_FoundConfirmedTransactions_ThrowsException() {
		final Integer gid = Integer.valueOf(
			experiments.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("GID"))
				.findFirst().get().getValue());
		final Integer entryNo = Integer.valueOf(
			experiments.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("ENTRY_NO"))
				.findFirst().get().getValue());
		final Lot lot = createLot(gid);
		this.createTransaction(lot, TransactionType.DEPOSIT, TransactionStatus.CONFIRMED, 100D);
		final Transaction transaction = this.createTransaction(lot, TransactionType.WITHDRAWAL, TransactionStatus.CONFIRMED, 50D);
		final ExperimentTransaction experimentTransaction =
			new ExperimentTransaction(new ExperimentModel(experiments.get(0).getId()), transaction,
				ExperimentTransactionType.PLANTING.getId());
		this.daoFactory.getExperimentTransactionDao().save(experimentTransaction);
		final PlantingRequestDto plantingRequestDto = new PlantingRequestDto();
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchCompositeDto = new SearchCompositeDto<>();
		searchCompositeDto.setItemIds(Collections.singleton(experiments.get(0).getId()));
		plantingRequestDto.setSelectedObservationUnits(searchCompositeDto);

		final PlantingRequestDto.WithdrawalInstruction withdrawalInstruction = new PlantingRequestDto.WithdrawalInstruction();
		withdrawalInstruction.setWithdrawAllAvailableBalance(true);
		withdrawalInstruction.setGroupTransactions(true);

		final Map<String, PlantingRequestDto.WithdrawalInstruction> withdrawalInstructionMap = new HashMap<>();
		withdrawalInstructionMap.put(unitName, withdrawalInstruction);
		plantingRequestDto.setWithdrawalsPerUnit(withdrawalInstructionMap);

		final PlantingRequestDto.LotEntryNumber lotEntryNumber = new PlantingRequestDto.LotEntryNumber();
		lotEntryNumber.setEntryNo(entryNo);
		lotEntryNumber.setLotId(lot.getId());
		plantingRequestDto.setLotPerEntryNo(Collections.singletonList(lotEntryNumber));

		try {
			this.plantingService.savePlanting(userId, studyId, observationDatasetId, plantingRequestDto, TransactionStatus.CONFIRMED);
		} catch (final MiddlewareRequestException e) {
			assertTrue(e.getErrorCodeParamsMultiMap().containsKey("planting.confirmed.transactions.found"));
		}
	}

	@Test
	public void test_SavePlanting_NotEnoughInventory_ThrowsException() {
		final Integer gid = Integer.valueOf(
			experiments.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("GID"))
				.findFirst().get().getValue());
		final Integer entryNo = Integer.valueOf(
			experiments.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("ENTRY_NO"))
				.findFirst().get().getValue());
		final Lot lot = createLot(gid);
		this.createTransaction(lot, TransactionType.DEPOSIT, TransactionStatus.CONFIRMED, 100D);

		final PlantingRequestDto plantingRequestDto = new PlantingRequestDto();
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchCompositeDto = new SearchCompositeDto<>();
		searchCompositeDto.setItemIds(Collections.singleton(experiments.get(0).getId()));
		plantingRequestDto.setSelectedObservationUnits(searchCompositeDto);

		final PlantingRequestDto.WithdrawalInstruction withdrawalInstruction = new PlantingRequestDto.WithdrawalInstruction();
		withdrawalInstruction.setWithdrawAllAvailableBalance(false);
		withdrawalInstruction.setGroupTransactions(true);
		withdrawalInstruction.setWithdrawalAmount(200D);

		final Map<String, PlantingRequestDto.WithdrawalInstruction> withdrawalInstructionMap = new HashMap<>();
		withdrawalInstructionMap.put(unitName, withdrawalInstruction);
		plantingRequestDto.setWithdrawalsPerUnit(withdrawalInstructionMap);

		final PlantingRequestDto.LotEntryNumber lotEntryNumber = new PlantingRequestDto.LotEntryNumber();
		lotEntryNumber.setEntryNo(entryNo);
		lotEntryNumber.setLotId(lot.getId());
		plantingRequestDto.setLotPerEntryNo(Collections.singletonList(lotEntryNumber));

		try {
			this.plantingService.savePlanting(userId, studyId, observationDatasetId, plantingRequestDto, TransactionStatus.CONFIRMED);
		} catch (final MiddlewareRequestException e) {
			assertTrue(e.getErrorCodeParamsMultiMap().containsKey("planting.not.enough.inventory"));
		}
	}

	@Test
	public void test_SavePlanting_GroupTransactions_Ok() {
		final Integer entryNo = Integer.valueOf(
			experiments.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("ENTRY_NO"))
				.findFirst().get().getValue());
		final List<Experiment> filteredByEntryNo = experiments.stream()
			.filter(e -> e.getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("ENTRY_NO"))
				.findFirst().get().getValue().equals(String.valueOf(entryNo))).collect(Collectors.toList());

		final Integer gid = Integer.valueOf(
			filteredByEntryNo.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("GID"))
				.findFirst().get().getValue());

		final Lot lot = createLot(gid);
		this.createTransaction(lot, TransactionType.DEPOSIT, TransactionStatus.CONFIRMED, 100D);

		final PlantingRequestDto plantingRequestDto = new PlantingRequestDto();
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchCompositeDto = new SearchCompositeDto<>();
		searchCompositeDto.setItemIds(filteredByEntryNo.stream().map(Experiment::getId).collect(Collectors.toSet()));
		plantingRequestDto.setSelectedObservationUnits(searchCompositeDto);

		final PlantingRequestDto.WithdrawalInstruction withdrawalInstruction = new PlantingRequestDto.WithdrawalInstruction();
		withdrawalInstruction.setWithdrawAllAvailableBalance(false);
		withdrawalInstruction.setGroupTransactions(true);
		withdrawalInstruction.setWithdrawalAmount(50D);

		final Map<String, PlantingRequestDto.WithdrawalInstruction> withdrawalInstructionMap = new HashMap<>();
		withdrawalInstructionMap.put(unitName, withdrawalInstruction);
		plantingRequestDto.setWithdrawalsPerUnit(withdrawalInstructionMap);

		final PlantingRequestDto.LotEntryNumber lotEntryNumber = new PlantingRequestDto.LotEntryNumber();
		lotEntryNumber.setEntryNo(entryNo);
		lotEntryNumber.setLotId(lot.getId());
		plantingRequestDto.setLotPerEntryNo(Collections.singletonList(lotEntryNumber));

		this.plantingService.savePlanting(userId, studyId, observationDatasetId, plantingRequestDto, TransactionStatus.CONFIRMED);

		final List<Transaction> transactions = this.daoFactory.getExperimentTransactionDao()
			.getTransactionsByNdExperimentIds(filteredByEntryNo.stream().map(Experiment::getId).collect(Collectors.toList()),
				TransactionStatus.CONFIRMED, ExperimentTransactionType.PLANTING);
		assertThat(transactions.size(), equalTo(1));
		assertThat(transactions.get(0).getQuantity(), equalTo(-100D));

	}

	@Test
	public void test_SavePlanting_UngroupTransactions_Ok() {
		final Integer entryNo = Integer.valueOf(
			experiments.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("ENTRY_NO"))
				.findFirst().get().getValue());
		final List<Experiment> filteredByEntryNo = experiments.stream()
			.filter(e -> e.getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("ENTRY_NO"))
				.findFirst().get().getValue().equals(String.valueOf(entryNo))).collect(Collectors.toList());

		final Integer gid = Integer.valueOf(
			filteredByEntryNo.get(0).getFactors().getVariables().stream().filter(v -> v.getVariableType().getLocalName().equals("GID"))
				.findFirst().get().getValue());

		final Lot lot = createLot(gid);
		this.createTransaction(lot, TransactionType.DEPOSIT, TransactionStatus.CONFIRMED, 100D);

		final PlantingRequestDto plantingRequestDto = new PlantingRequestDto();
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchCompositeDto = new SearchCompositeDto<>();
		searchCompositeDto.setItemIds(filteredByEntryNo.stream().map(Experiment::getId).collect(Collectors.toSet()));
		plantingRequestDto.setSelectedObservationUnits(searchCompositeDto);

		final PlantingRequestDto.WithdrawalInstruction withdrawalInstruction = new PlantingRequestDto.WithdrawalInstruction();
		withdrawalInstruction.setWithdrawAllAvailableBalance(false);
		withdrawalInstruction.setGroupTransactions(false);
		withdrawalInstruction.setWithdrawalAmount(50D);

		final Map<String, PlantingRequestDto.WithdrawalInstruction> withdrawalInstructionMap = new HashMap<>();
		withdrawalInstructionMap.put(unitName, withdrawalInstruction);
		plantingRequestDto.setWithdrawalsPerUnit(withdrawalInstructionMap);

		final PlantingRequestDto.LotEntryNumber lotEntryNumber = new PlantingRequestDto.LotEntryNumber();
		lotEntryNumber.setEntryNo(entryNo);
		lotEntryNumber.setLotId(lot.getId());
		plantingRequestDto.setLotPerEntryNo(Collections.singletonList(lotEntryNumber));

		this.plantingService.savePlanting(userId, studyId, observationDatasetId, plantingRequestDto, TransactionStatus.CONFIRMED);

		final List<Transaction> transactions = this.daoFactory.getExperimentTransactionDao()
			.getTransactionsByNdExperimentIds(filteredByEntryNo.stream().map(Experiment::getId).collect(Collectors.toList()),
				TransactionStatus.CONFIRMED, ExperimentTransactionType.PLANTING);
		assertThat(transactions.size(), equalTo(2));
		assertThat(transactions.get(0).getQuantity(), equalTo(-50D));
		assertThat(transactions.get(1).getQuantity(), equalTo(-50D));

		// Verify using other channels

		final TransactionDAO transactionDAO = this.daoFactory.getTransactionDAO();

		// Transaction search
		final TransactionsSearchDto transactionsSearchDto = new TransactionsSearchDto();
		transactionsSearchDto.setPlantingStudyIds(Collections.singletonList(studyId));
		transactionsSearchDto.setTransactionTypes(Collections.singletonList(TransactionType.WITHDRAWAL.getId()));
		transactionsSearchDto.setTransactionStatus(Collections.singletonList(TransactionStatus.CONFIRMED.getIntValue()));
		final List<TransactionDto> transactionDtos = transactionDAO.searchTransactions(transactionsSearchDto, null);

		assertThat(transactionDtos.size(), equalTo(transactions.size()));
		assertThat(transactionDtos.get(0).getTransactionId(), equalTo(transactions.get(0).getId()));
		assertThat(transactionDtos.get(1).getTransactionId(), equalTo(transactions.get(1).getId()));

		// Study transaction search
		final StudyTransactionsRequest studyTransactionsRequest = new StudyTransactionsRequest();
		final List<StudyTransactionsDto> studyTransactionsDtos =
			transactionDAO.searchStudyTransactions(studyId, studyTransactionsRequest, new PageRequest(0, Integer.MAX_VALUE));

		assertThat(studyTransactionsDtos.size(), equalTo(transactions.size()));
		assertThat(studyTransactionsDtos.get(0).getTransactionId(), equalTo(transactions.get(0).getId()));
		assertThat(studyTransactionsDtos.get(1).getTransactionId(), equalTo(transactions.get(1).getId()));

	}

	private void createTestStudyWithObservationDataset() {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, "PREFF", parentGermplasm);

		this.studyId = this.dataSetupTest.createNurseryForGermplasm(this.commonTestProject.getUniqueID(), gids, "ABCD");
		this.observationDatasetId = studyDataManager.getDataSetsByType(this.studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0).getId();
	}

	private void resolveStorageLocation() {
		final Integer id = locationDataManager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.name());
		storageLocationId = this.daoFactory.getLocationDAO().getDefaultLocationByType(id).getLocid();
	}

	private Lot createLot(final Integer gid) {
		final Lot lot =
			new Lot(null, userId, EntityType.GERMPLSM.name(), gid, storageLocationId, UNIT_ID, LotStatus.ACTIVE.getIntValue(), 0,
				"Lot", RandomStringUtils.randomAlphabetic(35));
		this.daoFactory.getLotDao().save(lot);
		this.daoFactory.getLotDao().refresh(lot);
		return lot;
	}

	private Transaction createTransaction(final Lot lot, final TransactionType transactionType, final TransactionStatus transactionStatus,
		final Double amount) {
		final Transaction transaction =
			new Transaction(null, userId, lot, Util.getCurrentDate(), transactionStatus.getIntValue(),
				amount, "Transaction for gid: " + lot.getEntityId(), Util.getCurrentDateAsIntegerValue(), null, null, null, userId, transactionType.getId());

		this.daoFactory.getTransactionDAO().save(transaction);

		return transaction;
	}

}
