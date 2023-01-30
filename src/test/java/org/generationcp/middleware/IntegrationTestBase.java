
package org.generationcp.middleware;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * A marker base class to hold common Spring test annotations and other common stuff when needed, for Middleware data managers and services
 * integration tests (i.e. tests that require actual workbench and crop database connections).
 *
 * @author Naymesh Mistry
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:BaseNamingRuleTest-context.xml", "classpath*:/testContext.xml"})
// Spring configuration to automatically rollback after test completion.
@TransactionConfiguration(defaultRollback = true)
@Transactional
public abstract class IntegrationTestBase {

	public static final String ADMIN_NAME = "admin";

	private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBase.class);
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	public static final int INDENT = 3;

	@Autowired
	@Qualifier(value = "cropSessionProvider")
	protected HibernateSessionProvider sessionProvder;

	@Autowired
	@Qualifier(value = "workbenchSessionProvider")
	protected HibernateSessionProvider workbenchSessionProvider;

	@Autowired
	private UserService userService;


	@Rule
	public TestName name = new TestName();
	private long startTime;

	@BeforeClass
	public static void setUpOnce() {
		// Variable caching relies on the context holder to determine current crop database in use
		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(PROGRAM_UUID);
	}

	@Before
	public void beforeEachTest() {
		LOG.info("+++++ Test : " + this.getClass().getSimpleName() + "." + this.name.getMethodName() + "() started +++++\n");
		this.startTime = System.nanoTime();

		ContextHolder.setLoggedInUserId(this.findAdminUser());
	}

	@After
	public void afterEachTest() {
		final long elapsedTime = System.nanoTime() - this.startTime;
		LOG.info(" +++++ Test : " + this.getClass().getSimpleName() + "." + this.name.getMethodName() + "() ended, took "
				+ (double) elapsedTime / 1000000 + " ms = " + (double) elapsedTime / 1000000000 + " s +++++\n");
	}

	protected Integer findAdminUser() {
		final WorkbenchUser user = this.userService.getUserByName(ADMIN_NAME, 0, 1, Operation.EQUAL).get(0);
		return user.getUserid();
	}

}
