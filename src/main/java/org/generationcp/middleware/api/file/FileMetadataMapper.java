package org.generationcp.middleware.api.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.pojos.FileMetadataExternalReference;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileMetadataMapper {

	public void map(final ImageNewRequest from, final FileMetadata to) {
		to.setCopyright(from.getCopyright());
		to.setName(from.getImageFileName());
		to.setDescription(from.getDescription());
		to.setFileTimestamp(from.getImageTimeStamp());
		to.setImageHeight(from.getImageHeight());
		to.setImageWidth(from.getImageWidth());
		to.setMimeType(from.getMimeType());
		to.setSize(from.getImageFileSize());

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			final Map<String, Object> imageLocation = from.getImageLocation();
			if (imageLocation != null) {
				to.setImageLocation(objectMapper.writeValueAsString(imageLocation));
			}
		} catch (final JsonProcessingException e) {
			throw new MiddlewareRequestException("", "filemetadata.brapi.location.parse.error");
		}

		if (!CollectionUtils.isEmpty(from.getExternalReferences())) {
			final List<FileMetadataExternalReference> externalReferences = new ArrayList<>();
			for (final ExternalReferenceDTO externalReferenceDTO: from.getExternalReferences()) {
				final FileMetadataExternalReference fileMetadataExternalReference = new FileMetadataExternalReference(to,
					externalReferenceDTO.getReferenceID(), externalReferenceDTO.getReferenceSource());
				externalReferences.add(fileMetadataExternalReference);
			}
			to.setExternalReferences(externalReferences);
		}
	}

	public Image map(final FileMetadata fileMetadata) {
		final Image image = new Image();
		image.setCopyright(fileMetadata.getCopyright());
		image.setImageFileName(fileMetadata.getName());
		image.setDescription(fileMetadata.getDescription());
		image.setImageTimeStamp(fileMetadata.getFileTimestamp());
		image.setImageHeight(fileMetadata.getImageHeight());
		image.setImageWidth(fileMetadata.getImageWidth());
		image.setMimeType(fileMetadata.getMimeType());
		image.setImageFileSize(fileMetadata.getSize());
		final ExperimentModel experimentModel = fileMetadata.getExperimentModel();
		Preconditions.checkNotNull(experimentModel, "Image is not linked to any observationUnit");
		image.setObservationUnitDbId(experimentModel.getObsUnitId());

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			final String imageLocation = fileMetadata.getImageLocation();
			if (imageLocation != null) {
				image.setImageLocation(objectMapper.readValue(imageLocation, HashMap.class));
			}
		} catch (final IOException e) {
			throw new MiddlewareRequestException("", "filemetadata.brapi.location.parse.error");
		}

		image.setImageDbId(fileMetadata.getFileUUID());

		if(!CollectionUtils.isEmpty(fileMetadata.getExternalReferences())) {
			final List<ExternalReferenceDTO> externalReferenceDTOS = new ArrayList<>();
			for (final FileMetadataExternalReference externalReference: fileMetadata.getExternalReferences()) {
				final ExternalReferenceDTO externalReferenceDTO = new ExternalReferenceDTO(fileMetadata.getFileId().toString(),
					externalReference.getReferenceId(), externalReference.getSource());
				externalReferenceDTOS.add(externalReferenceDTO);
			}
			image.setExternalReferences(externalReferenceDTOS);
		}
		return image;
	}

	public void map(final FileMetadata from, final FileMetadataDTO to) {
		to.setCopyright(from.getCopyright());
		to.setName(from.getName());
		to.setDescription(from.getDescription());
		to.setFileTimestamp(from.getFileTimestamp());
		to.setImageHeight(from.getImageHeight());
		to.setImageWidth(from.getImageWidth());
		to.setMimeType(from.getMimeType());
		to.setPath(from.getPath());
		to.setSize(from.getSize());
		final ExperimentModel experimentModel = from.getExperimentModel();
		if (experimentModel != null) {
			to.setObservationUnitUUID(experimentModel.getObsUnitId());
			to.setNdExperimentId(experimentModel.getNdExperimentId());
		}
		if (from.getGeolocation() != null) {
			to.setInstanceId(from.getGeolocation().getLocationId());
		}
		if (from.getLot() != null) {
			to.setLotId(from.getLot().getId());
		}

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			final String imageLocation = from.getImageLocation();
			if (imageLocation != null) {
				to.setImageLocation(objectMapper.readValue(imageLocation, HashMap.class));
			}
		} catch (final IOException e) {
			throw new MiddlewareRequestException("", "filemetadata.brapi.location.parse.error");
		}

		to.setFileUUID(from.getFileUUID());
		to.setFileId(from.getFileId());
	}

	public Image map(final FileMetadataDTO dto) {
		final Image image = new Image();
		image.setCopyright(dto.getCopyright());
		image.setImageFileName(dto.getName());
		image.setDescription(dto.getDescription());
		image.setImageTimeStamp(dto.getFileTimestamp());
		image.setImageHeight(dto.getImageHeight());
		image.setImageWidth(dto.getImageWidth());
		image.setMimeType(dto.getMimeType());
		image.setImageFileSize(dto.getSize());
		image.setImageLocation(dto.getImageLocation());
		image.setImageDbId(dto.getFileUUID());
		image.setObservationUnitDbId(dto.getObservationUnitUUID());
		return image;
	}

	public void map(final FileMetadataDTO from, final FileMetadata to) {
		to.setCopyright(from.getCopyright());
		to.setName(from.getName());
		to.setDescription(from.getDescription());
		to.setFileTimestamp(from.getFileTimestamp());
		to.setImageHeight(from.getImageHeight());
		to.setImageWidth(from.getImageWidth());
		to.setMimeType(from.getMimeType());
		to.setPath(from.getPath());
		to.setSize(from.getSize());

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			final Map<String, Object> imageLocation = from.getImageLocation();
			if (imageLocation != null) {
				to.setImageLocation(objectMapper.writeValueAsString(imageLocation));
			}
		} catch (final JsonProcessingException e) {
			throw new MiddlewareRequestException("", "filemetadata.brapi.location.parse.error");
		}
	}
}
