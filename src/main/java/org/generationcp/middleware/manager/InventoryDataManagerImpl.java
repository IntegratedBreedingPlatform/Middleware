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

package org.generationcp.middleware.manager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.uid.UIDGenerator;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the InventoryDataManager interface. Most of the functions in this class only use the connection to the local instance,
 * this is because the lot and transaction tables only exist in a local instance.
 *
 * @author Kevin Manansala
 *
 */
@Transactional
public class InventoryDataManagerImpl extends DataManager implements InventoryDataManager {

	private static final UIDGenerator.UID_ROOT UID_ROOT = UIDGenerator.UID_ROOT.LOT;
	public static final int SUFFIX_LENGTH = 8;

	@Resource
	private UserService userService;

	private DaoFactory daoFactory;

	public InventoryDataManagerImpl() {
	}

	public InventoryDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Lot> getLotsByEntityType(final String type, final int start, final int numOfRows) {
		return this.daoFactory.getLotDao().getByEntityType(type, start, numOfRows);
	}

	@Override
	public Integer addLot(final Lot lot) {
		final List<Lot> lots = new ArrayList<>();
		lots.add(lot);
		final List<Integer> ids = this.addOrUpdateLot(lots, Operation.ADD);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addLots(final List<Lot> lots) {
		return this.addOrUpdateLot(lots, Operation.ADD);
	}

	private List<Integer> addOrUpdateLot(final List<Lot> lots, final Operation operation) {
		final List<Integer> idLotsSaved = new ArrayList<>();
		try {
			final LotDAO dao = this.daoFactory.getLotDao();
			for (final Lot lot : lots) {
				final Lot recordSaved = dao.saveOrUpdate(lot);
				idLotsSaved.add(recordSaved.getId());
			}
		} catch (final ConstraintViolationException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		} catch (final MiddlewareQueryException e) {

			throw e;
		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving Lot: InventoryDataManager.addOrUpdateLot(lots=" + lots
					+ ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idLotsSaved;
	}

	@Override
	public Integer addTransaction(final org.generationcp.middleware.pojos.ims.Transaction transaction) {
		final List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
				new ArrayList<>();
		transactions.add(transaction);
		final List<Integer> ids = this.addTransactions(transactions);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Deprecated
	@Override
	public List<Integer> addTransactions(final List<org.generationcp.middleware.pojos.ims.Transaction> transactions) {
		return this.addOrUpdateTransaction(transactions, Operation.ADD);
	}

	@Override
	public Integer updateTransaction(final org.generationcp.middleware.pojos.ims.Transaction transaction) {
		final List<org.generationcp.middleware.pojos.ims.Transaction> transactions =
				new ArrayList<>();
		transactions.add(transaction);
		final List<Integer> ids = this.addOrUpdateTransaction(transactions, Operation.UPDATE);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	private List<Integer> addOrUpdateTransaction(final List<org.generationcp.middleware.pojos.ims.Transaction> transactions, final Operation operation) {
		final List<Integer> idTransactionsSaved = new ArrayList<>();
		try {
			

			final TransactionDAO dao = this.daoFactory.getTransactionDAO();

			for (final org.generationcp.middleware.pojos.ims.Transaction transaction : transactions) {
				final org.generationcp.middleware.pojos.ims.Transaction recordSaved = dao.saveOrUpdate(transaction);
				idTransactionsSaved.add(recordSaved.getId());
			}
			

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Transaction: InventoryDataManager.addOrUpdateTransaction(transactions=" + transactions
							+ ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idTransactionsSaved;
	}

	@Override
	public org.generationcp.middleware.pojos.ims.Transaction getTransactionById(final Integer id) {
		return this.daoFactory.getTransactionDAO().getById(id, false);
	}

	@Override
	public List<org.generationcp.middleware.pojos.ims.Transaction> getAllTransactions(final int start, final int numOfRows) {
		return this.daoFactory.getTransactionDAO().getAll(start, numOfRows);
	}

	private List<GermplasmListData> getGermplasmListDataByListId(final Integer id) {
		return this.daoFactory.getGermplasmListDataDAO().getByListId(id);
	}

	@Override
	public List<ListEntryLotDetails> getLotDetailsForListEntry(final Integer listId, final Integer recordId, final Integer gid) {
		return this.getListInventoryBuilder().retrieveInventoryLotsForListEntry(listId, recordId, gid);
	}

	@Override
	public List<GermplasmListData> getLotCountsForList(final Integer id, final int start, final int numOfRows) {
		final List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(id);
		return this.getListInventoryBuilder().retrieveLotCountsForList(listEntries);
	}

	@Override
	public List<LotDetails> getLotDetailsForGermplasm(final Integer gid) {
		return this.getListInventoryBuilder().retrieveInventoryLotsForGermplasm(gid);
	}

	@Override
	public Lot getLotById(final Integer id) {
		return this.daoFactory.getLotDao().getById(id, false);
	}

	@Override
	public List<TransactionReportRow> getTransactionDetailsForLot(final Integer lotId) {
		final List<TransactionReportRow> transactionDetailsForLot = this.daoFactory.getTransactionDAO().getTransactionDetailsForLot(lotId);
		final List<Integer> userIds = Lists.transform(transactionDetailsForLot, new Function<TransactionReportRow, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final TransactionReportRow input) {
				return input.getUserId();
			}
		});
		if (!userIds.isEmpty()) {
			final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
			for (final TransactionReportRow row : transactionDetailsForLot) {
				if (row.getUserId() != null) {
					row.setUser(userIDFullNameMap.get(row.getUserId()));
				}
			}
		}
		return transactionDetailsForLot;
	}

	@Override
	public void generateLotIds(final CropType crop, final List<Lot> lots) {
		UIDGenerator.<Lot>generate(crop, lots, UID_ROOT, SUFFIX_LENGTH,
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
}
