package org.generationcp.middleware.dao.feedback;

import java.util.Optional;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackUser;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class FeedbackUserDAO extends GenericDAO<FeedbackUser, Integer> {

	public FeedbackUserDAO(final Session session) {
		super(session);
	}

	public Optional<FeedbackUser> getByFeedbackAndUserId(final Feedback feedback, final Integer userId) {

		final Criteria criteria = this.getSession().createCriteria(FeedbackUser.class);
		criteria.add(Restrictions.eq("feedback", feedback));
		criteria.add(Restrictions.eq("user.userid", userId));

		final FeedbackUser feedbackUser = (FeedbackUser) criteria.uniqueResult();
		return Optional.ofNullable(feedbackUser);
	}

}
