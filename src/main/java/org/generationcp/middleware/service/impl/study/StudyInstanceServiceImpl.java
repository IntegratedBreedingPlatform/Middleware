package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl;
import org.generationcp.middleware.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class StudyInstanceServiceImpl implements StudyInstanceService {

	private static final List<Integer> GEOLOCATION_STORAGE_TERMIDS = Arrays
		.asList(TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.LATITUDE.getId(), TermId.LONGITUDE.getId(), TermId.GEODETIC_DATUM.getId(),
			TermId.ALTITUDE.getId());

	public StudyInstanceServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyInstanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.experimentModelSaver = new ExperimentModelSaver(sessionProvider);
		this.datasetService = new DatasetServiceImpl(sessionProvider);
	}

	private DaoFactory daoFactory;
	private ExperimentModelSaver experimentModelSaver;
	private DatasetService datasetService;

	@Override
	public void addStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber) {

		final List<MeasurementVariable> measurementVariables = this.datasetService.getObservationSetVariables(datasetId,
			Arrays.asList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.STUDY_CONDITION.getId()));

		final Geolocation geolocation = this.createGeolocation(measurementVariables);
		this.daoFactory.getGeolocationDao().save(geolocation);

		final ExperimentValues experimentValue = new ExperimentValues();
		experimentValue.setLocationId(geolocation.getLocationId());
		this.experimentModelSaver.addExperiment(crop, datasetId, ExperimentType.TRIAL_ENVIRONMENT, experimentValue);

	}

	@Override
	public void removeStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber) {

	}

	protected Geolocation createGeolocation(final List<MeasurementVariable> measurementVariables) {
		final Geolocation geolocation = new Geolocation();

		int rank = 1;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			final int variableId = measurementVariable.getTermId();
			final String value = measurementVariable.getValue();

			if (GEOLOCATION_STORAGE_TERMIDS.contains(variableId)) {
				this.setGeolocationValue(geolocation, variableId, value);
			} else if (VariableType.ENVIRONMENT_DETAIL == measurementVariable.getVariableType()
				|| VariableType.STUDY_CONDITION == measurementVariable.getVariableType()) {
				final GeolocationProperty geolocationProperty =
					new GeolocationProperty(geolocation, measurementVariable.getValue(), rank, 1);
				geolocation.setProperties(Arrays.asList(geolocationProperty));
			} else {
				throw new MiddlewareQueryException(
					"Non-Trial Environment Variable was used in calling create location: " + measurementVariable.getVariableType()
						.getName());
			}
			rank++;
		}
		return geolocation;
	}

	public void setGeolocationValue(final Geolocation geolocation, final int termId, final String value) {
		if (TermId.TRIAL_INSTANCE_FACTOR.getId() == termId) {
			geolocation.setDescription(value);

		} else if (TermId.LATITUDE.getId() == termId) {
			geolocation.setLatitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));

		} else if (TermId.LONGITUDE.getId() == termId) {
			geolocation.setLongitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));

		} else if (TermId.GEODETIC_DATUM.getId() == termId) {
			geolocation.setGeodeticDatum(value);

		} else if (TermId.ALTITUDE.getId() == termId) {
			geolocation.setAltitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
		}
	}

}
