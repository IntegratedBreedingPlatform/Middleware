package org.generationcp.middleware.service.impl.feedback;

import java.util.Optional;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackUser;
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
    final Optional<Feedback> optionalFeedback = this.getFeedback(feature);
    if (!optionalFeedback.isPresent()) {
      return false;
    }

    final Feedback feedback = optionalFeedback.get();
    final Optional<FeedbackUser> optionalFeedbackUser = this.getFeedbackUser(feedback);
    if (!optionalFeedbackUser.isPresent()) {
      return true;
    }
    return optionalFeedbackUser.get().getShowAgain();
  }

  @Override
  public void dontShowAgain(final FeedbackFeature feature) {
  	this.getFeedback(feature).ifPresent(feedback -> {
			final FeedbackUser feedbackUser =
					this.getFeedbackUser(feedback).orElseGet(() -> this.createFeedback(feedback));
			feedbackUser.dontShowAgain();
			this.workbenchDaoFactory.getFeedbackUserDAO().save(feedbackUser);
		});
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

}
