
package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.SampleList;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class SampleListDao extends GenericDAO<SampleList, Integer> {

	public SampleList getBySampleListId(final Integer sampleListId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
		criteria.add(Restrictions.eq("listId", sampleListId));
		return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}
}
