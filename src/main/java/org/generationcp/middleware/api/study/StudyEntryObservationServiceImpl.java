package org.generationcp.middleware.api.study;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.dataset.StockPropertyData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class StudyEntryObservationServiceImpl implements StudyEntryObservationService {

	private final DaoFactory daoFactory;

	public StudyEntryObservationServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Integer createObservation(final StockPropertyData stockPropertyData) {
		final StockModel stockModel = this.daoFactory.getStockDao().getById(stockPropertyData.getStockId());
		final StockProperty stockProperty = new StockProperty(stockModel, stockPropertyData.getVariableId(), stockPropertyData.getValue(),
			stockPropertyData.getCategoricalValueId());
		this.daoFactory.getStockPropertyDao().save(stockProperty);

		return stockProperty.getStockPropId();
	}

	@Override
	public Integer updateObservation(final StockPropertyData stockPropertyData) {
		final StockProperty stockProperty =
			this.daoFactory.getStockPropertyDao().getByStockIdAndTypeId(stockPropertyData.getStockId(), stockPropertyData.getVariableId())
				.orElseThrow(() -> new MiddlewareException(
					"Stock property: " + stockPropertyData.getStockId() + " with type: " + stockPropertyData.getVariableId()
						+ " not found."));
		stockProperty.setValue(stockPropertyData.getValue());
		stockProperty.setCategoricalValueId(stockPropertyData.getCategoricalValueId());
		this.daoFactory.getStockPropertyDao().update(stockProperty);

		return stockProperty.getStockPropId();
	}

	@Override
	public void deleteObservation(final Integer stockPropertyId) {
		final StockProperty stockProperty = this.daoFactory.getStockPropertyDao().getById(stockPropertyId);
		this.daoFactory.getStockPropertyDao().makeTransient(stockProperty);
	}

	@Override
	public long countObservationsByStudyAndVariables(final Integer studyId, final List<Integer> variableIds) {
		return this.daoFactory.getStockPropertyDao().countObservationsByStudyIdAndVariableIds(studyId, variableIds);
	}
}
