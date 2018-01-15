
package org.generationcp.middleware.dao;

import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.KeySequenceRegister;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KeySequenceRegisterDAOTest extends IntegrationTestBase {

	private static final String PREFIX = "QWERTY" + new Random().nextInt();
	private static final String SUFFIX = "XYZ" + new Random().nextInt();
	private static final Integer LAST_SEQUENCE_USED = 9;
	private static final Integer LAST_SEQUENCE_USED2 = 19;
	private static final Integer LAST_SEQUENCE_USED3 = 29;
	private KeySequenceRegisterDAO keySequenceRegisterDao;

	@Before
	public void setup() {
		final Session session = this.sessionProvder.getSession();
		this.keySequenceRegisterDao = new KeySequenceRegisterDAO();
		this.keySequenceRegisterDao.setSession(session);
	}

	@Test
	public void testGetByPrefixAndSuffixWithNoPrefix() {
		// Save new records: 1) with null suffix 2) with empty string suffix
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);

		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(null, KeySequenceRegisterDAOTest.SUFFIX);
		Assert.assertNull(retrievedKeyRegister);
		final KeySequenceRegister retrievedKeyRegisterWithEmptyPrefix =
				this.keySequenceRegisterDao.getByPrefixAndSuffix("", KeySequenceRegisterDAOTest.SUFFIX);
		Assert.assertNull(retrievedKeyRegisterWithEmptyPrefix);
	}

	@Test
	public void testGetByPrefixAndSuffixWithNoSuffix() {
		// Save new records: 1) with null suffix 2) with empty string suffix
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister keyRegister2 = new KeySequenceRegister();
		keyRegister2.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister2.setSuffix("");
		keyRegister2.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2);
		this.keySequenceRegisterDao.save(keyRegister2);

		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, null);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue(), retrievedKeyRegister.getLastUsedSequence());
		final KeySequenceRegister retrievedKeyRegisterWithEmptySuffix =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, "");
		Assert.assertNotNull(retrievedKeyRegisterWithEmptySuffix);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue(), retrievedKeyRegister.getLastUsedSequence());
	}

	@Test
	public void testGetByPrefixAndSuffixWithSpaceAfterPrefix() {
		// Save new records: 1) one with space after prefix and 2)one without space after prefix
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister keyRegister2 = new KeySequenceRegister();
		keyRegister2.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX + " ");
		keyRegister2.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2);
		this.keySequenceRegisterDao.save(keyRegister2);

		final KeySequenceRegister retrievedKeyRegister1 =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, null);
		Assert.assertNotNull(retrievedKeyRegister1);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), retrievedKeyRegister1.getLastUsedSequence());
		final KeySequenceRegister retrievedKeyRegister2 =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX + " ", null);
		Assert.assertNotNull(retrievedKeyRegister2);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue(), retrievedKeyRegister2.getLastUsedSequence());
	}

	@Test
	public void testGetByPrefixAndSuffixWithSuffixSpecified() {
		Assert.assertNull(
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX));

		// Save new records: 1)one without suffix 2) one without suffix and 3) one with space before suffix
		final KeySequenceRegister keyRegister1 = new KeySequenceRegister();
		keyRegister1.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister1.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister1);

		final KeySequenceRegister keyRegister2 = new KeySequenceRegister();
		keyRegister2.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister2.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister2.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2);
		this.keySequenceRegisterDao.save(keyRegister2);

		final KeySequenceRegister keyRegister3 = new KeySequenceRegister();
		keyRegister3.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister3.setSuffix(" " + KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister3.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED3);
		this.keySequenceRegisterDao.save(keyRegister3);

		final KeySequenceRegister retrievedKeyRegisterWithSuffix =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX);
		Assert.assertNotNull(retrievedKeyRegisterWithSuffix);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue(),
				retrievedKeyRegisterWithSuffix.getLastUsedSequence());
		final KeySequenceRegister retrievedKeyRegisterWithSpaceBeforeSuffix = this.keySequenceRegisterDao
				.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, " " + KeySequenceRegisterDAOTest.SUFFIX);
		Assert.assertNotNull(retrievedKeyRegisterWithSpaceBeforeSuffix);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED3.intValue(),
				retrievedKeyRegisterWithSpaceBeforeSuffix.getLastUsedSequence());
	}

	@Test
	public void testIncrementAndGetNextSequenceWhenSequenceAlreadyExists() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister existingKeyRegister =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX);
		Assert.assertNotNull(existingKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), existingKeyRegister.getLastUsedSequence());

		// Expecting last sequence used to be incremented
		final int newLastSequenceUsed = KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED + 1;
		Assert.assertEquals(newLastSequenceUsed, this.keySequenceRegisterDao.incrementAndGetNextSequence(KeySequenceRegisterDAOTest.PREFIX,
				KeySequenceRegisterDAOTest.SUFFIX));
		final KeySequenceRegister finalKeyRegister =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX);
		Assert.assertNotNull(finalKeyRegister);
		Assert.assertEquals(newLastSequenceUsed, finalKeyRegister.getLastUsedSequence());
		Assert.assertEquals(existingKeyRegister.getId(), finalKeyRegister.getId());
	}

	@Test
	public void testIncrementAndGetNextSequenceWhenSequenceNotYetExists() {
		Assert.assertNull(
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX));

		// Expecting 1 to be returned and afterwards a record saved if the sequence did not yet exist
		Assert.assertEquals(1, this.keySequenceRegisterDao.incrementAndGetNextSequence(KeySequenceRegisterDAOTest.PREFIX,
				KeySequenceRegisterDAOTest.SUFFIX));

		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefixAndSuffix(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(1, retrievedKeyRegister.getLastUsedSequence());
	}

}
