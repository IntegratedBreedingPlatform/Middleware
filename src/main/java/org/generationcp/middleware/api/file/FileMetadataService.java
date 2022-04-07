package org.generationcp.middleware.api.file;

import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FileMetadataService {

	Image save(ImageNewRequest imageNewRequest);

	Image update(String imageDbId, ImageNewRequest imageNewRequest);

	Image getImage(String imageDbId);

	FileMetadataDTO getByFileUUID(String fileUUID);

	List<FileMetadataDTO> getAll(List<Integer> variableId, Integer datasetId, String germplasmUUID, Integer instanceId);

	String getFilePathForObservationUnit(String observationUnitDbId, String fileName);

	String getFilePathForEnvironment(Integer instanceId, String fileName);

	String getFilePathForGermplasm(String germplasmUUID, String fileName);

	FileMetadataDTO save(FileMetadataDTO fileMetadataDTO, String observationUnitUUID, String germplasmUUID, Integer instanceId,
		Integer termId);

	List<FileMetadataDTO> search(FileMetadataFilterRequest filterRequest, String programUUID, Pageable pageable);

	long countSearch(FileMetadataFilterRequest filterRequest, String programUUID);

	void delete(String fileUUID);

	void detachFiles(List<Integer> variableIds, Integer datasetId, String germplasmUUID, Integer instanceId);

	void removeFiles(List<Integer> variableIds, Integer datasetId, String germplasmUUID, Integer instanceId);

	void updateGid(Integer newGid, List<String> targetFileUUIDs);

	List<FileMetadata> getByGids(List<Integer> gids);

}
