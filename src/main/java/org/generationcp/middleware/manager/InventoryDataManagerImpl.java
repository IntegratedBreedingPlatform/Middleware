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
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
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

	@Resource
	private UserService userService;

	private DaoFactory daoFactory;

	public InventoryDataManagerImpl() {
	}

	public InventoryDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
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

}
