package org.generationcp.middleware;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestBase {

	private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBase.class);

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

	protected abstract void stubCurrentDate(int year, int month, int day);

	protected abstract void stubCurrentDate(Date date);
}
