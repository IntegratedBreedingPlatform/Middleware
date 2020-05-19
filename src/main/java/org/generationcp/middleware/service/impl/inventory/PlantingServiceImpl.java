package org.generationcp.middleware.service.impl.inventory;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.planting.PlantingMetadata;
import org.generationcp.middleware.domain.inventory.planting.PlantingRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.inventory.PlantingService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

		// observation units
		final List<ObservationUnitRow> observationUnitRows =
			this.datasetService.getObservationUnitRows(studyId, datasetId, searchDTO.getSearchRequest());
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
				entry.setEntryType(observationUnitRow.getVariables().get("ENTRY_TYPE").getValue());

				entriesByEntryNo.put(observationUnitRow.getEntryNumber(), entry);

				if (entriesByGid.get(gid) == null) {
					entriesByGid.put(gid, new ArrayList<>());
				}
				entriesByGid.get(gid).add(entry);
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
		final List<ExtendedLotDto> extendedLotDtos = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null);
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
			daoFactory.getExperimentTransactionDao().countPlantingTransactionsByStatus(obsUnitIds, TransactionStatus.CONFIRMED));
		plantingMetadata.setPendingTransactionsCount(
			daoFactory.getExperimentTransactionDao().countPlantingTransactionsByStatus(obsUnitIds, TransactionStatus.PENDING));
		return plantingMetadata;
	}

	@Override
	public void savePlanting(final Integer userId, final Integer studyId, final Integer datasetId,
		final PlantingRequestDto plantingRequestDto,
		final TransactionStatus transactionStatus) {
		final PlantingPreparationDTO plantingPreparationDTO =
			this.searchPlantingPreparation(studyId, datasetId, plantingRequestDto.getSelectedObservationUnits());

		final List<Integer> requestedEntryNos =
			plantingRequestDto.getLotPerEntryNo().stream().map(PlantingRequestDto.LotEntryNumber::getEntryNo).collect(
				Collectors.toList());

		final List<Integer> requestedNdExperimentIds = new ArrayList<>();
		plantingPreparationDTO.getEntries().stream().filter(entry -> requestedEntryNos.contains(entry.getEntryNo()))
			.forEach(entry -> requestedNdExperimentIds.addAll(entry.getObservationUnits().stream().map(
				PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO::getNdExperimentId).collect(Collectors.toList())));

		// Verify that none of them has confirmed transactions, if there are, throw an exception
		if (daoFactory.getExperimentTransactionDao()
			.countPlantingTransactionsByStatus(requestedNdExperimentIds, TransactionStatus.CONFIRMED) > 0L) {
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

			final ExtendedLotDto lot = daoFactory.getLotDao().searchLots(lotsSearchDto, null).get(0);

			//Find instructions for the unit
			final PlantingRequestDto.WithdrawalInstruction withdrawalInstruction =
				plantingRequestDto.getWithdrawalsPerUnit().get(lot.getUnitName());
			final Double amount = (withdrawalInstruction.isWithdrawAllAvailableBalance()) ? lot.getAvailableBalance() :
				withdrawalInstruction.getWithdrawalAmount();
			final Double totalToWithdraw =
				(withdrawalInstruction.isWithdrawAllAvailableBalance()) ? amount : (amount * ndExperimentIds.size());

			if (totalToWithdraw > lot.getAvailableBalance()) {
				throw new MiddlewareRequestException("", "planting.not.enough.inventory", lot.getStockId());
			}

			if (withdrawalInstruction.isGroupTransactions()) {
				final Transaction transaction =
					new Transaction(TransactionType.WITHDRAWAL, transactionStatus, userId, plantingRequestDto.getNotes(), lot.getLotId(),
						-1 * totalToWithdraw);
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

	private void processSearchComposite(final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchDTO) {
		if (searchDTO.getItemIds() != null && !searchDTO.getItemIds().isEmpty()) {
			final ObservationUnitsSearchDTO searchRequest = new ObservationUnitsSearchDTO();
			final ObservationUnitsSearchDTO.Filter filter = searchRequest.new Filter();
			filter.setFilteredNdExperimentIds(searchDTO.getItemIds());
			searchRequest.setFilter(filter);
			searchDTO.setSearchRequest(searchRequest);
		}
	}

}
