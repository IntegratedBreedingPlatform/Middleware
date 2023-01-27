package org.generationcp.middleware.service.api;

import java.util.List;

public interface KeySequenceRegisterService {

	/**
	 * Finds next available sequence number for a given keyPrefix and increments the last known sequence number in the key sequence
	 * registry.
	 *
	 * <p>
	 * <strong>Important notes regarding concurrency and thread safety:</strong>
	 *
	 * <p>
	 * Calling code must employ thread synchronization at the appropriate level - usually the very top level entry point such as a controller
	 * method or a Vaadin action. For example, in case of Germplasm naming (coding) the top level action that uses key sequence registry is
	 * <strong>org.generationcp.breeding.manager.listmanager.dialog.AssignCodesDialog.assignCodes()</strong>. You can see that this method
	 * calls the entire back-end processing logic in a synchronized code block, using AssignCodesDialog class as the lock object which means
	 * only one instance of AssignCodesDialog can enter it at a time. This use case employs pessimistic locking to ensure that the germplasm
	 * coded names with sequences are unique and are in series as much as possible.
	 *
	 * <p>
	 * Service does implement Hibernate based optimistic locking as ultimate mechanism to ensure that two threads never end up getting the
	 * same sequence number from registry. If explicit thread synchronization is not done, in concurrent access situation, expect
	 * <strong>HibernateOptimisticLockingFailureException</strong> to occur. This is a re-triable exception. Callers can catch it and
	 * operation can be retried.
	 */
	int incrementAndGetNextSequence(String keyPrefix);

	/**
	 * Finds next available sequence number for a given keyPrefix and increments the last known sequence number in the key sequence
	 * registry.
	 *
	 * <p>
	 * <strong>Important notes regarding concurrency and thread safety:</strong>
	 *
	 * <p>
	 * Calling code must employ thread synchronization at the appropriate level - usually the very top level entry point such as a controller
	 * method or a Vaadin action. For example, in case of Germplasm naming (coding) the top level action that uses key sequence registry is
	 * <strong>org.generationcp.breeding.manager.listmanager.dialog.AssignCodesDialog.assignCodes()</strong>. You can see that this method
	 * calls the entire back-end processing logic in a synchronized code block, using AssignCodesDialog class as the lock object which means
	 * only one instance of AssignCodesDialog can enter it at a time. This use case employs pessimistic locking to ensure that the germplasm
	 * coded names with sequences are unique and are in series as much as possible.
	 *
	 * <p>
	 * Service does implement Hibernate based optimistic locking as ultimate mechanism to ensure that two threads never end up getting the
	 * same sequence number from registry. If explicit thread synchronization is not done, in concurrent access situation, expect
	 * <strong>HibernateOptimisticLockingFailureException</strong> to occur. This is a re-triable exception. Callers can catch it and
	 * operation can be retried.
	 */
	int incrementAndGetNextSequenceUsingNativeSQL(String keyPrefix);

	/**
	 * Finds next available sequence number for a given the keyPrefix in key sequence registry
	 *
	 * <p>
	 * Service does implement Hibernate based optimistic locking as ultimate mechanism to ensure that two threads never end up getting the
	 * same sequence number from registry. If explicit thread synchronization is not done, in concurrent access situation, expect
	 * <strong>HibernateOptimisticLockingFailureException</strong> to occur. This is a re-triable exception. Callers can catch it and
	 * operation can be retried.
	 *
	 */
	int getNextSequence(String keyPrefix);

	/**
	 * Finds next available sequence number for a given the keyPrefix in key sequence registry
	 *
	 * <p>
	 * Service does implement Hibernate based optimistic locking as ultimate mechanism to ensure that two threads never end up getting the
	 * same sequence number from registry. If explicit thread synchronization is not done, in concurrent access situation, expect
	 * <strong>HibernateOptimisticLockingFailureException</strong> to occur. This is a re-triable exception. Callers can catch it and
	 * operation can be retried.
	 *
	 */
	int getNextSequenceUsingNativeSQL(String keyPrefix);

	/**
	 * If keyPrefix exists in key sequence registry, update the last sequence number
	 * with lastSequenceUsed parameter if it is greater than the value of last sequence saved for that sequence.
	 *
	 * If keyPrefix does not yet exist in key sequence registry, create a new record in registry
	 * and save lastSequenceUsed parameter as the last sequence number used.
	 *
	 * <p>
	 * Service does implement Hibernate based optimistic locking as ultimate mechanism to ensure that two threads never end up updating
	 * the same sequence from registry. If explicit thread synchronization is not done, in concurrent access situation, expect
	 * <strong>HibernateOptimisticLockingFailureException</strong> to occur. This is a re-triable exception. Callers can catch it and
	 * operation can be retried.
	 *
	 * @param keyPrefix
	 * @param lastSequenceUsed
	 */
	void saveLastSequenceUsed(String keyPrefix, Integer lastSequenceUsed);

	/**
	 * If keyPrefix exists in key sequence registry, update the last sequence number
	 * with lastSequenceUsed parameter if it is greater than the value of last sequence saved for that sequence.
	 *
	 * If keyPrefix does not yet exist in key sequence registry, create a new record in registry
	 * and save lastSequenceUsed parameter as the last sequence number used.
	 *
	 * <p>
	 * Service does implement Hibernate based optimistic locking as ultimate mechanism to ensure that two threads never end up updating
	 * the same sequence from registry. If explicit thread synchronization is not done, in concurrent access situation, expect
	 * <strong>HibernateOptimisticLockingFailureException</strong> to occur. This is a re-triable exception. Callers can catch it and
	 * operation can be retried.
	 *
	 * @param keyPrefix
	 * @param lastSequenceUsed
	 */
	void saveLastSequenceUsedUsingNativeSQL(String keyPrefix, Integer lastSequenceUsed);

	/**
	 * Deletes the KeySequenceRegister entries given the Key Prefixes
	 *
	 * @param keyPrefixes
	 */
	void deleteKeySequences(List<String> keyPrefixes);
}
