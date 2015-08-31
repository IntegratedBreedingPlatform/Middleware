
package org.generationcp.middleware;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.junit.After;
import org.junit.Before;
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

/**
 * A marker base class to hold common Spring test annotations and other common stuff when needed, for Middleware data managers and services
 * integration tests (i.e. tests that require actual workbench and crop database connections).
 *
 * @author Naymesh Mistry
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/testContext.xml"})
// Spring configuration to automatically rollback after test completion.
@TransactionConfiguration(defaultRollback = true)
@Transactional
public abstract class IntegrationTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBase.class);
	public static final int INDENT = 3;

	@Autowired
	@Qualifier(value = "cropSessionProvider")
	protected HibernateSessionProvider sessionProvder;

	@Rule
	public TestName name = new TestName();
	private long startTime;

	@Before
	public void beforeEachTest() {
		LOG.info("+++++ Test : " + this.getClass().getSimpleName() + "." + this.name.getMethodName() + "() started +++++\n");
		this.startTime = System.nanoTime();
	}

	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - this.startTime;
		LOG.info(" +++++ Test : " + this.getClass().getSimpleName() + "." + this.name.getMethodName() + "() ended, took "
				+ (double) elapsedTime / 1000000 + " ms = " + (double) elapsedTime / 1000000000 + " s +++++\n");
	}

}
