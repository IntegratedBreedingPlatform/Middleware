package org.generationcp.middleware.api.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class FileMetadataMapper {

	private static final Logger LOG = LoggerFactory.getLogger(FileMetadataMapper.class);

	public void map(final FileMetadata fileMetadata, final ImageNewRequest imageNewRequest) {
		fileMetadata.setCopyright(imageNewRequest.getCopyright());
		fileMetadata.setName(imageNewRequest.getImageName());
		fileMetadata.setDescription(imageNewRequest.getDescription());
		fileMetadata.setFileTimestamp(imageNewRequest.getImageTimeStamp());
		fileMetadata.setImageHeight(imageNewRequest.getImageHeight());
		fileMetadata.setImageWidth(imageNewRequest.getImageWidth());
		fileMetadata.setMimeType(imageNewRequest.getMimeType());
		fileMetadata.setImageSize(imageNewRequest.getImageFileSize());

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			fileMetadata.setImageLocation(objectMapper.writeValueAsString(imageNewRequest.getImageLocation()));
		} catch (final JsonProcessingException e) {
			final String message = "couldn't parse imageLocation";
			LOG.error(message, e);
			throw new MiddlewareException(message);
		}
	}

	public Image map(final FileMetadata fileMetadata) {
		final Image image = new Image();
		image.setCopyright(fileMetadata.getCopyright());
		image.setImageName(fileMetadata.getName());
		image.setDescription(fileMetadata.getDescription());
		image.setImageTimeStamp(fileMetadata.getFileTimestamp());
		image.setImageHeight(fileMetadata.getImageHeight());
		image.setImageWidth(fileMetadata.getImageWidth());
		image.setMimeType(fileMetadata.getMimeType());
		image.setImageFileSize(fileMetadata.getImageSize());

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			image.setImageLocation(objectMapper.readValue(fileMetadata.getImageLocation(), HashMap.class));
		} catch (final IOException e) {
			final String message = "couldn't parse imageLocation";
			LOG.error(message, e);
			throw new MiddlewareException(message);
		}

		image.setImageDbId(fileMetadata.getFileUUID());
		return image;
	}

	public FileMetadataDTO mapToDTO(final FileMetadata fileMetadata) {
		final FileMetadataDTO fileMetadataDTO = new FileMetadataDTO();
		fileMetadataDTO.setCopyright(fileMetadata.getCopyright());
		fileMetadataDTO.setName(fileMetadata.getName());
		fileMetadataDTO.setDescription(fileMetadata.getDescription());
		fileMetadataDTO.setFileTimestamp(fileMetadata.getFileTimestamp());
		fileMetadataDTO.setImageHeight(fileMetadata.getImageHeight());
		fileMetadataDTO.setImageWidth(fileMetadata.getImageWidth());
		fileMetadataDTO.setMimeType(fileMetadata.getMimeType());
		fileMetadataDTO.setPath(fileMetadata.getPath());
		fileMetadataDTO.setImageSize(fileMetadata.getImageSize());

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			fileMetadataDTO.setImageLocation(objectMapper.readValue(fileMetadata.getImageLocation(), HashMap.class));
		} catch (final IOException e) {
			final String message = "couldn't parse imageLocation";
			LOG.error(message, e);
			throw new MiddlewareException(message);
		}

		fileMetadataDTO.setFileUUID(fileMetadata.getFileUUID());
		fileMetadataDTO.setFileId(fileMetadata.getFileId());
		return fileMetadataDTO;
	}

	public Image map(final FileMetadataDTO dto) {
		final Image image = new Image();
		image.setCopyright(dto.getCopyright());
		image.setImageName(dto.getName());
		image.setDescription(dto.getDescription());
		image.setImageTimeStamp(dto.getFileTimestamp());
		image.setImageHeight(dto.getImageHeight());
		image.setImageWidth(dto.getImageWidth());
		image.setMimeType(dto.getMimeType());
		image.setImageFileSize(dto.getImageSize());
		image.setImageLocation(dto.getImageLocation());
		image.setImageDbId(dto.getFileUUID());
		return image;
	}
}
