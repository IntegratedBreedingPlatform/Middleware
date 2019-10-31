package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
public class StudyInstanceServiceImpl implements StudyInstanceService {

	public StudyInstanceServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyInstanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.experimentModelSaver = new ExperimentModelSaver(sessionProvider);
	}

	private ExperimentModelSaver experimentModelSaver;
	private DaoFactory daoFactory;

	@Override
	public void createStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber) {

		// Get the existing environment dataset variables.
		// Since we are creating a new study instance, the values of these variables are just blank.
		final List<MeasurementVariable> measurementVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId,
			Arrays.asList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.STUDY_CONDITION.getId()));

		final Geolocation geolocation =
			this.createGeolocation(measurementVariables, instanceNumber);
		this.daoFactory.getGeolocationDao().save(geolocation);

		final ExperimentValues experimentValue = new ExperimentValues();
		experimentValue.setLocationId(geolocation.getLocationId());
		this.experimentModelSaver.addExperiment(crop, datasetId, ExperimentType.TRIAL_ENVIRONMENT, experimentValue);

	}

	@Override
	public void removeStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber) {
		// TODO: To be implemented in IBP-3160
	}

	protected Geolocation createGeolocation(final List<MeasurementVariable> measurementVariables, final String instanceNumber) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setProperties(new ArrayList<GeolocationProperty>());

		int rank = 1;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			final int variableId = measurementVariable.getTermId();
			if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variableId) {
				geolocation.setDescription(instanceNumber);
			} else if (VariableType.ENVIRONMENT_DETAIL == measurementVariable.getVariableType()) {
				String value = measurementVariable.getValue();

				// The default value of an instance's location name is "Unspecified Location"
				if (measurementVariable.getTermId() == TermId.LOCATION_ID.getId()) {
					value = this.getLocationIdOfUnspecifiedLocation();
				}
				final GeolocationProperty geolocationProperty =
					new GeolocationProperty(geolocation, value, rank, measurementVariable.getTermId());
				geolocation.getProperties().add(geolocationProperty);
				rank++;
			}

		}
		return geolocation;
	}

	protected String getLocationIdOfUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return String.valueOf(locations.get(0).getLocid());
		}
		return "";
	}

}
