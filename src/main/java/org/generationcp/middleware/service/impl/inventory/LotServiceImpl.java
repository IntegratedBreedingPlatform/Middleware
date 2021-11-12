package org.generationcp.middleware.service.impl.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotItemDto;
import org.generationcp.middleware.domain.inventory.manager.LotMultiUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotSearchMetadata;
import org.generationcp.middleware.domain.inventory.manager.LotSingleUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotUpdateRequestDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionSourceType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.generationcp.middleware.service.api.inventory.TransactionService;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LotServiceImpl implements LotService {

	private static final Logger LOG = LoggerFactory.getLogger(LotServiceImpl.class);

	private DaoFactory daoFactory;

	@Value("${export.lot.max.total.results}")
	public int maxTotalResults;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private TransactionService transactionService;

	private static final Set<Integer> STORAGE_LOCATION_TYPE = new HashSet<>(Arrays.asList(1500));

	public LotServiceImpl() {
	}

	public LotServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ExtendedLotDto> searchLots(final LotsSearchDto lotsSearchDto, final Pageable pageable) {
		return this.daoFactory.getLotDao().searchLots(lotsSearchDto, pageable, null);
	}

	@Override
	public List<ExtendedLotDto> searchLotsApplyExportResultsLimit(final LotsSearchDto lotsSearchDto, final Pageable pageable) {
		return this.daoFactory.getLotDao().searchLots(lotsSearchDto, pageable, this.maxTotalResults);
	}

	@Override
	public long countSearchLots(final LotsSearchDto lotsSearchDto) {
		return this.daoFactory.getLotDao().countSearchLots(lotsSearchDto);
	}

	@Override
	public Map<Integer, Map<Integer, String>> getGermplasmAttributeValues(final LotsSearchDto searchDto) {
		return this.daoFactory.getLotDao().getGermplasmAttributeValues(searchDto);
	}

	@Override
	public String saveLot(final CropType cropType, final Integer userId, final LotGeneratorInputDto lotDto) {

		final Lot lot = new Lot();
		lot.setUserId(userId);
		lot.setComments(lotDto.getNotes());
		lot.setCreatedDate(new Date());
		lot.setEntityId(lotDto.getGid());
		lot.setEntityType("GERMPLSM");
		lot.setLocationId(lotDto.getLocationId());
		lot.setStockId(lotDto.getStockId());
		lot.setStatus(0);
		//FIXME check if source has to be always 0
		lot.setSource(0);
		lot.setScaleId(lotDto.getUnitId());
		this.inventoryDataManager.generateLotIds(cropType, Lists.newArrayList(lot));
		this.daoFactory.getLotDao().save(lot);

		return lot.getLotUuId();
	}

	@Override
	public void updateLots(final List<ExtendedLotDto> lotDtos, final LotUpdateRequestDto lotUpdateRequestDto) {
		final List<Lot> lots =
			this.daoFactory.getLotDao()
				.filterByColumnValues("lotUuId", lotDtos.stream().map(ExtendedLotDto::getLotUUID).collect(
					Collectors.toList()));
		if (lotUpdateRequestDto.getSingleInput() != null) {
			this.updateLots(lots, lotUpdateRequestDto.getSingleInput());
		} else {
			this.updateLots(lots, lotUpdateRequestDto.getMultiInput());
		}
	}

	private void updateLots(final List<Lot> lots, final LotSingleUpdateRequestDto lotSingleUpdateRequestDto) {

		for (final Lot lot : lots) {
			if (lotSingleUpdateRequestDto.getGid() != null) {
				lot.setEntityId(lotSingleUpdateRequestDto.getGid());
			}
			if (lotSingleUpdateRequestDto.getLocationId() != null) {
				lot.setLocationId(lotSingleUpdateRequestDto.getLocationId());
			}
			if (lotSingleUpdateRequestDto.getUnitId() != null) {
				lot.setScaleId(lotSingleUpdateRequestDto.getUnitId());
			}
			if (!StringUtils.isBlank(lotSingleUpdateRequestDto.getNotes())) {
				lot.setComments(lotSingleUpdateRequestDto.getNotes());
			}
			this.daoFactory.getLotDao().save(lot);
		}
	}

	private void updateLots(final List<Lot> lots, final LotMultiUpdateRequestDto lotMultiUpdateRequestDto) {
		try {
			final Map<String, Integer> locationsByLocationAbbrMap =
				this.buildLocationsByLocationAbbrMap(lotMultiUpdateRequestDto.getLotList().stream()
					.map(LotMultiUpdateRequestDto.LotUpdateDto::getStorageLocationAbbr)
					.collect(Collectors.toList()));
			final Map<String, Integer> unitMapByName = this.buildUnitsByNameMap();
			final Map<String, LotMultiUpdateRequestDto.LotUpdateDto> lotUpdateMapByLotUID =
				Maps.uniqueIndex(lotMultiUpdateRequestDto.getLotList(), LotMultiUpdateRequestDto.LotUpdateDto::getLotUID);

			for (final Lot lot : lots) {
				final LotMultiUpdateRequestDto.LotUpdateDto lotUpdateDto = lotUpdateMapByLotUID.get(lot.getLotUuId());
				if (!StringUtils.isBlank(lotUpdateDto.getStorageLocationAbbr())) {
					lot.setLocationId(locationsByLocationAbbrMap.get(lotUpdateDto.getStorageLocationAbbr()));
				}
				if (!StringUtils.isBlank(lotUpdateDto.getUnitName())) {
					lot.setScaleId(unitMapByName.get(lotUpdateDto.getUnitName()));
				}
				if (!StringUtils.isBlank(lotUpdateDto.getNotes())) {
					lot.setComments(lotUpdateDto.getNotes());
				}
				if (!StringUtils.isBlank(lotUpdateDto.getNewLotUID())) {
					lot.setLotUuId(lotUpdateDto.getNewLotUID());
				}

				this.daoFactory.getLotDao().saveOrUpdate(lot);
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareRequestException("", "common.middleware.error.update.lots");
		}
	}

	private Map<String, Integer> buildLocationsByLocationAbbrMap(final List<String> locationAbbreviations) {

		final List<Location> locations =
			this.daoFactory.getLocationDAO()
				.filterLocations(new LocationSearchRequest(null, STORAGE_LOCATION_TYPE, null, locationAbbreviations, null),
					null);
		return locations.stream().collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
	}

	private Map<String, Integer> buildUnitsByNameMap() {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addPropertyId(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
		final List<Variable> unitVariables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
		return unitVariables.stream().collect(Collectors.toMap(Variable::getName, Variable::getId));
	}

	@Override
	public List<String> saveLots(final CropType cropType, final Integer userId,
		final List<LotItemDto> lotItemDtos) {
		try {
			final Map<String, Integer> locationsByLocationAbbrMap = this.buildLocationsByLocationAbbrMap(lotItemDtos.stream()
				.map(LotItemDto::getStorageLocationAbbr)
				.collect(Collectors.toList()));

			final Map<String, Integer> unitsByNameMap = this.buildUnitsByNameMap();

			final List<String> lotUUIDs = new ArrayList<>();

			for (final LotItemDto lotItemDto : lotItemDtos) {
				final Lot lot = new Lot();
				lot.setUserId(userId);
				lot.setComments(lotItemDto.getNotes());
				lot.setCreatedDate(new Date());
				lot.setEntityId(lotItemDto.getGid());
				lot.setEntityType("GERMPLSM");
				if (lotItemDto.getStorageLocationId() != null) {
					lot.setLocationId(lotItemDto.getStorageLocationId());
				} else {
					lot.setLocationId(locationsByLocationAbbrMap.get(lotItemDto.getStorageLocationAbbr()));
				}
				lot.setStockId(lotItemDto.getStockId());
				lot.setStatus(0);
				//FIXME check if source has to be always 0
				lot.setSource(0);
				if (lotItemDto.getUnitId() != null) {
					lot.setScaleId(lotItemDto.getUnitId());
				} else {
					lot.setScaleId(unitsByNameMap.get(lotItemDto.getUnitName()));
				}

				this.inventoryDataManager.generateLotIds(cropType, Lists.newArrayList(lot));
				final Lot savedLot = this.daoFactory.getLotDao().save(lot);
				lotUUIDs.add(savedLot.getLotUuId());

				final Double initialBalance = lotItemDto.getInitialBalance();
				if (initialBalance != null) {
					final Transaction transaction = new Transaction();
					if (lotItemDto.isPendingStatus()) {
						transaction.setStatus(TransactionStatus.PENDING.getIntValue());
					} else {
						transaction.setStatus(TransactionStatus.CONFIRMED.getIntValue());
					}
					transaction.setType(TransactionType.DEPOSIT.getId());
					transaction.setLot(lot);
					transaction.setPersonId(userId);
					transaction.setUserId(userId);
					transaction.setTransactionDate(new Date());
					transaction.setQuantity(initialBalance);
					transaction.setCommitmentDate(0);

					this.daoFactory.getTransactionDAO().save(transaction);
				}
			}
			return lotUUIDs;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareRequestException("", "common.middleware.error.import.lots");
		}
	}

	@Override
	public List<LotDto> getLotsByStockIds(final List<String> stockIds) {
		return this.daoFactory.getLotDao().getLotsByStockIds(stockIds);
	}

	@Override
	public LotSearchMetadata getLotSearchMetadata(final LotsSearchDto lotsSearchDto) {
		return new LotSearchMetadata(this.daoFactory.getLotDao().getLotsCountPerScaleName(lotsSearchDto));
	}

	@Override
	public void closeLots(final Integer userId, final List<Integer> lotIds) {
		final TransactionsSearchDto transactionsSearchDto = new TransactionsSearchDto();
		transactionsSearchDto.setLotIds(lotIds);
		transactionsSearchDto.setStatusIds(Collections.singletonList(TransactionStatus.PENDING.getIntValue()));
		final List<TransactionDto> pendingTransactionsDtos =
			this.daoFactory.getTransactionDAO().searchTransactions(transactionsSearchDto, null);
		this.transactionService.cancelPendingTransactions(pendingTransactionsDtos);

		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLotIds(lotIds);
		final List<ExtendedLotDto> lotsToBeCancelled = this.searchLots(lotsSearchDto, null);
		final List<ExtendedLotDto> lotsWithAvailableBalance = lotsToBeCancelled.stream().filter(x -> x.getAvailableBalance() > 0).collect(
			Collectors.toList());

		for (final ExtendedLotDto extendedLotDto : lotsWithAvailableBalance) {
			final Lot lot = new Lot();
			lot.setId(extendedLotDto.getLotId());
			final Transaction discardTransaction = new Transaction();
			discardTransaction.setStatus(TransactionStatus.CONFIRMED.getIntValue());
			discardTransaction.setType(TransactionType.DISCARD.getId());
			discardTransaction.setLot(lot);
			discardTransaction.setPersonId(userId);
			discardTransaction.setUserId(userId);
			discardTransaction.setTransactionDate(new Date());
			discardTransaction.setQuantity(-1 * extendedLotDto.getAvailableBalance());
			discardTransaction.setCommitmentDate(Util.getCurrentDateAsIntegerValue());
			this.daoFactory.getTransactionDAO().save(discardTransaction);
		}

		this.daoFactory.getLotDao().closeLots(lotIds);
	}

	@Override
	public void mergeLots(final Integer userId, final Integer keepLotId, final LotsSearchDto lotsSearchDto) {
		//Search selected lots to be merged and remove the one to keep
		final List<ExtendedLotDto> extendedLotDtos = this.searchLots(lotsSearchDto, null).stream()
			.filter(extendedLotDto -> extendedLotDto.getLotId().intValue() != keepLotId.intValue())
			.collect(Collectors.toList());
		//Create a transaction for each of the discarded lots
		extendedLotDtos.stream()
			.forEach(extendedLotDto -> {
				final Transaction transaction = new Transaction(TransactionType.DEPOSIT, TransactionStatus.CONFIRMED,
					userId, null, keepLotId, extendedLotDto.getActualBalance());
				transaction.setSourceType(TransactionSourceType.MERGED_LOT.name());
				transaction.setSourceId(extendedLotDto.getLotId());
				this.daoFactory.getTransactionDAO().save(transaction);
			});

		//Close the discarded lots
		final List<Integer> lotIds = extendedLotDtos.stream()
			.map(ExtendedLotDto::getLotId).collect(Collectors.toList());
		this.closeLots(userId, lotIds);
	}

	public void setTransactionService(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setOntologyVariableDataManager(final OntologyVariableDataManager ontologyVariableDataManager) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
	}
}
