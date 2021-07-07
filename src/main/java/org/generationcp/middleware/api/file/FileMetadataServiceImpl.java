package org.generationcp.middleware.api.file;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.uid.FileUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Transactional
@Service
public class FileMetadataServiceImpl implements FileMetadataService {

	private static final Logger LOG = LoggerFactory.getLogger(FileMetadataServiceImpl.class);

	private static final String FILE_PATH_PREFIX_PROGRAMUUID = "programuuid-";
	private static final String FILE_PATH_PREFIX_STUDYID = "studyid-";
	private static final String FILE_PATH_PREFIX_OBSUNITUUID = "obsunituuid-";
	private static final String FILE_PATH_PREFIX_TERMID = "termid-";
	/**
	 * AWS S3 uses forward slash to identify folders
	 */
	private static final String FILE_PATH_SLASH = "/";

	private final DaoFactory daoFactory;

	public FileMetadataServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Image save(final ImageNewRequest imageNewRequest) {
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		final FileMetadata fileMetadata = new FileMetadata();
		fileMetadataMapper.map(fileMetadata, imageNewRequest);

		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(ContextHolder.getCurrentCrop());
		FileUIDGenerator.generate(cropType, singletonList(fileMetadata));

		// TODO observationVariableDbId not available for images (https://github.com/plantbreeding/API/issues/477)
		//  assuming only one file variable per study, to get by obsUnitId
		final String observationUnitDbId = imageNewRequest.getObservationUnitDbId();
		final DatasetDTO dataset = this.daoFactory.getDmsProjectDAO().getDatasetByObsUnitDbId(observationUnitDbId);
		final List<MeasurementVariable> fileTypeVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(
			dataset.getDatasetId(),
			singletonList(VariableType.TRAIT.getId())
		).stream().filter(variable -> variable.getDataTypeId() == DataType.FILE_VARIABLE.getId()).collect(toList());

		if (fileTypeVariables == null || fileTypeVariables.isEmpty()) {
			throw new MiddlewareRequestException("", "filemetadata.variable.not.found", new String[] {observationUnitDbId});
		}

		if (fileTypeVariables.size() > 1) {
			throw new MiddlewareRequestException("", "filemetadata.brapi.multiple.file.variables");
		}

		final Integer termId = fileTypeVariables.get(0).getTermId();

		final String path = this.getFilePathForObservations(observationUnitDbId, termId, imageNewRequest.getImageFileName());
		fileMetadata.setPath(path);

		this.daoFactory.getFileDAO().save(fileMetadata);

		final Image response = fileMetadataMapper.map(fileMetadata);
		return response;
	}

	@Override
	public Image update(final String imageDbId, final ImageNewRequest imageNewRequest) {
		final FileMetadata fileMetadata = this.daoFactory.getFileDAO().getByFileUUID(imageDbId);
		if (fileMetadata == null) {
			throw new MiddlewareRequestException("", "filemetadata.record.not.found", new String[] {imageDbId});
		}
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		fileMetadataMapper.map(fileMetadata, imageNewRequest);

		this.daoFactory.getFileDAO().update(fileMetadata);

		return fileMetadataMapper.map(fileMetadata);
	}

	@Override
	public Image getImage(final String imageDbId) {
		final FileMetadata fileMetadata = this.daoFactory.getFileDAO().getByFileUUID(imageDbId);
		if (fileMetadata == null) {
			throw new MiddlewareRequestException("", "filemetadata.record.not.found", new String[] {imageDbId});
		}
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		return fileMetadataMapper.map(fileMetadata);
	}

	@Override
	public FileMetadataDTO getFileMetadataByUUID(final String fileUUID) {
		final FileMetadata fileMetadata = this.daoFactory.getFileDAO().getByFileUUID(fileUUID);
		if (fileMetadata == null) {
			throw new MiddlewareRequestException("", "filemetadata.record.not.found", new String[] {"fileUUID=" + fileUUID});
		}
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		return fileMetadataMapper.mapToDTO(fileMetadata);
	}

	@Override
	public String getFilePathForObservations(final String observationUnitDbId, final Integer termId, final String fileName) {
		final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitDbId);
		final DmsProject study = experimentModel.getProject().getStudy();
		return FILE_PATH_PREFIX_PROGRAMUUID + study.getProgramUUID()
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_STUDYID + study.getProjectId()
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_OBSUNITUUID + observationUnitDbId
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_TERMID + termId
			+ FILE_PATH_SLASH + fileName;
	}

}
