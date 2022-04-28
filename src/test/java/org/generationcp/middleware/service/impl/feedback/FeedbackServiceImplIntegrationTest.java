package org.generationcp.middleware.service.impl.feedback;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.pojos.workbench.feedback.FeedbackFeature;
import org.generationcp.middleware.service.api.feedback.FeedbackService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeedbackServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private FeedbackService feedbackService;

	private PersonDAO personDAO;
	private WorkbenchUserDAO workbenchUserDAO;

	@Before
	public void setUp() throws Exception {
		this.personDAO = new PersonDAO();
		this.personDAO.setSession(this.workbenchSessionProvider.getSession());

		this.workbenchUserDAO = new WorkbenchUserDAO();
		this.workbenchUserDAO.setSession(this.workbenchSessionProvider.getSession());

		final WorkbenchUser user = this.createUser();
		ContextHolder.setLoggedInUserId(user.getUserid());
	}

	@Test
	public void shouldShowFeedbackAndMarkItToNotShowAgain() {
		assertFalse(this.feedbackService.shouldShowFeedback(FeedbackFeature.GERMPLASM_LIST));
		assertFalse(this.feedbackService.shouldShowFeedback(FeedbackFeature.GERMPLASM_LIST));
		assertFalse(this.feedbackService.shouldShowFeedback(FeedbackFeature.GERMPLASM_LIST));
		assertFalse(this.feedbackService.shouldShowFeedback(FeedbackFeature.GERMPLASM_LIST));

		// Take in mind that attempts field in the feature table is set to 5. So, that's why
		// the feedback should be shown the fifth time this is method is called.
		assertTrue(this.feedbackService.shouldShowFeedback(FeedbackFeature.GERMPLASM_LIST));
		assertTrue(this.feedbackService.shouldShowFeedback(FeedbackFeature.GERMPLASM_LIST));

		this.feedbackService.dontShowAgain(FeedbackFeature.GERMPLASM_LIST);
		assertFalse(this.feedbackService.shouldShowFeedback(FeedbackFeature.GERMPLASM_LIST));
	}

	public WorkbenchUser createUser() {
		final Person person = new Person(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
			RandomStringUtils.randomAlphabetic(10));
		person.setEmail(RandomStringUtils.randomAlphabetic(10));
		person.setContact(RandomStringUtils.randomAlphabetic(10));
		person.setExtension(RandomStringUtils.randomAlphabetic(10));
		person.setFax(RandomStringUtils.randomAlphabetic(10));
		person.setInstituteId(0);
		person.setLanguage(0);
		person.setNotes("");
		person.setPhone("");
		person.setPositionName("");
		person.setTitle("");

		final Set<CropType> crops = new HashSet<>();
		person.setCrops(crops);
		this.personDAO.save(person);

		final WorkbenchUser user =
			new WorkbenchUser(null, 1, 0, 1, 1, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), person,
				20150101, 20150101);

		return this.workbenchUserDAO.save(user);
	}

}
