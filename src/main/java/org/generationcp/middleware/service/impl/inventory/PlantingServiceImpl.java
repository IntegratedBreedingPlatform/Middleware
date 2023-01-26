package org.generationcp.middleware.service.impl.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.planting.PlantingMetadata;
import org.generationcp.middleware.domain.inventory.planting.PlantingRequestDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.generationcp.middleware.service.api.inventory.PlantingService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class PlantingServiceImpl implements PlantingService {

	private DaoFactory daoFactory;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private LotService lotService;

	public PlantingServiceImpl() {
	}

	public PlantingServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public PlantingPreparationDTO searchPlantingPreparation(
		final Integer studyId,
		final Integer datasetId,
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchDTO) {

		this.processSearchComposite(searchDTO);

		// List Numeric Entry Details variables that are not system variable.
		final List<MeasurementVariable> entryDetails =
			this.datasetService.getObservationSetVariables(datasetId, Arrays.asList(VariableType.ENTRY_DETAIL.getId())) //
				.stream().filter(var -> DataType.NUMERIC_VARIABLE.getId().equals(var.getDataTypeId()) && !var.isSystemVariable())
				.collect(Collectors.toList());

		// Add only the required columns necessary for this function
		final Map<Integer, String> requiredColumns =
			this.daoFactory.getCvTermDao().getByIds(Lists.newArrayList(TermId.TRIAL_INSTANCE_FACTOR.getId(),
				TermId.GID.getId(), TermId.DESIG.getId(), TermId.ENTRY_TYPE.getId(), TermId.ENTRY_NO.getId(),
				TermId.OBS_UNIT_ID.getId())).stream().collect(Collectors.toMap(CVTerm::getCvTermId, CVTerm::getName));

		searchDTO.getSearchRequest().setVisibleColumns(new HashSet<>(requiredColumns.values()));

		// observation units
		final List<ObservationUnitRow> observationUnitRows =
			this.datasetService.getObservationUnitRows(studyId, datasetId, searchDTO.getSearchRequest(), null);
		Preconditions.checkArgument(!Util.isEmpty(observationUnitRows), "No results for observation units");

		// process observation units and build entries
		final Map<Integer, PlantingPreparationDTO.PlantingPreparationEntryDTO> entriesByEntryNo = new LinkedHashMap<>();
		// there might be some entries with the same gid
		final Map<Integer, List<PlantingPreparationDTO.PlantingPreparationEntryDTO>> entriesByGid = new HashMap<>();

		for (final ObservationUnitRow observationUnitRow : observationUnitRows) {
			if (entriesByEntryNo.get(observationUnitRow.getEntryNumber()) == null) {
				final PlantingPreparationDTO.PlantingPreparationEntryDTO entry = new PlantingPreparationDTO.PlantingPreparationEntryDTO();
				final Integer gid = observationUnitRow.getGid();

				entry.setEntryNo(observationUnitRow.getEntryNumber());
				entry.setGid(gid);
				entry.setDesignation(observationUnitRow.getDesignation());
				entry.setEntryType(observationUnitRow.getVariables().get(requiredColumns.get(TermId.ENTRY_TYPE.getId())).getValue());

				entriesByEntryNo.put(observationUnitRow.getEntryNumber(), entry);

				if (entriesByGid.get(gid) == null) {
					entriesByGid.put(gid, new ArrayList<>());
				}
				entriesByGid.get(gid).add(entry);

				for (final MeasurementVariable entryDetail : entryDetails) {
					final String value = observationUnitRow.getVariables().get(entryDetail.getName()).getValue();
					entriesByEntryNo.get(observationUnitRow.getEntryNumber()).getEntryDetailVariableId()
						.put(entryDetail.getTermId(), StringUtils.isEmpty(value) ? null : Double.valueOf(value));
				}
			}

			final PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO observationUnit =
				new PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO();
			observationUnit.setNdExperimentId(observationUnitRow.getObservationUnitId());
			observationUnit.setObservationUnitId(observationUnitRow.getObsUnitId());
			observationUnit.setInstanceId(observationUnitRow.getTrialInstance());
			entriesByEntryNo.get(observationUnitRow.getEntryNumber()).getObservationUnits().add(observationUnit);
		}

		// stock
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setGids(new ArrayList<>(entriesByGid.keySet()));
		lotsSearchDto.setStatus(0); // active
		lotsSearchDto.setMinAvailableBalance(Double.MIN_VALUE);
		final List<ExtendedLotDto> extendedLotDtos = this.lotService.searchLots(lotsSearchDto, null);
		if (!Util.isEmpty(extendedLotDtos)) {
			for (final ExtendedLotDto lot : extendedLotDtos) {
				final List<PlantingPreparationDTO.PlantingPreparationEntryDTO> entries = entriesByGid.get(lot.getGid());
				if (Util.isEmpty(entries)) {
					continue;
				}
				final PlantingPreparationDTO.PlantingPreparationEntryDTO.StockDTO stock =
					new PlantingPreparationDTO.PlantingPreparationEntryDTO.StockDTO();
				stock.setStockId(lot.getStockId());
				stock.setLotId(lot.getLotId());
				stock.setAvailableBalance(lot.getAvailableBalance());
				stock.setStorageLocation(lot.getLocationName());
				stock.setUnitId(lot.getUnitId());
				for (final PlantingPreparationDTO.PlantingPreparationEntryDTO entry : entries) {
					entry.getStockByStockId().put(lot.getStockId(), stock);
				}
			}
		}

		// transform final list
		final PlantingPreparationDTO plantingPreparationDTO = new PlantingPreparationDTO();
		plantingPreparationDTO.setEntries(new ArrayList<>(entriesByEntryNo.values()));

		return plantingPreparationDTO;
	}

	@Override
	public PlantingMetadata getPlantingMetadata(final Integer studyId,
		final Integer datasetId, final PlantingRequestDto plantingRequestDto) {
		final PlantingPreparationDTO plantingPreparationDTO =
			this.searchPlantingPreparation(studyId, datasetId, plantingRequestDto.getSelectedObservationUnits());

		final List<Integer> requestedEntryNos =
			plantingRequestDto.getLotPerEntryNo().stream().map(PlantingRequestDto.LotEntryNumber::getEntryNo).collect(
				Collectors.toList());

		final List<Integer> obsUnitIds = new ArrayList<>();
		plantingPreparationDTO.getEntries().stream().filter(entry -> requestedEntryNos.contains(entry.getEntryNo()))
			.forEach(entry -> obsUnitIds.addAll(entry.getObservationUnits().stream().map(
				PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO::getNdExperimentId).collect(Collectors.toList())));

		final PlantingMetadata plantingMetadata = new PlantingMetadata();
		plantingMetadata.setConfirmedTransactionsCount(
			daoFactory.getExperimentTransactionDao()
				.countTransactionsByNdExperimentIds(obsUnitIds, TransactionStatus.CONFIRMED, ExperimentTransactionType.PLANTING));
		plantingMetadata.setPendingTransactionsCount(
			daoFactory.getExperimentTransactionDao()
				.countTransactionsByNdExperimentIds(obsUnitIds, TransactionStatus.PENDING, ExperimentTransactionType.PLANTING));
		return plantingMetadata;
	}

	@Override
	public void savePlanting(final Integer userId, final Integer studyId, final Integer datasetId,
		final PlantingRequestDto plantingRequestDto,
		final TransactionStatus transactionStatus) {
		final PlantingPreparationDTO plantingPreparationDTO =
			this.searchPlantingPreparation(studyId, datasetId, plantingRequestDto.getSelectedObservationUnits());

		final Map<Integer, Map<Integer, Double>> variableMapByEntryNo = new LinkedHashMap<>();
		plantingPreparationDTO.getEntries()
			.forEach(entry -> variableMapByEntryNo.put(entry.getEntryNo(), entry.getEntryDetailVariableId()));

		final List<Integer> requestedEntryNos =
			plantingRequestDto.getLotPerEntryNo().stream().map(PlantingRequestDto.LotEntryNumber::getEntryNo).collect(
				Collectors.toList());

		final List<Integer> requestedNdExperimentIds = new ArrayList<>();
		plantingPreparationDTO.getEntries().stream().filter(entry -> requestedEntryNos.contains(entry.getEntryNo()))
			.forEach(entry -> requestedNdExperimentIds.addAll(entry.getObservationUnits().stream().map(
				PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO::getNdExperimentId).collect(Collectors.toList())));

		// Verify that none of them has confirmed transactions, if there are, throw an exception
		if (daoFactory.getExperimentTransactionDao()
			.countTransactionsByNdExperimentIds(requestedNdExperimentIds, TransactionStatus.CONFIRMED, ExperimentTransactionType.PLANTING)
			> 0L) {
			throw new MiddlewareRequestException("", "planting.confirmed.transactions.found");
		}

		// Find the pending transactions and cancel them
		final List<Transaction> pendingTransactions = this.daoFactory.getExperimentTransactionDao()
			.getTransactionsByNdExperimentIds(requestedNdExperimentIds, TransactionStatus.PENDING, ExperimentTransactionType.PLANTING);
		for (final Transaction transaction : pendingTransactions) {
			transaction.setStatus(TransactionStatus.CANCELLED.getIntValue());
			transaction.setCommitmentDate(Util.getCurrentDateAsIntegerValue());
			daoFactory.getTransactionDAO().update(transaction);
		}

		//Validate that entryNo are not repeated in BMSAPI
		for (final PlantingRequestDto.LotEntryNumber lotEntryNumber : plantingRequestDto.getLotPerEntryNo()) {
			final List<Integer> ndExperimentIds = new ArrayList<>();
			plantingPreparationDTO.getEntries().stream().filter(entry -> entry.getEntryNo().equals(lotEntryNumber.getEntryNo()))
				.forEach(entry -> ndExperimentIds.addAll(entry.getObservationUnits().stream().map(
						PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO::getNdExperimentId)
					.collect(Collectors.toList())));

			//Get requested lot
			final LotsSearchDto lotsSearchDto = new LotsSearchDto();
			lotsSearchDto.setLotIds(Collections.singletonList(lotEntryNumber.getLotId()));

			final ExtendedLotDto lot = this.lotService.searchLots(lotsSearchDto, null).get(0);

			//Find instructions for the unit
			final PlantingRequestDto.WithdrawalInstruction withdrawalInstruction =
				plantingRequestDto.getWithdrawalsPerUnit().get(lot.getUnitName());
			final Double amount = this.defineAmount(lotEntryNumber.getEntryNo(), lot, withdrawalInstruction, variableMapByEntryNo);
			final BigDecimal totalToWithdraw =
				(withdrawalInstruction.isWithdrawAllAvailableBalance()) ? new BigDecimal(amount) :
					(new BigDecimal(amount).multiply(new BigDecimal(ndExperimentIds.size())).setScale(3, RoundingMode.HALF_DOWN)
						.stripTrailingZeros());

			if (totalToWithdraw.doubleValue() > lot.getAvailableBalance()) {
				throw new MiddlewareRequestException("", "planting.not.enough.inventory", lot.getStockId());
			}

			if (withdrawalInstruction.isGroupTransactions()) {
				final Transaction transaction =
					new Transaction(TransactionType.WITHDRAWAL, transactionStatus, userId, plantingRequestDto.getNotes(), lot.getLotId(),
						-1 * totalToWithdraw.doubleValue());
				this.daoFactory.getTransactionDAO().save(transaction);
				for (final Integer ndExperimentId : ndExperimentIds) {
					final ExperimentModel experimentModel = new ExperimentModel(ndExperimentId);
					final ExperimentTransaction experimentTransaction = new ExperimentTransaction(experimentModel, transaction,
						ExperimentTransactionType.PLANTING.getId());
					this.daoFactory.getExperimentTransactionDao().save(experimentTransaction);
				}
			} else {
				for (final Integer ndExperimentId : ndExperimentIds) {
					final Transaction transaction =
						new Transaction(TransactionType.WITHDRAWAL, transactionStatus, userId, plantingRequestDto.getNotes(),
							lot.getLotId(),
							-1 * amount);
					this.daoFactory.getTransactionDAO().save(transaction);
					final ExperimentModel experimentModel = new ExperimentModel(ndExperimentId);
					final ExperimentTransaction experimentTransaction = new ExperimentTransaction(experimentModel, transaction,
						ExperimentTransactionType.PLANTING.getId());
					this.daoFactory.getExperimentTransactionDao().save(experimentTransaction);
				}
			}
		}
	}

	private Double defineAmount(final Integer entryNo, final ExtendedLotDto lot,
		final PlantingRequestDto.WithdrawalInstruction withdrawalInstruction,
		final Map<Integer, Map<Integer, Double>> variableMapByEntryNo) {

		if (withdrawalInstruction.isWithdrawAllAvailableBalance()) {
			return lot.getAvailableBalance();
		} else if (withdrawalInstruction.isWithdrawUsingEntryDetail()) {
			return variableMapByEntryNo.get(entryNo).get(withdrawalInstruction.getEntryDetailVariableId());
		}
		return withdrawalInstruction.getWithdrawalAmount();

	}

	@Override
	public List<Transaction> getPlantingTransactionsByStudyId(final Integer studyId, final TransactionStatus transactionStatus) {
		return daoFactory.getExperimentTransactionDao()
			.getTransactionsByStudyId(studyId, transactionStatus, ExperimentTransactionType.PLANTING);
	}

	@Override
	public List<Transaction> getPlantingTransactionsByStudyAndEntryId(Integer studyId, Integer entryId,
		TransactionStatus transactionStatus) {
		return daoFactory.getExperimentTransactionDao()
			.getTransactionsByStudyAndEntryId(studyId, entryId, transactionStatus, ExperimentTransactionType.PLANTING);
	}

	private void processSearchComposite(final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchDTO) {
		if (searchDTO.getItemIds() != null && !searchDTO.getItemIds().isEmpty()) {
			final ObservationUnitsSearchDTO searchRequest = new ObservationUnitsSearchDTO();
			final ObservationUnitsSearchDTO.Filter filter = searchRequest.new Filter();
			filter.setFilteredNdExperimentIds(searchDTO.getItemIds());
			searchRequest.setFilter(filter);
			searchDTO.setSearchRequest(searchRequest);
		}
	}

	public void setDatasetService(final DatasetService datasetService) {
		this.datasetService = datasetService;
	}

	public void setLotService(final LotService lotService) {
		this.lotService = lotService;
	}
}
