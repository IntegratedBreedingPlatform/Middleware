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

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryDataManagerImplTestIT extends IntegrationTestBase {

	@Autowired
	private InventoryDataManager manager;

	private DaoFactory daoFactory;

	GermplasmListTestDataInitializer germplasmListTestDataInitializer;

	private Lot lot;
	private CropType cropType;

	@Before
	public void setUpBefore() {
		if (this.daoFactory == null) {
			this.daoFactory = new DaoFactory(this.sessionProvder);
		}

		this.lot = new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9001, 1538, 0, 0, "sample added lot 1", RandomStringUtils.randomAlphabetic(35));
		this.daoFactory.getLotDao().save(lot);
		this.daoFactory.getLotDao().save(new Lot(null, 1, EntityType.GERMPLSM.name(), 50533, 9002, 1539, 0, 0, "sample added lot 2", RandomStringUtils.randomAlphabetic(35)));

		this.daoFactory.getTransactionDAO().save(new Transaction(null, 1, lot, new Date((20140413)), 1, -1d, "sample added transaction 1", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		this.daoFactory.getTransactionDAO().save(new Transaction(null, 1, lot, new Date((20140518)), 1, -2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		this.daoFactory.getTransactionDAO().save(new Transaction(null, 1, lot, new Date((20140518)), 0, -2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		this.daoFactory.getTransactionDAO().save(new Transaction(null, 1, lot, new Date((20140518)), 0, 2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		final Transaction t4 = new Transaction(null, 1, this.lot, new Date((20140413)), 1, -1d, "sample added transaction 1", 0, null, null, null, 1, TransactionType.DEPOSIT.getId());
		this.daoFactory.getTransactionDAO().save(t4);
		this.daoFactory.getTransactionDAO().save(new Transaction(null, 1, lot, new Date((20120518)), 1, 2d, "sample added transaction 2", 0, null, null, null, 1, TransactionType.DEPOSIT.getId()));
		final Set<Transaction> transactionSet = new HashSet<>();
		transactionSet.add(t4);
		this.lot.setTransactions(transactionSet);
		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);
	}


	@Test
	public void testGetTransactionById() {
		final Integer id = 1;
		final Transaction transactionid = this.daoFactory.getTransactionDAO().getById(id);
		Assert.assertNotNull(transactionid);
		Debug.println(IntegrationTestBase.INDENT, "testGetTransactionById(" + id + "): ");
		Debug.println(transactionid.toString());
	}

	@Test
	public void testGetLotCountsForGermplasmList() throws MiddlewareQueryException {
		final int listid = 1;
		final List<GermplasmListData> listEntries = this.manager.getLotCountsForList(listid, 0, Integer.MAX_VALUE);
		for (final GermplasmListData entry : listEntries) {
			final ListDataInventory inventory = entry.getInventoryInfo();
			if (inventory != null) {
				System.out.println(inventory);
			}
		}
	}

}
