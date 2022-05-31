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

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the InventoryDataManager interface. Most of the functions in this class only use the connection to the local instance,
 * this is because the lot and transaction tables only exist in a local instance.
 *
 * @author Kevin Manansala
 *
 */
@Transactional
public class InventoryDataManagerImpl extends DataManager implements InventoryDataManager {

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
	public List<GermplasmListData> getLotCountsForList(final Integer id, final int start, final int numOfRows) {
		final List<GermplasmListData> listEntries = this.getGermplasmListDataByListId(id);
		return this.getListInventoryBuilder().retrieveLotCountsForList(listEntries);
	}

}
