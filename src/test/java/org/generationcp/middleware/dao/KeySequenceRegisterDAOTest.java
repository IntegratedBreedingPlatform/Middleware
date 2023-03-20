
package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.KeySequenceRegister;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

public class KeySequenceRegisterDAOTest extends IntegrationTestBase {

	private static final String PREFIX = "QWERTY" + new Random().nextInt();
	private static final Integer LAST_SEQUENCE_USED = 9;
	private KeySequenceRegisterDAO keySequenceRegisterDao;

	@Before
	public void setup() {
		this.keySequenceRegisterDao = new KeySequenceRegisterDAO(this.sessionProvder.getSession());
	}

	@Test
	public void testGetByPrefixWithNoPrefix() {
		// Save new record
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
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
	public void testGetNextSequenceWithNoPrefix() {
		// Save new records: 1) with null suffix 2) with empty string suffix
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);

		Assert.assertEquals(1, this.keySequenceRegisterDao.getNextSequence(null));
		Assert.assertEquals(1, this.keySequenceRegisterDao.getNextSequence(""));
	}

	@Test
	public void testIncrementAndGetNextSequenceWhenSequenceAlreadyExists() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister existingKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(existingKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), existingKeyRegister.getLastUsedSequence());

		// Expecting last sequence used to be incremented
		final int newLastSequenceUsed = KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED + 1;
		Assert.assertEquals(newLastSequenceUsed, this.keySequenceRegisterDao.incrementAndGetNextSequence(KeySequenceRegisterDAOTest.PREFIX));
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
		Assert.assertEquals(1, this.keySequenceRegisterDao.incrementAndGetNextSequence(KeySequenceRegisterDAOTest.PREFIX));

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
		this.keySequenceRegisterDao.saveLastSequenceUsed(KeySequenceRegisterDAOTest.PREFIX, lastSequenceUsed);

		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(lastSequenceUsed.intValue(), retrievedKeyRegister.getLastUsedSequence());
	}

	@Test
	public void testSaveLastSequenceUsedWhenSequenceAlreadyExists() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister existingKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(existingKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), existingKeyRegister.getLastUsedSequence());

		final Integer lastSequenceUsed = 16;
		// Expecting record to be updated with specified last sequence #
		this.keySequenceRegisterDao.saveLastSequenceUsed(KeySequenceRegisterDAOTest.PREFIX, lastSequenceUsed);
		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(lastSequenceUsed.intValue(), retrievedKeyRegister.getLastUsedSequence());
	}

	@Test
	public void testSaveLastSequenceUsedWhenSequenceNumberSpecifiedIsLessThanLastSequenceNumberSaved() {
		final KeySequenceRegister keyRegister = new KeySequenceRegister();
		keyRegister.setKeyPrefix(KeySequenceRegisterDAOTest.PREFIX);
		keyRegister.setLastUsedSequence(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED);
		this.keySequenceRegisterDao.save(keyRegister);
		final KeySequenceRegister existingKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(existingKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), existingKeyRegister.getLastUsedSequence());

		final Integer lastSequenceUsed = KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED - 1 ;
		// Expecting record to retain the saved last sequence # since specified start number is smaller
		this.keySequenceRegisterDao.saveLastSequenceUsed(KeySequenceRegisterDAOTest.PREFIX, lastSequenceUsed);
		final KeySequenceRegister retrievedKeyRegister =
				this.keySequenceRegisterDao.getByPrefix(KeySequenceRegisterDAOTest.PREFIX);
		Assert.assertNotNull(retrievedKeyRegister);
		Assert.assertEquals(KeySequenceRegisterDAOTest.LAST_SEQUENCE_USED.intValue(), retrievedKeyRegister.getLastUsedSequence());
	}

	@Test
	public void testDeleteByKeyPrefixes() {
		final String prefix = "SKSKSKSPREFIXSKSKSK";
		this.keySequenceRegisterDao.save(new KeySequenceRegister(prefix, 10));
		Assert.assertNotNull(this.keySequenceRegisterDao.getByPrefix(prefix));
		this.keySequenceRegisterDao.deleteByKeyPrefixes(Collections.singletonList(prefix));
		Assert.assertNull(this.keySequenceRegisterDao.getByPrefix(prefix));
	}
}
