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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.LotBuilder;
import org.generationcp.middleware.operation.builder.TransactionBuilder;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.InventoryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the API for inventory management system.
 * 
 */

@Transactional
public class InventoryServiceImpl implements InventoryService {

	private LotBuilder lotBuilder;
	private TransactionBuilder transactionBuilder;
	private InventoryDaoFactory inventoryDaoFactory;

	public InventoryServiceImpl() {

	}

	public InventoryServiceImpl(final HibernateSessionProvider sessionProvider) {

		this.inventoryDaoFactory = new InventoryDaoFactory(sessionProvider);
		this.lotBuilder = new LotBuilder(sessionProvider);
		this.transactionBuilder = new TransactionBuilder(sessionProvider);

	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(final Integer listId) throws MiddlewareQueryException {
		return this.getInventoryDetailsByGermplasmList(listId, GermplasmListType.ADVANCED.name());
	}

	@Override
	public List<InventoryDetails> getInventoryDetailsByGermplasmList(final Integer listId, final String germplasmListType)
			throws MiddlewareQueryException {
		final GermplasmList germplasmList = this.inventoryDaoFactory.getGermplasmListDAO().getById(listId);
		final List<GermplasmListData> listData = this.getGermplasmListData(germplasmList, germplasmListType);
		return this.getInventoryDetailsList(germplasmList, listData);
	}

	private List<InventoryDetails> getInventoryDetailsList(final GermplasmList germplasmList, final List<GermplasmListData> listData) {
		// Get recordIds in list
		final List<Integer> recordIds = new ArrayList<Integer>();
		for (final GermplasmListData datum : listData) {
			if (datum != null) {
				recordIds.add(datum.getId());
			}
		}
		final List<InventoryDetails> inventoryDetails =
				this.inventoryDaoFactory.getTransactionDAO().getInventoryDetailsByTransactionRecordId(recordIds);
		this.fillInventoryDetailList(inventoryDetails, germplasmList, listData);
		this.fillLocationDetails(inventoryDetails);
		this.fillScaleDetails(inventoryDetails);
		this.fillUserDetails(inventoryDetails);
		Collections.sort(inventoryDetails);
		return inventoryDetails;
	}

	private void fillUserDetails(final List<InventoryDetails> inventoryDetails) {
		// collect all used users
		final Set<Integer> userIds = new HashSet<Integer>();
		for (final InventoryDetails detail : inventoryDetails) {
			final Integer userId = detail.getUserId();
			if (userId != null) {
				userIds.add(userId);
			}
		}
		// get the user details from database

		final Map<Integer, String> usersMap = new HashMap<Integer, String>();
		if (!userIds.isEmpty()) {
			usersMap.putAll(this.inventoryDaoFactory.getPersonDAO().getPersonNamesByUserIds(new ArrayList<>(userIds)));
		}

		// set scale details of the inventory
		for (final InventoryDetails detail : inventoryDetails) {
			if (detail.getUserId() != null && usersMap.containsKey(detail.getUserId())) {
				detail.setUserName(usersMap.get(detail.getUserId()));
			}
		}
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
			final List<CVTerm> cvtermList = this.inventoryDaoFactory.getCvTermDao().getByIds(new ArrayList<>(scaleIds));
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
			final List<Location> locations = this.inventoryDaoFactory.getLocationDAO().getByIds(new ArrayList<>(locationIds));
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

	protected void fillInventoryDetailList(final List<InventoryDetails> detailList, final GermplasmList germplasmList,
			final List<GermplasmListData> dataList) {
		final List<GermplasmListData> forFill = new ArrayList<>();

		final Map<Integer, List<InventoryDetails>> listDataIdToInventoryDetailsMap = new HashMap<>();

		for (final InventoryDetails inventoryDetails : detailList) {
		    Integer recordId = inventoryDetails.getSourceRecordId();
		  if (!listDataIdToInventoryDetailsMap.containsKey(recordId)) {
			listDataIdToInventoryDetailsMap.put(recordId, Lists.<InventoryDetails>newArrayList());
		  }
		  listDataIdToInventoryDetailsMap.get(recordId).add(inventoryDetails);
		}

		for (final GermplasmListData germplasmListData : dataList) {
			final List<InventoryDetails> inventoryDetails = listDataIdToInventoryDetailsMap.get(germplasmListData.getId());
			if (inventoryDetails != null) {
			  for(InventoryDetails inventoryDetailsList: inventoryDetails) {
				inventoryDetailsList.copyFromGermplasmListData(germplasmListData);
				inventoryDetailsList.setSourceId(germplasmList.getId());
				inventoryDetailsList.setSourceName(germplasmList.getName());
			  }
			} else {
				forFill.add(germplasmListData);
			}
		}

		for (final GermplasmListData data : forFill) {
			final InventoryDetails detail = new InventoryDetails();
			detail.copyFromGermplasmListData(data);
			detail.setSourceId(germplasmList.getId());
			detail.setSourceName(germplasmList.getName());
			detailList.add(detail);
		}
	}

	protected List<GermplasmListData> getGermplasmListData(final GermplasmList germplasmList, final String germplasmListType) {

		Integer germplasmListId = germplasmList.getId();
		if (germplasmList.getType() != null && germplasmList.getType().equalsIgnoreCase(germplasmListType)
				&& !GermplasmListType.LST.toString().equals(germplasmListType)) {
			germplasmListId = this.inventoryDaoFactory.getGermplasmListDAO().getListDataListIDFromListDataProjectListID(germplasmListId);
		}
		return this.inventoryDaoFactory.getGermplasmListDataDAO().getByListId(germplasmListId);
	}

	/**
	 * This method gets the maximum notation number of the existing stock IDs. For example, if there are existing stock IDs: SID1-1, SID1-2,
	 * SID2-1, SID2-2, SID2-3, SID3-1, SID3-2, and the breeder identifier is SID, this method returns 3, from SID3-1 or SID3-2. If there no
	 * existing stock IDs with matching breeder identifier, 0 is returned.
	 */
	@Override
	public Integer getCurrentNotationNumberForBreederIdentifier(final String breederIdentifier) throws MiddlewareQueryException {
		final List<String> inventoryIDs =
				this.inventoryDaoFactory.getTransactionDAO().getInventoryIDsWithBreederIdentifier(breederIdentifier);

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
	public void addLotAndTransaction(final InventoryDetails details, final GermplasmListData listData, final ListDataProject listDataProject)
			throws MiddlewareQueryException {
		final Lot existingLot =
				this.getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(EntityType.GERMPLSM.name(), details.getGid(),
						details.getLocationId(), details.getScaleId());

		if (existingLot != null) {
			throw new MiddlewareQueryException("A lot with the same entity id, location id, and scale id already exists");
		}

		final Lot lot =
				this.lotBuilder.createLotForAdd(details.getGid(), details.getLocationId(), details.getScaleId(), details.getComment(),
						details.getUserId());

		this.inventoryDaoFactory.getLotDao().saveOrUpdate(lot);

		final Transaction transaction =
				this.transactionBuilder.buildForAdd(lot, listData == null ? 0 : listData.getId(), details.getAmount(), details.getUserId(),
						details.getComment(), details.getSourceId(), details.getInventoryID(), details.getBulkWith(),
						details.getBulkCompl());
		this.inventoryDaoFactory.getTransactionDAO().saveOrUpdate(transaction);

		final StockTransaction stockTransaction = new StockTransaction(null, listDataProject, transaction);
		stockTransaction.setSourceRecordId(transaction.getSourceRecordId());
		this.inventoryDaoFactory.getStockTransactionDAO().saveOrUpdate(stockTransaction);
	}

	@Override
	public Lot getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(final String entityType, final Integer entityId,
			final Integer locationId, final Integer scaleId) {
		final List<Lot> lots =
				this.inventoryDaoFactory.getLotDao().getByEntityTypeEntityIdsLocationIdAndScaleId(entityType,
						Arrays.asList(new Integer[] {entityId}), locationId, scaleId);
		if (lots != null && !lots.isEmpty()) {
			return lots.get(0);
		}
		return null;
	}

	@Override
	public List<InventoryDetails> getInventoryListByListDataProjectListId(final Integer listDataProjectListId)
			throws MiddlewareQueryException {
		return this.inventoryDaoFactory.getStockTransactionDAO().retrieveInventoryDetailsForListDataProjectListId(listDataProjectListId);
	}

	@Override
	public List<InventoryDetails> getSummedInventoryListByListDataProjectListId(final Integer listDataProjectListId,
			final GermplasmListType type) throws MiddlewareQueryException {
		return this.inventoryDaoFactory.getStockTransactionDAO().retrieveSummedInventoryDetailsForListDataProjectListId(
				listDataProjectListId, type);
	}

	@Override
	public boolean stockHasCompletedBulking(final Integer listId) throws MiddlewareQueryException {
		return this.inventoryDaoFactory.getStockTransactionDAO().stockHasCompletedBulking(listId);
	}

	public void setLotBuilder(final LotBuilder lotBuilder) {
		this.lotBuilder = lotBuilder;
	}

	public void setTransactionBuilder(final TransactionBuilder transactionBuilder) {
		this.transactionBuilder = transactionBuilder;
	}

}
