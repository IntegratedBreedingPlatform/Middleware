package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.Sample;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SampleDao extends GenericDAO<Sample, Integer> {

	public Sample getBySampleId(Integer sampleId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(org.generationcp.middleware.pojos.Sample.class);
		criteria.add(Restrictions.eq("sampleId", sampleId));
		return (Sample) criteria.getExecutableCriteria(getSession()).uniqueResult();
	}

  public List<Sample> getBySampleIds(Collection<Integer> sampleIds) {
	List<Sample> samples = new ArrayList<>();
	Iterator<Integer> sampleIdsIterator = sampleIds.iterator();
	while (sampleIdsIterator.hasNext()){
	  samples.add(this.getBySampleId(sampleIdsIterator.next()));
	}
	return samples;
  }
}
