package org.generationcp.middleware.operation.builder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.domain.inventory.util.LotTransformer;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.oms.CVTerm;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.springframework.util.CollectionUtils;

public class ListInventoryBuilder extends Builder {

	private DaoFactory daoFactory;

	public ListInventoryBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	/**
	 * Return list of GermplasmListData objects for given list, with counts for number of lots with available balance and number of lots
	 * with reserved seed per entry
	 *
	 * @param listEntries list of {@link GermplasmListData} to which we append lot counts.
	 * @return a germplasm list with the lot count
	 */
	public List<GermplasmListData> retrieveLotCountsForList(final List<GermplasmListData> listEntries) {
		final List<Integer> listEntryIds = new ArrayList<Integer>();
		final List<Integer> gids = new ArrayList<Integer>();
		final List<Integer> lrecIds = new ArrayList<Integer>();
		for (final GermplasmListData entry : listEntries) {
			listEntryIds.add(entry.getId());
			gids.add(entry.getGid());
			entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
			lrecIds.add(entry.getId());
		}

		if (listEntries != null && !listEntries.isEmpty()) {
			this.retrieveLotCounts(listEntryIds, listEntries, gids, lrecIds);
			this.retrieveWithdrawalAndStatus(listEntryIds, listEntries, gids, lrecIds);
			this.retrieveGroupId(listEntries, gids);
		}
		return listEntries;
	}

	protected void retrieveWithdrawalAndStatus(final List<Integer> entryIds, final List<GermplasmListData> listEntries,
			final List<Integer> gids, final List<Integer> lrecIds) throws MiddlewareQueryException {
		this.retrieveWithdrawalBalance(listEntries, lrecIds);
		this.retrieveWithdrawalStatus(listEntries, gids);
		this.setAvailableBalanceScale(listEntries);
		this.setWithdrawalBalanceScale(listEntries);

	}

	/**
	 * Retrieves GROUP ID(germplsm.mgid) directly from the Germplasm Object
	 *
	 * @param listEntries
	 * @param gids
	 */
	void retrieveGroupId(final List<GermplasmListData> listEntries, final List<Integer> gids) {

		// We retrieve the actual germplasm object here instead of using the Germplasm from GermplasmListData, since sometimes it is null
		// due to transaction isolation level.
		final List<Germplasm> germplasms = this.getGermplasmDataManager().getGermplasms(gids);

		final Map<Integer, Germplasm> germplasmMap = Maps.uniqueIndex(germplasms, new Function<Germplasm, Integer>() {

			@Override
			public Integer apply(final Germplasm from) {
				return from.getGid();
			}
		});

		for (final GermplasmListData listEntry : listEntries) {
			final Integer gid = listEntry.getGid();
			if (germplasmMap.containsKey(gid)) {
				listEntry.setGroupId(germplasmMap.get(gid).getMgid());
			}
		}
	}

	public List<GermplasmListData> retrieveLotCountsForListEntries(final Integer listId, final List<Integer> entryIds)
			throws MiddlewareQueryException {
		List<GermplasmListData> listEntries = null;
		listEntries = daoFactory.getGermplasmListDataDAO().getByIds(entryIds);
		final List<Integer> gids = new ArrayList<Integer>();
		final List<Integer> lrecIds = new ArrayList<Integer>();
		for (final GermplasmListData entry : listEntries) {
			gids.add(entry.getGid());
			entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
			lrecIds.add(entry.getId());
		}
		this.retrieveLotCounts(entryIds, listEntries, gids, lrecIds);
		this.retrieveGroupId(listEntries, gids);
		this.retrieveWithdrawalAndStatus(entryIds, listEntries, gids, lrecIds);
		return listEntries;
	}

	private void retrieveLotCounts(final List<Integer> entryIds, final List<GermplasmListData> listEntries, final List<Integer> gids,
			final List<Integer> lrecIds) throws MiddlewareQueryException {

		// NEED to pass specific GIDs instead of listdata.gid because of handling for CHANGES table
		// where listdata.gid may not be the final germplasm displayed
		this.retrieveAvailableBalLotCounts(listEntries, gids);
		this.retrieveReservedLotCounts(listEntries, entryIds);
		this.retrieveStockIds(listEntries, gids);

	}

	// This will add overall scale for germplsm and set in InventoryInfo. Useful to display scale along with available balance
	protected void setAvailableBalanceScale(final List<GermplasmListData> listEntries) {
		Set<Integer> setScaleIds = new HashSet<>();
		Map<Integer, Term> mapScaleTerm = new HashMap<>();
		for (GermplasmListData entry : listEntries) {
			if (entry.getInventoryInfo() != null && entry.getInventoryInfo().getScaleIdForGermplsm() != null) {
				setScaleIds.add(entry.getInventoryInfo().getScaleIdForGermplsm());
			}
		}

		List<Integer> listScaleIds = Lists.newArrayList(setScaleIds);

		if (!listScaleIds.isEmpty()) {
			Map<Integer, Integer> scaleIdWiseTermId = new HashMap<>();
			final List<CVTermRelationship> relationshipsOfScales =
					daoFactory.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(listScaleIds, TermId.HAS_SCALE.getId());
			List<Integer> listObjectIds = new ArrayList<>();
			if (relationshipsOfScales != null) {
				for (CVTermRelationship relationship : relationshipsOfScales) {
					scaleIdWiseTermId.put(relationship.getSubjectId(), relationship.getObjectId());
					listObjectIds.add(relationship.getObjectId());
				}

				List<CVTerm> listScaleCvTerm = daoFactory.getCvTermDao().getByIds(listObjectIds);

				for (final CVTerm cvTerm : listScaleCvTerm) {
					Term term = TermBuilder.mapCVTermToTerm(cvTerm);
					mapScaleTerm.put(term.getId(), term);

				}

				for (GermplasmListData entry : listEntries) {
					if (entry.getInventoryInfo() != null && entry.getInventoryInfo().getScaleIdForGermplsm() != null) {
						Integer scaleId = entry.getInventoryInfo().getScaleIdForGermplsm();

						if (scaleIdWiseTermId.containsKey(scaleId)) {
							Integer objectId = scaleIdWiseTermId.get(scaleId);
							if (mapScaleTerm.containsKey(objectId)) {
								Term scale = mapScaleTerm.get(objectId);
								entry.getInventoryInfo().setScaleForGermplsm(scale.getName());
							} else {
								entry.getInventoryInfo().setScaleForGermplsm("");
							}
						} else {
							entry.getInventoryInfo().setScaleForGermplsm("");
						}

					}

				}
			}
		}

	}

	public void setAvailableBalanceScaleForGermplasm(final List<Germplasm> germplasmList) {
		if(!CollectionUtils.isEmpty(germplasmList)) {
			List<Integer> gids = Lists.newArrayList();

			for(Germplasm germplasm : germplasmList) {
				gids.add(germplasm.getGid());
			}

			List<Object[]> lotScalesForGermplsms = daoFactory.getLotDao().retrieveLotScalesForGermplasms(gids);

			if(!CollectionUtils.isEmpty(lotScalesForGermplsms)) {
				Map<Integer, Set<String>> germplsmWiseScales = new HashMap<>();

				for(Object[] entry : lotScalesForGermplsms) {
					Integer germplasmId = (Integer) entry[0];
					String scaleName = (String)entry[2];

					if(germplsmWiseScales.containsKey(germplasmId)) {
						germplsmWiseScales.get(germplasmId).add(scaleName);
					} else {
						germplsmWiseScales.put(germplasmId, Sets.newHashSet(scaleName));
					}

				}

				for(Germplasm germplasm : germplasmList) {
					if(germplsmWiseScales.containsKey(germplasm.getGid())) {
						Set<String> scales = germplsmWiseScales.get(germplasm.getGid());
						if(scales.size() > 1) {
							germplasm.getInventoryInfo().setScaleForGermplsm(ListDataInventory.MIXED);
						} else if(scales.size() == 1) {
							germplasm.getInventoryInfo().setScaleForGermplsm(scales.iterator().next());
						}
					}
				}
			}

		}

	}

	// This will set withdrawal scale for germplsm for selected list entries
	private void setWithdrawalBalanceScale(final List<GermplasmListData> listEntries) {
		Set<Integer> setScaleIds = new HashSet<>();
		Map<Integer, Term> mapScaleTerm = new HashMap<>();
		for (GermplasmListData entry : listEntries) {
			if (entry.getInventoryInfo() != null && entry.getInventoryInfo().getWithdrawalScaleId() != null) {
				setScaleIds.add(entry.getInventoryInfo().getWithdrawalScaleId());
			}
		}

		List<Integer> listScaleIds = Lists.newArrayList(setScaleIds);

		if (!listScaleIds.isEmpty()) {
			Map<Integer, Integer> scaleIdWiseTermId = new HashMap<>();
			final List<CVTermRelationship> relationshipsOfScales =
					daoFactory.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(listScaleIds, TermId.HAS_SCALE.getId());
			List<Integer> listObjectIds = new ArrayList<>();
			if (relationshipsOfScales != null) {
				for (CVTermRelationship relationship : relationshipsOfScales) {
					scaleIdWiseTermId.put(relationship.getSubjectId(), relationship.getObjectId());
					listObjectIds.add(relationship.getObjectId());
				}

				List<CVTerm> listScaleCvTerm = daoFactory.getCvTermDao().getByIds(listObjectIds);

				for (final CVTerm cvTerm : listScaleCvTerm) {
					Term term = TermBuilder.mapCVTermToTerm(cvTerm);
					mapScaleTerm.put(term.getId(), term);

				}

				for (GermplasmListData entry : listEntries) {
					if (entry.getInventoryInfo() != null && entry.getInventoryInfo().getWithdrawalScaleId() != null) {
						Integer scaleId = entry.getInventoryInfo().getWithdrawalScaleId();

						if (scaleIdWiseTermId.containsKey(scaleId)) {
							Integer objectId = scaleIdWiseTermId.get(scaleId);
							if (mapScaleTerm.containsKey(objectId)) {
								Term scale = mapScaleTerm.get(objectId);
								entry.getInventoryInfo().setWithdrawalScale(scale.getName());

							} else {
								entry.getInventoryInfo().setWithdrawalScale("");
							}
						} else {
							entry.getInventoryInfo().setWithdrawalScale("");
						}

					}

				}
			}
		}

	}

	private void retrieveStockIds(final List<GermplasmListData> listEntries, final List<Integer> gIds) {
		final Map<Integer, String> stockIDs = daoFactory.getTransactionDAO().retrieveStockIds(gIds);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				String stockIdsForGid = stockIDs.get(entry.getGid());
				inventory.setStockIDs(stockIdsForGid);
				entry.setStockIDs(stockIdsForGid);
			}
		}
	}

	public Integer countLotsWithAvailableBalanceForGermplasm(final Integer gid) throws MiddlewareQueryException {
		Integer lotCount = null;
		final Map<Integer, BigInteger> lotCounts = daoFactory.getLotDao().countLotsWithAvailableBalance(Collections.singletonList(gid));
		final BigInteger lotCountBigInt = lotCounts.get(gid);
		if (lotCounts != null && lotCountBigInt != null) {
			lotCount = lotCountBigInt.intValue();
		}
		return lotCount;
	}

	public List<LotDetails> retrieveInventoryLotsForGermplasm(final Integer gid) throws MiddlewareQueryException {
		List<LotDetails> lotDetails = null;
		final List<Lot> lots = daoFactory.getLotDao().getLotAggregateDataForGermplasm(gid);
		final Map<Integer, Object[]> lotStatusDataForGermplasm = daoFactory.getLotDao().getLotStatusDataForGermplasm(gid);
		this.setLotStatusForGermplasm(lots, lotStatusDataForGermplasm);
		lotDetails = LotTransformer.extraLotDetails(lots);
		this.setLocationsAndScales(lotDetails);
		return lotDetails;
	}

	/**
	 * Return list of GermplasmListData objects for given list with list of lots associated per germplasm entry
	 *
	 * @param listId
	 * @param start
	 * @param numOfRows
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public List<GermplasmListData> retrieveInventoryLotsForList(final Integer listId, final int start, final int numOfRows,
			final List<GermplasmListData> listEntries) throws MiddlewareQueryException {

		final List<Integer> listEntryIds = new ArrayList<Integer>();
		final List<Integer> gids = new ArrayList<Integer>();
		for (final GermplasmListData entry : listEntries) {
			listEntryIds.add(entry.getId());
			gids.add(entry.getGid());
			entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
		}

		if (listEntries != null && !listEntries.isEmpty()) {
			// retrieve inventory information from local db

			// NEED to pass specific GIDs instead of listdata.gid because of handling for CHANGES table
			// where listdata.gid may not be the final germplasm displayed
			final List<Lot> lots = daoFactory.getLotDao().getLotAggregateDataForList(listId, gids);

			// add to each list entry related lot information
			final List<ListEntryLotDetails> lotRows = LotTransformer.extractLotRowsForList(listEntries, lots);
			this.setLocationsAndScales(lotRows);
		}
		return listEntries;
	}

	/**
	 * Return list of GermplasmListData objects for given list with list of reserved lots associated per germplasm entry
	 *
	 * @param listId
	 * @param listEntries germplsm list entries
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public List<GermplasmListData> retrieveReservedInventoryLotsForList(final Integer listId, final List<GermplasmListData> listEntries)
			throws MiddlewareQueryException {

		final List<Integer> listEntryIds = new ArrayList<Integer>();
		final List<Integer> gids = new ArrayList<Integer>();
		for (final GermplasmListData entry : listEntries) {
			listEntryIds.add(entry.getId());
			gids.add(entry.getGid());
			entry.setInventoryInfo(new ListDataInventory(entry.getId(), entry.getGid()));
		}

		if (listEntries != null && !listEntries.isEmpty()) {

			final List<Lot> lots = daoFactory.getLotDao().getReservedLotAggregateDataForList(listId, gids);

			final List<ListEntryLotDetails> lotRows = LotTransformer.extractLotRowsForList(listEntries, lots);
			this.setLocationsAndScales(lotRows);
		}
		return listEntries;
	}

	public List<ListEntryLotDetails> retrieveInventoryLotsForListEntry(final Integer listId, final Integer recordId, final Integer gid)
			throws MiddlewareQueryException {
		List<ListEntryLotDetails> lotRows = new ArrayList<ListEntryLotDetails>();

		final List<Lot> lots = daoFactory.getLotDao().getLotAggregateDataForListEntry(listId, gid);
		lotRows = LotTransformer.extractLotDetailsForListEntry(lots, recordId);
		this.setLocationsAndScales(lotRows);

		return lotRows;
	}

	/*
	 * Retrieve the number of lots with available balance per germplasm and available seed balance per germplsm along with overall scaleId
	 */
	private void retrieveAvailableBalLotCounts(final List<GermplasmListData> listEntries, final List<Integer> gids)
			throws MiddlewareQueryException {
		final Map<Integer, Object[]> lotCounts = daoFactory.getLotDao().getAvailableBalanceCountAndTotalLotsCount(gids);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				final Object[] count = lotCounts.get(entry.getGid());
				this.setAvailableBalanceAndScale(inventory, count);
			}
		}
	}

	public void setAvailableBalanceAndScale(GermplasmInventory listDataInventory, Object[] balanceValues) {
		if (balanceValues != null) {
			BigInteger actualInventoryLotCount = (BigInteger) balanceValues[0];
			listDataInventory.setActualInventoryLotCount(actualInventoryLotCount.intValue());

			BigInteger lotCount = (BigInteger) balanceValues[1];
			listDataInventory.setLotCount(lotCount.intValue());

			Double totalAvailableBalance = (Double) balanceValues[2];
			listDataInventory.setTotalAvailableBalance(totalAvailableBalance.doubleValue());

			BigInteger distinctScaleCount = (BigInteger) balanceValues[3];
			listDataInventory.setDistinctScaleCountForGermplsm(distinctScaleCount.intValue());

			if (balanceValues[4] != null) {
				Integer scaleId = (Integer) balanceValues[4];
				listDataInventory.setScaleIdForGermplsm(scaleId.intValue());
			}
		} else {
			listDataInventory.setActualInventoryLotCount(0);
			listDataInventory.setLotCount(0);
			listDataInventory.setTotalAvailableBalance(0.0);
			listDataInventory.setDistinctScaleCountForGermplsm(0);
			listDataInventory.setScaleIdForGermplsm(null);
		}
	}
	/*
	 * Retrieve the number of lots with reserved seeds per list entry
	 */
	private void retrieveReservedLotCounts(final List<GermplasmListData> listEntries, final List<Integer> listEntryIds)
			throws MiddlewareQueryException {
		final Map<Integer, BigInteger> reservedLotCounts = daoFactory.getTransactionDAO().countLotsWithReservationForListEntries(listEntryIds);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				final BigInteger count = reservedLotCounts.get(entry.getId());
				if (count != null) {
					inventory.setReservedLotCount(count.intValue());
				} else {
					inventory.setReservedLotCount(0);
				}
			}
		}
	}

	/*
	 * Retrieve withdrawal balance per entry along with overall status
	 */
	protected void retrieveWithdrawalBalance(final List<GermplasmListData> listEntries, final List<Integer> listEntryIds)
			throws MiddlewareQueryException {
		final Map<Integer, Object[]> withdrawalData = daoFactory.getTransactionDAO().retrieveWithdrawalBalanceWithDistinctScale(listEntryIds);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				final Object[] withdrawalPerRecord = withdrawalData.get(entry.getId());
				if (withdrawalPerRecord != null) {
					inventory.setWithdrawalBalance((Double) withdrawalPerRecord[0]);

					BigInteger countWithDrawalScale = (BigInteger) withdrawalPerRecord[1];
					inventory.setDistinctCountWithdrawalScale(countWithDrawalScale.intValue());
					inventory.setWithdrawalScaleId((Integer) withdrawalPerRecord[2]);

				} else {
					inventory.setWithdrawalBalance(0.0);
					inventory.setDistinctCountWithdrawalScale(0);
					inventory.setWithdrawalScaleId(null);
					inventory.setWithdrawalScale(null);
				}
			}
		}
	}

	/*
	 * Retrieve withdrawal status per entry along with overall status
	 */
	protected void retrieveWithdrawalStatus(final List<GermplasmListData> listEntries, final List<Integer> gIds)
			throws MiddlewareQueryException {
		Integer sourceId = listEntries.get(0).getList().getId();
		final List<Object[]> withdrawalData = daoFactory.getTransactionDAO().retrieveWithdrawalStatus(sourceId, gIds);

		final Map<Integer, HashSet<Integer>> germplasmWiseLotsMap = new HashMap<>();
		final Map<Integer, HashSet<Integer>> lotWiseStatus = new HashMap<>();
		final Map<Integer, Integer> lotWiseDistinctStatus = new HashMap<>();
		Integer lotId = null;
		Integer germplsmId = null;
		Object tranStatus = null;
		if (withdrawalData != null && !withdrawalData.isEmpty()) {
			for (Object[] withdrawalStatusData : withdrawalData) {
				lotId = (Integer) withdrawalStatusData[0];
				germplsmId = (Integer) withdrawalStatusData[1];
				tranStatus = withdrawalStatusData[3];

				if (!lotWiseDistinctStatus.containsKey(lotId)) {
					lotWiseDistinctStatus.put((Integer) lotId, 0);
				}
				Integer lotCount = lotWiseDistinctStatus.get(lotId);
				lotCount += 1;
				lotWiseDistinctStatus.put((Integer) lotId, lotCount);

				if (tranStatus != null) {
					if (!lotWiseStatus.containsKey(lotId)) {
						lotWiseStatus.put(lotId, new HashSet<Integer>());
					}
					lotWiseStatus.get(lotId).add((Integer) tranStatus);
				} else {
					lotWiseStatus.put(lotId, new HashSet<Integer>());
				}

				if (!germplasmWiseLotsMap.containsKey(germplsmId)) {
					germplasmWiseLotsMap.put(germplsmId, Sets.<Integer>newHashSet());
				}
				germplasmWiseLotsMap.get(germplsmId).add(lotId);

			}
		}

		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			Integer germplasmId = entry.getGermplasmId();
			if (inventory != null) {
				String status = "";
				if (germplasmWiseLotsMap.containsKey(germplasmId)) {
					HashSet<Integer> germplsLots = germplasmWiseLotsMap.get(germplasmId);

					if (germplsLots.size() == 1) {
						Integer lotID = germplsLots.iterator().next();
						HashSet<Integer> oneLotStatusSet = lotWiseStatus.get(lotID);

						if (oneLotStatusSet != null) {
							if (oneLotStatusSet.size() == 1) {
								Integer oneLotStatus = oneLotStatusSet.iterator().next();
								if (oneLotStatus == 1) {
									status = ListDataInventory.WITHDRAWN;
								} else if (oneLotStatus == 0) {
									status = ListDataInventory.RESERVED;
								}
							} else if (oneLotStatusSet.size() > 1) {
								status = ListDataInventory.RESERVED;
							}

						}
					} else if (germplsLots.size() > 1) {
						HashSet<Integer> distinctStatus = new HashSet<>();
						for (Integer lotID : germplsLots) {
							distinctStatus.addAll(lotWiseStatus.get(lotID));
						}

						if (distinctStatus.size() == 0) {
							status = "";
						} else if (distinctStatus.size() == 1) {
							Integer singleStatus = distinctStatus.iterator().next();
							if (singleStatus == 1) {
								status = ListDataInventory.WITHDRAWN;
							} else if (singleStatus == 0) {
								status = ListDataInventory.RESERVED;
							}
						} else {
							Set<Integer> overallStatus = new HashSet<>();
							for (Integer lotID : germplsLots) {
								HashSet<Integer> lotStatus = lotWiseStatus.get(lotID);

								if (lotStatus.size() == 1) {
									if (lotStatus.contains(0)) {
										overallStatus.add(0);
									} else if (lotStatus.contains(1)) {
										overallStatus.add(1);
									}
								} else if (lotStatus.size() > 1) {
									overallStatus.add(0);
								}

							}

							if (overallStatus.size() == 1) {
								Integer transactionStatus = overallStatus.iterator().next();
								if (transactionStatus == 0) {
									status = ListDataInventory.RESERVED;
								} else if (transactionStatus == 1) {
									status = ListDataInventory.WITHDRAWN;
								}

							} else if (overallStatus.size() > 1) {
								status = ListDataInventory.MIXED;
							}

						}
					}
				}
				inventory.setTransactionStatus(status);
			}
		}
	}

	/*
	 * Perform one-retrieval for central/local scales and central/local locations for list of lots
	 */
	private void setLocationsAndScales(final List<? extends LotDetails> lots) throws MiddlewareQueryException {

		final List<Integer> locationIds = new ArrayList<Integer>();
		final List<Integer> scaleIds = new ArrayList<Integer>();
		final Map<Integer, List<LotDetails>> scaleLotMap = new HashMap<Integer, List<LotDetails>>();
		final Map<Integer, List<LotDetails>> locationLotMap = new HashMap<Integer, List<LotDetails>>();

		this.createScaleAndLocationMaps(lots, locationIds, scaleIds, scaleLotMap, locationLotMap);

		final List<Location> allLocations = daoFactory.getLocationDAO().getByIds(locationIds);
		final List<Term> allScales = new ArrayList<Term>();
		final List<CVTerm> cvTerms = daoFactory.getCvTermDao().getByIds(scaleIds);
		for (final CVTerm cvTerm : cvTerms) {
			allScales.add(TermBuilder.mapCVTermToTerm(cvTerm));
		}

		Map<Integer, String> mapScaleAbbrMap = new HashMap<>();
		Map<Integer, String> mapScaleMethodMap = new HashMap<>();
		Map<Integer, Integer> scaleIdWiseTermId = new HashMap<>();
		Map<Integer, Integer> scaleIdWiseMethodTermId = new HashMap<>();
		if (!scaleIds.isEmpty()) {

			final List<CVTermRelationship> relationshipsOfScales =
					daoFactory.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(scaleIds, TermId.HAS_SCALE.getId());

			List<Integer> listObjectIds = new ArrayList<>();
			if (relationshipsOfScales != null) {
				for (CVTermRelationship relationship : relationshipsOfScales) {
					scaleIdWiseTermId.put(relationship.getSubjectId(), relationship.getObjectId());
					listObjectIds.add(relationship.getObjectId());
				}

				List<CVTerm> listScaleCvTerm = daoFactory.getCvTermDao().getByIds(listObjectIds);

				for (final CVTerm cvTerm : listScaleCvTerm) {
					Term term = TermBuilder.mapCVTermToTerm(cvTerm);
					mapScaleAbbrMap.put(term.getId(), term.getName());
				}

			}

			final List<CVTermRelationship> scaleMethodRelationship =
					daoFactory.getCvTermRelationshipDao().getBySubjectIdsAndTypeId(scaleIds, TermId.HAS_METHOD.getId());

			List<Integer> listMethodObjectIds = new ArrayList<>();
			if (scaleMethodRelationship != null) {
				for (CVTermRelationship relationship : scaleMethodRelationship) {
					scaleIdWiseMethodTermId.put(relationship.getSubjectId(), relationship.getObjectId());
					listMethodObjectIds.add(relationship.getObjectId());
				}

				List<CVTerm> listScaleMethodCvTerm = daoFactory.getCvTermDao().getByIds(listMethodObjectIds);

				for (final CVTerm cvTerm : listScaleMethodCvTerm) {
					Term term = TermBuilder.mapCVTermToTerm(cvTerm);
					mapScaleMethodMap.put(term.getId(), term.getName());
				}

			}
		}

		for (final Location location : allLocations) {
			final List<LotDetails> lotList = locationLotMap.get(location.getLocid());
			for (final LotDetails lot : lotList) {
				lot.setLocationOfLot(location);
			}
		}

		for (final Term scale : allScales) {
			final List<LotDetails> lotList = scaleLotMap.get(scale.getId());
			for (final LotDetails lot : lotList) {
				lot.setScaleOfLot(scale);
				String scaleName = "";

				if (scaleIdWiseTermId.containsKey(lot.getScaleId())) {
					Integer hasScaleCvTermId = scaleIdWiseTermId.get(lot.getScaleId());

					if (mapScaleAbbrMap.containsKey(hasScaleCvTermId)) {
						scaleName = mapScaleAbbrMap.get(hasScaleCvTermId);
					}

				}

				lot.setLotScaleNameAbbr(scaleName);

				String scaleMethodName = "";
				if (scaleIdWiseMethodTermId.containsKey(lot.getScaleId())) {
					Integer hasMethodCvTermId = scaleIdWiseMethodTermId.get(lot.getScaleId());

					if (mapScaleMethodMap.containsKey(hasMethodCvTermId)) {
						scaleMethodName = mapScaleMethodMap.get(hasMethodCvTermId);
					}
				}
				lot.setLotScaleMethodName(scaleMethodName);

			}
		}
	}

	// create maps of scale/location IDs to lots for easier setting of Terms and Locations
	private void createScaleAndLocationMaps(final List<? extends LotDetails> lots, final List<Integer> locationIds,
			final List<Integer> scaleIds, final Map<Integer, List<LotDetails>> scaleLotMap,
			final Map<Integer, List<LotDetails>> locationLotMap) {

		for (final LotDetails lot : lots) {
			final Integer locationId = lot.getLocId();
			final List<LotDetails> lotList = locationLotMap.get(locationId);
			if (lotList != null) {
				lotList.add(lot);
			} else {
				final List<LotDetails> listLot = new ArrayList<LotDetails>();
				listLot.add(lot);
				locationLotMap.put(locationId, listLot);
			}
			locationIds.add(locationId);

			final Integer scaleId = lot.getScaleId();
			if (scaleLotMap.get(scaleId) != null) {
				scaleLotMap.get(scaleId).add(lot);
			} else {
				final List<LotDetails> listLot = new ArrayList<LotDetails>();
				listLot.add(lot);
				scaleLotMap.put(scaleId, listLot);
			}
			scaleIds.add(scaleId);
		}
	}

	private void setLotStatusForGermplasm(final List<Lot> lots, final Map<Integer, Object[]> lotStatusMap) {
		String lotStatus = null;
		for (Lot lot : lots) {
			lotStatus = "";
			if (lotStatusMap.containsKey(lot.getId())) {
				Object[] row = lotStatusMap.get(lot.getId());
				BigInteger distinctStatusCount = (BigInteger) row[0];
				if (distinctStatusCount.intValue() > 1) {
					lotStatus = ListDataInventory.RESERVED;
				} else if (distinctStatusCount.intValue() == 1) {
					Integer status = (Integer) row[1];
					if (status == 0) {
						lotStatus = ListDataInventory.RESERVED;
					} else if (status == 1) {
						lotStatus = ListDataInventory.WITHDRAWN;
					}
				}
			}
			lot.getAggregateData().setLotStatus(lotStatus);
		}
	}
}
