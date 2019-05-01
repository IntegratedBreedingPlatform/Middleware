
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class StudyMeasurements {

	public static final String PROJECT_NAME = "PROJECT_NAME";
	public static final String LOCATION_DB_ID = "locationDbId";
	public static final String ND_GEOLOCATION_ID = "nd_geolocation_id";
	public static final String FIELD_MAP_ROW = "FieldMapRow";
	public static final String FIELD_MAP_COLUMN = "FieldMapColumn";
	public static final String LOCATION_ABBREVIATION = "LocationAbbreviation";
	public static final String LOCATION_NAME = "LocationName";
	public static final String OBS_UNIT_ID = "OBS_UNIT_ID";
	public static final String COL = "COL";
	public static final String ROW = "ROW";
	public static final String BLOCK_NO = "BLOCK_NO";
	public static final String PLOT_NO = "PLOT_NO";
	public static final String REP_NO = "REP_NO";
	public static final String ENTRY_CODE = "ENTRY_CODE";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String GID = "GID";
	public static final String ENTRY_TYPE = "ENTRY_TYPE";
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String ND_EXPERIMENT_ID = "nd_experiment_id";

	public static final String STUDY_ID = "studyId";

	private final Session session;

	private final ObservationQuery measurementQuery = new ObservationQuery();

	public StudyMeasurements(final Session session) {
		this.session = session;

	}

	List<ObservationDto> getAllMeasurements(final int projectBusinessIdentifier,
			final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors,
			final List<String> designFactors, final int instanceId, final int pageNumber, final int pageSize,
			final String sortBy, final String sortOrder) {
		final String generateQuery = this.measurementQuery.getAllObservationsQuery(selectionMethodsAndTraits,
				germplasmDescriptors, designFactors, sortBy, sortOrder);
		return this.executeQueryAndMapResults(projectBusinessIdentifier, selectionMethodsAndTraits,
				germplasmDescriptors, designFactors, generateQuery, instanceId, pageNumber, pageSize);
	}

	List<ObservationDto> getMeasurement(final int projectBusinessIdentifier, final List<MeasurementVariableDto> traits,
			final List<String> germplasmDescriptors, final List<String> designFactors, final Integer measurementId) {
		final String generateQuery = this.measurementQuery.getSingleObservationQuery(traits, germplasmDescriptors,
				designFactors);
		final List<ObservationDto> measurement = this.executeQueryAndMapResults(projectBusinessIdentifier, traits,
				germplasmDescriptors, designFactors, generateQuery, measurementId);
		// Defensive programming
		if (measurement.size() > 1) {

			throw new IllegalStateException("We should never have more than one measurement in the measurment list. "
					+ "Please contact support for further help.");
		}
		return measurement;
	}

	@SuppressWarnings("unchecked")
	private List<ObservationDto> executeQueryAndMapResults(final int projectBusinessIdentifier,
			final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors,
			final List<String> designFactors, final String generateQuery, final int instanceId, final int pageNumber,
			final int pageSize) {
		final SQLQuery createSQLQuery = this.createQueryAndAddScalar(selectionMethodsAndTraits, germplasmDescriptors,
				designFactors, generateQuery);
		createSQLQuery.setParameter(StudyMeasurements.STUDY_ID, projectBusinessIdentifier);
		createSQLQuery.setParameter("instanceId", String.valueOf(instanceId));

		createSQLQuery.setFirstResult(pageSize * (pageNumber - 1));
		createSQLQuery.setMaxResults(pageSize);

		return this.mapResults(createSQLQuery.list(), selectionMethodsAndTraits, germplasmDescriptors, designFactors);
	}

	@SuppressWarnings("unchecked")
	private List<ObservationDto> executeQueryAndMapResults(final int projectBusinessIdentifier,
			final List<MeasurementVariableDto> traits, final List<String> germplasmDescriptors,
			final List<String> designFactors, final String generateQuery, final Integer measurementId) {
		final SQLQuery createSQLQuery = this.createQueryAndAddScalar(traits, germplasmDescriptors, designFactors,
				generateQuery);
		createSQLQuery.setParameter(StudyMeasurements.STUDY_ID, projectBusinessIdentifier);
		createSQLQuery.setParameter("experiment_id", measurementId);
		return this.mapResults(createSQLQuery.list(), traits, germplasmDescriptors, designFactors);
	}

	private SQLQuery createQueryAndAddScalar(final List<MeasurementVariableDto> selectionMethodsAndTraits,
			final List<String> germplasmDescriptors, final List<String> designFactors, final String generateQuery) {
		final SQLQuery createSQLQuery = this.session.createSQLQuery(generateQuery);

		this.addScalar(createSQLQuery);
		createSQLQuery.addScalar("FIELDMAP COLUMN");
		createSQLQuery.addScalar("FIELDMAP RANGE");
		createSQLQuery.addScalar("SUM_OF_SAMPLES");

		this.addScalarForTraits(selectionMethodsAndTraits, createSQLQuery, true);

		for (final String gpDescriptor : germplasmDescriptors) {
			createSQLQuery.addScalar(gpDescriptor, new StringType());
		}

		for (final String designFactor : designFactors) {
			createSQLQuery.addScalar(designFactor, new StringType());
		}

		return createSQLQuery;
	}

	private void addScalarForTraits(final List<MeasurementVariableDto> selectionMethodsAndTraits, final SQLQuery createSQLQuery, final Boolean addStatus) {
		for (final MeasurementVariableDto measurementVariable : selectionMethodsAndTraits) {
			createSQLQuery.addScalar(measurementVariable.getName());
			createSQLQuery.addScalar(measurementVariable.getName() + "_PhenotypeId", new IntegerType());
			if (addStatus) {
				createSQLQuery.addScalar(measurementVariable.getName() + "_Status");
			}
		}
	}

	private List<ObservationDto> mapResults(final List<Object[]> results,
			final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors,
			final List<String> designFactors) {
		final List<ObservationDto> measurements = new ArrayList<>();
		final int fixedColumns = 16;

		if (results != null && !results.isEmpty()) {
			for (final Object[] row : results) {

				final List<MeasurementDto> measurementVariableResults = new ArrayList<>();
				int counterThree = 0;
				for (final MeasurementVariableDto variable : selectionMethodsAndTraits) {
					final String status = (String) row[fixedColumns + counterThree + 2];
					measurementVariableResults.add(new MeasurementDto(
						variable,
						(Integer) row[fixedColumns + counterThree + 1],
						(String) row[fixedColumns + counterThree],
						(status != null ? Phenotype.ValueStatus.valueOf(status) : null)));
					counterThree += 3;
				}
				final ObservationDto measurement = new ObservationDto((Integer) row[0], (String) row[1],
						(String) row[2], (Integer) row[3], (String) row[4], (String) row[5], (String) row[6],
						(String) row[7], (String) row[8], (String) row[9], measurementVariableResults);
				measurement.setRowNumber((String) row[10]);
				measurement.setColumnNumber((String) row[11]);
				measurement.setObsUnitId((String) row[12]);
				measurement.setFieldMapColumn((String) row[13]);
				measurement.setFieldMapRange((String) row[14]);
				measurement.setSamples((String) row[15]);

				int additionalFactorsIndex = fixedColumns + selectionMethodsAndTraits.size() * 3;
				for (final String gpDesc : germplasmDescriptors) {
					measurement.additionalGermplasmDescriptor(gpDesc, (String) row[additionalFactorsIndex++]);
				}
				for (final String designFactor : designFactors) {
					measurement.additionalDesignFactor(designFactor, (String) row[additionalFactorsIndex++]);
				}
				measurements.add(measurement);
			}
		}
		return Collections.unmodifiableList(measurements);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getAllStudyDetailsAsTable(final int projectBusinessIdentifier,
			final List<MeasurementVariableDto> measurementVariables, final Integer instanceId) {
		final String generateQuery = this.measurementQuery.getObservationQueryWithBlockRowCol(measurementVariables,
				instanceId);
		final SQLQuery createSQLQuery = this.createQueryAndAddScalarWithBlockRowCol(measurementVariables,
				generateQuery);

		createSQLQuery.setParameter("projectId", projectBusinessIdentifier);

		if (instanceId != null) {
			createSQLQuery.setParameter("instanceId", instanceId);
		}

		return createSQLQuery.list();
	}

	private SQLQuery createQueryAndAddScalarWithBlockRowCol(final List<MeasurementVariableDto> measurementVariables,
			final String generateQuery) {
		final SQLQuery createSQLQuery = this.session.createSQLQuery(generateQuery);

		this.addScalar(createSQLQuery);
		createSQLQuery.addScalar(StudyMeasurements.LOCATION_NAME);
		createSQLQuery.addScalar(StudyMeasurements.LOCATION_ABBREVIATION);
		createSQLQuery.addScalar(StudyMeasurements.FIELD_MAP_COLUMN);
		createSQLQuery.addScalar(StudyMeasurements.FIELD_MAP_ROW);
		createSQLQuery.addScalar(StudyMeasurements.ND_GEOLOCATION_ID);
		createSQLQuery.addScalar(StudyMeasurements.LOCATION_DB_ID);
		createSQLQuery.addScalar(StudyMeasurements.PROJECT_NAME);
		this.addScalarForTraits(measurementVariables, createSQLQuery, false);
		return createSQLQuery;
	}

	private void addScalar(final SQLQuery createSQLQuery) {
		createSQLQuery.addScalar(StudyMeasurements.ND_EXPERIMENT_ID);
		createSQLQuery.addScalar(StudyMeasurements.TRIAL_INSTANCE);
		createSQLQuery.addScalar(StudyMeasurements.ENTRY_TYPE);
		createSQLQuery.addScalar(StudyMeasurements.GID);
		createSQLQuery.addScalar(StudyMeasurements.DESIGNATION);
		createSQLQuery.addScalar(StudyMeasurements.ENTRY_NO);
		createSQLQuery.addScalar(StudyMeasurements.ENTRY_CODE);
		createSQLQuery.addScalar(StudyMeasurements.REP_NO);
		createSQLQuery.addScalar(StudyMeasurements.PLOT_NO);
		createSQLQuery.addScalar(StudyMeasurements.BLOCK_NO);
		createSQLQuery.addScalar(StudyMeasurements.ROW);
		createSQLQuery.addScalar(StudyMeasurements.COL);
		createSQLQuery.addScalar(StudyMeasurements.OBS_UNIT_ID, new StringType());
	}

	@SuppressWarnings("unchecked")
	public List<ObservationDto> getSampleObservations(final int datasetId, final List<Integer> instanceIds,
			final Integer selectionVariableId) {
		final SQLQuery createSQLQuery = this.session.createSQLQuery(this.measurementQuery.getSampleObservationQuery());

		createSQLQuery.addScalar(StudyMeasurements.ND_EXPERIMENT_ID, new IntegerType());
		createSQLQuery.addScalar("preferred_name", new StringType());
		createSQLQuery.addScalar("value", new StringType());
		createSQLQuery.addScalar("gid", new IntegerType());

		createSQLQuery.setParameter("datasetId", datasetId);
		createSQLQuery.setParameter("selectionVariableId", selectionVariableId);
		createSQLQuery.setParameterList("instanceIds", instanceIds);
		return this.mapSampleObservations(createSQLQuery.list());
	}

	private List<ObservationDto> mapSampleObservations(final List<Object[]> results) {
		final List<ObservationDto> measurements = new ArrayList<>();

		if (results != null && !results.isEmpty()) {
			for (final Object[] row : results) {
				final String value = (String) row[2];
				if (StringUtils.isNotBlank(value) && !"0".equals(value)) {
					final List<MeasurementDto> measurementVariableResults = new ArrayList<>();

					final MeasurementDto measurementDto = new MeasurementDto(value);
					measurementVariableResults.add(measurementDto);

					final ObservationDto measurement = new ObservationDto((Integer) row[0], (String) row[1],
							measurementVariableResults, (Integer) row[3]);
					measurements.add(measurement);
				}
			}
		}
		return Collections.unmodifiableList(measurements);
	}
}
