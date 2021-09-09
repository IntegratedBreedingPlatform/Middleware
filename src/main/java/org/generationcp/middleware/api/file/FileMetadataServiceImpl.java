package org.generationcp.middleware.api.file;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.binary.Hex;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.brapi.v1.image.Image;
import org.generationcp.middleware.api.brapi.v1.image.ImageNewRequest;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Transactional
@Service
public class FileMetadataServiceImpl implements FileMetadataService {

	private static final Logger LOG = LoggerFactory.getLogger(FileMetadataServiceImpl.class);

	public static final String FILE_PATH_PREFIX_PROGRAMUUID = "programuuid-";
	public static final String FILE_PATH_PREFIX_STUDYID = "studyid-";
	public static final String FILE_PATH_PREFIX_OBSUNITUUID = "obsunituuid-";
	/**
	 * AWS S3 uses forward slash to identify folders
	 */
	public static final String FILE_PATH_SLASH = "/";
	public static final String FILE_PATH_GERMPLASM_ROOT = "germplasm";
	public static final String FILE_PATH_PREFIX_GERMPLASMUUID = "germplasmuuid-";

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
		final String path = this.getFilePathForObservationUnit(observationUnitDbId, imageNewRequest.getImageFileName());
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

	@Override
	public List<FileMetadataDTO> getAll(final List<Integer> variableIds, final Integer datasetId, final String germplasmUUID) {
		final List<FileMetadata> fileMetadataList = this.daoFactory.getFileMetadataDAO()
			.getAll(variableIds, datasetId, germplasmUUID);
		final FileMetadataMapper fileMetadataMapper = new FileMetadataMapper();
		final List<FileMetadataDTO> fileMetadataDTOList = new ArrayList<>();
		for (FileMetadata fileMetadata : fileMetadataList) {
			final FileMetadataDTO fileMetadataDTO = new FileMetadataDTO();
			fileMetadataMapper.map(fileMetadata, fileMetadataDTO);
			fileMetadataDTOList.add(fileMetadataDTO);
		}
		return fileMetadataDTOList;
	}

	/**
	 * Resolve predetermined path based on params (e.g, for observations, germplasm, etc)
	 */
	@Override
	public String getFilePathForObservationUnit(final String observationUnitId, final String fileName) {
		final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitId);
		final DmsProject study = experimentModel.getProject().getStudy();
		final String path = FILE_PATH_PREFIX_PROGRAMUUID + study.getProgramUUID()
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_STUDYID + study.getProjectId()
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_OBSUNITUUID + observationUnitId
			+ FILE_PATH_SLASH + fileName;
		this.validatePathNotExists(path);
		return path;
	}

	@Override
	public String getFilePathForGermplasm(final String germplasmUUID, final String fileName) {
		final MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}
		/*
		 * Because there is no natural structure for germplasm like in obsunits (program/study/)
		 * we generate a virtual deterministic folder structure base on the uuid.
		 * Since the uuid can be the custom uid format which has the same prefix the the entire crop,
		 * we run it first through a hash function to get a hex string where each character has equal probability.
		 * We then use the first hash characters as folder names
		 * e.g final path e.g /germplasm/9/5/9/e/1/X2GIGdNDukMIGaa/myfile.png
		 * In theory this should result in not too many files per directory and not too many directories either.
		 */
		final String hexString = Hex.encodeHexString(md5.digest(germplasmUUID.getBytes(StandardCharsets.UTF_8)));
		final String path = FILE_PATH_GERMPLASM_ROOT
			+ FILE_PATH_SLASH + hexString.charAt(0)
			+ FILE_PATH_SLASH + hexString.charAt(1)
			+ FILE_PATH_SLASH + hexString.charAt(2)
			+ FILE_PATH_SLASH + hexString.charAt(3)
			+ FILE_PATH_SLASH + hexString.charAt(4)
			+ FILE_PATH_SLASH + FILE_PATH_PREFIX_GERMPLASMUUID + germplasmUUID
			+ FILE_PATH_SLASH + fileName;
		this.validatePathNotExists(path);
		return path;
	}

	private void validatePathNotExists(final String path) {
		final FileMetadata fileMetadata = this.daoFactory.getFileMetadataDAO().getByPath(path);
		if (fileMetadata != null) {
			throw new MiddlewareRequestException("", "filemetadata.path.overwrite");
		}
	}

	@Override
	public FileMetadataDTO save(
		final FileMetadataDTO fileMetadataDTO,
		final String observationUnitUUID,
		final String germplasmUUID,
		final Integer termId
	) {

		final FileMetadata fileMetadata = new FileMetadata();
		Preconditions.checkArgument(isBlank(observationUnitUUID) != isBlank(germplasmUUID));

		if (!isBlank(observationUnitUUID)) {
			final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getByObsUnitId(observationUnitUUID);
			if (experimentModel == null) {
				throw new MiddlewareRequestException("", "filemetadata.observationunit.not.found", new String[] {observationUnitUUID});
			}
			fileMetadata.setExperimentModel(experimentModel);
		} else {
			final Optional<Germplasm> germplasmOptional = this.daoFactory.getGermplasmDao().getGermplasmByGUIDs(singletonList(germplasmUUID))
				.stream().findFirst();
			if (!germplasmOptional.isPresent()) {
				throw new MiddlewareRequestException("", "filemetadata.germplasm.not.found", new String[] {germplasmUUID});
			}
			fileMetadata.setGermplasm(germplasmOptional.get());
		}

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
	public List<FileMetadataDTO> search(final FileMetadataFilterRequest filterRequest, final String programUUID, final Pageable pageable) {

		final List<FileMetadata> fileMetadataList = this.daoFactory.getFileMetadataDAO().search(filterRequest, programUUID, pageable);

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
	public long countSearch(final FileMetadataFilterRequest filterRequest, final String programUUID) {
		return this.daoFactory.getFileMetadataDAO().countSearch(filterRequest, programUUID);
	}

	@Override
	public void delete(final String fileUUID) {
		final FileMetadata fileMetadata = this.daoFactory.getFileMetadataDAO().getByFileUUID(fileUUID);
		this.daoFactory.getFileMetadataDAO().makeTransient(fileMetadata);
	}

	@Override
	public void detachFiles(final List<Integer> variableIds, final Integer datasetId, final String germplasmUUID) {
		Preconditions.checkArgument((datasetId == null) != isBlank(germplasmUUID));

		this.daoFactory.getFileMetadataDAO().detachFiles(variableIds, datasetId, germplasmUUID);
	}

	@Override
	public void removeFiles(final List<Integer> variableIds, final Integer datasetId, final String germplasmUUID) {
		Preconditions.checkArgument((datasetId == null) != isBlank(germplasmUUID));

		this.daoFactory.getFileMetadataDAO().removeFiles(variableIds, datasetId, germplasmUUID);
	}

}
