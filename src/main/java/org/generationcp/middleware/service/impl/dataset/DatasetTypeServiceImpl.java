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
	public Map<Integer, DatasetTypeDTO> getAllDatasetTypesMap() {
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
		datasetTypeDTO.setDescription(datasetType.getDescription());
		datasetTypeDTO.setName(datasetType.getName());
		datasetTypeDTO.setSubObservationType(datasetType.isSubObservationType());
		datasetTypeDTO.setObservationType(datasetType.isObservationType());
		return datasetTypeDTO;
	}

	@Override
	public List<Integer> getObservationDatasetTypeIds() {
		return this.daoFactory.getDatasetTypeDao().getObservationDatasetTypeIds();
	}

	@Override
	public List<Integer> getSubObservationDatasetTypeIds() {
		return this.daoFactory.getDatasetTypeDao().getSubObservationDatasetTypeIds();
	}

	@Override
	public List<String> getObservationLevels(final Integer pageSize, final Integer pageNumber) {
		return this.daoFactory.getDatasetTypeDao().getObservationLevels(pageSize, pageNumber);
	}

	@Override
	public Long countObservationLevels() {
		return this.daoFactory.getDatasetTypeDao().countObservationLevels();
	}
}
