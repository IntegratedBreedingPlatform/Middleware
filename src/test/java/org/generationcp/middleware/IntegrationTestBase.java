
package org.generationcp.middleware;

import java.util.Date;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.TestDataHelper;
import org.generationcp.middleware.util.CustomClock;
import org.junit.runner.RunWith;
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
public abstract class IntegrationTestBase extends TestBase {

	public static final int INDENT = 3;

	@Autowired
	@Qualifier(value = "cropSessionProvider")
	protected HibernateSessionProvider sessionProvder;

	@Autowired
	private CustomClock customClock;

	protected void stubCurrentDate(int year, int month, int day) {
		customClock.set(TestDataHelper.constructDate(year, month, day));
	}

	protected void stubCurrentDate(Date date) {
		customClock.set(date);
	}
}


