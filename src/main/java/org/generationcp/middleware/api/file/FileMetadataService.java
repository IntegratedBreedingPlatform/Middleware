package org.generationcp.middleware.api.file;

import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;

public interface FileMetadataService {

	Image save(ImageNewRequest imageNewRequest);

	Image update(String imageDbId, ImageNewRequest imageNewRequest);

	Image getImage(String imageDbId);

	FileMetadataDTO getFileMetadataByUUID(String fileUUID);

	String getFilePathForObservations(String observationUnitDbId, Integer termId, String fileName);
}
