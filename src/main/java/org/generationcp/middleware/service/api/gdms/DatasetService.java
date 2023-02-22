package org.generationcp.middleware.service.api.gdms;

public interface DatasetService {

	Integer saveDataset(final DatasetUploadDto datasetUploadDto);

	DatasetRetrieveDto getDataset(final String datasetName);

}
