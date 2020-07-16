/**
 * **************************************************************************** Copyright (c) 2014, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * <p/>
 * *****************************************************************************
 */

package org.generationcp.middleware.service;


import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the API for inventory management syste
 *
 */

@Transactional
public class InventoryServiceImpl implements InventoryService {

	private DaoFactory daoFactory;

	private static final Logger LOG = LoggerFactory.getLogger(InventoryServiceImpl.class);

	public InventoryServiceImpl() {

	}


	public InventoryServiceImpl(final HibernateSessionProvider sessionProvider) {

		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public boolean hasInventoryDetails(final Integer studyId) {
		return this.daoFactory.getTransactionDAO().hasInventoryDetails(studyId);
	}

	@Override
	public List<InventoryDetails> getInventoryDetails(final Integer studyId) {
		final List<InventoryDetails> inventoryDetails = this.daoFactory.getTransactionDAO().getInventoryDetails(studyId);
		this.fillLocationDetails(inventoryDetails);
		this.fillScaleDetails(inventoryDetails);
		return inventoryDetails;
	}

	private void fillScaleDetails(final List<InventoryDetails> inventoryDetails) {
		// collect all used scales
		final Set<Integer> scaleIds = new HashSet<Integer>();
		for (final InventoryDetails detail : inventoryDetails) {
			final Integer scaleId = detail.getScaleId();
			if (scaleId != null) {
				scaleIds.add(scaleId);
			}
		}
		// get the scale details from database
		final Map<Integer, CVTerm> scalesMap = new HashMap<>();
		if (!scaleIds.isEmpty()) {
			final List<CVTerm> cvtermList = this.daoFactory.getCvTermDao().getByIds(new ArrayList<>(scaleIds));
			for (final CVTerm cvTerm : cvtermList) {
				scalesMap.put(cvTerm.getCvTermId(), cvTerm);
			}
		}
		// set scale details of the inventory
		for (final InventoryDetails detail : inventoryDetails) {
			if (detail.getScaleId() != null && scalesMap.containsKey(detail.getScaleId())) {
				final CVTerm cvTerm = scalesMap.get(detail.getScaleId());
				detail.setScaleName(cvTerm.getName());
			}
		}
	}

	private void fillLocationDetails(final List<InventoryDetails> inventoryDetails) {
		// collect all used locations
		final Set<Integer> locationIds = new HashSet<Integer>();
		for (final InventoryDetails detail : inventoryDetails) {
			final Integer locationId = detail.getLocationId();
			if (locationId != null) {
				locationIds.add(locationId);
			}
		}
		// get the location details from database
		final Map<Integer, Location> locationsMap = new HashMap<>();
		if (!locationIds.isEmpty()) {
			final List<Location> locations = this.daoFactory.getLocationDAO().getByIds(new ArrayList<>(locationIds));
			for (final Location location : locations) {
				locationsMap.put(location.getLocid(), location);
			}
		}
		// set location details of the inventory
		for (final InventoryDetails detail : inventoryDetails) {
			if (detail.getLocationId() != null && locationsMap.containsKey(detail.getLocationId())) {
				final Location location = locationsMap.get(detail.getLocationId());
				detail.setLocationName(location.getLname());
				detail.setLocationAbbr(location.getLabbr());
			}
		}
	}



	/**
	 * This method gets the maximum notation number of the existing stock IDs. For example, if there are existing stock IDs: SID1-1, SID1-2,
	 * SID2-1, SID2-2, SID2-3, SID3-1, SID3-2, and the breeder identifier is SID, this method returns 3, from SID3-1 or SID3-2. If there no
	 * existing stock IDs with matching breeder identifier, 0 is returned.
	 */
	@Override
	public Integer getCurrentNotationNumberForBreederIdentifier(final String breederIdentifier) {
		final List<String> inventoryIDs =
				this.daoFactory.getLotDao().getInventoryIDsWithBreederIdentifier(breederIdentifier);

		if (inventoryIDs == null || inventoryIDs.isEmpty()) {
			return 0;
		}

		final String expression = breederIdentifier + "([0-9]+)";
		final Pattern pattern = Pattern.compile(expression);

		return this.findCurrentMaxNotationNumberInInventoryIDs(inventoryIDs, pattern);

	}

	protected Integer findCurrentMaxNotationNumberInInventoryIDs(final List<String> inventoryIDs, final Pattern pattern) {
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

	/**
	 * This method creates a new inventory lot, inventory transaction and stock transaction and save them in the database. An inventory lot
	 * tracks individual entities, where an entity is stored, what units they are managed in, what quantities are in storage and what
	 * quantities are available for use. Thus, it should be unique by entity id (ex. germplasm id), entity type (ex. germplasm if the
	 * entities are seed stocks), location id (where the lot is stored) and scale id (scale in which quantities of entity are measured). An
	 * inventory transaction tracks anticipated transactions (Deposit or Reserved), committed transactions (Stored or Retrieved) and
	 * cancelled transactions made on inventory lots. On the other hand, an stock transaction tracks the inventory transaction made on
	 * generated seed stock of a nursery/trial
	 *
	 */


	@Override
	public Lot getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(final String entityType, final Integer entityId,
			final Integer locationId, final Integer scaleId) {
		final List<Lot> lots =
				this.daoFactory.getLotDao().getByEntityTypeEntityIdsLocationIdAndScaleId(entityType,
						Arrays.asList(new Integer[] {entityId}), locationId, scaleId);
		if (lots != null && !lots.isEmpty()) {
			return lots.get(0);
		}
		return null;
	}


}
