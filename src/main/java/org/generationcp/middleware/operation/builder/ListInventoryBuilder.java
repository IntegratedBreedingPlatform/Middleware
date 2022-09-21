package org.generationcp.middleware.operation.builder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	//FIXME This function is not needed. Wherever it is called it should be calling retrieveLotCountsForListEntries
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
			this.retrieveLotCounts( listEntries, gids, lrecIds);
			this.setAvailableBalanceScale(listEntries);
			this.retrieveGroupId(listEntries, gids);
		}
		return listEntries;
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

	private void retrieveLotCounts(final List<GermplasmListData> listEntries, final List<Integer> gids,
			final List<Integer> lrecIds) throws MiddlewareQueryException {

		// NEED to pass specific GIDs instead of listdata.gid because of handling for CHANGES table
		// where listdata.gid may not be the final germplasm displayed
		this.retrieveAvailableBalLotCounts(listEntries, gids);
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

}
