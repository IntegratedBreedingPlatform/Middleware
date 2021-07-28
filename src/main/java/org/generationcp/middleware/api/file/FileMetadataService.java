package org.generationcp.middleware.api.file;

import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;

import java.util.List;

public interface FileMetadataService {

	Image save(ImageNewRequest imageNewRequest);

	Image update(String imageDbId, ImageNewRequest imageNewRequest);

	Image getImage(String imageDbId);

	FileMetadataDTO getByFileUUID(String fileUUID);

	String getFilePath(String observationUnitDbId, Integer termId, String fileName);

	/**
	 *
	 * @param fileMetadataDTO extract the filename from this object
	 * @param termId use this termid for the observation. If null, the first available variable in the study for
	 * {@link FileMetadataDTO#observationUnitUUID} will be used.
	 *                  FIXME use descriptiveOntologyTerm https://github.com/PhenoApps/Field-Book/issues/286
	 */
	void linkToObservation(FileMetadataDTO fileMetadataDTO, Integer termId);

	FileMetadataDTO save(FileMetadataDTO fileMetadataDTO, String observationUnitUUID);

	List<FileMetadataDTO> list(Integer observationId);

	void delete(String fileUUID);
}
