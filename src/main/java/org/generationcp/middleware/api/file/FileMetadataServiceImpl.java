package org.generationcp.middleware.api.file;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.util.uid.FileUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;

@Transactional
@Service
public class FileMetadataServiceImpl implements FileMetadataService {

	private static final Logger LOG = LoggerFactory.getLogger(FileMetadataServiceImpl.class);

	private static final String FILE_PATH_PREFIX_PROGRAMUUID = "programuuid-";
	private static final String FILE_PATH_PREFIX_STUDYID = "studyid-";
	private static final String FILE_PATH_PREFIX_OBSUNITUUID = "obsunituuid-";
	/**
	 * AWS S3 uses forward slash to identify folders
	 */
	private static final String FILE_PATH_SLASH = "/";

	private final DaoFactory daoFactory;
	
	@Autowired
	private DatasetService datasetService;

	public FileMetadataServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Image save(final ImageNewRequest imageNewRequest) {
		final String observationUnitDbId = imageNewRequest.getObservationUnitDbId();
		final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitDbId);

		if (experimentModel == null) {
			throw new MiddlewareRequestException("", "filemetadata.observationunit.not.found", new String[] {observationUnitDbId});
		}

		final FileMetadata fileMetadata = new FileMetadata();
		fileMetadata.setExperimentModel(experimentModel);

		final List<String> terms = imageNewRequest.getDescriptiveOntologyTerms();
		if (!isEmpty(terms)) {
			for (final String term : terms) {
				final CVTerm cvTerm = this.daoFactory.getCvTermDao().getById(Integer.valueOf(term));
				if (cvTerm == null) {
					throw new MiddlewareRequestException("", "error.record.not.found", new String[] {"cvterm=" + term});
				}
				fileMetadata.getVariables().add(cvTerm);
			}
		}

		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(ContextHolder.getCurrentCrop());
		FileUIDGenerator.generate(cropType, singletonList(fileMetadata));

		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		fileMetadataMapper.map(imageNewRequest, fileMetadata);

		// assigned path, to be saved later using file storage
		final String path = this.getFilePath(observationUnitDbId, imageNewRequest.getImageFileName());
		fileMetadata.setPath(path);

		this.daoFactory.getFileMetadataDAO().save(fileMetadata);

		final Image response = fileMetadataMapper.map(fileMetadata);
		return response;
	}

	@Override
	public Image update(final String imageDbId, final ImageNewRequest imageNewRequest) {
		final FileMetadata fileMetadata = this.daoFactory.getFileMetadataDAO().getByFileUUID(imageDbId);
		if (fileMetadata == null) {
			throw new MiddlewareRequestException("", "filemetadata.record.not.found", new String[] {imageDbId});
		}
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		fileMetadataMapper.map(imageNewRequest, fileMetadata);

		this.daoFactory.getFileMetadataDAO().update(fileMetadata);

		return fileMetadataMapper.map(fileMetadata);
	}

	@Override
	public Image getImage(final String imageDbId) {
		final FileMetadata fileMetadata = this.daoFactory.getFileMetadataDAO().getByFileUUID(imageDbId);
		if (fileMetadata == null) {
			throw new MiddlewareRequestException("", "filemetadata.record.not.found", new String[] {imageDbId});
		}
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		return fileMetadataMapper.map(fileMetadata);
	}

	@Override
	public FileMetadataDTO getByFileUUID(final String fileUUID) {
		final FileMetadata fileMetadata = this.daoFactory.getFileMetadataDAO().getByFileUUID(fileUUID);
		if (fileMetadata == null) {
			throw new MiddlewareRequestException("", "filemetadata.record.not.found", new String[] {"fileUUID=" + fileUUID});
		}
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		final FileMetadataDTO fileMetadataDTO = new FileMetadataDTO();
		fileMetadataMapper.map(fileMetadata, fileMetadataDTO);
		return fileMetadataDTO;
	}

	/**
	 * Resolve predetermined path based on params (e.g, for observations, germplasm, etc)
	 */
	@Override
	public String getFilePath(final String observationUnitId, final String fileName) {
		final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitId);
		final DmsProject study = experimentModel.getProject().getStudy();
		return FILE_PATH_PREFIX_PROGRAMUUID + study.getProgramUUID()
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_STUDYID + study.getProjectId()
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_OBSUNITUUID + observationUnitId
			+ FILE_PATH_SLASH + fileName;
	}

	@Override
	public FileMetadataDTO save(final FileMetadataDTO fileMetadataDTO, final String observationUnitUUID, final Integer termId) {
		final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitUUID);

		if (experimentModel == null) {
			throw new MiddlewareRequestException("", "filemetadata.observationunit.not.found", new String[] {observationUnitUUID});
		}

		final FileMetadata fileMetadata = new FileMetadata();
		fileMetadata.setExperimentModel(experimentModel);

		if (termId != null) {
			final CVTerm cvTerm = this.daoFactory.getCvTermDao().getById(termId);
			if (cvTerm == null) {
				throw new MiddlewareRequestException("", "error.record.not.found", new String[] {"cvterm=" + termId});
			}
			fileMetadata.getVariables().add(cvTerm);
		}

		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(ContextHolder.getCurrentCrop());
		FileUIDGenerator.generate(cropType, singletonList(fileMetadata));

		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		fileMetadataMapper.map(fileMetadataDTO, fileMetadata);

		final FileMetadata entity = this.daoFactory.getFileMetadataDAO().save(fileMetadata);
		final FileMetadataDTO fileMetadataDTOSaved = new FileMetadataDTO();
		fileMetadataMapper.map(entity, fileMetadataDTOSaved);
		return fileMetadataDTOSaved;
	}

	@Override
	public List<FileMetadataDTO> list(
		final String observationUnitUUID,
		final String programUUID,
		final String variableName,
		final String fileName
	) {

		final List<FileMetadata> fileMetadataList = this.daoFactory.getFileMetadataDAO().list(
			programUUID, observationUnitUUID, variableName, fileName);

		// collect variables
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.setProgramUuid(programUUID);
		final List<CVTerm> cvTerms = fileMetadataList.stream().flatMap(fileMetadata -> fileMetadata.getVariables().stream())
			.collect(toList());
		cvTerms.forEach(cvTerm -> variableFilter.addVariableId(cvTerm.getCvTermId()));
		final Map<Integer, Variable> variablesById = this.daoFactory.getCvTermDao().getVariablesWithFilter(variableFilter)
			.stream().collect(toMap(Term::getId, identity()));

		// map result
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		return fileMetadataList.stream().map(fileMetadata -> {
			final FileMetadataDTO fileMetadataDTO = new FileMetadataDTO();
			fileMetadataMapper.map(fileMetadata, fileMetadataDTO);

			fileMetadataDTO.setVariables(fileMetadata.getVariables().stream().map(cvTerm -> variablesById.get(cvTerm.getCvTermId()))
				.collect(toList()));

			return fileMetadataDTO;
		}).collect(toList());
	}

	@Override
	public void delete(final String fileUUID) {
		final FileMetadata fileMetadata = this.daoFactory.getFileMetadataDAO().getByFileUUID(fileUUID);
		this.daoFactory.getFileMetadataDAO().makeTransient(fileMetadata);
	}

}
