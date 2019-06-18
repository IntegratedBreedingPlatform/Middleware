
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
	public void testGetByPrefixWithNoPrefix() {
		// Save new record
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);

		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(null);
		Assert.assertNull(retrievedKeyRegister);
		final KeySequenceRegister retrievedKeyRegisterWithEmptyPrefix =
				this.keySequenceRegisterDao.getByPrefix("");
		Assert.assertNull(retrievedKeyRegisterWithEmptyPrefix);
	}

	@Test
	public void testGetByPrefixWithSpaceAfterPrefix() {
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
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister1);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue(), retrievedKeyRegister1.getLastUsedSequence());
		final KeySequenceRegister retrievedKeyRegister2 =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX + " ");
		Assert.assertNotNull(retrievedKeyRegister2);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue(), retrievedKeyRegister2.getLastUsedSequence());
	}

	@Test
	public void testGetNextSequenceWithNoPrefix() {
		// Save new records: 1) with null suffix 2) with empty string suffix
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);

		Assert.assertEquals(1, this.keySequenceRegisterDao.getNextSequence(null));
		Assert.assertEquals(1, this.keySequenceRegisterDao.getNextSequence(""));
	}

	@Test
	public void testGetNextSequenceWithNoSuffix() {
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

		final String prefix2 = "ABC" + new Random().nextInt() + "DE";
		final KeySequenceRegister keyRegister3 = new KeySequenceRegister();
		keyRegister3.setKeyPrefix(prefix2);
		keyRegister3.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister3.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister3);

		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue() + 1, this.keySequenceRegisterDao.getNextSequence(KeySequenceRegisterDAOTest.PREFIX));
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue() + 1, this.keySequenceRegisterDao.getNextSequence(KeySequenceRegisterDAOTest.PREFIX));
		// Expecting no record to be retrieved when suffix parameter is null/empty and existing DB sequence has suffix
		Assert.assertEquals(1, this.keySequenceRegisterDao.getNextSequence(prefix2));
		Assert.assertEquals(1, this.keySequenceRegisterDao.getNextSequence(prefix2));
	}

	@Test
	public void testGetNextSequenceWithSpaceAfterPrefix() {
		// Save new records: 1) one with space after prefix and 2)one without space after prefix
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister keyRegister2 = new KeySequenceRegister();
		keyRegister2.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX + " ");
		keyRegister2.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2);
		this.keySequenceRegisterDao.save(keyRegister2);

		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue() + 1, this.keySequenceRegisterDao.getNextSequence(KeySequenceRegisterDAOTest.PREFIX));
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED2.intValue() + 1, this.keySequenceRegisterDao.getNextSequence(KeySequenceRegisterDAOTest.PREFIX + " "));
	}


	@Test
	public void testIncrementAndGetNextSequenceWhenSequenceAlreadyExists() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister existingKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(existingKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), existingKeyRegister.getLastUsedSequence());

		// Expecting last sequence used to be incremented
		final int newLastSequenceUsed = KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED + 1;
		Assert.assertEquals(newLastSequenceUsed, this.keySequenceRegisterDao.incrementAndGetNextSequence(KeySequenceRegisterDAOTest.PREFIX,
				KeySequenceRegisterDAOTest.SUFFIX));
		final KeySequenceRegister finalKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(finalKeyRegister);
		Assert.assertEquals(newLastSequenceUsed, finalKeyRegister.getLastUsedSequence());
		Assert.assertEquals(existingKeyRegister.getId(), finalKeyRegister.getId());
	}

	@Test
	public void testIncrementAndGetNextSequenceWhenSequenceNotYetExists() {
		Assert.assertNull(
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX));

		// Expecting 1 to be returned and afterwards a record saved if the sequence did not yet exist
		Assert.assertEquals(1, this.keySequenceRegisterDao.incrementAndGetNextSequence(KeySequenceRegisterDAOTest.PREFIX,
				KeySequenceRegisterDAOTest.SUFFIX));

		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(1, retrievedKeyRegister.getLastUsedSequence());
	}

	@Test
	public void testSaveLastSequenceUsedWhenSequenceNotYetExists() {
		Assert.assertNull(
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX));

		final Integer lastSequenceUsed = 16;
		// Expecting a record to be saved with specified last sequence #
		this.keySequenceRegisterDao.saveLastSequenceUsed(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX,
				lastSequenceUsed);

		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(lastSequenceUsed.intValue(), retrievedKeyRegister.getLastUsedSequence());
	}

	@Test
	public void testSaveLastSequenceUsedWhenSequenceAlreadyExists() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister existingKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(existingKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), existingKeyRegister.getLastUsedSequence());

		final Integer lastSequenceUsed = 16;
		// Expecting record to be updated with specified last sequence #
		this.keySequenceRegisterDao.saveLastSequenceUsed(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX,
				lastSequenceUsed);
		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(lastSequenceUsed.intValue(), retrievedKeyRegister.getLastUsedSequence());
	}

	@Test
	public void testSaveLastSequenceUsedWhenSequenceNumberSpecifiedIsLessThanLastSequenceNumberSaved() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setSuffix(KeySequenceRegisterDAOTest.SUFFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister existingKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(existingKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), existingKeyRegister.getLastUsedSequence());

		final Integer lastSequenceUsed = KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue() - 1 ;
		// Expecting record to retain the saved last sequence # since specified start number is smaller
		this.keySequenceRegisterDao.saveLastSequenceUsed(KeySequenceRegisterDAOTest.PREFIX, KeySequenceRegisterDAOTest.SUFFIX,
				lastSequenceUsed);
		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), retrievedKeyRegister.getLastUsedSequence());
	}

}
