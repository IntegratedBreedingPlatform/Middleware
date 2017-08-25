
package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Sample;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleDao extends GenericDAO<Sample, Integer> {

	protected final static String SQL_SAMPLES_AND_EXPERIMENTS =
		"SELECT  nde.nd_experiment_id, (SELECT COALESCE(NULLIF(COUNT(sp.sample_id), 0), '-')\n FROM plant pl INNER JOIN\n"
			+ "            						sample AS sp ON pl.plant_id = sp.sample_id\n" + "        WHERE\n"
			+ "            						nde.nd_experiment_id = pl.nd_experiment_id) 'SAMPLES'"
			+ "		FROM project p INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id\n"
			+ "			INNER JOIN nd_experiment_project ep ON pr.subject_project_id = ep.project_id\n"
			+ "			INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id\n"
			+ "		WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr "
			+ "								INNER JOIN project p ON p.project_id = pr.subject_project_id\n"
			+ "        						WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA'))\n"
			+ "GROUP BY nde.nd_experiment_id";

	public Sample getBySampleId(final Integer sampleId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Sample.class);
		criteria.add(Restrictions.eq("sampleId", sampleId));
		return (Sample) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public List<Sample> getBySampleIds(final Collection<Integer> sampleIds) {
		final List<Sample> samples = new ArrayList<>();

		for (Integer id : sampleIds) {
			samples.add(this.getBySampleId(id));
		}

		return samples;
	}

	@SuppressWarnings("unchecked")
	public List<Sample> getByPlotId(final String plotId) {
		return this.getSession()
			.createCriteria(Sample.class, "sample")
			.createAlias("sample.plant", "plant")
			.createAlias("plant.experiment", "experiment")
			.add(Restrictions.eq("experiment.plotId", plotId))
			.list();
	}

	public Map<Integer, String> getExperimentToSample(final Integer studyDbId) {
		Map<Integer, String> samplesMap = new HashMap<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(SQL_SAMPLES_AND_EXPERIMENTS);

			query.setParameter("studyId", studyDbId);
			List results = query.list();

			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					samplesMap.put((Integer) result[0], (String) result[1]);
				}
			}

		} catch (HibernateException he) {
			throw new MiddlewareException(
				"Unexpected error in executing getSampleByExperiment(studyDbId = " + studyDbId + ") query: " + he.getMessage(), he);
		}
		return samplesMap;
	}
}
