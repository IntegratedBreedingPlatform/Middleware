package org.generationcp.middleware.service.impl.dataset;

import org.generationcp.middleware.domain.dms.DatasetTypeDTO;
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
	public DatasetTypeDTO getDatasetTypeById(final int datasetTypeId) {
		return this.convertToDatasetTypeDTO(this.daoFactory.getDatasetTypeDao().getById(datasetTypeId));
	}

	@Override
	public Map<Integer, DatasetTypeDTO> getAllDatasetTypes() {
		final Map<Integer, DatasetTypeDTO> datasetTypeMap = new HashMap<>();
		final List<DatasetType> datasetTypes = this.daoFactory.getDatasetTypeDao().getAll();
		for (final DatasetType datasetType : datasetTypes) {
			datasetTypeMap.put(datasetType.getDatasetTypeId(), this.convertToDatasetTypeDTO(datasetType));
		}
		return datasetTypeMap;
	}

	DatasetTypeDTO convertToDatasetTypeDTO(final DatasetType datasetType) {
		final DatasetTypeDTO datasetTypeDTO = new DatasetTypeDTO();
		datasetTypeDTO.setDatasetTypeId(datasetType.getDatasetTypeId());
		datasetTypeDTO.setCvTermId(datasetType.getCvTermId());
		datasetTypeDTO.setDescription(datasetType.getDescription());
		datasetTypeDTO.setName(datasetType.getName());
		datasetTypeDTO.setSubObservationType(datasetType.isSubObservationType());
		return datasetTypeDTO;
	}

}
