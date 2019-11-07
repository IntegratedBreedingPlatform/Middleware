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

		return new StudyInstance();
	}

	@Override
	public List<StudyInstance> getStudyInstances(final int studyId) {
		try {
			final String sql = "select \n" + "	geoloc.nd_geolocation_id as instanceDbId, \n"
				+ "	nde.nd_experiment_id as experimentId, \n"
				+ "	max(if(geoprop.type_id = 8190, loc.locid, null)) as locationId, \n" // 8190 = cvterm for LOCATION_ID
				+ "	max(if(geoprop.type_id = 8190, loc.lname, null)) as locationName, \n" +
				"	max(if(geoprop.type_id = 8190, loc.labbr, null)) as locationAbbreviation, \n" + // 8189 = cvterm for LOCATION_ABBR
				"	max(if(geoprop.type_id = 8189, geoprop.value, null)) as customLocationAbbreviation, \n" +
				// 8189 = cvterm for customLocationAbbreviation
				"	case when max(if(geoprop.type_id = 8583, geoprop.value, null)) "
				+ "is null then 0 else 1 end as hasFieldmap, \n" +
				// 8583 = cvterm for BLOCK_ID (meaning instance has fieldmap)
				"   geoloc.description as instanceNumber \n" + " from \n" + "	nd_geolocation geoloc \n"
				+ "    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "    inner join project proj on proj.project_id = nde.project_id \n"
				+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "	   left outer join location loc on geoprop.value = loc.locid and geoprop.type_id = 8190 \n"
				+ " where \n"
				+ "    proj.study_id = :studyId and proj.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId() + " \n"
				+ "    group by geoloc.nd_geolocation_id \n" + "    order by (1 * geoloc.description) asc ";

			final SQLQuery query = this.sessionProvider.getSession().createSQLQuery(sql);
			query.setParameter("studyId", studyId);
			query.addScalar("instanceDbId", new IntegerType());
			query.addScalar("experimentId", new IntegerType());
			query.addScalar("locationId", new IntegerType());
			query.addScalar("locationName", new StringType());
			query.addScalar("locationAbbreviation", new StringType());
			query.addScalar("customLocationAbbreviation", new StringType());
			query.addScalar("hasFieldmap", new BooleanType());
			query.addScalar("instanceNumber", new IntegerType());
			query.setResultTransformer(Transformers.aliasToBean(StudyInstance.class));
			return query.list();
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				"Unexpected error in executing getStudyInstances(studyId = " + studyId + ") query: " + he.getMessage(), he);
		}
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
