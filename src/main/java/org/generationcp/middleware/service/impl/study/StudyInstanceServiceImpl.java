package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
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
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.generationcp.middleware.service.impl.study.generation.GeolocationGenerator;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Transactional
public class StudyInstanceServiceImpl implements StudyInstanceService {

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
	}

	@Override
	public StudyInstance createStudyInstance(final CropType crop, final int datasetId, final int instanceNumber) {

		// Get the existing environment dataset variables.
		// Since we are creating a new study instance, the values of these variables are just blank.
		final List<MeasurementVariable> measurementVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId,
			Arrays.asList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.STUDY_CONDITION.getId()));

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
		return this.daoFactory.getDmsProjectDAO().getDatasetInstances(environmentDatasetId);
	}

	protected Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.absent();
	}

}
