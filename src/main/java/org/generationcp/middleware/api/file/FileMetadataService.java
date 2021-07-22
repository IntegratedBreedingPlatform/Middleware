package org.generationcp.middleware.api.file;

import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;

import java.util.List;

public interface FileMetadataService {

	Image save(ImageNewRequest imageNewRequest);

	Image update(String imageDbId, ImageNewRequest imageNewRequest);

	Image getImage(String imageDbId);

	FileMetadataDTO getFileMetadataByUUID(String fileUUID);

	String getFilePath(String observationUnitDbId, Integer termId, String fileName);

	void saveFilenameToObservation(FileMetadataDTO fileMetadataDTO);

	String save(FileMetadataDTO fileMetadataDTO, String observationUnitId);

	List<FileMetadataDTO> list(Integer observationId);
}
