package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.service.api.study.StudyGermplasmSourceRequest;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class GermplasmStudySourceDAO extends GenericDAO<GermplasmStudySource, Integer> {

	public List<GermplasmStudySource> getGermplasmStudySourceList(final StudyGermplasmSourceRequest searchParameters) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmStudySource.class);
		criteria.add(Restrictions.eq("study.projectId", searchParameters.getStudyId()));
		// TODO: Implement filter and pagination
		criteria.setMaxResults(Integer.MAX_VALUE);
		return criteria.list();
	}

	public long countGermplasmStudySourceList(final StudyGermplasmSourceRequest searchParameters) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmStudySource.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("study.projectId", searchParameters.getStudyId()));
		// TODO: Implement filter
		criteria.setMaxResults(Integer.MAX_VALUE);
		return (long) criteria.uniqueResult();
	}

}
