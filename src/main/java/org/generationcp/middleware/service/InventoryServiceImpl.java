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
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

		final String expression = "(?i)"+ breederIdentifier + "([0-9]+)";
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


}
