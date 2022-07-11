package org.generationcp.middleware.service.impl.inventory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.LocationDTO;
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
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotAttribute;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionSourceType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.generationcp.middleware.service.api.inventory.TransactionService;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.util.VariableValueUtil;
import org.generationcp.middleware.util.uid.UIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class LotServiceImpl implements LotService {

	private static final UIDGenerator.UID_ROOT UID_ROOT = UIDGenerator.UID_ROOT.LOT;
	public static final int SUFFIX_LENGTH = 8;

	private static final Logger LOG = LoggerFactory.getLogger(LotServiceImpl.class);

	private DaoFactory daoFactory;

	@Value("${export.lot.max.total.results}")
	public int maxTotalResults;

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
		this.generateLotIds(cropType, Lists.newArrayList(lot));
		this.daoFactory.getLotDao().save(lot);

		return lot.getLotUuId();
	}

	@Override
	public void updateLots(final List<ExtendedLotDto> lotDtos, final LotUpdateRequestDto lotUpdateRequestDto, final String programUUID) {
		final List<Lot> lots =
			this.daoFactory.getLotDao()
				.filterByColumnValues("lotUuId", lotDtos.stream().map(ExtendedLotDto::getLotUUID).collect(
					Collectors.toList()));
		if (lotUpdateRequestDto.getSingleInput() != null) {
			this.updateLots(lots, lotUpdateRequestDto.getSingleInput());
		} else {
			this.updateLots(lots, lotUpdateRequestDto.getMultiInput(), programUUID);
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

	private void updateLots(final List<Lot> lots, final LotMultiUpdateRequestDto lotMultiUpdateRequestDto, final String programUUID) {

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		try {
			final List<String> locationAbbreviations = lotMultiUpdateRequestDto.getLotList().stream()
				.filter(lotUpdateDto -> StringUtils.isNotBlank(lotUpdateDto.getStorageLocationAbbr())).distinct()
				.map(LotMultiUpdateRequestDto.LotUpdateDto::getStorageLocationAbbr).collect(Collectors.toList());

			final Map<String, Integer> locationsByLocationAbbrMap = CollectionUtils.isEmpty(locationAbbreviations) ? new HashMap<>() :
				this.buildLocationsByLocationAbbrMap(locationAbbreviations);

			final Map<String, Integer> unitMapByName = this.buildUnitsByNameMap();
			final Map<String, LotMultiUpdateRequestDto.LotUpdateDto> lotUpdateMapByLotUID =
				Maps.uniqueIndex(lotMultiUpdateRequestDto.getLotList(), LotMultiUpdateRequestDto.LotUpdateDto::getLotUID);

			final Set<String> attributeKeys = new HashSet<>();
			lotMultiUpdateRequestDto.getLotList().forEach(lot -> {
				attributeKeys.addAll(lot.getAttributes().keySet().stream().map(String::toUpperCase).collect(Collectors.toList()));
			});

			final List<LotAttribute> attributes =
				this.daoFactory.getLotAttributeDAO().getLotAttributeByIds(
					lots.stream().map(a -> a.getId()).collect(Collectors.toList()));
			final Map<String, Variable> attributeVariablesNameMap = this.getAttributesMap(programUUID, attributeKeys);
			final Map<Integer, List<LotAttribute>> attributesMap =
				attributes.stream().collect(groupingBy(LotAttribute::getLotId, LinkedHashMap::new, Collectors.toList()));

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

				if (!CollectionUtils.isEmpty(lotUpdateDto.getAttributes())) {
					lotUpdateDto.getAttributes().forEach((key, value) -> {
						final String variableNameOrAlias = key.toUpperCase();
						this.saveOrUpdateAttribute(attributeVariablesNameMap, attributesMap, lot,
							variableNameOrAlias, value, conflictErrors);
					});
				}
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareRequestException("", "common.middleware.error.update.lots");
		}

		if (!conflictErrors.isEmpty()) {
			throw new MiddlewareRequestException("", conflictErrors);
		}
	}

	private void saveOrUpdateAttribute(final Map<String, Variable> attributeVariables,
		final Map<Integer, List<LotAttribute>> attributesMap, final Lot lot,
		final String variableNameOrAlias, final String value, final Multimap<String, Object[]> conflictErrors) {
		// Check first if the code to save is a valid Attribute
		if (attributeVariables.containsKey(variableNameOrAlias) && StringUtils.isNotEmpty(value)) {
			final Variable variable = attributeVariables.get(variableNameOrAlias);
			final List<LotAttribute> lotAttributes = attributesMap.getOrDefault(lot.getId(), new ArrayList<>());
			final List<LotAttribute> existingLotAttributesForVariable =
				lotAttributes.stream().filter(n -> n.getTypeId().equals(variable.getId())).collect(Collectors.toList());

			// Check if there are multiple attributes with same type
			if (existingLotAttributesForVariable.size() > 1) {
				conflictErrors.put("lot.update.duplicate.attributes", new String[] {
					variableNameOrAlias, String.valueOf(lot.getId())});
			} else {
				final boolean isValidValue = VariableValueUtil.isValidAttributeValue(variable, value);
				if (isValidValue) {
					final Integer cValueId = VariableValueUtil.resolveCategoricalValueId(variable, value);
					if (existingLotAttributesForVariable.size() == 1) {
						// if an attribute already exists for the variable, update the details from data input
						final LotAttribute attribute = existingLotAttributesForVariable.get(0);
						attribute.setLocationId(lot.getLocationId());
						attribute.setAval(value);
						attribute.setcValueId(cValueId);
						this.daoFactory.getLotAttributeDAO().update(attribute);
					} else {
						// create new attribute for the variable and data input
						final LotAttribute attribute =
							new LotAttribute(null, lot.getId(), variable.getId(), value, cValueId,
								lot.getLocationId(),
								0, Util.getCurrentDateAsIntegerValue());
						this.daoFactory.getLotAttributeDAO().save(attribute);
					}
				}
			}
		}
	}

	private Map<String, Variable> getAttributesMap(final String programUUID, final Set<String> variableNamesOrAlias) {
		if (!variableNamesOrAlias.isEmpty()) {
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.setProgramUuid(programUUID);
			variableFilter.addVariableType(VariableType.INVENTORY_ATTRIBUTE);
			variableNamesOrAlias.forEach(variableFilter::addName);

			final List<Variable> existingAttributeVariables =
				this.ontologyVariableDataManager.getWithFilter(variableFilter);

			final Map<String, Variable> map = new HashMap<>();
			existingAttributeVariables.forEach(a -> {
				map.put(a.getName().toUpperCase(), a);
				if (StringUtils.isNotEmpty(a.getAlias())) {
					map.put(a.getAlias().toUpperCase(), a);
				}
			});
			return map;
		} else {
			return new HashMap<>();
		}
	}

	private Map<String, Integer> buildLocationsByLocationAbbrMap(final List<String> locationAbbreviations) {

		final List<LocationDTO> locations =
			this.daoFactory.getLocationDAO()
				.searchLocations(new LocationSearchRequest(STORAGE_LOCATION_TYPE, null, locationAbbreviations, null),
					null, null);
		return locations.stream().collect(Collectors.toMap(LocationDTO::getAbbreviation, LocationDTO::getId));
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
			final List<String> locationAbbreviations = lotItemDtos.stream()
				.map(LotItemDto::getStorageLocationAbbr)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

			final Map<String, Integer> locationsByLocationAbbrMap = CollectionUtils.isEmpty(locationAbbreviations) ?
				new HashMap<>() :
				this.buildLocationsByLocationAbbrMap(locationAbbreviations);

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

				this.generateLotIds(cropType, Lists.newArrayList(lot));
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

	@Override
	public boolean isLocationUsedInLot(final Integer locationId) {
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setLocationIds(Arrays.asList(locationId));
		return this.daoFactory.getLotDao().countSearchLots(lotsSearchDto) > 0;
	}

	@Override
	public void generateLotIds(final CropType crop, final List<Lot> lots) {
		UIDGenerator.generate(crop, lots, UID_ROOT, SUFFIX_LENGTH,
			new UIDGenerator.UIDAdapter<Lot>() {

				@Override
				public String getUID(final Lot entry) {
					return entry.getLotUuId();
				}

				@Override
				public void setUID(final Lot entry, final String uid) {
					entry.setLotUuId(uid);
				}
			});
	}

	@Override
	public Integer getCurrentNotationNumberForBreederIdentifier(final String breederIdentifier) {
		final List<String> inventoryIDs = this.daoFactory.getLotDao().getInventoryIDsWithBreederIdentifier(breederIdentifier);

		if (CollectionUtils.isEmpty(inventoryIDs)) {
			return 0;
		}

		final String expression = "(?i)"+ breederIdentifier + "([0-9]+)";
		final Pattern pattern = Pattern.compile(expression);

		return this.findCurrentMaxNotationNumberInInventoryIDs(inventoryIDs, pattern);
	}

	private Integer findCurrentMaxNotationNumberInInventoryIDs(final List<String> inventoryIDs, final Pattern pattern) {
		Integer currentMax = 0;

		for (final String inventoryID : inventoryIDs) {
			final Matcher matcher = pattern.matcher(inventoryID);
			if (matcher.find()) {
				// Matcher.group(1) is needed because group(0) includes the identifier in the match
				// Matcher.group(1) only captures the value inside the parenthesis
				currentMax = Math.max(currentMax, Integer.valueOf(matcher.group(1)));
			}
		}

		return currentMax;
	}

	public void setTransactionService(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setOntologyVariableDataManager(final OntologyVariableDataManager ontologyVariableDataManager) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
	}
}
