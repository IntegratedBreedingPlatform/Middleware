package org.generationcp.middleware.service.api.feedback;

import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;

import java.util.Optional;

public interface FeedbackService {

	boolean shouldShowFeedback(FeedbackFeature feature);

	void dontShowAgain(FeedbackFeature feature);

	Optional<Feedback> getFeedback(FeedbackFeature feature);
}
