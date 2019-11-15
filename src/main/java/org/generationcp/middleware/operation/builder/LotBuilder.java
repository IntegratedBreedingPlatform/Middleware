/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import com.google.common.collect.Lists;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.LotsResult;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.ArrayList;
import java.util.List;

public class LotBuilder extends Builder {

	private static final int LOT_NOT_DERIVED_FROM_ANOTHER = 0;

	private final DaoFactory daoFactory;

	public LotBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public List<Lot> build(
		final List<Integer> gids, final Integer locationId, final Integer scaleId, final String comment, final Integer userId, final Double amount,
		final Integer sourceId, final CropType cropType) throws MiddlewareQueryException {

		final List<Lot> lots = this.createLotsForAdd(gids, locationId, scaleId, comment, userId, amount, sourceId, cropType);
		return lots;
	}

	public List<Lot> buildForUpdate(final List<Integer> gids, final Integer locationId, final Integer scaleId, final String comment)
			throws MiddlewareQueryException {

		final List<Lot> lots = this.createLotsForUpdate(gids, locationId, scaleId, comment);
		return lots;
	}

	public List<Lot> buildForSave(
		final List<Integer> gids, final Integer locationId, final Integer scaleId, final String comment, final Integer userId, final Double amount,
		final Integer sourceId, final CropType cropType) throws MiddlewareQueryException {

		final List<Integer> newGids = this.removeGidsWithExistingCombination(gids, locationId, scaleId);
		final List<Lot> lots = this.createLotsForAdd(newGids, locationId, scaleId, comment, userId, amount, sourceId, cropType);
		return lots;
	}

	private List<Integer> removeGidsWithExistingCombination(final List<Integer> gids, final Integer locationId, final Integer scaleId)
			throws MiddlewareQueryException {

		final List<Integer> newGids = new ArrayList<Integer>();

		final List<Lot> existingLots =
			this.daoFactory.getLotDao().getByEntityTypeEntityIdsLocationIdAndScaleId(EntityType.GERMPLSM.name(), gids, locationId, scaleId);
		final List<Integer> gidsWithExistingCombi = new ArrayList<Integer>();
		if (existingLots != null && !existingLots.isEmpty()) {
			for (final Lot lot : existingLots) {
				gidsWithExistingCombi.add(lot.getEntityId());
			}
		}

		for (final Integer gid : gids) {
			if (!gidsWithExistingCombi.contains(gid)) {
				newGids.add(gid);
			}
		}

		return newGids;
	}

	private List<Lot> createLotsForAdd(
		final List<Integer> gids, final Integer locationId, final Integer scaleId, final String comment, final Integer userId,
		final Double amount, final Integer sourceId, final CropType cropType) throws MiddlewareQueryException {
		final List<Lot> lots = new ArrayList<>();

		if (gids != null && !gids.isEmpty()) {
			for (final Integer gid : gids) {

				lots.add(this.createLotForAdd(gid, locationId, scaleId, comment, userId, cropType));
			}
		}

		return lots;
	}

	public Lot createLotForAdd(
		final Integer gid, final Integer locationId, final Integer scaleId, final String comment, final Integer userId,
		final CropType cropType)
			throws MiddlewareQueryException {
		final Lot lot = new Lot(null, userId, EntityType.GERMPLSM.name(), gid, locationId, scaleId, LotStatus.ACTIVE.getIntValue(),
			LotBuilder.LOT_NOT_DERIVED_FROM_ANOTHER, comment);

		this.getInventoryDataManager().generateLotIds(cropType, Lists.newArrayList(lot));
		return lot;
	}

	private List<Lot> createLotsForUpdate(final List<Integer> gids, final Integer locationId, final Integer scaleId, final String comment)
			throws MiddlewareQueryException {
		final List<Lot> existingLots = this.daoFactory.getLotDao().getByEntityTypeAndEntityIds(EntityType.GERMPLSM.name(), gids);

		if (gids != null && !gids.isEmpty()) {

			for (final Lot lot : existingLots) {
				lot.setLocationId(locationId);
				lot.setScaleId(scaleId);
				lot.setComments(comment);
			}
		}

		return existingLots;
	}

	public LotsResult getGidsForUpdateAndAdd(final List<Integer> gids) throws MiddlewareQueryException {

		final List<Integer> newGids = new ArrayList<Integer>();
		final List<Lot> existingLots = this.daoFactory.getLotDao().getByEntityTypeAndEntityIds(EntityType.GERMPLSM.name(), gids);
		final List<Integer> gidsWithExistingCombi = new ArrayList<Integer>();
		if (existingLots != null && !existingLots.isEmpty()) {
			for (final Lot lot : existingLots) {
				gidsWithExistingCombi.add(lot.getEntityId());
			}
		}

		for (final Integer gid : gids) {
			if (!gidsWithExistingCombi.contains(gid)) {
				newGids.add(gid);
			}
		}

		final LotsResult result = new LotsResult();
		result.setGidsUpdated(gidsWithExistingCombi);
		result.setGidsAdded(newGids);
		return result;
	}

}
