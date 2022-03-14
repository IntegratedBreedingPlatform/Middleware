package org.generationcp.middleware.api.study;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.dataset.StockPropertyData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StudyEntryObservationServiceImpl implements StudyEntryObservationService {

	private final DaoFactory daoFactory;

	public StudyEntryObservationServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Integer createObservation(final Integer studyId, final StockPropertyData stockPropertyData) {
		final StockModel stockModel = this.daoFactory.getStockDao().getById(stockPropertyData.getStockId());
		final StockProperty stockProperty = new StockProperty(stockModel, stockPropertyData.getVariableId(), stockPropertyData.getValue(),
			stockPropertyData.getCategoricalValueId());
		this.daoFactory.getStockPropertyDao().save(stockProperty);

		return stockProperty.getStockPropId();
	}

}
