
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.pojos.Sample;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class SampleDao extends GenericDAO<Sample, Integer> {

	public Sample getBySampleId(final Integer sampleId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(org.generationcp.middleware.pojos.Sample.class);
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
}
