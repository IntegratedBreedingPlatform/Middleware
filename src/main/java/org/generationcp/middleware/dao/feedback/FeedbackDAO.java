package org.generationcp.middleware.dao.feedback;

import java.util.Optional;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class FeedbackDAO extends GenericDAO<Feedback, Integer> {

	public FeedbackDAO(final Session session) {
		super(session);
	}

	public Optional<Feedback> getByUserIdAndFeature(final FeedbackFeature feature) {

		final Criteria criteria = this.getSession().createCriteria(Feedback.class);
		criteria.add(Restrictions.eq("user.userid", ContextHolder.getLoggedInUserId()));
		criteria.add(Restrictions.eq("feature", feature));

		final Feedback feedback = (Feedback) criteria.uniqueResult();
		return Optional.ofNullable(feedback);
	}

}
