package org.generationcp.middleware.service.impl.feedback;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.dao.feedback.FeedbackDAO;
import org.generationcp.middleware.dao.feedback.FeedbackUserDAO;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.pojos.workbench.feedback.Feedback;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FeedbackServiceImplTest {

	private static final FeedbackFeature FEEDBACK_FEATURE = FeedbackFeature.GERMPLASM_LIST;
	private static final int USER_ID = new Random().nextInt();
	private static final int SHOW_FEEDBACK_AFTER_VIEWS = 5;

	@InjectMocks
	private FeedbackServiceImpl feedbackService;

	@Mock
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Mock
	private FeedbackDAO feedbackDAO;

	@Mock
	private FeedbackUserDAO feedbackUserDAO;

	@Mock
	private WorkbenchUserDAO workbenchUserDAO;

	@Captor
	private ArgumentCaptor<FeedbackUser> feedbackUserArgumentCaptor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		ContextHolder.setLoggedInUserId(USER_ID);

		Mockito.when(this.workbenchDaoFactory.getFeedbackDAO()).thenReturn(this.feedbackDAO);
		Mockito.when(this.workbenchDaoFactory.getFeedbackUserDAO()).thenReturn(this.feedbackUserDAO);
		Mockito.when(this.workbenchDaoFactory.getWorkbenchUserDAO()).thenReturn(this.workbenchUserDAO);

		ReflectionTestUtils.setField(this.feedbackService, "workbenchDaoFactory", this.workbenchDaoFactory);
		ReflectionTestUtils.setField(this.feedbackService, "showFeedbackAfterFeatureViews", SHOW_FEEDBACK_AFTER_VIEWS);
		ReflectionTestUtils.setField(this.feedbackService, "feedbackFeatureEnabled", true);
	}

	@Test
	public void testShouldShowFeedback_featureReachedAmountOfViews() {
		this.assertFeatureReachedAmountOfViews(4, true);
	}

	@Test
	public void testShouldShowFeedback_featureNotReachedAmountOfViews() {
		this.assertFeatureReachedAmountOfViews(3, false);
	}

	@Test
	public void testShouldShowFeedback_feedbackMarkedAsDontShowAgain() {
		final Feedback feedback = this.createMockFeedback(true);
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.of(feedback));

		final FeedbackUser feedbackUser = this.createMockFeedbackUser(false, 1);
		Mockito.when(this.feedbackUserDAO.getByFeedbackAndUserId(feedback, USER_ID)).thenReturn(Optional.of(feedbackUser));

		final boolean shouldShowFeedback = this.feedbackService.shouldShowFeedback(FEEDBACK_FEATURE);
		assertFalse(shouldShowFeedback);

		Mockito.verify(this.feedbackDAO).getByFeature(FEEDBACK_FEATURE);
		Mockito.verify(this.feedbackUserDAO).getByFeedbackAndUserId(feedback, USER_ID);

		Mockito.verifyNoMoreInteractions(this.feedbackDAO);
		Mockito.verifyNoMoreInteractions(this.feedbackUserDAO);
		Mockito.verifyNoInteractions(this.workbenchUserDAO);
	}

	@Test
	public void testShouldShowFeedback_feedbackNotPresent() {
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.empty());

		final boolean shouldShowFeedback = this.feedbackService.shouldShowFeedback(FEEDBACK_FEATURE);
		assertFalse(shouldShowFeedback);

		Mockito.verify(this.feedbackDAO).getByFeature(FEEDBACK_FEATURE);

		Mockito.verifyNoMoreInteractions(this.feedbackDAO);
		Mockito.verifyNoInteractions(this.feedbackUserDAO);
		Mockito.verifyNoInteractions(this.workbenchUserDAO);
	}

	@Test
	public void testShouldShowFeedback_feedbackUserNotPresentAndNotReachedAmountOfViews() {
		this.assertFeedbackUserNotPresent(false);
	}

	@Test
	public void testShouldShowFeedback_feedbackUserNotPresentShowFeedbackAfterTheFirstTimeViewed() {
		ReflectionTestUtils.setField(this.feedbackService, "showFeedbackAfterFeatureViews", 1);

		this.assertFeedbackUserNotPresent(true);
	}

	@Test
	public void testShouldShowFeedback_feedbackNotEnabledGlobally() {
		ReflectionTestUtils.setField(this.feedbackService, "feedbackFeatureEnabled", false);

		final boolean shouldShowFeedback = this.feedbackService.shouldShowFeedback(FEEDBACK_FEATURE);
		assertFalse(shouldShowFeedback);
	}

	@Test
	public void testShouldShowFeedback_specificFeedbackNotEnabled() {
		final Feedback feedback = this.createMockFeedback(false);
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.of(feedback));

		final boolean shouldShowFeedback = this.feedbackService.shouldShowFeedback(FEEDBACK_FEATURE);
		assertFalse(shouldShowFeedback);
	}

	@Test
	public void testDontShowAgain_OK() {
		final Feedback feedback = this.createMockFeedback(true);
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.of(feedback));

		final FeedbackUser feedbackUser = Mockito.mock(FeedbackUser.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(this.feedbackUserDAO.getByFeedbackAndUserId(feedback, USER_ID)).thenReturn(Optional.of(feedbackUser));

		this.feedbackService.dontShowAgain(FEEDBACK_FEATURE);

		Mockito.verify(this.feedbackDAO).getByFeature(FEEDBACK_FEATURE);
		Mockito.verify(this.feedbackUserDAO).getByFeedbackAndUserId(feedback, USER_ID);

		Mockito.verify(this.feedbackUserDAO).save(this.feedbackUserArgumentCaptor.capture());
		final FeedbackUser actualFeedbackUser = this.feedbackUserArgumentCaptor.getValue();
		assertNotNull(actualFeedbackUser);
		assertFalse(actualFeedbackUser.getShowAgain());

		Mockito.verify(feedbackUser).dontShowAgain();

		Mockito.verifyNoMoreInteractions(this.feedbackDAO);
		Mockito.verifyNoMoreInteractions(this.feedbackUserDAO);
	}

	@Test
	public void testDontShowAgain_feedBackNotPresent() {
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.empty());

		this.feedbackService.dontShowAgain(FEEDBACK_FEATURE);

		Mockito.verify(this.feedbackDAO).getByFeature(FEEDBACK_FEATURE);

		Mockito.verifyNoMoreInteractions(this.feedbackDAO);
		Mockito.verifyNoInteractions(this.feedbackUserDAO);
	}

	@Test
	public void testDontShowAgain_feedbackUserNotPresent() {
		final Feedback feedback = this.createMockFeedback(true);
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.of(feedback));

		Mockito.when(this.feedbackUserDAO.getByFeedbackAndUserId(feedback, USER_ID)).thenReturn(Optional.empty());

		this.feedbackService.dontShowAgain(FEEDBACK_FEATURE);

		Mockito.verify(this.feedbackDAO).getByFeature(FEEDBACK_FEATURE);
		Mockito.verify(this.feedbackUserDAO).getByFeedbackAndUserId(feedback, USER_ID);

		Mockito.verifyNoMoreInteractions(this.feedbackDAO);
		Mockito.verifyNoMoreInteractions(this.feedbackUserDAO);
	}

	private Feedback createMockFeedback(boolean isEnabled) {
		final Feedback feedback = Mockito.mock(Feedback.class);
		Mockito.when(feedback.isEnabled()).thenReturn(isEnabled);
		return feedback;
	}

	private FeedbackUser createMockFeedbackUser(boolean showAgain, int views) {
		final FeedbackUser feedbackUser = Mockito.mock(FeedbackUser.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(feedbackUser.getShowAgain()).thenReturn(showAgain);

		ReflectionTestUtils.setField(feedbackUser, "views", views);

		return feedbackUser;
	}

	private void assertFeatureReachedAmountOfViews(int views, boolean shouldShow) {
		final Feedback feedback = this.createMockFeedback(true);
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.of(feedback));

		final FeedbackUser feedbackUser = this.createMockFeedbackUser(true, views);
		Mockito.when(this.feedbackUserDAO.getByFeedbackAndUserId(feedback, USER_ID)).thenReturn(Optional.of(feedbackUser));

		final boolean shouldShowFeedback = this.feedbackService.shouldShowFeedback(FEEDBACK_FEATURE);
		assertThat(shouldShowFeedback, is(shouldShow));

		Mockito.verify(this.feedbackDAO).getByFeature(FEEDBACK_FEATURE);
		Mockito.verify(this.feedbackUserDAO).getByFeedbackAndUserId(feedback, USER_ID);

		Mockito.verify(this.feedbackUserDAO).save(this.feedbackUserArgumentCaptor.capture());
		final FeedbackUser actualFeedbackUser = this.feedbackUserArgumentCaptor.getValue();
		assertNotNull(actualFeedbackUser);
		assertThat(actualFeedbackUser.getViews(), is(views + 1));
		assertTrue(actualFeedbackUser.getShowAgain());

		Mockito.verify(feedbackUser).hasSeen();

		Mockito.verifyNoMoreInteractions(this.feedbackDAO);
		Mockito.verifyNoMoreInteractions(this.feedbackUserDAO);
		Mockito.verifyNoInteractions(this.workbenchUserDAO);
	}

	private void assertFeedbackUserNotPresent(boolean shouldShow) {
		final Feedback feedback = this.createMockFeedback(true);
		Mockito.when(this.feedbackDAO.getByFeature(FEEDBACK_FEATURE)).thenReturn(Optional.of(feedback));

		Mockito.when(this.feedbackUserDAO.getByFeedbackAndUserId(feedback, USER_ID)).thenReturn(Optional.empty());

		final WorkbenchUser user = Mockito.mock(WorkbenchUser.class);
		Mockito.when(this.workbenchUserDAO.getById(USER_ID)).thenReturn(user);

		final boolean shouldShowFeedback = this.feedbackService.shouldShowFeedback(FEEDBACK_FEATURE);
		assertThat(shouldShowFeedback, is(shouldShow));

		Mockito.verify(this.feedbackDAO).getByFeature(FEEDBACK_FEATURE);
		Mockito.verify(this.feedbackUserDAO).getByFeedbackAndUserId(feedback, USER_ID);

		Mockito.verify(this.workbenchUserDAO).getById(USER_ID);

		Mockito.verify(this.feedbackUserDAO).save(this.feedbackUserArgumentCaptor.capture());
		final FeedbackUser actualFeedbackUser = this.feedbackUserArgumentCaptor.getValue();
		assertNotNull(actualFeedbackUser);
		assertThat(actualFeedbackUser.getViews(), is(1));

		Mockito.verifyNoMoreInteractions(this.feedbackDAO);
		Mockito.verifyNoMoreInteractions(this.feedbackUserDAO);
		Mockito.verifyNoMoreInteractions(this.workbenchUserDAO);
	}

}
