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
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.workbench.CropType;

public class LotBuilder extends Builder {

	private static final int LOT_NOT_DERIVED_FROM_ANOTHER = 0;

	public LotBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Lot createLotForAdd(
		final Integer gid, final Integer locationId, final Integer scaleId, final String comment, final Integer userId,
		final CropType cropType, final String stockId)
			throws MiddlewareQueryException {
		final Lot lot = new Lot(null, userId, EntityType.GERMPLSM.name(), gid, locationId, scaleId, LotStatus.ACTIVE.getIntValue(),
			LotBuilder.LOT_NOT_DERIVED_FROM_ANOTHER, comment, stockId);

		this.getInventoryDataManager().generateLotIds(cropType, Lists.newArrayList(lot));
		return lot;
	}

}
