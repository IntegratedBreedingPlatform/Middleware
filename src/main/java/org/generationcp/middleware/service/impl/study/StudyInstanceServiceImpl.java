package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Transactional
public class StudyInstanceServiceImpl implements StudyInstanceService {

	public StudyInstanceServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyInstanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(sessionProvider);
		this.experimentModelSaver = new ExperimentModelSaver(sessionProvider);
	}

	private HibernateSessionProvider sessionProvider;
	private ExperimentModelSaver experimentModelSaver;
	private DaoFactory daoFactory;

	@Override
	public StudyInstance createStudyInstance(final CropType crop, final int datasetId, final int instanceNumber) {

		// Get the existing environment dataset variables.
		// Since we are creating a new study instance, the values of these variables are just blank.
		final List<MeasurementVariable> measurementVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId,
			Arrays.asList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.STUDY_CONDITION.getId()));

		// The default value of an instance's location name is "Unspecified Location"
		final Optional<Location> location = this.getUnspecifiedLocation();
		final Geolocation geolocation =
			this.createGeolocation(measurementVariables, instanceNumber, location.isPresent() ? location.get().getLocid() : null);
		this.daoFactory.getGeolocationDao().save(geolocation);

		final ExperimentValues experimentValue = new ExperimentValues();
		experimentValue.setLocationId(geolocation.getLocationId());
		final ExperimentModel experimentModel =
			this.experimentModelSaver.addExperiment(crop, datasetId, ExperimentType.TRIAL_ENVIRONMENT, experimentValue);

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

	protected Geolocation createGeolocation(final List<MeasurementVariable> measurementVariables, final int instanceNumber,
		final Integer locationId) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setProperties(new ArrayList<GeolocationProperty>());

		int rank = 1;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			final int variableId = measurementVariable.getTermId();
			if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variableId) {
				geolocation.setDescription(String.valueOf(instanceNumber));
			} else if (VariableType.ENVIRONMENT_DETAIL == measurementVariable.getVariableType()) {
				String value = "";
				if (measurementVariable.getTermId() == TermId.LOCATION_ID.getId()) {
					value = String.valueOf(locationId);
				}
				final GeolocationProperty geolocationProperty =
					new GeolocationProperty(geolocation, value, rank, measurementVariable.getTermId());
				geolocation.getProperties().add(geolocationProperty);
				rank++;
			}

		}
		return geolocation;
	}

	protected Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.empty();
	}

}
