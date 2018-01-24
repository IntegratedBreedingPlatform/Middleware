
package org.generationcp.middleware.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.KeySequenceRegisterDAO;
import org.generationcp.middleware.pojos.KeySequenceRegister;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * This test is just an integration test harness that enables testing of the service without worrying about deploying the user interface
 * that calls it in real-life, but still simulating the way the UI calls it. Must disable the auto transaction rollback
 * (@TransactionConfiguration(defaultRollback = false)) in IntegrationTestBase to use this test.
 * 
 * Intentionally not added to Middleware pom so that it does not run as part of maven builds.
 */
public class KeySequenceRegisterServiceImplIntegrationTest extends IntegrationTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(KeySequenceRegisterServiceImplIntegrationTest.class);
	private static final String PREFIX = "QWERTY" + new Random().nextInt();
	private static final String SUFFIX = "XYZ" + new Random().nextInt();
	private static final Integer LAST_SEQUENCE_USED = 9;
	
	@Autowired
	@Qualifier(value = "IBDBV2_MAIZE_MERGED_SessionFactory")
	private SessionFactory sessionFactory;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;
	
	private KeySequenceRegisterDAO keySequenceRegisterDao;
	
	@Before
	public void setup() {
		final Session session = this.sessionProvder.getSession();
		this.keySequenceRegisterDao = new KeySequenceRegisterDAO();
		this.keySequenceRegisterDao.setSession(session);
	}

	@Test
	public void testIncrementAndGetNextSequence() throws Exception {

		int threads = 10;
		List<Future<Integer>> results = new ArrayList<>();

		ExecutorService threadPool = Executors.newFixedThreadPool(threads);

		// Simulating how vaadin client components use the middleware service.
		// Also simulating multiple parallel users/threads invoking same operation.

		for (int i = 1; i <= threads; i++) {
			Future<Integer> result = threadPool.submit(new Callable<Integer>() {
				@Override
				public Integer call() {
					return new AssignCodeVaadinComponent().assignCodes();
				}
			});
			results.add(result);
		}

		threadPool.shutdown();
		while (!threadPool.isTerminated()) {
		}

		Set<Integer> uniqueSequences = new HashSet<>();
		for (Future<Integer> future : results) {
			Integer generatedSequence = future.get();
			uniqueSequences.add(generatedSequence);
			LOG.info("Sequence returned: {}.", generatedSequence);
		}
		Assert.assertEquals("Each thread must return a unique sequence.", threads, uniqueSequences.size());
	}
	
	@Test
	public void testGetNextSequence() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterServiceImplIntegrationTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterServiceImplIntegrationTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterServiceImplIntegrationTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		
		final KeySequenceRegisterService keySequenceRegisterService = new KeySequenceRegisterServiceImpl(this.sessionProvder.getSession());
		final int nextSequence = keySequenceRegisterService.getNextSequence(PREFIX, SUFFIX);
		Assert.assertEquals(LAST_SEQUENCE_USED + 1, nextSequence);
	}

	/**
	 * Represents a vaadin component like class which is an example client of the service under test.
	 */
	class AssignCodeVaadinComponent {

		public int assignCodes() {

			synchronized (AssignCodeVaadinComponent.class) {

				TransactionTemplate tx = new TransactionTemplate(platformTransactionManager);
				return tx.execute(new TransactionCallback<Integer>() {

					@Override
					public Integer doInTransaction(TransactionStatus status) {
						KeySequenceRegisterService keySequenceRegisterService =
								new KeySequenceRegisterServiceImpl(SessionFactoryUtils.getSession(sessionFactory, false));
						return keySequenceRegisterService.incrementAndGetNextSequence("CML", null);
					}
				});
			}
		}
	}
}
