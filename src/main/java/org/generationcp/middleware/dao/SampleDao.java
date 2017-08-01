package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.Sample;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class SampleDao extends GenericDAO<Sample, Integer> {

	public Sample getBySampleId(Integer sampleId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(org.generationcp.middleware.pojos.Sample.class);
		criteria.add(Restrictions.eq("sampleId", sampleId));
		return (Sample) criteria.getExecutableCriteria(getSession()).uniqueResult();
	}
}
