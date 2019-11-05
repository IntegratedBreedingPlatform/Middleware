package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.StringUtils;
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
	public StudyInstance createStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber) {

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
		studyInstance.setInstanceNumber(Integer.valueOf(instanceNumber));
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

		try {
			final String sql = "select \n" + "	geoloc.nd_geolocation_id as INSTANCE_DBID, \n"
				+ "	max(if(geoprop.type_id = 8190, loc.locid, null)) as LOCATION_ID, \n" // 8190 = cvterm for LOCATION_ID
				+ "	max(if(geoprop.type_id = 8190, loc.lname, null)) as LOCATION_NAME, \n" +
				"	max(if(geoprop.type_id = 8190, loc.labbr, null)) as LOCATION_ABBR, \n" + // 8189 = cvterm for LOCATION_ABBR
				"	max(if(geoprop.type_id = 8189, geoprop.value, null)) as CUSTOM_LOCATION_ABBR, \n" +
				// 8189 = cvterm for CUSTOM_LOCATION_ABBR
				"	max(if(geoprop.type_id = 8583, geoprop.value, null)) as FIELDMAP_BLOCK, \n" +
				// 8583 = cvterm for BLOCK_ID (meaning instance has fieldmap)
				"   geoloc.description as INSTANCE_NUMBER \n" + " from \n" + "	nd_geolocation geoloc \n"
				+ "    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "    inner join project proj on proj.project_id = nde.project_id \n"
				+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "	   left outer join location loc on geoprop.value = loc.locid and geoprop.type_id = 8190 \n"
				+ " where \n"
				+ "    proj.study_id = :studyId and proj.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId() + " \n"
				+ "    group by geoloc.nd_geolocation_id \n" + "    order by (1 * geoloc.description) asc ";

			final SQLQuery query = this.sessionProvider.getSession().createSQLQuery(sql);
			query.setParameter("studyId", studyId);
			query.addScalar("INSTANCE_DBID", new IntegerType());
			query.addScalar("LOCATION_ID", new IntegerType());
			query.addScalar("LOCATION_NAME", new StringType());
			query.addScalar("LOCATION_ABBR", new StringType());
			query.addScalar("CUSTOM_LOCATION_ABBR", new StringType());
			query.addScalar("FIELDMAP_BLOCK", new StringType());
			query.addScalar("INSTANCE_NUMBER", new IntegerType());

			final List queryResults = query.list();
			final List<StudyInstance> instances = new ArrayList<>();
			for (final Object result : queryResults) {
				final Object[] row = (Object[]) result;
				final boolean hasFieldmap = !StringUtils.isEmpty((String) row[5]);
				final StudyInstance instance =
					new StudyInstance((Integer) row[0], (Integer) row[1], (String) row[2], (String) row[3], (Integer) row[6],
						(String) row[4], hasFieldmap);
				instances.add(instance);
			}
			return instances;
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				"Unexpected error in executing getStudyInstances(studyId = " + studyId + ") query: " + he.getMessage(), he);
		}
	}

	@Override
	public void removeStudyInstance(final CropType crop, final Integer datasetId, final String instanceNumber) {
		// TODO: To be implemented in IBP-3160
	}

	protected Geolocation createGeolocation(final List<MeasurementVariable> measurementVariables, final String instanceNumber,
		final Integer locationId) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setProperties(new ArrayList<GeolocationProperty>());

		int rank = 1;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			final int variableId = measurementVariable.getTermId();
			if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variableId) {
				geolocation.setDescription(instanceNumber);
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
