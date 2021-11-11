package org.generationcp.middleware.service.impl.feedback;

import java.util.Optional;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.generationcp.middleware.service.api.feedback.FeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public FeedbackServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public boolean shouldShowFeedback(final FeedbackFeature feature) {
		final Optional<Feedback> feedback =
				this.workbenchDaoFactory.getFeedbackDAO().getByUserIdAndFeature(feature);
		if (!feedback.isPresent()) {
			return true;
		}
		return feedback.get().getShowAgain();
	}

	@Override
	public void dontShowAgain(final FeedbackFeature feature) {
		final Feedback feedback =
				this.workbenchDaoFactory.getFeedbackDAO().getByUserIdAndFeature(feature)
				.orElseGet(() -> this.createFeedback(feature));
		feedback.dontShowAgain();
		this.workbenchDaoFactory.getFeedbackDAO().save(feedback);
	}

	private Feedback createFeedback(final FeedbackFeature feature) {
		final WorkbenchUser user = this.workbenchDaoFactory.getWorkbenchUserDAO().getById(ContextHolder.getLoggedInUserId());
		final Feedback feedback = new Feedback(user, feature);
		return feedback;
	}

}
