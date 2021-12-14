package org.generationcp.middleware.api.file;

import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface FileMetadataService {

	Image save(ImageNewRequest imageNewRequest);

	Image update(String imageDbId, ImageNewRequest imageNewRequest);

	Image getImage(String imageDbId);

	FileMetadataDTO getByFileUUID(String fileUUID);

	List<FileMetadataDTO> getAll(List<Integer> variableId, Integer datasetId, String germplasmUUID);

	String getFilePathForObservationUnit(String observationUnitDbId, String fileName);

	String getFilePathForGermplasm(String germplasmUUID, String fileName);

	FileMetadataDTO save(FileMetadataDTO fileMetadataDTO, String observationUnitUUID, String germplasmUUID, Integer termId);

	List<FileMetadataDTO> search(FileMetadataFilterRequest filterRequest, String programUUID, Pageable pageable);

	long countSearch(FileMetadataFilterRequest filterRequest, String programUUID);

	void delete(String fileUUID);

	void detachFiles(List<Integer> variableIds, Integer datasetId, String germplasmUUID);

	void removeFiles(List<Integer> variableIds, Integer datasetId, String germplasmUUID);

	void updateGid(final Integer gid, final Set<Integer> targetGids);

}
