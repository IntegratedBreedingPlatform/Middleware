package org.generationcp.middleware.api.file;

import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;

import java.util.List;

public interface FileMetadataService {

	Image save(ImageNewRequest imageNewRequest);

	Image update(String imageDbId, ImageNewRequest imageNewRequest);

	Image getImage(String imageDbId);

	FileMetadataDTO getByFileUUID(String fileUUID);

	String getFilePath(String observationUnitDbId, String fileName);

	FileMetadataDTO save(FileMetadataDTO fileMetadataDTO, String observationUnitUUID);

	List<FileMetadataDTO> list(String observationUnitUUID);

	void delete(String fileUUID);
}
