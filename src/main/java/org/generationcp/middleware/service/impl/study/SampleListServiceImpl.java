
package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
public class SampleListServiceImpl implements SampleListService {

	private final SampleListDao sampleListDao;

	private final SampleDao sampleDao;

	private final UserDAO userDao;

	private final StudyMeasurements studyMeasurements;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private StudyDataManager studyService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	private final PlantDao plantDao;

	public SampleListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sampleListDao = new SampleListDao();
		this.sampleListDao.setSession(sessionProvider.getSession());
		this.sampleDao = new SampleDao();
		this.sampleDao.setSession(sessionProvider.getSession());
		this.userDao = new UserDAO();
		this.userDao.setSession(sessionProvider.getSession());
		this.plantDao = new PlantDao();
		this.plantDao.setSession(sessionProvider.getSession());
		this.studyMeasurements = new StudyMeasurements(sessionProvider.getSession());
	}

	@Override
	public Integer createOrUpdateSampleList(final SampleListDTO sampleListDTO) {

		Preconditions.checkArgument(sampleListDTO.getInstanceIds() != null, "The Instance List must not be null");
		Preconditions.checkArgument(!sampleListDTO.getInstanceIds().isEmpty(), "The Instance List must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getSelectionVariableId(), "The Selection Variable Id must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getStudyId(), "The Study Id must not be empty");

		try {
			final Study study = this.studyService.getStudy(sampleListDTO.getStudyId());
			Preconditions.checkNotNull(study, "The study must not be null");
			final SampleList sampleList = new SampleList();

			sampleList.setCreatedDate(new Date());
			sampleList.setCreatedBy(this.userDao.getUserByUserName(sampleListDTO.getCreatedBy()));
			sampleList.setDescription(sampleListDTO.getDescription());
			sampleList.setListName(study.getName() + "#" + Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS"));
			sampleList.setNotes(sampleListDTO.getNotes());
			final SampleList parent = this.sampleListDao.getRotSampleList();
			sampleList.setHierarchy(parent);

			final List<ObservationDto> observationDtos = this.studyMeasurements
				.getSampleObservations(sampleListDTO.getStudyId(), sampleListDTO.getInstanceIds(), sampleListDTO.getSelectionVariableId());

			Preconditions.checkArgument(!observationDtos.isEmpty(), "The observation list must not be empty");

			final String cropPrefix = this.workbenchDataManager.getCropTypeByName(sampleListDTO.getCropName()).getPlotCodePrefix();

			final Map<Integer, Integer> maxPlantNumbers = this.getMaxPlantNumber(observationDtos);
			final List<Sample> samples = new ArrayList<>();

			for (final ObservationDto observationDto : observationDtos) {

				final BigInteger sampleNumber = new BigInteger(observationDto.getVariableMeasurements().get(0).getVariableValue());
				Integer count = maxPlantNumbers.get(observationDto.getMeasurementId());
				if (count == null) {
					// counter should be start in 1
					count = 0;
				}
				for (double i = 0; i < sampleNumber.doubleValue(); i++) {
					count++;
					final Sample sample = this.sampleService
						.buildSample(sampleListDTO.getCropName(), cropPrefix, count, sampleListDTO.getTakenBy(),
							observationDto.getDesignation(), sampleListDTO.getSamplingDate(), observationDto.getMeasurementId(),
							sampleList);
					samples.add(sample);
				}
			}

			sampleList.setSamples(samples);
			this.sampleListDao.saveOrUpdate(sampleList);
			return sampleList.getListId();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in createOrUpdateSampleList in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	private Map<Integer, Integer> getMaxPlantNumber(final List<ObservationDto> observationDtos) {

		@SuppressWarnings("unchecked")
		final Collection<Integer> experimentIds = CollectionUtils.collect(observationDtos, new Transformer() {

			@Override
			public Object transform(final Object input) {
				final ObservationDto observationDto = (ObservationDto) input;
				return observationDto.getMeasurementId();
			}
		});

		return this.plantDao.getMaxPlantNumber(experimentIds);
	}

	/**
	 * Create a sample list folder
	 * Sample List folder name must be unique across the elements in the parent folder
	 *
	 * @param folderName
	 * @param parentId
	 * @return Folder ID
	 * @throws Exception
	 */
	@Override
	public Integer createSampleListFolder(final String folderName, final Integer parentId) throws Exception {
		return null;
	}

	/**
	 * Update sample list folder name
	 * New folder name should be unique across the elements in the parent folder
	 *
	 * @param folderId
	 * @param newFolderName
	 * @throws Exception
	 */
	@Override
	public void updateSampleListFolderName(final Integer folderId, final String newFolderName) throws Exception {

	}

	/**
	 * Move a folder to another folder
	 * FolderID must exist, newParentID must exist
	 * newParentID folder must not contain another sample list with the name that the one that needs to be moved
	 *
	 * @param folderId
	 * @param newParentId
	 * @throws Exception
	 */
	@Override
	public void moveSampleListFolder(final Integer folderId, final Integer newParentId) throws Exception {

	}

	/**
	 * Delete a folder
	 * Folder ID must exist and it can not contain any child
	 *
	 * @param folderId
	 * @throws Exception
	 */
	@Override
	public void deleteSampleListFolder(final Integer folderId) throws Exception {

	}
}
