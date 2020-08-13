package org.generationcp.middleware.service.impl.inventory;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;
import org.generationcp.middleware.domain.inventory.manager.LotItemDto;
import org.generationcp.middleware.domain.inventory.manager.LotSearchMetadata;
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
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.generationcp.middleware.service.api.inventory.TransactionService;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	public List<ExtendedLotDto> searchLots(final LotsSearchDto lotsSearchDto,final Pageable pageable) {
		return this.daoFactory.getLotDao().searchLots(lotsSearchDto, pageable);
	}

	@Override
	public long countSearchLots(final LotsSearchDto lotsSearchDto) {
		return this.daoFactory.getLotDao().countSearchLots(lotsSearchDto);
	}

	@Override
	public List<UserDefinedField> getGermplasmAttributeTypes(final LotsSearchDto searchDto) {
		return this.daoFactory.getLotDao().getGermplasmAttributeTypes(searchDto);
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
	public void updateLots(final List<ExtendedLotDto> lotDtos, final LotUpdateRequestDto lotRequest) {
		final LotDAO lotDao = this.daoFactory.getLotDao();
		for (final LotDto lotDto : lotDtos) {
			final Lot lot = lotDao.getById(lotDto.getLotId());
			if (lotRequest.getGid() != null) {
				lot.setEntityId(lotRequest.getGid());
			}
			if (lotRequest.getLocationId() != null) {
				lot.setLocationId(lotRequest.getLocationId());
			}
			if (lotRequest.getUnitId() != null) {
				lot.setScaleId(lotRequest.getUnitId());
			}
			if (!StringUtils.isBlank(lotRequest.getNotes())) {
				lot.setComments(lotRequest.getNotes());
			}
			lotDao.save(lot);
		}
	}

	@Override
	public List<String> saveLots(final CropType cropType, final Integer userId, final List<LotItemDto> lotItemDtos) {
		try {
			// locationsByAbbreviationMap
			final List<Location> locations = this.daoFactory.getLocationDAO()
				.filterLocations(STORAGE_LOCATION_TYPE, null, lotItemDtos.stream()
					.map(LotItemDto::getStorageLocationAbbr)
					.collect(Collectors.toList()));
			final Map<String, Integer> locationsByAbbreviationMap =
				locations.stream().collect(Collectors.toMap(Location::getLabbr, Location::getLocid));

			// scaleVariablesByNameMap
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.addPropertyId(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
			final List<Variable> scaleVariables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
			final Map<String, Integer> scaleVariablesByNameMap =
				scaleVariables.stream().collect(Collectors.toMap(Variable::getName, Variable::getId));

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
					lot.setLocationId(locationsByAbbreviationMap.get(lotItemDto.getStorageLocationAbbr()));
				}
				lot.setStockId(lotItemDto.getStockId());
				lot.setStatus(0);
				//FIXME check if source has to be always 0
				lot.setSource(0);
				if (lotItemDto.getUnitId() != null) {
					lot.setScaleId(lotItemDto.getUnitId());
				} else {
					lot.setScaleId(scaleVariablesByNameMap.get(lotItemDto.getUnitName()));
				}

				this.inventoryDataManager.generateLotIds(cropType, Lists.newArrayList(lot));
				final Lot savedLot = this.daoFactory.getLotDao().save(lot);
				lotUUIDs.add(savedLot.getLotUuId());

				final Double initialBalance = lotItemDto.getInitialBalance();
				if (initialBalance != null) {
					final Transaction transaction = new Transaction();
					transaction.setStatus(TransactionStatus.CONFIRMED.getIntValue());
					transaction.setType(TransactionType.DEPOSIT.getId());
					transaction.setLot(lot);
					transaction.setPersonId(userId);
					transaction.setUserId(userId);
					transaction.setTransactionDate(new Date());
					transaction.setQuantity(initialBalance);
					transaction.setPreviousAmount(0D);
					transaction.setCommitmentDate(0);

					daoFactory.getTransactionDAO().save(transaction);
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
		return daoFactory.getLotDao().getLotsByStockIds(stockIds);
	}

	@Override
	public LotSearchMetadata getLotSearchMetadata(final LotsSearchDto lotsSearchDto) {
		return new LotSearchMetadata(daoFactory.getLotDao().getLotsCountPerScaleName(lotsSearchDto));
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
		final List<ExtendedLotDto> lotsToBeCancelled = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null);
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
			discardTransaction.setPreviousAmount(0D);
			discardTransaction.setCommitmentDate(Util.getCurrentDateAsIntegerValue());
			daoFactory.getTransactionDAO().save(discardTransaction);
		}

		this.daoFactory.getLotDao().closeLots(lotIds);
	}

	public void setTransactionService(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}
}
