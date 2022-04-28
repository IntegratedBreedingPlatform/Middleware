package org.generationcp.middleware.service.impl.feedback;

import java.util.Optional;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.feedback.FeedbackDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackUser;
import org.generationcp.middleware.service.api.feedback.FeedbackService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

	@Value("#{new Boolean('${feedback.enabled}')}")
	private boolean feedbackFeatureEnabled;

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public FeedbackServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public boolean shouldShowFeedback(final FeedbackFeature feature) {
		if (!this.feedbackFeatureEnabled) {
			return false;
		}

		final Optional<Feedback> optionalFeedback = this.getFeedback(feature);
		if (!optionalFeedback.isPresent()) {
			return false;
		}

		final Feedback feedback = optionalFeedback.get();
		if (!feedback.isEnabled()) {
			return false;
		}

		final Optional<FeedbackUser> optionalFeedbackUser = this.getFeedbackUser(feedback);
		final FeedbackUser feedbackUser;
		if (!optionalFeedbackUser.isPresent()) {
			feedbackUser = this.createFeedback(feedback);
			this.workbenchDaoFactory.getFeedbackUserDAO().save(feedbackUser);
			// We check here if the feature should be shown just in case that the amount of feature views is set to 1
			return this.checkFeedbackShouldBeShown(feedbackUser, feedback.getAttempts());
		}

		feedbackUser = optionalFeedbackUser.get();
		if (!feedbackUser.getShowAgain()) {
			return false;
		}

		feedbackUser.hasSeen();
		this.workbenchDaoFactory.getFeedbackUserDAO().save(feedbackUser);

		return this.checkFeedbackShouldBeShown(feedbackUser, feedback.getAttempts());
	}

	@Override
	public void dontShowAgain(final FeedbackFeature feature) {
		this.getFeedback(feature).ifPresent(feedback -> {
			this.getFeedbackUser(feedback).ifPresent(feedbackUser -> {
				feedbackUser.dontShowAgain();
				this.workbenchDaoFactory.getFeedbackUserDAO().save(feedbackUser);
			});
		});
	}

	@Override
	public FeedbackDto getFeedBack(final FeedbackFeature feature) {
		Optional<Feedback> feedback = this.getFeedback(feature);
		if (feedback.isPresent()) {
			return new FeedbackDto(feedback.get().getId(), feedback.get().getFeature(), feedback.get().getCollectorId(),
				feedback.get().getAttempts(), feedback.get().isEnabled());
		}
		return null;
	}

	private FeedbackUser createFeedback(final Feedback feedback) {
		final WorkbenchUser user =
			this.workbenchDaoFactory.getWorkbenchUserDAO().getById(ContextHolder.getLoggedInUserId());
		final FeedbackUser feedbackUser = new FeedbackUser(feedback, user);
		return feedbackUser;
	}

	private Optional<Feedback> getFeedback(final FeedbackFeature feature) {
		return this.workbenchDaoFactory.getFeedbackDAO().getByFeature(feature);
	}

	private Optional<FeedbackUser> getFeedbackUser(final Feedback feedback) {
		return this.workbenchDaoFactory.getFeedbackUserDAO()
			.getByFeedbackAndUserId(feedback, ContextHolder.getLoggedInUserId());
	}

	private boolean checkFeedbackShouldBeShown(final FeedbackUser feedbackUser, final Integer attempts) {
		return (feedbackUser.getViews() >= attempts);
	}

}
