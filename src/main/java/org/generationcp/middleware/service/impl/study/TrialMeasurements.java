package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.service.api.study.Measurement;
import org.generationcp.middleware.service.api.study.Trait;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class TrialMeasurements {

	private Session session;

	public TrialMeasurements(final Session session) {
		this.session = session;

	}

	@SuppressWarnings("unchecked")
	List<Measurement> getAllMeasurements(final int projectBusinessIdentifier,
			final List<String> projectTraits) {
		final MeasurementQuery measurementQuery = new MeasurementQuery();
		String generateQuery = measurementQuery.generateQuery(projectTraits);
		
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
		
		
		for (final String trait : projectTraits) {
			createSQLQuery.addScalar(trait);
			createSQLQuery.addScalar(trait + "_PhenotypeId");

		}
		
		setQueryParameters(projectBusinessIdentifier, projectTraits, createSQLQuery);

		return mapStuff(createSQLQuery.list(), projectTraits);
	}
	
	
	private List<Measurement> mapStuff(List<Object[]> results, final List<String> projectTraits)  {
		final List<Measurement> traits = new ArrayList<Measurement>();

		if (results != null && !results.isEmpty()) {
			for (Object[] row : results) {

				final List<Trait> stuff = new ArrayList<Trait>();
				int counterTwo = 1;
				for (final String trait : projectTraits) {
					stuff.add(new Trait(trait, (Integer) row[(8 + counterTwo+1)], (String) row[(8 + counterTwo)]));
					counterTwo+=2;
				}
				Measurement measurement = new Measurement((Integer) row[0], (String) row[1],
						(String) row[2], (Integer) row[3], (String) row[4], (String) row[5],
						(String) row[6], (String) row[7], (String) row[7], stuff);
				traits.add(measurement);

			}
		}
		return traits;
	}

	private void setQueryParameters(final int projectBusinessIdentifier,
			final List<String> projectTraits, final SQLQuery createSQLQuery) {
		int counter = 0;
		for (final String trait : projectTraits) {
			createSQLQuery.setParameter(counter++, trait);
		}
		createSQLQuery.setParameter(counter++, projectBusinessIdentifier);
	}
}
