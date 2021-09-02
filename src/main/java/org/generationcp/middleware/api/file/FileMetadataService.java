package org.generationcp.middleware.api.file;

import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FileMetadataService {

	Image save(ImageNewRequest imageNewRequest);

	Image update(String imageDbId, ImageNewRequest imageNewRequest);

	Image getImage(String imageDbId);

	FileMetadataDTO getByFileUUID(String fileUUID);

	String getFilePath(String observationUnitDbId, String fileName);

	String getFilePathForGermplasm(String germplasmUUID, String fileName);

	FileMetadataDTO save(FileMetadataDTO fileMetadataDTO, String observationUnitUUID, Integer termId);

	List<FileMetadataDTO> search(FileMetadataFilterRequest filterRequest, String programUUID,
		final Pageable pageable);

	long countSearch(FileMetadataFilterRequest filterRequest, String programUUID, Pageable pageable);

	void delete(String fileUUID);
}
