package org.generationcp.middleware.service.impl.dataset;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.service.api.dataset.DatasetTypeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class DatasetTypeServiceImpl implements DatasetTypeService {

	private final DaoFactory daoFactory;

	public DatasetTypeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public DatasetType getDatasetTypeById(final int datasetTypeId) {
		return this.daoFactory.getDatasetTypeDao().getById(datasetTypeId);
	}

	@Override
	public Map<Integer, DatasetType> getAllDatasetTypes() {
		final Map<Integer, DatasetType> datasetTypeMap = new HashMap<>();
		final List<DatasetType> datasetTypes = this.daoFactory.getDatasetTypeDao().getAll();
		for (final DatasetType datasetType : datasetTypes) {
			datasetTypeMap.put(datasetType.getDatasetTypeId(), datasetType);
		}
		return datasetTypeMap;
	}

}
