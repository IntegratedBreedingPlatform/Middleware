package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Optional;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.generationcp.middleware.service.impl.study.generation.GeolocationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Transactional
public class StudyInstanceServiceImpl implements StudyInstanceService {

	@Autowired
	private StudyService studyService;

	private ExperimentModelGenerator experimentModelGenerator;
	private GeolocationGenerator geolocationGenerator;
	private DaoFactory daoFactory;


	public StudyInstanceServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyInstanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.experimentModelGenerator = new ExperimentModelGenerator(sessionProvider);
		this.geolocationGenerator = new GeolocationGenerator(sessionProvider);
		this.studyService = new StudyServiceImpl(sessionProvider);
	}

	@Override
	public StudyInstance createStudyInstance(final CropType crop, final int studyId, final int datasetId) {

		// Get the existing environment dataset variables.
		// Since we are creating a new study instance, the values of these variables are just blank.
		final List<MeasurementVariable> measurementVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId,
			Arrays.asList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.STUDY_CONDITION.getId()));

		final int instanceNumber = this.getNextInstanceNumber(studyId);

		// The default value of an instance's location name is "Unspecified Location"
		final Optional<Location> location = this.getUnspecifiedLocation();
		final Geolocation geolocation =
			this.geolocationGenerator
				.createGeolocation(measurementVariables, instanceNumber, location.isPresent() ? location.get().getLocid() : null);

		final ExperimentModel experimentModel =
			this.experimentModelGenerator.generate(crop, datasetId, Optional.of(geolocation), ExperimentType.TRIAL_ENVIRONMENT);

		this.daoFactory.getExperimentDao().save(experimentModel);

		final StudyInstance studyInstance = new StudyInstance();
		studyInstance.setInstanceDbId(geolocation.getLocationId());
		studyInstance.setInstanceNumber(instanceNumber);
		studyInstance.setExperimentId(experimentModel.getNdExperimentId());
		if (location.isPresent()) {
			studyInstance.setLocationId(location.get().getLocid());
			studyInstance.setLocationName(location.get().getLname());
			studyInstance.setLocationAbbreviation(location.get().getLabbr());
		}

		return studyInstance;
	}

	@Override
	public List<StudyInstance> getStudyInstances(final int studyId) {
		final int environmentDatasetId =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0)
				.getProjectId();
		final List<StudyInstance> instances = this.daoFactory.getDmsProjectDAO().getDatasetInstances(environmentDatasetId);
		// If study has advance or cross list and instance has experiment design, mark instance as cannot be deleted
		final boolean hasAdvancedOrCrossesList = this.daoFactory.getGermplasmListDAO().hasAdvancedOrCrossesList(studyId);
		if (hasAdvancedOrCrossesList) {
			for (final StudyInstance instance : instances) {
				if (instance.isHasExperimentalDesign()) {
					instance.setCanBeDeleted(false);
				}
			}
		}
		return instances;
	}

	@Override
	public void deleteStudyInstance(final Integer studyId, final Integer instanceId) {
		final GeolocationDao geolocationDao = this.daoFactory.getGeolocationDao();
		final Geolocation geolocation = geolocationDao.getById(instanceId);
		final Integer instanceNumber = Integer.valueOf(geolocation.getDescription());
		this.daoFactory.getExperimentDao().updateStudyExperimentGeolocationIfNecessary(studyId, instanceId);

		// Delete plot and environment experiments
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		this.daoFactory.getExperimentDao()
			.deleteExperimentsForDatasets(Arrays.asList(this.studyService.getPlotDatasetId(studyId),
				environmentDatasetId), Collections.singletonList(instanceNumber));

		// Delete geolocation and geolocationprops
		geolocationDao.makeTransient(geolocation);
	}

	protected Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.absent();
	}

	protected int getNextInstanceNumber(final int studyId) {
		final List<StudyInstance> studyInstances = this.getStudyInstances(studyId);
		if (!studyInstances.isEmpty()) {
			final StudyInstance maxStudyInstance = Collections.max(studyInstances, new Comparator<StudyInstance>() {

				@Override
				public int compare(final StudyInstance o1, final StudyInstance o2) {
					return Integer.compare(o1.getInstanceNumber(), o2.getInstanceNumber());
				}
			});
			return maxStudyInstance.getInstanceNumber() + 1;
		}
		return 1;

	}

}
