
package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.SampleList;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class SampleListDao extends GenericDAO<SampleList, Integer> {

	public static final String ROOT_FOLDER = "Samples";

	public SampleList getBySampleListId(final Integer sampleListId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
		criteria.add(Restrictions.eq("listId", sampleListId));
		return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public SampleList getBySampleListName(final String sampleListName) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
		criteria.add(Restrictions.like("listName", sampleListName));
		return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public SampleList getRotSampleList() {
		return this.getBySampleListName(ROOT_FOLDER);
	}
}
