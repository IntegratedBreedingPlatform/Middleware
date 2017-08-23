
package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.Sample;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SampleDao extends GenericDAO<Sample, Integer> {

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
}
