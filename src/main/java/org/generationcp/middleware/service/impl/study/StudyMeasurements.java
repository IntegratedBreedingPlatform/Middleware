
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class StudyMeasurements {

	private Session session;

	private final ObservationQuery measurementQuery = new ObservationQuery();

	public StudyMeasurements(final Session session) {
		this.session = session;

	}

	List<ObservationDto> getAllMeasurements(final int projectBusinessIdentifier, final List<TraitDto> traits) {
		final String generateQuery = measurementQuery.getObservationQuery(traits);
		return executeQueryAndMapResults(projectBusinessIdentifier, traits, generateQuery);
	}
	
	

	List<ObservationDto> getMeasurement(final int projectBusinessIdentifier, final List<TraitDto> traits, final Integer measurementId) {
		final String generateQuery = measurementQuery.getSingleObservationQuery(traits);
		final List<ObservationDto> measurement = executeQueryAndMapResults(projectBusinessIdentifier, traits, generateQuery, measurementId);
		//Defensive programming
		if (measurement.size() > 1) {
			
			throw new IllegalStateException(
					"We should never have more than on measurment in the measurment list. "
					+ "Please contact support for further help.");
		}
		return measurement;
	}
	


	@SuppressWarnings("unchecked")
	private List<ObservationDto> executeQueryAndMapResults(final int projectBusinessIdentifier, final List<TraitDto> traits,
			final String generateQuery) {
		final SQLQuery createSQLQuery = createQueryAndAddScalar(traits, generateQuery);

		setQueryParameters(projectBusinessIdentifier, traits, createSQLQuery);

		return mapResults(createSQLQuery.list(), traits);
	}

	@SuppressWarnings("unchecked")
	private List<ObservationDto> executeQueryAndMapResults(final int projectBusinessIdentifier, final List<TraitDto> traits,
			final String generateQuery, final Integer measurementId) {
		final SQLQuery createSQLQuery = createQueryAndAddScalar(traits, generateQuery);

		setQueryParameters(projectBusinessIdentifier, traits, createSQLQuery, measurementId);

		return mapResults(createSQLQuery.list(), traits);
	}

	private SQLQuery createQueryAndAddScalar(final List<TraitDto> traits, final String generateQuery) {
		final SQLQuery createSQLQuery = this.session.createSQLQuery(generateQuery);

		createSQLQuery.addScalar("nd_experiment_id");
		createSQLQuery.addScalar("TRIAL_INSTANCE");
		createSQLQuery.addScalar("ENTRY_TYPE");
		createSQLQuery.addScalar("GID");
		createSQLQuery.addScalar("DESIGNATION");
		createSQLQuery.addScalar("ENTRY_NO");
		createSQLQuery.addScalar("SEED_SOURCE");
		createSQLQuery.addScalar("REP_NO");
		createSQLQuery.addScalar("PLOT_NO");

		for (final TraitDto trait : traits) {
			createSQLQuery.addScalar(trait.getTraitName());
			createSQLQuery.addScalar(trait.getTraitName() + "_PhenotypeId");
		}
		return createSQLQuery;
	}

	private List<ObservationDto> mapResults(final List<Object[]> results, final List<TraitDto> projectTraits) {
		final List<ObservationDto> measurements = new ArrayList<ObservationDto>();

		if (results != null && !results.isEmpty()) {
			for (final Object[] row : results) {

				final List<MeasurementDto> traitResults = new ArrayList<MeasurementDto>();
				int counterTwo = 1;
				for (final TraitDto trait : projectTraits) {
					traitResults.add(new MeasurementDto(trait, (Integer) row[(8 + counterTwo + 1)], (String) row[(8 + counterTwo)]));
					counterTwo += 2;
				}
				ObservationDto measurement =
						new ObservationDto((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3], (String) row[4],
								(String) row[5], (String) row[6], (String) row[7], (String) row[7], traitResults);
				measurements.add(measurement);

			}
		}
		return Collections.unmodifiableList(measurements);
	}

	private void setQueryParameters(int projectBusinessIdentifier, List<TraitDto> traits, SQLQuery createSQLQuery, Integer measurementId) {
		int parameterCounter = setQueryParameters(projectBusinessIdentifier, traits, createSQLQuery);
		createSQLQuery.setParameter(parameterCounter++, measurementId);

	}

	private int setQueryParameters(final int studyIdentifier, final List<TraitDto> projectTraits, final SQLQuery createSQLQuery) {
		int counter = 0;
		for (final TraitDto trait : projectTraits) {
			createSQLQuery.setParameter(counter++, trait.getTraitName());
		}
		createSQLQuery.setParameter(counter++, studyIdentifier);
		return counter;
	}


}
