package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class GermplasmStudySourceDAO extends GenericDAO<GermplasmStudySource, Integer> {

	public List<GermplasmStudySource> search(final int studyId) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmStudySource.class);
		criteria.add(Restrictions.eq("study.projectId", studyId));
		// TODO: Implement filter and pagination
		criteria.setMaxResults(Integer.MAX_VALUE);
		return criteria.list();
	}

	public long count(final int studyId) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmStudySource.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("study.projectId", studyId));
		// TODO: Implement filter
		criteria.setMaxResults(Integer.MAX_VALUE);
		return (long) criteria.uniqueResult();
	}

}
