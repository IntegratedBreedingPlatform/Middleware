package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.pojos.Stock;

public class StockBuilder extends Builder {

	public StockBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Stock get(int stockId) throws MiddlewareQueryException {
		Stock stock = null;
		if (setWorkingDatabase(stockId)) {
			stock = getStockDao().getById(stockId);
		}
		return stock;
	}
}
