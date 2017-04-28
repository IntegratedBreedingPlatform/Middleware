
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class StudyMeasurements {

	private final Session session;

	private final ObservationQuery measurementQuery = new ObservationQuery();

	public StudyMeasurements(final Session session) {
		this.session = session;

	}

	List<ObservationDto> getAllMeasurements(final int projectBusinessIdentifier, final List<TraitDto> selectionMethodsAndTraits,
			List<String> germplasmDescriptors, final int instanceId,
			final int pageNumber, final int pageSize, final String sortBy, final String sortOrder) {
		final String generateQuery = this.measurementQuery.getAllObservationsQuery(selectionMethodsAndTraits, germplasmDescriptors, sortBy, sortOrder);
		return this.executeQueryAndMapResults(projectBusinessIdentifier, selectionMethodsAndTraits, germplasmDescriptors, generateQuery, instanceId,
				pageNumber, pageSize);
	}

	List<ObservationDto> getMeasurement(final int projectBusinessIdentifier, final List<TraitDto> traits, List<String> germplasmDescriptors,
			final Integer measurementId) {
		final String generateQuery = this.measurementQuery.getSingleObservationQuery(traits, germplasmDescriptors);
		final List<ObservationDto> measurement =
				this.executeQueryAndMapResults(projectBusinessIdentifier, traits, germplasmDescriptors, generateQuery, measurementId);
		// Defensive programming
		if (measurement.size() > 1) {

			throw new IllegalStateException("We should never have more than on measurment in the measurment list. "
							+ "Please contact support for further help.");
		}
		return measurement;
	}

	@SuppressWarnings("unchecked")
	private List<ObservationDto> executeQueryAndMapResults(final int projectBusinessIdentifier, final List<TraitDto> selectionMethodsAndTraits,
			List<String> germplasmDescriptors, final String generateQuery, final int instanceId, final int pageNumber, final int pageSize) {
		final SQLQuery createSQLQuery = this.createQueryAndAddScalar(selectionMethodsAndTraits, germplasmDescriptors, generateQuery);
		createSQLQuery.setParameter("studyId", projectBusinessIdentifier);
		createSQLQuery.setParameter("instanceId", String.valueOf(instanceId));

		createSQLQuery.setFirstResult(pageSize * (pageNumber - 1));
		createSQLQuery.setMaxResults(pageSize);

		return this.mapResults(createSQLQuery.list(), selectionMethodsAndTraits, germplasmDescriptors);
	}

	@SuppressWarnings("unchecked")
	private List<ObservationDto> executeQueryAndMapResults(final int projectBusinessIdentifier, final List<TraitDto> traits,
			List<String> germplasmDescriptors, final String generateQuery, final Integer measurementId) {
		final SQLQuery createSQLQuery = this.createQueryAndAddScalar(traits, germplasmDescriptors, generateQuery);
		createSQLQuery.setParameter("studyId", projectBusinessIdentifier);
		createSQLQuery.setParameter("experiment_id", measurementId);
		return this.mapResults(createSQLQuery.list(), traits, germplasmDescriptors);
	}

	private SQLQuery createQueryAndAddScalar(final List<TraitDto> selectionMethodsAndTraits, List<String> germplasmDescriptors, final String generateQuery) {
		final SQLQuery createSQLQuery = this.session.createSQLQuery(generateQuery);

		this.addScalar(createSQLQuery);

		this.addScalarForTraits(selectionMethodsAndTraits, createSQLQuery);

		for (String gpDescriptor : germplasmDescriptors) {
			createSQLQuery.addScalar(gpDescriptor, new StringType());
		}

		return createSQLQuery;
	}

	private void addScalarForTraits(final List<TraitDto> selectionMethodsAndTraits, final SQLQuery createSQLQuery) {
		for (final TraitDto trait : selectionMethodsAndTraits) {
			createSQLQuery.addScalar(trait.getTraitName());
			createSQLQuery.addScalar(trait.getTraitName() + "_PhenotypeId", new IntegerType());
		}
	}

	private void addScalar(final SQLQuery createSQLQuery) {
		createSQLQuery.addScalar("nd_experiment_id");
		createSQLQuery.addScalar("TRIAL_INSTANCE");
		createSQLQuery.addScalar("ENTRY_TYPE");
		createSQLQuery.addScalar("GID");
		createSQLQuery.addScalar("DESIGNATION");
		createSQLQuery.addScalar("ENTRY_NO");
		createSQLQuery.addScalar("ENTRY_CODE");
		createSQLQuery.addScalar("REP_NO");
		createSQLQuery.addScalar("PLOT_NO");
		createSQLQuery.addScalar("BLOCK_NO");
		createSQLQuery.addScalar("ROW");
		createSQLQuery.addScalar("COL");
		createSQLQuery.addScalar("PLOT_ID", new StringType());
		createSQLQuery.addScalar("FIELDMAP COLUMN");
		createSQLQuery.addScalar("FIELDMAP RANGE");
	}

	private List<ObservationDto> mapResults(final List<Object[]> results, final List<TraitDto> projectTraits,
			List<String> germplasmDescriptors) {
		final List<ObservationDto> measurements = new ArrayList<ObservationDto>();
		final int FIXED_COLUMNS = 15;

		if (results != null && !results.isEmpty()) {
			for (final Object[] row : results) {

				final List<MeasurementDto> traitResults = new ArrayList<MeasurementDto>();
				int counterTwo = 0;
				for (final TraitDto trait : projectTraits) {
					traitResults.add(new MeasurementDto(trait, (Integer) row[FIXED_COLUMNS + counterTwo + 1], (String) row[FIXED_COLUMNS + counterTwo]));
					counterTwo += 2;
				}
				ObservationDto measurement =
						new ObservationDto((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3], (String) row[4],
								(String) row[5], (String) row[6], (String) row[7], (String) row[8], (String) row[9], traitResults);
				measurement.setRowNumber((String) row[10]);
				measurement.setColumnNumber((String) row[11]);
				measurement.setPlotId((String) row[12]);
				measurement.setFieldMapColumn((String) row[13]);
				measurement.setFieldMapRange((String) row[14]);

				int gpDescIndex = FIXED_COLUMNS + projectTraits.size() * 2;
				for (String gpDesc : germplasmDescriptors) {
					measurement.additionalGermplasmDescriptor(gpDesc, (String) row[gpDescIndex++]);
				}
				measurements.add(measurement);
			}
		}
		return Collections.unmodifiableList(measurements);
	}

	private int setQueryParameters(final int studyIdentifier, final List<TraitDto> projectTraits, final SQLQuery createSQLQuery) {
		int counter = 0;
		for (final TraitDto trait : projectTraits) {
			createSQLQuery.setParameter(counter++, trait.getTraitName());
		}
		createSQLQuery.setParameter(counter++, studyIdentifier);
		return counter;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getAllStudyDetailsAsTable(final int projectBusinessIdentifier, final List<TraitDto> traits, Integer instanceId) {
		final String generateQuery = this.measurementQuery.getObservationQueryWithBlockRowCol(traits, instanceId);
		final SQLQuery createSQLQuery = this.createQueryAndAddScalarWithBlockRowCol(traits, generateQuery);

		this.setQueryParameters(projectBusinessIdentifier, traits, createSQLQuery);

		if (instanceId != null) {
			createSQLQuery.setParameter("instanceId", instanceId);
		}

		final List<Object[]> result = createSQLQuery.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getAllStudyDetailsAsTable(final int projectBusinessIdentifier, final List<TraitDto> traits,
			final List<String> germplasmDescriptors) {
		final String generateQuery =
				this.measurementQuery.getObservationsMainQuery(traits, germplasmDescriptors) + this.measurementQuery.getGroupingClause();

		final SQLQuery createSQLQuery = this.createQueryAndAddScalar(traits, germplasmDescriptors, generateQuery);
		createSQLQuery.setParameter("studyId", projectBusinessIdentifier);

		final List<Object[]> result = createSQLQuery.list();
		return result;
	}

	private SQLQuery createQueryAndAddScalarWithBlockRowCol(final List<TraitDto> traits, final String generateQuery) {
		final SQLQuery createSQLQuery = this.session.createSQLQuery(generateQuery);

		this.addScalar(createSQLQuery);
		createSQLQuery.addScalar("LocationName");
		createSQLQuery.addScalar("LocationAbbreviation");
		createSQLQuery.addScalar("FieldMapColumn");
		createSQLQuery.addScalar("FieldMapRow");
		this.addScalarForTraits(traits, createSQLQuery);
		return createSQLQuery;
	}
}
