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
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
	public Integer saveLot(final CropType cropType, final Integer userId, final LotGeneratorInputDto lotDto) {

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

		return lot.getId();
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
	public void saveLotsWithInitialTransaction(final CropType cropType, final Integer userId, final List<LotItemDto> lotItemDtos) {
		try {
			final List<Location> locations = this.daoFactory.getLocationDAO()
				.filterLocations(STORAGE_LOCATION_TYPE, null, lotItemDtos.stream().map(LotItemDto::getStorageLocationAbbr).collect(
			Collectors.toList()));
			final Map<String, Integer> locationsByAbbreviationMap =
				locations.stream().collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
			final VariableFilter variableFilter = new VariableFilter();
			final List<Variable> scaleVariables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
			final Map<String, Integer> scaleVariablesByNameMap =
				scaleVariables.stream().collect(Collectors.toMap(Variable::getName, Variable::getId));
			for (final LotItemDto lotItemDto : lotItemDtos) {
				final Lot lot = new Lot();
				lot.setUserId(userId);
				lot.setComments(lotItemDto.getNotes());
				lot.setCreatedDate(new Date());
				lot.setEntityId(lotItemDto.getGid());
				lot.setEntityType("GERMPLSM");
				lot.setLocationId(locationsByAbbreviationMap.get(lotItemDto.getStorageLocationAbbr()));
				lot.setStockId(lotItemDto.getStockId());
				lot.setStatus(0);
				//FIXME check if source has to be always 0
				lot.setSource(0);
				lot.setScaleId(scaleVariablesByNameMap.get(lotItemDto.getScaleName()));
				this.inventoryDataManager.generateLotIds(cropType, Lists.newArrayList(lot));
				this.daoFactory.getLotDao().save(lot);

				final Transaction transaction = new Transaction();
				transaction.setStatus(TransactionStatus.CONFIRMED.getIntValue());
				transaction.setType(TransactionType.DEPOSIT.getId());
				transaction.setLot(lot);
				transaction.setPersonId(userId);
				transaction.setUserId(userId);
				transaction.setTransactionDate(new Date());
				transaction.setQuantity(lotItemDto.getInitialBalance());
				transaction.setPreviousAmount(0D);
				//FIXME Commitment date in some cases is not 0. For Deposits is always zero, but for other types it will be the current date
				transaction.setCommitmentDate(0);

				daoFactory.getTransactionDAO().save(transaction);
			}
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

}
