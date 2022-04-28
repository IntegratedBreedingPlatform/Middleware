package org.generationcp.middleware.service.api.feedback;

import org.generationcp.middleware.api.feedback.FeedbackDto;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;

public interface FeedbackService {

	boolean shouldShowFeedback(FeedbackFeature feature);

	void dontShowAgain(FeedbackFeature feature);

	FeedbackDto getFeedBack(FeedbackFeature feature);
}
